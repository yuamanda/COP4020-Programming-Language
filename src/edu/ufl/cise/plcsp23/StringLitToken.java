package edu.ufl.cise.plcsp23;

public class StringLitToken extends Token implements IStringLitToken{

    //The string value passed in represented by a string
    String stringValue = "";

    public StringLitToken(Kind kind, int pos, int length, char[] source){
        super(kind, pos, length, source);
        setStringValue(source, length);
    }

    public void setStringValue(char[] source, int length){
        for(int i = pos; i < pos+length; i++){
            if(source[i] == '\"'){
                continue;
            }
            else if (source[i] == '\\') {
                switch(source[i+1]){
                    case 'b' -> {
                        stringValue = stringValue + "\b";
                    }
                    case 't' -> {
                        stringValue = stringValue + "\t";
                    }
                    case 'n' -> {
                        stringValue = stringValue + "\n";
                    }
                    case 'r' -> {
                        stringValue = stringValue + "\r";
                    }
                    case '\\' -> {
                        stringValue = stringValue + "\\";
                    }
                    case '\"' -> {
                        stringValue = stringValue + "\"";
                    }
                }
                i++;
            }

            else{
                stringValue = stringValue + source[i];
            }

        }
    }

    @Override
    public String getValue(){
        return stringValue;
    }
}
