package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.ast.*;
import org.hamcrest.core.IsEqual;

import javax.naming.Name;
import java.lang.annotation.Target;
import java.security.PublicKey;
import java.util.*;

public class TypeChecker implements ASTVisitor {

    //Helper Classes
    public static class Pair{
        int scope;
        NameDef dec;

        Pair(int s, NameDef nd){
            this.scope = s;
            this.dec = nd;
        }
    }

    public static class Symbol{
        ArrayList<Pair> chainList = new ArrayList<>();

        public Symbol(int _scope, NameDef nd){
            Pair pair = new Pair(_scope, nd);
            chainList.add(pair);
        }

        public void add(int _scope, NameDef _nd){
            Pair pair = new Pair(_scope, _nd);
            chainList.add(pair);
        }
    }

    //Creating Symbol Table
    public static class SymbolTable{
        //Serial number of current scope and the next serial number to assign
        int current_num;
        int next_num = 0;

        //Scope Stack
        Stack<Integer> scope_stack = new Stack<>();

        void enterScope(){
            current_num = next_num++;
            scope_stack.push(current_num);
        }

        void closeScope(){
            current_num = scope_stack.pop();
        }

        HashMap<String, Symbol> entries = new HashMap<>();

        //returns true if name successfully inserted in symbol table, false if already present
        public boolean insert(String name, NameDef nd){
            //Returns true if name successfully inserted into table, false if already present
            Boolean inserted = entries.putIfAbsent(name, new Symbol(scope_stack.peek(), nd)) == null;
            if(inserted == false){
                Stack<Integer> tempScope_Stack = (Stack<Integer>) scope_stack.clone();

                while(!tempScope_Stack.empty()) {
                    //Check if you are adding an identifier at a scope that is already in the list
                    for (Pair pair : entries.get(name).chainList) {
                        if (pair.scope == scope_stack.peek()) {
                            return false;
                        }
                    }
                    tempScope_Stack.pop();
                }
                //If name exists, chain to a new pair of scope and nameDef to arrayList for current identifier
                entries.get(name).add(scope_stack.peek(), nd);
                inserted = true;
            }
            return true;
        }

        public NameDef lookup(String name) {
            Stack<Integer> tempScope_Stack = (Stack<Integer>) scope_stack.clone();

            if(entries.get(name) != null) {
                while (!tempScope_Stack.empty()) {
                    for (Pair pair : entries.get(name).chainList) {
                        if (pair.scope == tempScope_Stack.peek()) {
                            return pair.dec;
                        }
                    }
                    tempScope_Stack.pop();
                }
            }


            return null;
        }

        public boolean isUniqueAtScope(String name){
            //Get value at top stack
            //See if name exists at that scope level
            int currScope = scope_stack.peek();
            if(entries.get(name) != null) {
                for (Pair pair : entries.get(name).chainList) {
                    if (pair.scope == currScope) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    //Create instance of table
    SymbolTable symbolTable = new SymbolTable();

    //Save the instance of the Program's Type
    Type programType;

    //Helper function to help check conditions
    private void check(boolean condition, AST node, String message) throws TypeCheckException{
        if(!condition){
            throw new TypeCheckException(message + "at line: " + node.getLine() + " and column: " + node.getColumn());
        }
    }

    //Helper Function to check assignment compatibility
    private boolean assignmentCompatible(Type targetType, Type rhsType){
        return (targetType == rhsType || targetType==Type.STRING && rhsType==Type.INT || targetType==Type.STRING && rhsType==Type.PIXEL ||  targetType==Type.STRING && rhsType==Type.IMAGE ||  targetType==Type.INT && rhsType==Type.PIXEL ||  targetType==Type.PIXEL && rhsType==Type.INT ||  targetType==Type.IMAGE && rhsType==Type.PIXEL ||  targetType==Type.IMAGE && rhsType==Type.STRING);
    }

    public Object visitProgram(Program program, Object arg) throws PLCException{

        symbolTable.enterScope();

        //Save Program's Type
        programType = program.getType();

        //Make list and visit all param lists
        List<NameDef> list = program.getParamList();
        for(AST node: list){
            node.visit(this, arg);
        }

        //Visit block
        program.getBlock().visit(this, arg);

        symbolTable.closeScope();

        return program;
    }

    public Object visitBlock(Block block, Object arg) throws PLCException{
        //Makes lists of declarations and statements
        List<Declaration> listOfDecks = block.getDecList();
        List<Statement> statementList = block.getStatementList();

        //Traverse and visit all declarations and statements
        for(AST node: listOfDecks){
            node.visit(this, arg);
        }
        for(AST node: statementList){
            node.visit(this, arg);
        }
        return null;
    }

    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCException{

        Expr initializer = declaration.getInitializer();
        //Check that Expr is defined
        if(initializer != null){
            //It is not allowed to refer to the name being defined
            declaration.getInitializer().visit(this, arg);
            Type initializerType = initializer.getType();
            check(assignmentCompatible(declaration.getNameDef().getType(), initializerType), declaration, "Type of expression and declared type do not match");

        }

        //Visit nameDef
        declaration.getNameDef().visit(this, arg);

        //If nameDef type is an image, then either the initializer is not null, the dimension of NameDef is not null, or both
        if(declaration.getNameDef().getType() == Type.IMAGE){
            check(initializer != null || declaration.getNameDef().getDimension() != null, declaration, "Either initializer or dimensions are null when nameDef is an IMAGE");
        }

        return null;

    }

    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCException{
        //Check constraints when dimension is not empty
        if(nameDef.getDimension() != null){
            //Make sure type is an image and dimension is properly typed
            check(nameDef.getType() == Type.IMAGE, nameDef, "nameDef isn't an image when dimension isn't null");
            nameDef.getDimension().visit(this, arg);
        }

        String name = nameDef.getIdent().getName();

        //Call function to assure that the same ident isn't declared in the same name
        check(symbolTable.isUniqueAtScope(name), nameDef, "Ident's name has been previously declared at this scope");

        //Check type isn't equal to void
        check(nameDef.getType() != Type.VOID, nameDef, "The type of this NameDef is VOID");

        //Insert the Name and NameDef to table at the current scope
        symbolTable.insert(name, nameDef);
        nameDef.setScope(symbolTable.current_num);

        return null;
    }

    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCException{
        //Assigns the conditional values name
        Expr expr0 = conditionalExpr.getGuard();
        Expr expr1 = conditionalExpr.getTrueCase();
        Expr expr2 = conditionalExpr.getFalseCase();

        //Ensure expr0-2 are all properly typed
        expr0.visit(this, arg);
        expr1.visit(this, arg);
        expr2.visit(this, arg);

        //Checks that expr0 is an int and expr1 and 2 are the same type
        check(expr0.getType() == Type.INT, conditionalExpr, "The guard is not an INT");
        check(expr1.getType() == expr2.getType(), conditionalExpr, "True value isn't the same as the false value");

        //Assign condtionalExpr the type of expr1
        conditionalExpr.setType(expr1.getType());

        return conditionalExpr.getType();
    }

    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCException {
        //Storing the values of the left, right and operator and type checking the left and right
        IToken.Kind op = binaryExpr.getOp();
        Type leftType = (Type)binaryExpr.getLeft().visit(this, arg);
        Type rightType =(Type)binaryExpr.getRight().visit(this, arg);
        Type resultType = null;

        //Checks operator and types accordingly
        switch (op){
            case BITOR, BITAND ->{
                if(leftType == Type.PIXEL && rightType == Type.PIXEL){
                    resultType = Type.PIXEL;
                }
            }

            case AND, OR -> {
                if(leftType == Type.INT && rightType == Type.INT){
                    resultType = Type.INT;
                }
            }

            case LT, GT, LE, GE ->{
                if(leftType == Type.INT && rightType == Type.INT){
                    resultType = Type.INT;
                }
            }

            case EQ ->{
                if(leftType == Type.INT && rightType == Type.INT || leftType == Type.PIXEL && rightType == Type.PIXEL || leftType == Type.IMAGE && rightType == Type.IMAGE || leftType == Type.STRING && rightType == Type.STRING){
                    resultType = Type.INT;
                }

            }

            case EXP -> {
                if(leftType == Type.INT && rightType == Type.INT){
                    resultType = Type.INT;
                }
                else if(leftType == Type.PIXEL && rightType == Type.INT){
                    resultType = Type.PIXEL;
                }
            }

            case PLUS -> {
                if(leftType == Type.INT && rightType == Type.INT){
                    resultType = Type.INT;
                }
                else if(leftType == Type.PIXEL && rightType == Type.PIXEL){
                    resultType = Type.PIXEL;
                }
                else if(leftType == Type.IMAGE && rightType == Type.IMAGE){
                    resultType = Type.IMAGE;
                }
                else if(leftType == Type.STRING && rightType == Type.STRING){
                    resultType = Type.STRING;
                }
            }

            case MINUS -> {
                if(leftType == Type.INT && rightType == Type.INT){
                    resultType = Type.INT;
                }
                else if(leftType == Type.PIXEL && rightType == Type.PIXEL){
                    resultType = Type.PIXEL;
                }
                else if(leftType == Type.IMAGE && rightType == Type.IMAGE){
                    resultType = Type.IMAGE;
                }
            }

            case TIMES, DIV, MOD -> {
                if(leftType == Type.INT && rightType == Type.INT){
                    resultType = Type.INT;
                }
                else if(leftType == Type.PIXEL && rightType == Type.PIXEL){
                    resultType = Type.PIXEL;
                }
                else if(leftType == Type.IMAGE && rightType == Type.IMAGE){
                    resultType = Type.IMAGE;
                }
                else if(leftType == Type.PIXEL && rightType == Type.INT){
                    resultType = Type.PIXEL;
                }
                else if(leftType == Type.IMAGE && rightType == Type.INT){
                    resultType = Type.IMAGE;
                }
            }

            default -> {
                throw new TypeCheckException("compiler error");
            }
        }

        //Sets binary expression with a type
        binaryExpr.setType(resultType);
        return resultType;

    }

    public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws PLCException {
        //Store operator and expression and make sure expression is properly typed
        IToken.Kind op = unaryExpr.getOp();
        Type eType = (Type)unaryExpr.getE().visit(this, arg);

        //Prepare type variables
        Type resultType = null;

        //Checks operator and types accordingly
        switch (op){
            case BANG -> {
                if(eType == Type.INT){
                    resultType = Type.INT;
                }
                else if(eType == Type.PIXEL){
                    resultType = Type.PIXEL;
                }
            }

            case MINUS, RES_cos, RES_sin, RES_atan -> {
                if(eType == Type.INT){
                    resultType = Type.INT;
                }
            }

            default -> {
                throw new TypeCheckException("compiler error");
            }
        }

        //Sets binary expression with a type
        unaryExpr.setType(resultType);
        return resultType;
    }

    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCException{
        //Store string into Type
        stringLitExpr.setType(Type.STRING);

        return Type.STRING;
    }

    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCException{
        //Check if the name has been defined AND is visible in this scope ~~~~~~IFFY~~~~~
        String name = identExpr.getName();
        //Checks if in scope
        check(symbolTable.lookup(name) != null, identExpr, "Ident Expr has not been defined or is not visible in this scope");

        //If check works, store the type of the nameDef into the type of the ident expression
        Type resultType = symbolTable.lookup(name).getType();
        identExpr.setType(resultType);
        identExpr.setDef(symbolTable.lookup(name));
        identExpr.setScope(symbolTable.lookup(name).getScope());
        return resultType;
    }

    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCException{
        //Assign and return INT as the type
        numLitExpr.setType(Type.INT);
        return Type.INT;
    }

    public Object visitZExpr(ZExpr zExpr, Object arg) throws PLCException{
        //Assign and return INT as the type
        zExpr.setType(Type.INT);
        return Type.INT;
    }

    public Object visitRandomExpr(RandomExpr randomExpr, Object arg) throws PLCException{
        //Assign and return INT as the type
        randomExpr.setType(Type.INT);
        return Type.INT;
    }

    public Object visitUnaryExprPostFix(UnaryExprPostfix unaryExprPostfix, Object arg) throws PLCException{
        //If present, check that PixelSelector is properly typed
        if(unaryExprPostfix.getPixel() != null){
            unaryExprPostfix.getPixel().visit(this, arg);
        }

        //Check that Primary Expr is properly typed
        unaryExprPostfix.getPrimary().visit(this, arg);

        Type primaryExprType = unaryExprPostfix.getPrimary().getType();
        PixelSelector pixel = unaryExprPostfix.getPixel();
        ColorChannel color = unaryExprPostfix.getColor();

        Type resultType = null;

        //Check the conditions for Postfixes
        switch (primaryExprType){
            case PIXEL -> {
                if(pixel == null && color != null){
                    resultType = Type.INT;
                }
            }

            case IMAGE -> {
                if(pixel == null && color != null){
                    resultType = Type.IMAGE;
                }
                else if (pixel != null && color == null){
                    resultType = Type.PIXEL;
                }
                else if(pixel != null && color != null){
                    resultType = Type.INT;
                }
            }

            default ->{
                throw new TypeCheckException("compiler error");
            }
        }
        //Sets unary expression post with a type
        unaryExprPostfix.setType(resultType);
        return resultType;
    }

    public Object visitPixelFuncExpr(PixelFuncExpr pixelFuncExpr, Object arg) throws PLCException{
        //Check that pixel selector is properly typed
        pixelFuncExpr.getSelector().visit(this, arg);

        //Assign and return INT as the type
        pixelFuncExpr.setType(Type.INT);
        return Type.INT;
    }

    public Object visitPredeclaredVarExpr(PredeclaredVarExpr predeclaredVarExpr, Object arg) throws PLCException{
        //Assign and return INT as the type
        predeclaredVarExpr.setType(Type.INT);
        return Type.INT;
    }

    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws PLCException{
        //Check that expr0 and expr1 are properly typed
        Type expr0Type = (Type)pixelSelector.getX().visit(this, arg);
        Type expr1Type =(Type)pixelSelector.getY().visit(this, arg);

        check(expr0Type == Type.INT, pixelSelector, "Expr0 is not an INT");
        check(expr1Type == Type.INT, pixelSelector, "Expr1 is not an INT");
        return null;
    }

    public Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCException{
        //Makes sure expr0-2 are all properly typed
        Type expr0Type = (Type)expandedPixelExpr.getRedExpr().visit(this, arg);
        Type expr1Type =(Type)expandedPixelExpr.getGrnExpr().visit(this, arg);
        Type expr2Type = (Type)expandedPixelExpr.getBluExpr().visit(this, arg);

        //Assures that Expr0-2 are all of Type INT
        check(expr0Type == Type.INT, expandedPixelExpr, "Expr0 is not an INT");
        check(expr1Type == Type.INT, expandedPixelExpr, "Expr1 is not an INT");
        check(expr2Type == Type.INT, expandedPixelExpr, "Expr2 is not an INT");

        //Assign ExpandedPixelExpr to have type pixel
        expandedPixelExpr.setType(Type.PIXEL);
        return Type.PIXEL;
    }

    public Object visitDimension(Dimension dimension, Object arg) throws PLCException{
        //Makes sure Expr0-1 are all properly typed
        Type expr0Type = (Type)dimension.getWidth().visit(this, arg);
        Type expr1Type =(Type)dimension.getHeight().visit(this, arg);

        //Assures that Expr0-1 are all of Type INT
        check(expr0Type == Type.INT, dimension, "Expr0 is not an INT");
        check(expr1Type == Type.INT, dimension, "Expr1 is not an INT");

        return null;
    }

    public Object visitLValue(LValue lValue, Object arg) throws PLCException{
        //Check if the name has been defined AND is visible in this scope ~~~~~~IFFY~~~~~
        String name = lValue.getIdent().getName();
        check(symbolTable.lookup(name) != null, lValue, "Ident in LValue has not been defined or is not visible in this scope");

        Type exprType = (Type)lValue.getIdent().visit(this, arg);

        PixelSelector pixel = lValue.getPixelSelector();
        ColorChannel color = lValue.getColor();

        Type resultType = null;

        switch (exprType){
            case IMAGE -> {
                if(pixel == null && color == null){
                    resultType = Type.IMAGE;
                }
                else if(pixel != null && color == null){
                    resultType = Type.PIXEL;
                }
                else if(pixel == null && color != null){
                    resultType = Type.IMAGE;
                }
                else if(pixel != null && color != null){
                    resultType = Type.INT;
                }
            }

            case PIXEL -> {
                if(pixel == null && color == null){
                    resultType = Type.PIXEL;
                }
                else if(pixel == null && color != null){
                    resultType = Type.INT;
                }
            }

            case STRING -> {
                if(pixel == null && color == null)
                {
                    resultType = Type.STRING;
                }
            }

            case INT -> {
                if(pixel == null && color == null)
                {
                    resultType = Type.INT;
                }
            }
            default ->{
                throw new TypeCheckException("compiler error");
            }

        }

        //Return LValue type
        lValue.setScope(symbolTable.lookup(name).getScope());
        lValue.setType(resultType);
        return resultType;
    }

    public Object visitIdent(Ident ident, Object arg) throws PLCException{
        //Check if the Ident is in the symbol table and in scope
        check(symbolTable.lookup(ident.getName()) != null, ident, "Ident not in the symbol table");
        NameDef nameDef = symbolTable.lookup(ident.getName());
        ident.setDef(nameDef);
        return nameDef.getType(); //????

    }

    public Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg) throws PLCException{
        //Check that lvalue and expr are properly typed
        Type lValueType = (Type)statementAssign.getLv().visit(this,arg);
        Type exprType = (Type)statementAssign.getE().visit(this,arg);

        //Check that the two are assignment compatible
        check(assignmentCompatible(lValueType, exprType), statementAssign, "L Value type is not compatible with Expression type");

        return null;
    }

    public Object visitWriteStatement(WriteStatement statementWrite, Object arg) throws PLCException{
        //Checks that the expr is properly typed
        statementWrite.getE().visit(this, arg);
        return null;
    }

    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg)throws PLCException{
        //Checks that the expr is properly typed
        returnStatement.getE().visit(this, arg);

        //Check that expr type is compatible with program type
        check(assignmentCompatible(programType, returnStatement.getE().getType()), returnStatement, "Program type and expression type aren't compatible");
        return null;
    }

    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws PLCException{
        //Checks that the expr is properly typed
        Type exprType = (Type)whileStatement.getGuard().visit(this, arg);

        //Check that the expression type is an int
        check(exprType == Type.INT, whileStatement, "Guard's type in the while statement is not an INT");

        //Enter scope
        symbolTable.enterScope();

        //Checks that the block is properly typed
        whileStatement.getBlock().visit(this, arg);

        symbolTable.closeScope();

        return null;
    }

}
