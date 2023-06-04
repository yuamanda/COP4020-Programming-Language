package edu.ufl.cise.plcsp23;

public class NumLitToken extends Token implements INumLitToken{

    //Numeric Value
    int numericValue;

    public NumLitToken(Kind kind, int pos, int length, char[] source){
        super(kind, pos, length, source);
    }

    public void setNumericValue(int numericValue){
        this.numericValue = numericValue;
    }


    //Returns the numeric value of the source
    //Missing the TRY AND CATCH FOR LEXICAL EXCEPTIONS
    @Override
    public int getValue(){
        return numericValue;
    }
}