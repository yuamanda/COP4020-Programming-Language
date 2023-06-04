package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.ast.*;

import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import java.rmi.server.RemoteRef;
import java.util.ArrayList;
import java.util.List;

public class Parser implements IParser{
    //The scanner object we pass into Parser
    Scanner scanner;
    IToken currToken;

    //Constructor
    public Parser(Scanner scanner) throws LexicalException{
        this.scanner = scanner;
        //This will be the first token we observe, will change to be current token
            currToken = scanner.next();
    }

    //Functions to check the "KIND" of the tokens being read
    protected boolean isKind(IToken.Kind kind){
        return currToken.getKind() == kind;
    }

    protected boolean isKind(IToken.Kind...kinds){
        for(IToken.Kind k: kinds){
            if(k==currToken.getKind()){
                return true;
            }
        }
        return false;
    }

    //Consume method
    void consume() throws SyntaxException, LexicalException {
        currToken = scanner.next();
    }

    //Match method
    void match(IToken.Kind c) throws SyntaxException, LexicalException {
        if(currToken.getKind() == c){
            consume();
        }
        else{
            throw new SyntaxException("Error in the matching method with current token: " + currToken + "and c: " + c);
        }
    }

    //Check to see if at end
    private boolean isAtEnd(){
        if(currToken.getKind() == IToken.Kind.EOF){
            return true;
        }

        return false;
    }

    //Handles Program
    Program program() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;
        Type typeExpr = type();
        Ident identifier = new Ident(currToken);
        match(IToken.Kind.IDENT);
        match(IToken.Kind.LPAREN);
        List<NameDef> parameterList = paramlist(); //Handle soon
        match(IToken.Kind.RPAREN);
        Block blk = block();
        if(currToken.getKind() != IToken.Kind.EOF){
            throw new SyntaxException("Error: there is something after the block has ended");
        }
        return new Program(firstToken, typeExpr, identifier, parameterList, blk);
    }

    //Handles Block
    Block block() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;
        match(IToken.Kind.LCURLY);
        List<Declaration> decklist = deckList();
        List<Statement> statementList = statementList();
        match(IToken.Kind.RCURLY);
        return new Block(firstToken, decklist, statementList);
    }

    //Handles DeckList
    List<Declaration> deckList() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;
        List<Declaration> deckList = new ArrayList<>();

        while (isKind(IToken.Kind.RES_image) || isKind(IToken.Kind.RES_pixel) || isKind(IToken.Kind.RES_int) || isKind(IToken.Kind.RES_string) || isKind(IToken.Kind.RES_void)) {
            Declaration dec = declaration();
            match(IToken.Kind.DOT);
            deckList.add(dec);
        }

        return deckList;
    }

    //Handles StatementList
    List<Statement> statementList() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;
        List<Statement> statList = new ArrayList<>();

        while(isKind(IToken.Kind.RES_write) || isKind(IToken.Kind.RES_while) || isKind(IToken.Kind.IDENT) || isKind(IToken.Kind.COLON)){
            Statement stat = statement();
            match(IToken.Kind.DOT);
            statList.add(stat);
        }

        return statList;
    }

    //Handles ParamList
    List<NameDef> paramlist() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;
        List<NameDef> paramsList = new ArrayList<>();

        if(isKind(IToken.Kind.RES_image) || isKind(IToken.Kind.RES_pixel) || isKind(IToken.Kind.RES_int) || isKind(IToken.Kind.RES_string) || isKind(IToken.Kind.RES_void)){
            NameDef def = namedef();
            paramsList.add(def);

            while(isKind(IToken.Kind.COMMA)){
                match(IToken.Kind.COMMA);
                NameDef nextDef = namedef();
                paramsList.add(nextDef);
            }
        }

        return paramsList;
    }

    //Handles NameDef
    NameDef namedef() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;
        Type theType = type();
        Dimension dim;

        //Check if it has a dimension
        if(isKind(IToken.Kind.LSQUARE)){
            dim = dimension();
        }
        else{
            dim = null;
        }

        Ident identifier = new Ident(currToken);
        match(IToken.Kind.IDENT);

        return new NameDef(firstToken, theType, dim, identifier);
    }

    //Handles types
    Type type() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;
        Expr e = null;

        if(isKind(IToken.Kind.RES_image)){
            match(IToken.Kind.RES_image);
            return Type.IMAGE;
        }

        else if(isKind(IToken.Kind.RES_pixel)) {
            match(IToken.Kind.RES_pixel);
            return Type.PIXEL;
        }

        else if(isKind(IToken.Kind.RES_int)) {
            match(IToken.Kind.RES_int);
            return Type.INT;
        }

        else if(isKind(IToken.Kind.RES_string)) {
            match(IToken.Kind.RES_string);
            return Type.STRING;
        }

        else if(isKind(IToken.Kind.RES_void)) {
            match(IToken.Kind.RES_void);
            return Type.VOID;
        }

        else {
            throw new SyntaxException("Unable to parse the current token");
        }
    }

    //Handles declaration
    Declaration declaration() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;
        Expr e;
        NameDef def = namedef();

        if(isKind(IToken.Kind.ASSIGN)){
            match(IToken.Kind.ASSIGN);
            e = expr();
        }
        else{
            e = null;
        }

        return new Declaration(firstToken, def, e);

    }

    //Expr function: expr ::= <conditional_expr> | <or_expr>
    Expr expr() throws SyntaxException, LexicalException {
        IToken firstToken = currToken;
        Expr e = null;

        //Handles if conditional expressions
        if(isKind(IToken.Kind.RES_if)){
            e = conditional_expr();
        }

        //else if(isKind(IToken.Kind.BANG) || isKind(IToken.Kind.MINUS) || isKind(IToken.Kind.RES_sin) || isKind(IToken.Kind.RES_cos) || isKind(IToken.Kind.RES_atan)){}

        //Is it an or_expression
        else{
            e = or_expr();
        }

        return e;
    }

    //Handles conditional  expressions
    Expr conditional_expr() throws SyntaxException, LexicalException {
        IToken firstToken = currToken;
        Expr e = null;
        match(IToken.Kind.RES_if);
        Expr guard = expr();
        match(IToken.Kind.QUESTION);
        Expr trueStatement = expr();
        match(IToken.Kind.QUESTION);
        Expr falseStatement = expr();
        e = new ConditionalExpr(firstToken, guard, trueStatement, falseStatement);
        return e;
    }

    //Handles or expressions
    Expr or_expr() throws SyntaxException, LexicalException {
        IToken firstToken = currToken;
        Expr left = null;
        Expr right = null;
        left = and_expr();

        while(isKind(IToken.Kind.BITOR) || isKind(IToken.Kind.OR)){
            IToken op = currToken;
            consume();
            right = and_expr();
            left = new BinaryExpr(firstToken, left, op.getKind(), right);
        }

        return left;
    }

    //Handles and expressions
    Expr and_expr() throws SyntaxException, LexicalException {
        IToken firstToken = currToken;
        Expr left = null;
        Expr right = null;
        left = comparison_expr();

        while(isKind(IToken.Kind.BITAND) || isKind(IToken.Kind.AND)){
            IToken op = currToken;
            consume();
            right = comparison_expr();
            left = new BinaryExpr(firstToken, left, op.getKind(), right);
        }

        return left;
    }

    //Handles comparison expressions
    Expr comparison_expr() throws SyntaxException, LexicalException {
        IToken firstToken = currToken;
        Expr left = null;
        Expr right = null;
        left = power_expr();

        while(isKind(IToken.Kind.LT) || isKind(IToken.Kind.GT) || isKind(IToken.Kind.EQ) || isKind(IToken.Kind.LE) || isKind(IToken.Kind.GE)){
            IToken op = currToken;
            consume();
            right = power_expr();
            left = new BinaryExpr(firstToken, left, op.getKind(), right);
        }

        return left;
    }

    //Handles power expressions
    Expr power_expr() throws SyntaxException, LexicalException {
        IToken firstToken = currToken;
        Expr left = null;
        Expr right = null;
        left = additive_expr();

        if(isKind(IToken.Kind.EXP)){
            IToken op = currToken;
            consume();
            right = power_expr();
            right = new BinaryExpr(firstToken, left, op.getKind(), right);
            return right;
        }

        return left;
    }

    //Handles additive expressions
    Expr additive_expr() throws SyntaxException, LexicalException {
        IToken firstToken = currToken;
        Expr left = null;
        Expr right = null;
        left = multiplicative_expr();

        while(isKind(IToken.Kind.PLUS) || isKind(IToken.Kind.MINUS)){
            IToken op = currToken;
            consume();
            right = multiplicative_expr();
            left = new BinaryExpr(firstToken, left, op.getKind(), right);
        }

        return left;
    }

    //Handles multiplicative expressions
    Expr multiplicative_expr() throws SyntaxException, LexicalException {
        IToken firstToken = currToken;
        Expr left = null;
        Expr right = null;
        left = unary_expr();

        while(isKind(IToken.Kind.TIMES) || isKind(IToken.Kind.DIV) || isKind(IToken.Kind.MOD)){
            IToken op = currToken;
            consume();
            right = unary_expr();
            left = new BinaryExpr(firstToken, left, op.getKind(), right);
        }

        return left;
    }

    Expr unary_expr() throws SyntaxException, LexicalException {
        IToken firstToken = currToken;
        Expr right = null;

        if(isKind(IToken.Kind.BANG) || isKind(IToken.Kind.MINUS) || isKind(IToken.Kind.RES_sin) || isKind(IToken.Kind.RES_cos) || isKind(IToken.Kind.RES_atan)){
            IToken op = currToken;
            consume();
            right = unary_expr();
            return new UnaryExpr(firstToken, op.getKind(), right);
        }

        //Case that it is unary expression postfix
        else {
            right = unary_expr_postfix();
        }

        return right;
    }

    Expr unary_expr_postfix() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;
        //First match will be a primary expression
        Expr primary = primary_expr();
        PixelSelector param1 = null;
        ColorChannel param2 = null;

        //First parameter case matches left square bracket:
        if(isKind(IToken.Kind.LSQUARE)){
            param1 = pixel_selector();
        }
        else{
            param1 = null;
        }

        //Check param2
        if(isKind(IToken.Kind.COLON)){
            param2 = channel_selector();
        }
        else{
            param2 = null;
        }

        if(param1 == null && param2 == null){
            return primary;
        }

        else {
            return new UnaryExprPostfix(firstToken, primary, param1, param2);
        }
    }

    Expr primary_expr() throws SyntaxException, LexicalException {
        IToken firstToken = currToken;
        Expr e = null;

        //String Lit
        if(isKind(IToken.Kind.STRING_LIT)){
            match(IToken.Kind.STRING_LIT);
            return new StringLitExpr(firstToken);
        }

        //Num Lit
        else if(isKind(IToken.Kind.NUM_LIT)){
            match(IToken.Kind.NUM_LIT);
            return new NumLitExpr(firstToken);
        }

        //Ident
        else if(isKind(IToken.Kind.IDENT)){
            match(IToken.Kind.IDENT);
            return new IdentExpr(firstToken);
        }

        // ( expr )
        else if(isKind(IToken.Kind.LPAREN)){
            match(IToken.Kind.LPAREN);
            Expr expr = expr();
            match(IToken.Kind.RPAREN);
            return expr;
        }

        //Z
        else if(isKind(IToken.Kind.RES_Z)){
            match(IToken.Kind.RES_Z);
            return new ZExpr(firstToken);
        }

        //rand
        else if(isKind(IToken.Kind.RES_rand)) {
            match(IToken.Kind.RES_rand);
            return new RandomExpr(firstToken);
        }

        //x
        else if(isKind(IToken.Kind.RES_x)) {
            match(IToken.Kind.RES_x);
            return new PredeclaredVarExpr(firstToken);
        }

        //y
        else if(isKind(IToken.Kind.RES_y)) {
            match(IToken.Kind.RES_y);
            return new PredeclaredVarExpr(firstToken);
        }

        //a
        else if(isKind(IToken.Kind.RES_a)) {
            match(IToken.Kind.RES_a);
            return new PredeclaredVarExpr(firstToken);
        }

        //r
        else if(isKind(IToken.Kind.RES_r)) {
            match(IToken.Kind.RES_r);
            return new PredeclaredVarExpr(firstToken);
        }

        //Expanded Pixel
        else if(isKind(IToken.Kind.LSQUARE)) {
            ExpandedPixelExpr pix = expandedpixel();
            return pix;
        }

        //PixelFunctionExpr
        else if(isKind(IToken.Kind.RES_x_cart) || isKind(IToken.Kind.RES_y_cart) || isKind(IToken.Kind.RES_a_polar) || isKind(IToken.Kind.RES_r_polar)) {
            PixelFuncExpr pix = pixelfunction_expr();
            return pix;
        }

        else {
            throw new SyntaxException("Unable to parse the current token");
        }

    }

    ColorChannel channel_selector() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;
        ColorChannel color;

        match(IToken.Kind.COLON);

        if(isKind(IToken.Kind.RES_red)){
            color = ColorChannel.red;
            match(IToken.Kind.RES_red);
            return color;
        }
        else if (isKind(IToken.Kind.RES_grn)){
            color = ColorChannel.grn;
            match(IToken.Kind.RES_grn);
            return color;
        }
        else if (isKind(IToken.Kind.RES_blu)){
            color = ColorChannel.blu;
            match(IToken.Kind.RES_blu);
            return color;
        }

        else{
            throw new SyntaxException("Unable to parse the current token in color");
        }

    }

    PixelSelector pixel_selector() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;
        match(IToken.Kind.LSQUARE);
        Expr expr1 = expr();
        match(IToken.Kind.COMMA);
        Expr expr2 = expr();
        match(IToken.Kind.RSQUARE);

        return new PixelSelector(firstToken, expr1, expr2);

    }

    ExpandedPixelExpr expandedpixel() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;
        match(IToken.Kind.LSQUARE);
        Expr expr1 = expr();
        match(IToken.Kind.COMMA);
        Expr expr2 = expr();
        match(IToken.Kind.COMMA);
        Expr expr3 = expr();
        match(IToken.Kind.RSQUARE);

        return new ExpandedPixelExpr(firstToken, expr1, expr2, expr3);
    }

    PixelFuncExpr pixelfunction_expr() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;
        IToken op = null;

        //X_CART
        if(isKind(IToken.Kind.RES_x_cart)){
            op = currToken;
            match(IToken.Kind.RES_x_cart);
        }

        //Y_CART
        else if(isKind(IToken.Kind.RES_y_cart)){
            op = currToken;
            match(IToken.Kind.RES_y_cart);
        }

        //A_POLAR
        else if(isKind(IToken.Kind.RES_a_polar)){
            op = currToken;
            match(IToken.Kind.RES_a_polar);
        }

        //R_POLAR
        else if(isKind(IToken.Kind.RES_r_polar)){
            op = currToken;
            match(IToken.Kind.RES_r_polar);
        }

        PixelSelector pix = pixel_selector();

        return new PixelFuncExpr(firstToken, op.getKind(), pix);
    }

    Dimension dimension() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;
        match(IToken.Kind.LSQUARE);
        Expr expr1 = expr();
        match(IToken.Kind.COMMA);
        Expr expr2 = expr();
        match(IToken.Kind.RSQUARE);

        return new Dimension(firstToken, expr1, expr2);
    }

    LValue l_value() throws LexicalException, SyntaxException{
        IToken firstToken = currToken;
        Ident ident = new Ident(currToken);
        match(IToken.Kind.IDENT);
        PixelSelector pix;
        ColorChannel color;

        //First parameter case matches left square bracket:
        if(isKind(IToken.Kind.LSQUARE)){
            pix = pixel_selector();
        }
        else{
            pix = null;
        }

        //Check param2
        if(isKind(IToken.Kind.COLON)){
            color = channel_selector();
        }
        else{
            color = null;
        }

        return new LValue(firstToken, ident, pix, color);
    }

    Statement statement() throws SyntaxException, LexicalException{
        IToken firstToken = currToken;

        if(isKind(IToken.Kind.IDENT)){
            LValue value = l_value();
            IToken op = currToken;
            match(IToken.Kind.ASSIGN);
            Expr expr = expr();
            return new AssignmentStatement(firstToken, value, expr);
        }
        else if(isKind(IToken.Kind.RES_write)){
            match(IToken.Kind.RES_write);
            Expr expr = expr();
            return new WriteStatement(firstToken, expr);
        }

        else if(isKind(IToken.Kind.RES_while)){
            match(IToken.Kind.RES_while);
            Expr expr = expr();
            Block blk = block();
            return new WhileStatement(firstToken, expr, blk);
        }

        else if(isKind(IToken.Kind.COLON)){
            match(IToken.Kind.COLON);
            Expr expr = expr();
            return new ReturnStatement(firstToken, expr);
        }

        else{
            throw new SyntaxException("Unable to parse the current token, cannot identify statement");
        }
    }


    @Override
    public AST parse() throws PLCException {
        //For Assignment 2: start at expression
        //Expr e = expr();
        Program e = program();
        return e;
    }
}
