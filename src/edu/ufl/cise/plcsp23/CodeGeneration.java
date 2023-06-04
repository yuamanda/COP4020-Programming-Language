package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.ast.*;
import edu.ufl.cise.plcsp23.runtime.ConsoleIO;
import org.hamcrest.core.IsEqual;

import javax.naming.Name;
import java.lang.annotation.Target;
import java.security.PublicKey;
import java.sql.SQLWarning;
import java.util.*;

public class CodeGeneration implements ASTVisitor {

    //Stores package name
    String packageName;

    CodeGeneration(String param){
        this.packageName = param;
    }

    //Imported
    boolean importedIO = false;
    boolean isImportedMath = false;
    boolean importedBufferedImage = false;
    boolean importedFileURLIO = false;
    boolean importedImageOps = false;
    boolean importedPixelOps = false;
    Type programType;

    //String for the top information like imports and packages
    String topPortion = "";

    //String to store entire program
    String programString = "";

    Type programTypeUniversal;
    public Object visitProgram(Program program, Object arg) throws PLCException {
        if(packageName != "") {
            topPortion += "package " + packageName + ";\n\n";
        }
        programString += "public class " + program.getIdent().getName() + " {\n \t public static ";
        programType = program.getType();
        programTypeUniversal = programType;

        //Determine the type
        switch (program.getType()){
            case INT -> {
                programString += "int";
            }
            case PIXEL -> {
                programString += "int";
            }
            case STRING -> {
                programString += "String";
            }
            case IMAGE -> {
                programString += "BufferedImage";
            }
            case VOID -> {
                programString += "void";
            }

            default -> {
                throw new PLCException("NOT A RECOGNIZED TYPE");
            }
        }

        programString += " apply(";

        int count = 0;
        List<NameDef> list = program.getParamList();
        for(AST node: list){
            if(count <  program.getParamList().size() - 1){
                node.visit(this, arg);
                programString += ", ";
            }
            else{
                node.visit(this, arg);
            }
            count++;
        }

        programString += ") {\n\t";


        program.getBlock().visit(this, arg);

        programString +=  "\n}";

        programString +=  "\n}";

        programString = topPortion + programString;

        return programString;
    }

    public Object visitBlock(Block block, Object arg) throws PLCException {
        //Makes lists of declarations and statements
        List<Declaration> listOfDecks = block.getDecList();
        List<Statement> statementList = block.getStatementList();

        //Traverse and visit all declarations and statements
        int count1 = 0;
        for(AST node: listOfDecks){
            node.visit(this, arg);
            programString += ";\n\t";
        }

        int count2 = 0;
        for(AST node: statementList){
            node.visit(this, arg);
            try {
                WhileStatement e = (WhileStatement)node;
                programString += "\n\t";
            }
            catch (Exception e){
                programString += ";\n\t";
            }
        }

        return null;
    }

    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCException {
        declaration.getNameDef().visit(this, arg);
        Type declaredType = declaration.getNameDef().getType();

        //If namedef is an image
        if(declaredType == Type.IMAGE){
            //Case where dimension is null
            if(declaration.getNameDef().getDimension() == null){
                //Case initializer is a string
                if(declaration.getInitializer().getType() == Type.STRING){
                    programString += " = (FileURLIO.readImage(";
                    declaration.getInitializer().visit(this, arg);
                    programString += "))";

                    if(importedFileURLIO == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.FileURLIO;\n";
                        topPortion += importStatement;
                        importedFileURLIO = true;
                    }
                }

                //Case initializer is an image
                else if(declaration.getInitializer().getType() == Type.IMAGE){
                    programString += " = ImageOps.cloneImage(";
                    declaration.getInitializer().visit(this, arg);
                    programString += ")";

                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }
                }

                else{
                    throw new PLCException("COULDN'T FIND AN INTIALIZER WHEN DECLARED TYPE IS AN IMAGE INSIDE OF DECLARATION");
                }
            }

            //Case where dimension is not null
            else if (declaration.getNameDef().getDimension() != null){
                //Case where no initializer
                if(declaration.getInitializer() == null){
                    programString += " = ImageOps.makeImage(";
                    declaration.getNameDef().getDimension().visit(this,arg);
                    programString += ")";

                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }
                }

                //Case where initializer is real
                else if(declaration.getInitializer() != null){
                    //Case that initializer is a string
                    if(declaration.getInitializer().getType() == Type.STRING){
                        programString += " = FileURLIO.readImage(";
                        declaration.getInitializer().visit(this, arg);
                        programString += ", ";
                        declaration.getNameDef().getDimension().visit(this,arg);
                        programString += ")";

                        if(importedFileURLIO == false){
                            String importStatement = "import edu.ufl.cise.plcsp23.runtime.FileURLIO;\n";
                            topPortion += importStatement;
                            importedFileURLIO = true;
                        }
                    }

                    //Case if initializer is an image
                    else if(declaration.getInitializer().getType() == Type.IMAGE){
                        programString += " = ImageOps.copyAndResize(";
                        declaration.getInitializer().visit(this, arg);
                        programString += ", ";
                        declaration.getNameDef().getDimension().visit(this,arg);
                        programString += ")";

                        if(importedImageOps == false){
                            String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                            topPortion += importStatement;
                            importedImageOps = true;
                        }
                    }

                    //Case if initializer is a pixel
                    else if(declaration.getInitializer().getType() == Type.PIXEL){
                        programString += " = ImageOps.makeImage(";
                        declaration.getNameDef().getDimension().visit(this, arg);
                        programString += ");\n";
                        programString += declaration.getNameDef().getIdent().getName() + "_" + declaration.getNameDef().getScope();
                        programString += " = ImageOps.setAllPixels(";
                        programString += declaration.getNameDef().getIdent().getName() + "_" + declaration.getNameDef().getScope();
                        programString += ", ";
                        declaration.getInitializer().visit(this, arg);
                        programString += ")";

                        //Import ImageOp
                        if(importedImageOps == false){
                            String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                            topPortion += importStatement;
                            importedImageOps = true;
                        }
                    }
                }


                else{
                    throw new PLCException("COULDN'T FIND AN INTIALIZER WHEN DECLARED TYPE IS AN IMAGE INSIDE OF DECLARATION");
                }
            }

        }

        else {
            //Check that Expr is defined
            Expr initializer = declaration.getInitializer();
            if (initializer != null) {
                programString += " = ";
                if(declaredType == Type.PIXEL){
                    initializer.visit(this, arg);

                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }
                else {
                    try {
                        NumLitExpr e = (NumLitExpr) initializer;
                        if (declaredType == Type.STRING) {
                            programString += "\"";
                            initializer.visit(this, arg);
                            programString += "\"";
                        } else {
                            initializer.visit(this, arg);
                        }
                    } catch (Exception e5) {
                        try {
                            IdentExpr e1 = (IdentExpr) initializer;
                            if (declaredType == Type.STRING) {
                                programString += "String.valueOf(";
                                initializer.visit(this, arg);
                                programString += ")";
                            } else {

                                initializer.visit(this, arg);
                            }
                        } catch (Exception e6) {
                            try {
                                UnaryExpr e3 = (UnaryExpr) initializer;
                                if(declaredType == Type.STRING){
                                    programString += "String.valueOf(";
                                    initializer.visit(this, arg);
                                    programString += ")";
                                }
                                else {
                                    initializer.visit(this, arg);
                                }
                            }
                            catch (Exception e7){
                                try {
                                    BinaryExpr e4 = (BinaryExpr) initializer;
                                    if(declaredType == Type.STRING){
                                        programString += "String.valueOf(";
                                        initializer.visit(this, arg);
                                        programString += ")";
                                    }
                                    else {
                                        initializer.visit(this, arg);
                                    }
                                }
                                catch (Exception e8){
                                    initializer.visit(this, arg);
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCException {

        //Determine the type
        switch (nameDef.getType()) {
            case INT -> {
                programString += "int";
            }
            //IF it's an image type, the Java equivalent is int
            case PIXEL -> {
                programString += "int";
            }
            case STRING -> {
                programString += "String";
            }
            //If the type of NameDef is an image, it has java type Buffered Image
            case IMAGE -> {
                programString += "BufferedImage";
                //Import statement if needed
                if(importedBufferedImage == false){
                    String importStatement = "import java.awt.image.BufferedImage;\n";
                    topPortion += importStatement;
                    importedBufferedImage = true;
                }
            }
            case VOID -> {
                programString += "void";
            }

            default -> {
                throw new PLCException("NOT A RECOGNIZED TYPE");
            }
        }

        programString += " " + nameDef.getIdent().getName() + "_" +nameDef.getScope();

        //DO NOT IMPLEMENT DIMENSION
        //if(nameDef.getDimension() != null){
        //    throw new PLCException("Not Implemented");
        //}
        return null;
    }

    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCException {
        Expr expr0 = conditionalExpr.getGuard();
        Expr expr1 = conditionalExpr.getTrueCase();
        Expr expr2 = conditionalExpr.getFalseCase();

        programString += "(";
        expr0.visit(this, arg);

        //Since conditional values are now all ints, we make the int a boolean
        programString += " != 0";
        programString += " ? ";
        expr1.visit(this, arg);
        programString += " : ";
        expr2.visit(this, arg);
        programString += ")";

        return null;

    }

    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCException {
        Expr left = binaryExpr.getLeft();
        Expr right = binaryExpr.getRight();
        IToken.Kind op = binaryExpr.getOp();

        Type leftType = binaryExpr.getLeft().getType();
        Type rightType = binaryExpr.getRight().getType();

        //Don't Implement the == nor != operations for pixels nor images

        switch (op){
            case PLUS -> {
                if(leftType == Type.IMAGE && rightType ==Type.IMAGE){
                    programString += "ImageOps.binaryImageImageOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }
                }
                else if(leftType == Type.IMAGE && rightType ==Type.INT){
                    programString += "ImageOps.binaryImageScalarOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }
                }
                else if(leftType == Type.PIXEL && rightType ==Type.PIXEL){
                    programString += "ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }
                else if(leftType == Type.PIXEL && rightType ==Type.INT){
                    programString += "ImageOps.binaryPackedPixelIntOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }
                else if(leftType == Type.IMAGE && rightType ==Type.PIXEL){
                    programString += "ImageOps.binaryImagePixelOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }



                else {
                    programString += "(";
                    left.visit(this, arg);
                    programString += " + ";
                    right.visit(this, arg);
                    programString += ")";
                }
            }
            case MINUS -> {
                if(leftType == Type.IMAGE && rightType ==Type.IMAGE){
                    programString += "ImageOps.binaryImageImageOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }
                }
                else if(leftType == Type.IMAGE && rightType ==Type.INT){
                    programString += "ImageOps.binaryImageScalarOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }
                }
                else if(leftType == Type.PIXEL && rightType ==Type.PIXEL){
                    programString += "ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }
                else if(leftType == Type.PIXEL && rightType ==Type.INT){
                    programString += "ImageOps.binaryPackedPixelIntOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }
                else if(leftType == Type.IMAGE && rightType ==Type.PIXEL){
                    programString += "ImageOps.binaryImagePixelOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }

                else {
                    programString += "(";
                    left.visit(this, arg);
                    programString += " - ";
                    right.visit(this, arg);
                    programString += ")";
                }
            }
            case TIMES -> {
                if(leftType == Type.IMAGE && rightType ==Type.IMAGE){
                    programString += "ImageOps.binaryImageImageOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }
                }
                else if(leftType == Type.IMAGE && rightType ==Type.INT){
                    programString += "ImageOps.binaryImageScalarOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }
                }
                else if(leftType == Type.PIXEL && rightType ==Type.PIXEL){
                    programString += "ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }
                else if(leftType == Type.PIXEL && rightType ==Type.INT){
                    programString += "ImageOps.binaryPackedPixelIntOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }
                else if(leftType == Type.IMAGE && rightType ==Type.PIXEL){
                    programString += "ImageOps.binaryImagePixelOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }

                else {
                    programString += "(";
                    left.visit(this, arg);
                    programString += " * ";
                    right.visit(this, arg);
                    programString += ")";
                }
            }
            case DIV -> {
                if(leftType == Type.IMAGE && rightType ==Type.IMAGE){
                    programString += "ImageOps.binaryImageImageOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }
                }
                else if(leftType == Type.IMAGE && rightType ==Type.INT){
                    programString += "ImageOps.binaryImageScalarOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }
                }
                else if(leftType == Type.PIXEL && rightType ==Type.PIXEL){
                    programString += "ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }
                else if(leftType == Type.PIXEL && rightType ==Type.INT){
                    programString += "ImageOps.binaryPackedPixelIntOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }
                else if(leftType == Type.IMAGE && rightType ==Type.PIXEL){
                    programString += "ImageOps.binaryImagePixelOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }

                else {
                    programString += "(";
                    left.visit(this, arg);
                    programString += " / ";
                    right.visit(this, arg);
                    programString += ")";
                }
            }
            case MOD -> {
                if(leftType == Type.IMAGE && rightType ==Type.IMAGE){
                    programString += "ImageOps.binaryImageImageOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }
                }
                else if(leftType == Type.IMAGE && rightType ==Type.INT){
                    programString += "ImageOps.binaryImageScalarOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }
                }
                else if(leftType == Type.PIXEL && rightType ==Type.PIXEL){
                    programString += "ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }
                else if(leftType == Type.PIXEL && rightType ==Type.INT){
                    programString += "ImageOps.binaryPackedPixelIntOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }
                else if(leftType == Type.IMAGE && rightType ==Type.PIXEL){
                    programString += "ImageOps.binaryImagePixelOp(ImageOps.OP.";
                    programString += op;
                    programString += ", ";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }

                    //Import Pixel OP
                    if(importedPixelOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                        topPortion += importStatement;
                        importedPixelOps = true;
                    }
                }

                else {
                    programString += "(";
                    left.visit(this, arg);
                    programString += " % ";
                    right.visit(this, arg);
                    programString += ")";
                }
            }
            case LT -> {
                programString += "((";
                left.visit(this, arg);
                programString += " < ";
                right.visit(this, arg);
                programString += ") ? 1 : 0)";
            }
            case GT -> {
                programString += "((";
                left.visit(this, arg);
                programString += " > ";
                right.visit(this, arg);
                programString += ") ? 1 : 0)";
            }
            case LE -> {
                programString += "((";
                left.visit(this, arg);
                programString += " <= ";
                right.visit(this, arg);
                programString += ") ? 1 : 0)";
            }
            case GE -> {
                programString += "((";
                left.visit(this, arg);
                programString += " >= ";
                right.visit(this, arg);
                programString += ") ? 1 : 0)";
            }
            case EQ -> {
                if(leftType == Type.IMAGE && rightType == Type.IMAGE){
                    programString += "ImageOps.equalsForCodeGen(";
                    left.visit(this, arg);
                    programString += ", ";
                    right.visit(this, arg);
                    programString += ")";

                    //Import ImageOp
                    if(importedImageOps == false){
                        String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                        topPortion += importStatement;
                        importedImageOps = true;
                    }
                }
                else {
                    programString += "((";
                    left.visit(this, arg);
                    programString += " == ";
                    right.visit(this, arg);
                    programString += ") ? 1 : 0)";
                }
            }
            case BITOR -> {
                programString += "(";
                left.visit(this, arg);
                programString += " | ";
                right.visit(this, arg);
                programString += ")";
            }
            case OR -> {
                programString += "((";
                left.visit(this, arg);
                programString += " !=0 ";
                programString += ") || (";
                right.visit(this, arg);
                programString += " !=0) ";
                programString += "? 1 : 0)";
            }
            case BITAND -> {
                programString += "(";
                left.visit(this, arg);
                programString += " & ";
                right.visit(this, arg);
                programString += ")";
            }
            case AND -> {
                programString += "((";
                left.visit(this, arg);
                programString += " !=0 ";
                programString += ") && (";
                right.visit(this, arg);
                programString += " !=0) ";
                programString += "? 1 : 0)";

            }
            case EXP -> {
                programString += "((int)java.lang.Math.pow(";
                left.visit(this, arg);
                programString +=  ", ";
                right.visit(this, arg);
                programString += "))";
            }

            default -> {
                throw new PLCException("NOT A RECOGNIZED OPERATOR");
            }
        }

        return null;


    }

    // do not implement in assignment 5
    public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws PLCException {
        IToken.Kind op = unaryExpr.getOp();

        switch (op){
            case BANG -> {
                programString += "(";
                unaryExpr.getE().visit(this, arg);
                programString += " ==0 ? 1 : 0)";
            }
            case MINUS -> {
                programString += "((-1)*";
                unaryExpr.getE().visit(this, arg);
                programString += ")";
            }
            default -> {
                throw new PLCException("Unary Expression that was either not implemented or some logical flaw");
            }
        }

        return null;
    }

    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCException {
        programString += "\"" + stringLitExpr.getValue() + "\"";
        return null;
    }

    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCException {
        programString += identExpr.getName() + "_" + identExpr.getScope();
        return null;
    }

    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCException {
        programString += numLitExpr.getValue();
        return null;
    }

    public Object visitZExpr(ZExpr zExpr, Object arg) throws PLCException {
        programString += "255";
        return null;
    }

    public Object visitRandomExpr(RandomExpr randomExpr, Object arg) throws PLCException {
        int randomNumber = (int) Math.floor(Math.random() * 256);
        programString += randomNumber;

        if(isImportedMath == false){
            String importStatement = "import java.lang.Math;\n";
            topPortion += importStatement;
            isImportedMath = true;
        }
        return null;
    }

    // Implemented in 6
    public Object visitUnaryExprPostFix(UnaryExprPostfix unaryExprPostfix, Object arg) throws PLCException {
        //If PrimaryExpr has type image
        if(unaryExprPostfix.getPrimary().getType() == Type.IMAGE){
            //Pixel Selector and NO Channel
            if(unaryExprPostfix.getPixel() != null && unaryExprPostfix.getColor() == null){
                programString += "ImageOps.getRGB(";
                unaryExprPostfix.getPrimary().visit(this, arg);
                programString += ", ";
                unaryExprPostfix.getPixel().visit(this, arg);
                programString += ")";

                if(importedImageOps == false){
                    String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                    topPortion += importStatement;
                    importedImageOps = true;
                }
            }

            //When both are present
            else if(unaryExprPostfix.getPixel() != null && unaryExprPostfix.getColor() != null) {
                programString += "PixelOps.";
                programString += unaryExprPostfix.getColor();
                programString += "(ImageOps.getRGB(";
                unaryExprPostfix.getPrimary().visit(this, arg);
                programString += ", ";
                unaryExprPostfix.getPixel().visit(this, arg);
                programString += "))";


                if(importedImageOps == false){
                    String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                    topPortion += importStatement;
                    importedImageOps = true;
                }
                if(importedPixelOps == false){
                    String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                    topPortion += importStatement;
                    importedPixelOps = true;
                }

            }

            //When only channel selector exists
            else if(unaryExprPostfix.getPixel() == null && unaryExprPostfix.getColor() != null) {
                switch (unaryExprPostfix.getColor()){
                    case red -> {
                        programString += "ImageOps.extractRed(";
                        unaryExprPostfix.getPrimary().visit(this, arg);
                        programString += ")";
                    }
                    case blu -> {
                        programString += "ImageOps.extractBlu(";
                        unaryExprPostfix.getPrimary().visit(this, arg);
                        programString += ")";
                    }
                    case grn -> {
                        programString += "ImageOps.extractGrn(";
                        unaryExprPostfix.getPrimary().visit(this, arg);
                        programString += ")";
                    }

                }

                if(importedImageOps == false){
                    String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                    topPortion += importStatement;
                    importedImageOps = true;
                }
            }
        }

        else if(unaryExprPostfix.getPrimary().getType() == Type.PIXEL){
            //Will only be case of PrimaryExpr and ChannelSelector
            programString += "(PixelOps.";
            programString += unaryExprPostfix.getColor();
            programString += "(";
            unaryExprPostfix.getPrimary().visit(this, arg);
            programString += "))";

            if(importedPixelOps == false){
                String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
                topPortion += importStatement;
                importedPixelOps = true;
            }
        }
        else{
            unaryExprPostfix.getPrimary().visit(this, arg);
        }

        return null;
    }

    // do not implement in assignment 5
    public Object visitPixelFuncExpr(PixelFuncExpr pixelFuncExpr, Object arg) throws PLCException {
        //NOT IN AS5
        throw new PLCException("Will Not Implemented");
    }

    // Implemented in 6
    public Object visitPredeclaredVarExpr(PredeclaredVarExpr predeclaredVarExpr, Object arg) throws PLCException {
        IToken.Kind variable = predeclaredVarExpr.getKind();

        switch (variable){
            case RES_x -> {
                programString += "x";
            }
            case RES_y -> {
                programString += "y";
            }
            default -> {
                throw new PLCException("Something is wrong in predeclared variable structure");
            }
        }
        return null;
    }

    // Implemented in 6
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws PLCException {
        //Visit children, with commas separating them
        pixelSelector.getX().visit(this, arg);
        programString += ", ";
        pixelSelector.getY().visit(this, arg);
        return null;
    }

    // Implemented in 6
    public Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCException {
        //Invoke PixelOps.pack
        programString += "PixelOps.pack(";
        expandedPixelExpr.getRedExpr().visit(this, arg);
        programString += ", ";
        expandedPixelExpr.getGrnExpr().visit(this, arg);
        programString += ", ";
        expandedPixelExpr.getBluExpr().visit(this, arg);
        programString += ")";

        if(importedPixelOps == false){
            String importStatement = "import edu.ufl.cise.plcsp23.runtime.PixelOps;\n";
            topPortion += importStatement;
            importedPixelOps = true;
        }

        return null;
    }

    // Implemented in 6
    public Object visitDimension(Dimension dimension, Object arg) throws PLCException {
        dimension.getWidth().visit(this, arg);
        programString += ", ";
        dimension.getHeight().visit(this, arg);
        return null;
    }

    public Object visitLValue(LValue lValue, Object arg) throws PLCException {
        PixelSelector pixel = lValue.getPixelSelector();
        ColorChannel color = lValue.getColor();

        //Only handles the case where there is no PixelSelector and no ChanelSelector
        if(pixel == null && color == null){
            String name = lValue.getIdent().getName() + "_" + lValue.getScope();
            programString += name;
        }
        else{
            //NOT IN AS5
//            String name = lValue.getIdent().getName() + "_" + lValue.getScope();
//            programString += name;
        }
        return null;
    }

    public Object visitIdent(Ident ident, Object arg) throws PLCException {
        programString += ident.getName()+ "_" + ident.getDef().getScope();
        return null;
    }

    public Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg) throws PLCException {

        //Case of variable being a pixel
        if(statementAssign.getLv().getType() == Type.PIXEL || statementAssign.getLv().getType() == Type.INT){
            if(statementAssign.getLv().getIdent().getDef().getType() == Type.IMAGE && statementAssign.getLv().getPixelSelector() != null && statementAssign.getLv().getColor() == null){
                //Define loops
                //Y
                programString += "for (int y";
                //statementAssign.getLv().getPixelSelector().getY().visit(this, arg);
                programString += "= 0; y != ";
                String name = statementAssign.getLv().getIdent().getName() + "_" + statementAssign.getLv().getScope();
                programString += name;
                programString += ".getHeight(); y++){\n";

                //X
                programString += "for (int x";
                //statementAssign.getLv().getPixelSelector().getX().visit(this, arg);
                programString += "= 0; x != ";
                programString += name;
                programString += ".getWidth(); x++){\n";

                //Image set
                programString += "ImageOps.setRGB(";
                programString += name;
                programString += ", x, y,";
                statementAssign.getE().visit(this, arg);
                programString += ");\n}}";

            }
        else if((statementAssign.getLv().getType() == Type.IMAGE || statementAssign.getLv().getType() == Type.INT) && statementAssign.getLv().getPixelSelector() != null && statementAssign.getLv().getColor() != null){
                programString += "for (int y";
                //statementAssign.getLv().getPixelSelector().getY().visit(this, arg);
                programString += "= 0; y != ";
                String name = statementAssign.getLv().getIdent().getName() + "_" + statementAssign.getLv().getScope();
                programString += name;
                programString += ".getHeight(); y++){\n";

                //X
                programString += "for (int x";
                //statementAssign.getLv().getPixelSelector().getX().visit(this, arg);
                programString += "= 0; x != ";
                programString += name;
                programString += ".getWidth(); x++){\n";

                //Inside
                programString += "ImageOps.setRGB(";
                programString += name;
                programString += ", x, y,";

                //Determine color
                switch (statementAssign.getLv().getColor()){
                    case red -> {
                        programString += "PixelOps.setRed(";
                        statementAssign.getE().visit(this, arg);
                        programString += ", 255));\n}}";
                    }
                    case blu -> {
                        programString += "PixelOps.setBlu(";
                        statementAssign.getE().visit(this, arg);
                        programString += ", 255));\n}}";
                    }
                    case grn -> {
                        programString += "PixelOps.setGrn(";
                        statementAssign.getE().visit(this, arg);
                        programString += ", 255));\n}}";
                    }
                    default -> {
                        throw new PLCException("Something is wrong in assignment statement");
                    }
                }

            }
            else{
                statementAssign.getLv().visit(this, arg);
                programString += " = ";
                statementAssign.getE().visit(this, arg);
            }
        }

        //Case of variable being an image, no pixel selector, and no color channel
        else if (statementAssign.getLv().getType() == Type.IMAGE && statementAssign.getLv().getPixelSelector() == null && statementAssign.getLv().getColor() == null) {
            //Right side is a string
            //Ex: ImageOps.copyInto(FileURLIO.readImage(s), tallOwl);
            if(statementAssign.getE().getType() == Type.STRING){
                programString += "ImageOps.copyInto(FileURLIO.readImage(";
                statementAssign.getE().visit(this, arg);
                programString += "), ";
                String name = statementAssign.getLv().getIdent().getName() + "_" + statementAssign.getLv().getScope();
                programString += name;
                programString += ")";


                if(importedImageOps == false){
                    String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                    topPortion += importStatement;
                    importedImageOps = true;
                }

                if(importedFileURLIO == false){
                    String importStatement = "import edu.ufl.cise.plcsp23.runtime.FileURLIO;\n";
                    topPortion += importStatement;
                    importedFileURLIO = true;
                }


            }

            //Right side is an image
            //Ex: ImageOps.copyInto(sourceImage, expected);
            else if(statementAssign.getE().getType() == Type.IMAGE){
                programString += "ImageOps.copyInto(";
                statementAssign.getE().visit(this, arg);
                programString += ", ";
                String name = statementAssign.getLv().getIdent().getName() + "_" + statementAssign.getLv().getScope();
                programString += name;
                programString += ")";

                if(importedImageOps == false){
                    String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                    topPortion += importStatement;
                    importedImageOps = true;
                }
            }

            //Right side is Pixel
            //EX: ImageOps.setAllPixels(kk, PixelOps.pack(255, 0, 255));
            else if(statementAssign.getE().getType() == Type.PIXEL){
                programString += "ImageOps.setAllPixels(";
                String name = statementAssign.getLv().getIdent().getName() + "_" + statementAssign.getLv().getScope();
                programString += name;
                programString += ", ";
                statementAssign.getE().visit(this, arg);
                programString += ")";

                if(importedImageOps == false){
                    String importStatement = "import edu.ufl.cise.plcsp23.runtime.ImageOps;\n";
                    topPortion += importStatement;
                    importedImageOps = true;
                }

            }
        }

        //Case of variable being an image with pixel selector and no channel color
        else if (statementAssign.getLv().getType() == Type.IMAGE && statementAssign.getLv().getPixelSelector() != null && statementAssign.getLv().getColor() == null){
            //Define loops
            //Y
            programString += "for (int y";
            //statementAssign.getLv().getPixelSelector().getY().visit(this, arg);
            programString += "= 0; y != ";
            String name = statementAssign.getLv().getIdent().getName() + "_" + statementAssign.getLv().getScope();
            programString += name;
            programString += ".getHeight(); y++){\n";

            //X
            programString += "for (int x";
            //statementAssign.getLv().getPixelSelector().getX().visit(this, arg);
            programString += "= 0; x != ";
            programString += name;
            programString += ".getWidth(); x++){\n";

            //Image set
            programString += "ImageOps.setRGB(";
            programString += name;
            programString += ", x, y,";
            statementAssign.getE().visit(this, arg);
            programString += ");\n}}";
        }

        //Case of variable being an image with a pixel selector and channel caller
        else if (statementAssign.getLv().getType() == Type.IMAGE && statementAssign.getLv().getPixelSelector() != null && statementAssign.getLv().getColor() != null){
            programString += "for (int y";
            //statementAssign.getLv().getPixelSelector().getY().visit(this, arg);
            programString += "= 0; y != ";
            String name = statementAssign.getLv().getIdent().getName() + "_" + statementAssign.getLv().getScope();
            programString += name;
            programString += ".getHeight(); y++){\n";

            //X
            programString += "for (int x";
            //statementAssign.getLv().getPixelSelector().getX().visit(this, arg);
            programString += "= 0; x != ";
            programString += name;
            programString += ".getWidth(); x++){\n";

            //Inside
            programString += "ImageOps.setRGB(";
            programString += name;
            programString += ", x, y,";

            //Determine color
            switch (statementAssign.getLv().getColor()){
                case red -> {
                    programString += "PixelOps.setRed(";
                    statementAssign.getE().visit(this, arg);
                    programString += ", 255));\n}}";
                }
                case blu -> {
                    programString += "PixelOps.setBlu(";
                    statementAssign.getE().visit(this, arg);
                    programString += ", 255));\n}}";
                }
                case grn -> {
                    programString += "PixelOps.setGrn(";
                    statementAssign.getE().visit(this, arg);
                    programString += ", 255));\n}}";
                }
                default -> {
                    throw new PLCException("Something is wrong in assignment statement");
                }
            }

        }

        else if(statementAssign.getLv().getType() == Type.IMAGE && statementAssign.getLv().getPixelSelector() == null && statementAssign.getLv().getColor() != null){
            programString += "for (int y";
            //statementAssign.getLv().getPixelSelector().getY().visit(this, arg);
            programString += "= 0; y != ";
            String name = statementAssign.getLv().getIdent().getName() + "_" + statementAssign.getLv().getScope();
            programString += name;
            programString += ".getHeight(); y++){\n";

            //X
            programString += "for (int x";
            //statementAssign.getLv().getPixelSelector().getX().visit(this, arg);
            programString += "= 0; x != ";
            programString += name;
            programString += ".getWidth(); x++){\n";

            //Inside
            programString += "ImageOps.setRGB(";
            programString += name;
            programString += ", x, y,";

            //Determine color
            switch (statementAssign.getLv().getColor()) {
                case red -> {
                    programString += "PixelOps.red(";
                    statementAssign.getE().visit(this, arg);
                    programString += "));\n}}";
                }
                case blu -> {
                    programString += "PixelOps.blu(";
                    statementAssign.getE().visit(this, arg);
                    programString += "));\n}}";
                }
                case grn -> {
                    programString += "PixelOps.grn(";
                    statementAssign.getE().visit(this, arg);
                    programString += "));\n}}";
                }
                default -> {
                    throw new PLCException("Something is wrong in assignment statement");
                }
            }
        }

        else {
            //statementAssign.getLv().visit(this, arg);
            String name = statementAssign.getLv().getIdent().getName() + "_" + statementAssign.getLv().getScope();
            programString += name;
            programString += " = ";
//            try {
//                NumLitExpr e = (NumLitExpr) statementAssign.getE();
//                if (statementAssign.getLv().getType() == Type.STRING) {
//                    programString += "\"";
//                    statementAssign.getE().visit(this, arg);
//                    programString += "\"";
//                } else {
//                    statementAssign.getE().visit(this, arg);
//                }
//            } catch (Exception e) {
//
//                try {
//                    IdentExpr e1 = (IdentExpr) statementAssign.getE();
//                    if (statementAssign.getLv().getType() == Type.STRING) {
//                        programString += "String.valueOf(";
//                        statementAssign.getE().visit(this, arg);
//                        programString += ")";
//                    } else {
//                        statementAssign.getE().visit(this, arg);
//                    }
//                } catch (Exception e1) {
//                    statementAssign.getE().visit(this, arg);
//                }
//            }

            try {
                NumLitExpr e = (NumLitExpr) statementAssign.getE();
                if (statementAssign.getLv().getType() == Type.STRING) {
                    programString += "\"";
                    statementAssign.getE().visit(this, arg);
                    programString += "\"";
                } else {
                    statementAssign.getE().visit(this, arg);
                }
            } catch (Exception e5) {
                try {
                    IdentExpr e1 = (IdentExpr) statementAssign.getE();
                    if (statementAssign.getLv().getType() == Type.STRING) {
                        programString += "String.valueOf(";
                        statementAssign.getE().visit(this, arg);
                        programString += ")";
                    } else {
                        statementAssign.getE().visit(this, arg);
                    }
                } catch (Exception e6) {
                    try {
                        UnaryExpr e3 = (UnaryExpr) statementAssign.getE();
                        if (statementAssign.getLv().getType() == Type.STRING) {
                            programString += "String.valueOf(";
                            statementAssign.getE().visit(this, arg);
                            programString += ")";
                        } else {
                            statementAssign.getE().visit(this, arg);
                        }
                    } catch (Exception e7) {
                        try {
                            BinaryExpr e4 = (BinaryExpr) statementAssign.getE();
                            if (statementAssign.getLv().getType() == Type.STRING) {
                                programString += "String.valueOf(";
                                statementAssign.getE().visit(this, arg);
                                programString += ")";
                            } else {
                                statementAssign.getE().visit(this, arg);
                            }
                        } catch (Exception e8) {
                            if(statementAssign.getLv().getType() == Type.STRING && statementAssign.getE().getType() == Type.INT){
                                programString += "String.valueOf(";
                                statementAssign.getE().visit(this, arg);
                                programString += ")";
                            }
                            else if(statementAssign.getLv().getType() == Type.STRING && statementAssign.getE().getType() == Type.PIXEL){
                                programString += "PixelOps.packedToString(";
                                statementAssign.getE().visit(this, arg);
                                programString += ")";
                            }
                            else {
                                statementAssign.getE().visit(this, arg);
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public Object visitWriteStatement(WriteStatement statementWrite, Object arg) throws PLCException {
        //Case where the thing being printed is a pixel, otherwise, call normal version of Console.IO
        if(statementWrite.getE().getType() == Type.PIXEL){
            programString += "ConsoleIO.writePixel(";
            statementWrite.getE().visit(this, arg);
            programString += ")";
        }
        else {
            programString += "ConsoleIO.write(";
            statementWrite.getE().visit(this, arg);
            programString += ")";
        }

        //IDEA
        if(importedIO == false){
            String importStatement = "import edu.ufl.cise.plcsp23.runtime.ConsoleIO;\n";
            topPortion += importStatement;
            importedIO = true;
        }
        return null;
    }

    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg)throws PLCException {
        programString += "return ";

        //Check if number is being returned
        //First check if a pixel and a string, then check if it's a raw number, then check if it's a variable number
        if(returnStatement.getE().getType() == Type.PIXEL && programTypeUniversal == Type.STRING){
            programString += "PixelOps.packedToString(";
            returnStatement.getE().visit(this, arg);
            programString += ")";
        }
        else {
//            try {
//                NumLitExpr e = (NumLitExpr) returnStatement.getE();
//                if (programType == Type.STRING) {
//                    programString += "\"";
//                    returnStatement.getE().visit(this, arg);
//                    programString += "\"";
//                } else {
//                    returnStatement.getE().visit(this, arg);
//                }
//            } catch (Exception e) {
//                try {
//                    IdentExpr e1 = (IdentExpr) returnStatement.getE();
//                    if (programTypeUniversal == Type.STRING) {
//                        programString += "String.valueOf(";
//                        returnStatement.getE().visit(this, arg);
//                        programString += ")";
//                    } else {
//                        returnStatement.getE().visit(this, arg);
//                    }
//                } catch (Exception e1) {
//                    returnStatement.getE().visit(this, arg);
//                }
//            }
            try {
                NumLitExpr e = (NumLitExpr) returnStatement.getE();
                if (programTypeUniversal == Type.STRING) {
                    programString += "\"";
                    returnStatement.getE().visit(this, arg);
                    programString += "\"";
                } else {
                    returnStatement.getE().visit(this, arg);
                }
            } catch (Exception e5) {
                try {
                    IdentExpr e1 = (IdentExpr) returnStatement.getE();
                    if (programTypeUniversal == Type.STRING) {
                        programString += "String.valueOf(";
                        returnStatement.getE().visit(this, arg);
                        programString += ")";
                    } else {
                        returnStatement.getE().visit(this, arg);
                    }
                } catch (Exception e6) {
                    try {
                        UnaryExpr e3 = (UnaryExpr) returnStatement.getE();
                        if (programTypeUniversal == Type.STRING) {
                            programString += "String.valueOf(";
                            returnStatement.getE().visit(this, arg);
                            programString += ")";
                        } else {
                            returnStatement.getE().visit(this, arg);
                        }
                    } catch (Exception e7) {
                        try {
                            BinaryExpr e4 = (BinaryExpr) returnStatement.getE();
                            if (programTypeUniversal == Type.STRING) {
                                programString += "String.valueOf(";
                                returnStatement.getE().visit(this, arg);
                                programString += ")";
                            } else {
                                returnStatement.getE().visit(this, arg);
                            }
                        } catch (Exception e8) {
                            returnStatement.getE().visit(this, arg);
                        }
                    }
                }
            }
        }
        return null;
    }

    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws PLCException {
        programString += "while (";
        whileStatement.getGuard().visit(this, arg);

        //Since conditions are always numbers, do a conversion from int to bool
        programString += " !=0 ";
        programString += ") {\n";
        whileStatement.getBlock().visit(this, arg);
        programString += "\n}";
        return null;
    }
}
