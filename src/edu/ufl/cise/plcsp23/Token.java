package edu.ufl.cise.plcsp23;

public class Token implements IToken{
    //Defining variables
    final Kind kind;
    final int pos;
    final int length;
    final char[] source;
    int line = -1;
    int column = -1;

    //Constructor initializes the final field
    public Token(Kind kind, int pos, int length, char[] source){
        super(); //?
        this.kind = kind;
        this.pos = pos;
        this.length = length;
        this.source = source;
    }

    //Set the line and column values
    public void setPosition(int line, int column){
        this.line = line;
        this.column = column;
    }

    //Returns a record of the line and column number
    public SourceLocation getSourceLocation(){
        SourceLocation d0 = new SourceLocation(line, column);
        return d0;
    }

    //Return the kind of this token
    public Kind getKind(){
        return kind;
    }

    //Return the characters from the source belonging to the token
    public String getTokenString(){
//        String temp = "";
//        for (char x : source) {
//            temp = temp + x;
//        }
//
//        return temp;
        String temp = "";
        for (int i = pos; i < (pos+length); i++){
            temp  = temp + source[i];
        }

        return temp;

    }

    //Print Token (FOR DEVELOPMENT)
    //WIP
//    @Override
//    public String toString(){
//         System.out.println(getTokenString());
//    }
}

