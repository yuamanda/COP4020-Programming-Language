package edu.ufl.cise.plcsp23;

import java.util.Arrays;
import java.util.HashMap;

public class Scanner implements IScanner{
    final String input;

    //Array containing input characters, terminated with input character 0
    final char[] inputChars;

    //Invariant ch == inputChar[pos]
    int pos; //Pos of char
    char ch; //next char

    //Line and column its passing through
    int line = 1;
    int column = 1;

    //Line and column that current token begins in
    int tokenLine;
    int tokenColumn;

    //Constructor initializes all values
    public Scanner(String input){
        this.input = input;

        this.inputChars = Arrays.copyOf(input.toCharArray(), input.length()+1);

        this.pos = 0;
        this.ch = inputChars[pos];
    }

    //This return the tokens
    @Override
    public IToken next() throws LexicalException{
        Token nextToken = scanToken();
        nextToken.setPosition(tokenLine, tokenColumn);

        if(nextToken.kind == IToken.Kind.ERROR){
            throw new LexicalException("Invalid Character");
        }

        return nextToken;
    }

    //Labels the state of each current token
    private enum State{
        START,
        HAVE_EQ,
        IN_IDENT,
        IN_NUM_LIT,
        HAVE_LT,
        HAVE_GT,
        HAVE_BITAND,
        HAVE_BITOR,
        HAVE_MINUS,
        HAVE_TIMES,
        IS_COMMENT,
        IS_STRING,
        IS_ESCAPE
    }

    //Updates the position and currently looked at character
    public void nextChar(){
        column++;
        // if there is a "\n" then increase the line number and reset the column number back to 1
        if(inputChars[pos] == '\n'){
            line++;
            column = 1;
        }

        this.pos++;

        if(pos == inputChars.length){
            pos--;
        }

        this.ch = inputChars[pos];
    }

    //Utility Functions
    private boolean isDigit (int ch){
        return '0' <= ch && ch <= '9';
    }

    private boolean isLetter(int ch){
        return ('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z');
    }

    private boolean isIdentStart(int ch){
        return isLetter(ch) || (ch == '_');
    }

    private void error(String message) throws LexicalException{
        throw new LexicalException("Error at pos: " + pos + ": " + message);
    }

    //Returns the numeric value of the token
    private int numericConversion(NumLitToken token) throws LexicalException{
        //Creates an array of just numbers
        char tempArr[] = new char[token.length];
        int currIndex = 0;

        for(int i = token.pos; i < (token.pos+token.length); i++){
            tempArr[currIndex] = token.source[i];
            currIndex++;
        }

        //Converts to string
        String temp = String.valueOf(tempArr);
        int tempNumber;

        // throws a lexical exception if the string can't be converted into a number
        try{
            tempNumber = Integer.parseInt(temp);
        }

        catch (NumberFormatException e){
            throw new LexicalException("Incorrect conversion to number from character array");
        }

        return tempNumber;
    }

    //Creates hashmap of reserved words
    private static HashMap<String, IToken.Kind> reservedWords;
    static{
        reservedWords = new HashMap<String, IToken.Kind>();
        reservedWords.put("image", IToken.Kind.RES_image);
        reservedWords.put("pixel", IToken.Kind.RES_pixel);
        reservedWords.put("int", IToken.Kind.RES_int);
        reservedWords.put("string", IToken.Kind.RES_string);
        reservedWords.put("void", IToken.Kind.RES_void);
        reservedWords.put("nil", IToken.Kind.RES_nil);
        reservedWords.put("load", IToken.Kind.RES_load);
        reservedWords.put("display", IToken.Kind.RES_display);
        reservedWords.put("write", IToken.Kind.RES_write);
        reservedWords.put("x", IToken.Kind.RES_x);
        reservedWords.put("y", IToken.Kind.RES_y);
        reservedWords.put("a", IToken.Kind.RES_a);
        reservedWords.put("r", IToken.Kind.RES_r);
        reservedWords.put("X", IToken.Kind.RES_X);
        reservedWords.put("Y", IToken.Kind.RES_Y);
        reservedWords.put("Z", IToken.Kind.RES_Z);
        reservedWords.put("x_cart", IToken.Kind.RES_x_cart);
        reservedWords.put("y_cart", IToken.Kind.RES_y_cart);
        reservedWords.put("a_polar", IToken.Kind.RES_a_polar);
        reservedWords.put("r_polar", IToken.Kind.RES_r_polar);
        reservedWords.put("rand", IToken.Kind.RES_rand);
        reservedWords.put("sin", IToken.Kind.RES_sin);
        reservedWords.put("cos", IToken.Kind.RES_cos);
        reservedWords.put("atan", IToken.Kind.RES_atan);
        reservedWords.put("if", IToken.Kind.RES_if);
        reservedWords.put("while", IToken.Kind.RES_while);
        reservedWords.put("red", IToken.Kind.RES_red);
        reservedWords.put("grn", IToken.Kind.RES_grn);
        reservedWords.put("blu", IToken.Kind.RES_blu);

    }

    //Checks if char is an escape sequence
    private boolean isEscapeSequence(char ch){
        if(ch == '\b' || ch == '\t'){
            return true;
        }

        return false;
    }

    //The traversal of the input
    private Token scanToken() throws LexicalException{
        State state = State.START;
        int tokenStart = -1;

        //Read characters, loop ends when a token is returned
        while(true){
            switch(state){
                case START -> {
                    tokenStart = pos;
                    tokenLine = line;
                    tokenColumn = column;
                    switch (ch){
                        //In case its end of file
                        case 0 -> {
                            return new Token(IToken.Kind.EOF, tokenStart, 0, inputChars);
                        }

                        //Separators (whitespace)
                        case ' ', '\n', '\r', '\t', '\f' -> {
                            nextChar();
                        }

                        //Operators
                        case '.' -> {
                            nextChar();
                            return new Token(IToken.Kind.DOT, tokenStart, 1, inputChars);
                        }
                        case ',' -> {
                            nextChar();
                            return new Token(IToken.Kind.COMMA, tokenStart, 1, inputChars);
                        }
                        case '?' -> {
                            nextChar();
                            return new Token(IToken.Kind.QUESTION, tokenStart, 1, inputChars);
                        }
                        case ':' -> {
                            nextChar();
                            return new Token(IToken.Kind.COLON, tokenStart, 1, inputChars);
                        }
                        case '(' -> {
                            nextChar();
                            return new Token(IToken.Kind.LPAREN, tokenStart, 1, inputChars);
                        }
                        case ')' -> {
                            nextChar();
                            return new Token(IToken.Kind.RPAREN, tokenStart, 1, inputChars);
                        }
                        case '<' -> {
                            state = state.HAVE_LT;
                            nextChar();
                        }
                        case '>' -> {
                            state = state.HAVE_GT;
                            nextChar();
                        }
                        case '[' -> {
                            nextChar();
                            return new Token(IToken.Kind.LSQUARE, tokenStart, 1, inputChars);
                        }
                        case ']' -> {
                            nextChar();
                            return new Token(IToken.Kind.RSQUARE, tokenStart, 1, inputChars);
                        }
                        case '{' -> {
                            nextChar();
                            return new Token(IToken.Kind.LCURLY, tokenStart, 1, inputChars);
                        }
                        case '}' -> {
                            nextChar();
                            return new Token(IToken.Kind.RCURLY, tokenStart, 1, inputChars);
                        }
                        case '=' -> {
                            state = State.HAVE_EQ;
                            nextChar();
                        }
                        case '!' -> {
                            nextChar();
                            return new Token(IToken.Kind.BANG, tokenStart, 1, inputChars);
                        }
                        case '&' -> {
                            state = state.HAVE_BITAND;
                            nextChar();
                        }
                        case '|' -> {
                            state = state.HAVE_BITOR;
                            nextChar();
                        }
                        case '+' -> {
                            nextChar();
                            return new Token(IToken.Kind.PLUS, tokenStart, 1, inputChars);
                        }
                        case '-' -> {
                            nextChar();
                            return new Token(IToken.Kind.MINUS, tokenStart, 1, inputChars);
                        }
                        case '*' -> {
                            state = state.HAVE_TIMES;
                            nextChar();
                        }
                        case '/' -> {
                            nextChar();
                            return new Token(IToken.Kind.DIV, tokenStart, 1, inputChars);
                        }
                        case '%' -> {
                            nextChar();
                            return new Token(IToken.Kind.MOD, tokenStart, 1, inputChars);
                        }

                        //Comment
                        case '~'-> {
                            state = State.IS_COMMENT;
                            nextChar();
                        }

                        //Digit zero
                        case '0' -> {
                            nextChar();
                            NumLitToken token = new NumLitToken(IToken.Kind.NUM_LIT, tokenStart, 1, inputChars);

                            int numericVal = numericConversion(token);
                            token.setNumericValue(numericVal);
                            return token;
                        }

                        //Non-zero digits
                        case '1' , '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            state = State.IN_NUM_LIT;
                            nextChar();
                        }

                        //String literals
                        case '"' -> {
                            // added IS_STRING to states
                            state = State.IS_STRING;

                            // get the next character if the string doesn't have "\n"
                            nextChar();
                        }

                        default -> {
                            if(isLetter(ch) || isIdentStart(ch)){
                                state = State.IN_IDENT;
                                nextChar();
                            }
                            else {
                                error("illegal char with ascii value: " + (int)ch);
                                return new Token(IToken.Kind.ERROR, tokenStart, 1, inputChars);
                            }
                        }
                    }
                }

                case HAVE_EQ -> {
                    if(ch == '='){
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.EQ, tokenStart, 2, inputChars);
                    }
                    else{
                        state = state.START;
                        return new Token(IToken.Kind.ASSIGN, tokenStart, 1, inputChars);
                    }
                }
                case IN_IDENT -> {
                    if(isIdentStart(ch) || isDigit(ch)){
                        nextChar();
                    }
                    else{
                        //Current char belongs to the next token
                        int length = pos-tokenStart;

                        //See if it's a reserved word
                        String text = input.substring(tokenStart, tokenStart+length);
                        IToken.Kind kind = reservedWords.get(text);

                        //When not an identifier
                        if(kind == null){
                            kind = IToken.Kind.IDENT;
                        }

                        return new Token(kind, tokenStart, length, inputChars);
                    }
                }
                case IN_NUM_LIT -> {
                    //If it's a digit, continue in the IN_NUM_LIT state
                    if(isDigit(ch)){
                        nextChar();
                    }
                    else{
                        //The char is no longer a NUM_LIT, so the current char belongs to the next token
                        int length = pos-tokenStart;
                        NumLitToken token = new NumLitToken(IToken.Kind.NUM_LIT, tokenStart, length, inputChars);
                        int numericVal = numericConversion(token);
                        token.setNumericValue(numericVal);
                        return token;
                    }
                }
                case HAVE_LT -> {
                    if (ch == '='){
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.LE, tokenStart, 2, inputChars);
                    }
                    else if (ch == '-'){
                        state = state.HAVE_MINUS;
                        nextChar();
                    }
                    else {
                        state = state.START;
                        return new Token(IToken.Kind.LT, tokenStart, 1, inputChars);
                    }

                }
                case HAVE_GT -> {
                    if(ch == '='){
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.GE, tokenStart, 2, inputChars);
                    }
                    else{
                        state = state.START;
                        return new Token(IToken.Kind.GT, tokenStart, 1, inputChars);
                    }
                }
                case HAVE_BITAND -> {
                    if(ch == '&'){
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.AND, tokenStart, 2, inputChars);
                    }
                    else{
                        state = state.START;
                        return new Token(IToken.Kind.BITAND, tokenStart, 1, inputChars);
                    }
                }
                case HAVE_BITOR -> {
                    if (ch == '|'){
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.OR, tokenStart, 2, inputChars);
                    }
                    else {
                        state = state.START;
                        return new Token(IToken.Kind.BITOR, tokenStart, 1, inputChars);
                    }
                }
                case HAVE_TIMES -> {
                    if(ch == '*') {
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.EXP, tokenStart, 2, inputChars);
                    }
                    else {
                        state = state.START;
                        return new Token(IToken.Kind.TIMES, tokenStart, 1, inputChars);
                    }
                }
                case HAVE_MINUS -> {
                    if(ch == '>'){
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.EXCHANGE, tokenStart, 3, inputChars);
                    }
                    else{
                        state = state.START;
//                        return new Token(IToken.Kind.MINUS, tokenStart, 1, inputChars);
                        return new Token(IToken.Kind.ERROR, tokenStart, 1, inputChars);
                    }
                }

                case IS_COMMENT -> {
                    if(ch == '\n'){
                        state = State.START;
                    }
                    nextChar();
                }

                case IS_ESCAPE -> {
                    if(ch == 'b' || ch == 't' || ch == 'n' || ch == 'r' || ch == '"' || ch == '\\'){
                        state = State.IS_STRING;
                        nextChar();
                    }
                    else{
                        error("illegal string value: " + ch);
                        return new Token(IToken.Kind.ERROR, tokenStart, 1, inputChars);
                    }
                }

                case IS_STRING -> {
                    // Check if the escape sequence is valid, is so, check if the string has '\n' or '\r' then throw a lexical exception
                    if(isEscapeSequence(ch)){
                        nextChar();
                    }

                    else if (ch == '\\') {
                        state = State.IS_ESCAPE;
                        nextChar();
                    }

                    else if (ch == '\n' || ch == '\r') {
                        error("illegal string value: " + ch);
                        return new Token(IToken.Kind.ERROR, tokenStart, 1, inputChars);
                    }

                    // if the next character is a '"' then end string
                   else if(ch == '"'){
                        state = State.START;
                        int length = pos-tokenStart + 1;
                        nextChar();
                        return new StringLitToken(IToken.Kind.STRING_LIT, tokenStart, length, inputChars); //DOUBLE CHECK
                   }

                   //Check to see out of bounds (if there are illegal quotes)
                   else if (ch == 0) {
                        error("illegal string value: " + ch);
                        return new Token(IToken.Kind.ERROR, tokenStart, 1, inputChars);
                    }

                    // else go to the next character and add it to the string
                    else{
                        nextChar();
                    }
                }

                default -> {
                    throw new UnsupportedOperationException("Bug in Scanner");
                }
            }
        }
    }
}
