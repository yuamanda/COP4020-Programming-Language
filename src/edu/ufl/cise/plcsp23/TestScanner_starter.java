/*Copyright 2023 by Beverly A Sanders
 * 
 * This code is provided for solely for use of students in COP4020 Programming Language Concepts at the 
 * University of Florida during the spring semester 2023 as part of the course project.  
 * 
 * No other use is authorized. 
 * 
 * This code may not be posted on a public web site either during or after the course.  
 */

package edu.ufl.cise.plcsp23;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import edu.ufl.cise.plcsp23.IToken.Kind;
import edu.ufl.cise.plcsp23.IToken.SourceLocation;

class TestScanner_starter {

	// makes it easy to turn output on and off (and less typing than System.out.println)
	static final boolean VERBOSE = true;

	void show(Object obj) {
		if (VERBOSE) {
			System.out.println(obj);
		}
	}

	// check that this token has the expected kind
	void checkToken(Kind expectedKind, IToken t) {
		assertEquals(expectedKind, t.getKind());
	}
	
	void checkToken(Kind expectedKind, String expectedChars, SourceLocation expectedLocation, IToken t) {
		assertEquals(expectedKind, t.getKind());
		assertEquals(expectedChars, t.getTokenString());
		assertEquals(expectedLocation, t.getSourceLocation());
		;
	}

	void checkIdent(String expectedChars, IToken t) {
		checkToken(Kind.IDENT, t);
		assertEquals(expectedChars.intern(), t.getTokenString().intern());
		;
	}

	void checkString(String expectedValue, IToken t) {
		assertTrue(t instanceof IStringLitToken);
		assertEquals(expectedValue, ((IStringLitToken) t).getValue());
	}

	void checkString(String expectedChars, String expectedValue, SourceLocation expectedLocation, IToken t) {
		assertTrue(t instanceof IStringLitToken);
		assertEquals(expectedValue, ((IStringLitToken) t).getValue());
		assertEquals(expectedChars, t.getTokenString());
		assertEquals(expectedLocation, t.getSourceLocation());
	}

	void checkNUM_LIT(int expectedValue, IToken t) {
		checkToken(Kind.NUM_LIT, t);
		int value = ((INumLitToken) t).getValue();
		assertEquals(expectedValue, value);
	}
	
	void checkNUM_LIT(int expectedValue, SourceLocation expectedLocation, IToken t) {
		checkToken(Kind.NUM_LIT, t);
		int value = ((INumLitToken) t).getValue();
		assertEquals(expectedValue, value);
		assertEquals(expectedLocation, t.getSourceLocation());
	}

	void checkTokens(IScanner s, IToken.Kind... kinds) throws LexicalException {
		for (IToken.Kind kind : kinds) {
			checkToken(kind, s.next());
		}
	}

	void checkTokens(String input, IToken.Kind... kinds) throws LexicalException {
		IScanner s = CompilerComponentFactory.makeScanner(input);
		for (IToken.Kind kind : kinds) {
			checkToken(kind, s.next());
		}
	}

	// check that this token is the EOF token
	void checkEOF(IToken t) {
		checkToken(Kind.EOF, t);
	}


	//PASSED
	@Test
	void emptyProg() throws LexicalException {
		String input = "";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkEOF(scanner.next());
	}

	//PASSED
	@Test
	void onlyWhiteSpace() throws LexicalException {
		String input = " \t \r\n \f \n";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkEOF(scanner.next());
		checkEOF(scanner.next());  //repeated invocations of next after end reached should return EOF token
	}


	//PASSED
	@Test
	void numLits1() throws LexicalException {
		String input = """
				123
				05 240
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkNUM_LIT(123, scanner.next());
		checkNUM_LIT(0, scanner.next());
		checkNUM_LIT(5, scanner.next());
		checkNUM_LIT(240, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	@Test
	//Too large should still throw LexicalException
	void numLitTooBig() throws LexicalException {
		String input = "999999999999999999999";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}

	//PASSED
	@Test
	void identsAndReserved() throws LexicalException {
		String input = """
				i0
				  i1  x ~~~2 spaces at beginning and after il
				y Y
				""";

		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.IDENT,"i0", new SourceLocation(1,1), scanner.next());
		checkToken(Kind.IDENT, "i1",new SourceLocation(2,3), scanner.next());
		checkToken(Kind.RES_x, "x", new SourceLocation(2,7), scanner.next());		
		checkToken(Kind.RES_y, "y", new SourceLocation(3,1), scanner.next());
		checkToken(Kind.RES_Y, "Y", new SourceLocation(3,3), scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	@Test
	void operators0() throws LexicalException {
		String input = """
				==
				+
				/
				====
				=
				===
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.PLUS, scanner.next());
		checkToken(Kind.DIV, scanner.next());
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.ASSIGN, scanner.next());
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.ASSIGN, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	@Test
	void stringLiterals1() throws LexicalException {
		String input = """
				"hello"
				"\t"
				"\\""
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString(input.substring(0, 7),"hello", new SourceLocation(1,1), scanner.next());
		checkString(input.substring(8, 11), "\t", new SourceLocation(2,1), scanner.next());
		checkString(input.substring(12, 16), "\"",  new SourceLocation(3,1), scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	@Test
	void illegalEscape() throws LexicalException {
		String input = """
				"\\t"
				"\\k"
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString("\"\\t\"","\t", new SourceLocation(1,1), scanner.next());
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}

	//PASSED
	@Test
	void illegalLineTermInStringLiteral() throws LexicalException {
		String input = """
				"\\n"  ~ this one passes the escape sequence--it is OK
				"\n"   ~ this on passes the LF, it is illegal.
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString("\"\\n\"","\n", new SourceLocation(1,1), scanner.next());
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}

	//PASSED
	@Test
	void lessThanGreaterThanExchange() throws LexicalException {
		String input = """
				<->>>>=
				<<=<
				""";
		checkTokens(input, Kind.EXCHANGE, Kind.GT, Kind.GT, Kind.GE, Kind.LT, Kind.LE, Kind.LT, Kind.EOF);
	}

	//PASSED
	/** The Scanner should not backtrack so this input should throw an exception */
	@Test
	void incompleteExchangeThrowsException() throws LexicalException {
		String input = " <- ";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});	
	}

	//PASSED
	@Test
	void illegalChar() throws LexicalException {
		String input = """
				abc
				@
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkIdent("abc", scanner.next());
		assertThrows(LexicalException.class, () -> {
			@SuppressWarnings("unused")
			IToken t = scanner.next();
		});
	}

	//MY TESTS

	//PASSED
	@Test
	void singleCharTokens() throws LexicalException{
		String input = "+*00";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.PLUS,scanner.next());
		checkToken(Kind.TIMES,scanner.next());
		checkToken(Kind.NUM_LIT,scanner.next());
		checkToken(Kind.NUM_LIT,scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	@Test
	void singleCharTokensWithWhiteSpace() throws LexicalException{
		String input = """
    + *
    0
    0
    """;
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.PLUS,scanner.next());
		checkToken(Kind.TIMES,scanner.next());
		checkToken(Kind.NUM_LIT,scanner.next());
		checkToken(Kind.NUM_LIT,scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	@Test
	void equals() throws LexicalException{
		String input = """
    ==
    == ==
    ==*==
    *==+
    """;
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.TIMES,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.TIMES,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.PLUS,scanner.next());
	}


	//PASSED
	@Test
	void eqWithError() throws LexicalException{
		String input = """
    ==
    $
    """;
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.EQ,scanner.next());
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}

	//PASSED
	@Test
	void numLits() throws LexicalException{
		String input = """
    123
    05
    240
    1+2
    """;
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkNUM_LIT(123,scanner.next());
		checkNUM_LIT(0,scanner.next());
		checkNUM_LIT(5,scanner.next());
		checkNUM_LIT(240,scanner.next());
		checkNUM_LIT(1,scanner.next());
		checkToken(Kind.PLUS,scanner.next());
		checkNUM_LIT(2,scanner.next());
	}

	//PASSED
	//Checks if comments work, only exits on "\n"
	@Test
	void checkComment() throws LexicalException{
		String input = """
    "hello"
				~abc\n
				abc
				1223
				1+2
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString(input.substring(0, 7),"hello", new SourceLocation(1,1), scanner.next());
		checkToken(Kind.IDENT, scanner.next());
		checkNUM_LIT(1223,scanner.next());
		checkNUM_LIT(1,scanner.next());
		checkToken(Kind.PLUS,scanner.next());
		checkNUM_LIT(2,scanner.next());
	}

	//PASSED
	// number literal test with spaces and zeros added
	@Test
	void numLits2() throws LexicalException {
		String input = """
				123 456
				05 240
				0
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkNUM_LIT(123, scanner.next());
		checkNUM_LIT(456, scanner.next());
		checkNUM_LIT(0, scanner.next());
		checkNUM_LIT(5, scanner.next());
		checkNUM_LIT(240, scanner.next());
		checkNUM_LIT(0, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// operator test with all the operators/separators
	@Test
	void operators2() throws LexicalException {
		String input = """
				.
				,
				?
				:
				(
				)
				<
				>
				[
				]
				{
				}
				=
				==
				<->
				<=
				>=
				!
				&
				&&
				|
				||
				+
				-
				*
				**
				/
				%
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.DOT, scanner.next());
		checkToken(Kind.COMMA, scanner.next());
		checkToken(Kind.QUESTION, scanner.next());
		checkToken(Kind.COLON, scanner.next());
		checkToken(Kind.LPAREN, scanner.next());
		checkToken(Kind.RPAREN, scanner.next());
		checkToken(Kind.LT, scanner.next());
		checkToken(Kind.GT, scanner.next());
		checkToken(Kind.LSQUARE, scanner.next());
		checkToken(Kind.RSQUARE, scanner.next());
		checkToken(Kind.LCURLY, scanner.next());
		checkToken(Kind.RCURLY, scanner.next());
		checkToken(Kind.ASSIGN, scanner.next());
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.EXCHANGE, scanner.next());
		checkToken(Kind.LE, scanner.next());
		checkToken(Kind.GE, scanner.next());
		checkToken(Kind.BANG, scanner.next());
		checkToken(Kind.BITAND, scanner.next());
		checkToken(Kind.AND, scanner.next());
		checkToken(Kind.BITOR, scanner.next());
		checkToken(Kind.OR, scanner.next());
		checkToken(Kind.PLUS, scanner.next());
		checkToken(Kind.MINUS, scanner.next());
		checkToken(Kind.TIMES, scanner.next());
		checkToken(Kind.EXP, scanner.next());
		checkToken(Kind.DIV, scanner.next());
		checkToken(Kind.MOD, scanner.next());

		checkEOF(scanner.next());
	}

	//PASSED
	// checks for illegal line term in string literal: "\\r" and "\r" with the test expected to throw an exception
	@Test
	void illegalLineTermInStringLiteral3() throws LexicalException {
		String input = """
				"\\r"  ~ this one passes the escape sequence--it is OK
				"\r"   ~ this on passes the LF, it is illegal.
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString("\"\\r\"","\r", new SourceLocation(1,1), scanner.next());
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}

	//PASSED
	// checks for illegal char in string literal: used "#" with the test expected to throw a lexical exception
	@Test
	void illegalChar2() throws LexicalException {
		String input = """
				abc
				#
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkIdent("abc", scanner.next());
		assertThrows(LexicalException.class, () -> {
			@SuppressWarnings("unused")
			IToken t = scanner.next();
		});
	}

	//PASSED
	// checks the string literal containing "==" in combination with other symbols
	@Test
	void equals2() throws LexicalException{
		String input = """
    == ==
    == ==
    ==*==
    ==*==
    *==+
    *==+
    """;
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.TIMES,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.TIMES,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.TIMES,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.PLUS,scanner.next());
		checkToken(Kind.TIMES,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.PLUS,scanner.next());
	}

	@Test
	void allOperatorsAndSeparators() throws LexicalException {
		/*  Operators and Separators . | , | ? | : | ( | ) | < | > | [ | ] | { | } | = | == | <-> | <= |  >= | ! | & | && | | | || |
      + | - | * | ** | / | %   */
		String input = """
				. , ? : ( ) < > [ ] { } = == <-> <= >= ! & && | || + - * ** / %
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.DOT, scanner.next());
		checkToken(Kind.COMMA, scanner.next());
		checkToken(Kind.QUESTION, scanner.next());
		checkToken(Kind.COLON, scanner.next());
		checkToken(Kind.LPAREN, scanner.next());
		checkToken(Kind.RPAREN, scanner.next());
		checkToken(Kind.LT, scanner.next());
		checkToken(Kind.GT, scanner.next());
		checkToken(Kind.LSQUARE, scanner.next());
		checkToken(Kind.RSQUARE, scanner.next());
		checkToken(Kind.LCURLY, scanner.next());
		checkToken(Kind.RCURLY, scanner.next());
		checkToken(Kind.ASSIGN, scanner.next());
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.EXCHANGE, scanner.next());
		checkToken(Kind.LE, scanner.next());
		checkToken(Kind.GE, scanner.next());
		checkToken(Kind.BANG, scanner.next());
		checkToken(Kind.BITAND, scanner.next());
		checkToken(Kind.AND, scanner.next());
		checkToken(Kind.BITOR, scanner.next());
		checkToken(Kind.OR, scanner.next());
		checkToken(Kind.PLUS, scanner.next());
		checkToken(Kind.MINUS, scanner.next());
		checkToken(Kind.TIMES, scanner.next());
		checkToken(Kind.EXP, scanner.next());
		checkToken(Kind.DIV, scanner.next());
		checkToken(Kind.MOD, scanner.next());
	}

	@Test
	void allReservedWords() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				image pixel int string void nil load display write x y a r X Y Z x_cart y_cart a_polar r_polar rand sin cos atan if while
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_image, scanner.next());
		checkToken(Kind.RES_pixel, scanner.next());
		checkToken(Kind.RES_int, scanner.next());
		checkToken(Kind.RES_string, scanner.next());
		checkToken(Kind.RES_void, scanner.next());
		checkToken(Kind.RES_nil, scanner.next());
		checkToken(Kind.RES_load, scanner.next());
		checkToken(Kind.RES_display, scanner.next());
		checkToken(Kind.RES_write, scanner.next());
		checkToken(Kind.RES_x, scanner.next());
		checkToken(Kind.RES_y, scanner.next());
		checkToken(Kind.RES_a, scanner.next());
		checkToken(Kind.RES_r, scanner.next());
		checkToken(Kind.RES_X, scanner.next());
		checkToken(Kind.RES_Y, scanner.next());
		checkToken(Kind.RES_Z, scanner.next());
		checkToken(Kind.RES_x_cart, scanner.next());
		checkToken(Kind.RES_y_cart, scanner.next());
		checkToken(Kind.RES_a_polar, scanner.next());
		checkToken(Kind.RES_r_polar, scanner.next());
		checkToken(Kind.RES_rand, scanner.next());
		checkToken(Kind.RES_sin, scanner.next());
		checkToken(Kind.RES_cos, scanner.next());
		checkToken(Kind.RES_atan, scanner.next());
		checkToken(Kind.RES_if, scanner.next());
		checkToken(Kind.RES_while, scanner.next());
	}

	@Test
	void doOperatorsSeparateTokens() throws LexicalException {
		String input = """
				doesthis+work.for-you?
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.IDENT,"doesthis", new SourceLocation(1,1), scanner.next());
		checkToken(Kind.PLUS, scanner.next());
		checkToken(Kind.IDENT,"work", new SourceLocation(1,10), scanner.next());
		checkToken(Kind.DOT, scanner.next());
		// for is NOT a reserved word, oddly enough
		checkToken(Kind.IDENT,"for", new SourceLocation(1,15), scanner.next());
		checkToken(Kind.MINUS, scanner.next());
		checkToken(Kind.IDENT,"you", new SourceLocation(1,19), scanner.next());
		checkToken(Kind.QUESTION, scanner.next());
	}

	@Test
	void reservedWordsWithAddedText() throws LexicalException {
		String input = """
				image imagee limage pixelx inty int stringz astring voida nill loadd load displayy ewrite write
				xx yy aa rr XX YY ZZ x_cartt y_cartt xa_polar r_polar randd sinn cosss atann iff whilee
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);

		checkToken(Kind.RES_image, scanner.next());
		checkToken(Kind.IDENT,"imagee", new SourceLocation(1,7), scanner.next());
		checkToken(Kind.IDENT,"limage", new SourceLocation(1,14), scanner.next());
		checkToken(Kind.IDENT,"pixelx", new SourceLocation(1,21), scanner.next());
		checkToken(Kind.IDENT,"inty", new SourceLocation(1,28), scanner.next());
		checkToken(Kind.RES_int, scanner.next());
		checkToken(Kind.IDENT,"stringz", new SourceLocation(1,37), scanner.next());
		checkToken(Kind.IDENT,"astring", new SourceLocation(1,45), scanner.next());
		checkToken(Kind.IDENT,"voida", new SourceLocation(1,53), scanner.next());
		checkToken(Kind.IDENT,"nill", new SourceLocation(1,59), scanner.next());
		checkToken(Kind.IDENT,"loadd", new SourceLocation(1,64), scanner.next());
		checkToken(Kind.RES_load, scanner.next());
		checkToken(Kind.IDENT,"displayy", new SourceLocation(1,75), scanner.next());
		checkToken(Kind.IDENT,"ewrite", new SourceLocation(1,84), scanner.next());
		checkToken(Kind.RES_write, scanner.next());
		checkToken(Kind.IDENT,"xx", new SourceLocation(2,1), scanner.next());
		checkToken(Kind.IDENT,"yy", new SourceLocation(2,4), scanner.next());
		checkToken(Kind.IDENT,"aa", new SourceLocation(2,7), scanner.next());
		checkToken(Kind.IDENT,"rr", new SourceLocation(2,10), scanner.next());
		checkToken(Kind.IDENT,"XX", new SourceLocation(2,13), scanner.next());
		checkToken(Kind.IDENT,"YY", new SourceLocation(2,16), scanner.next());
		checkToken(Kind.IDENT,"ZZ", new SourceLocation(2,19), scanner.next());
		checkToken(Kind.IDENT,"x_cartt", new SourceLocation(2,22), scanner.next());
		checkToken(Kind.IDENT,"y_cartt", new SourceLocation(2,30), scanner.next());
		checkToken(Kind.IDENT,"xa_polar", new SourceLocation(2,38), scanner.next());
		checkToken(Kind.RES_r_polar, scanner.next());
		checkToken(Kind.IDENT,"randd", new SourceLocation(2,55), scanner.next());
		checkToken(Kind.IDENT,"sinn", new SourceLocation(2,61), scanner.next());
		checkToken(Kind.IDENT,"cosss", new SourceLocation(2,66), scanner.next());
		checkToken(Kind.IDENT,"atann", new SourceLocation(2,72), scanner.next());
		checkToken(Kind.IDENT,"iff", new SourceLocation(2,78), scanner.next());
		checkToken(Kind.IDENT,"whilee", new SourceLocation(2,82), scanner.next());
	}

	@Test
	void identifierTest() throws LexicalException {
		String input = "T6_y_MX__6NRKZt t09 pS_ __0IHtYMoJ4629qz_3 d1hlp__QV O_f2W7z ____liu9mi a__Q5x88i9ac_i8449 \nSe_CG__U_bjrt BR_44y_0Yb_r h_k_3746X _ W6xSh3 _iMu_eny__hg__j V_u__LRTm_ AX_R_8sNy9_9G0iq__ \nb1Y C6_z_ _or5 Y_G4Oj3_ay Hth2w_q43_6__1tdA yl8WUL6qn_ aO_ \nfG__O_Wsy e_S2T \nG_6l __3_6__D_Y91admK q_R0GW_BwRK _g9z jmUMrkC1 \n";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.IDENT, "T6_y_MX__6NRKZt", new SourceLocation(1, 1), scanner.next());
		checkToken(Kind.IDENT, "t09", new SourceLocation(1, 17), scanner.next());
		checkToken(Kind.IDENT, "pS_", new SourceLocation(1, 21), scanner.next());
		checkToken(Kind.IDENT, "__0IHtYMoJ4629qz_3", new SourceLocation(1, 25), scanner.next());
		checkToken(Kind.IDENT, "d1hlp__QV", new SourceLocation(1, 44), scanner.next());
		checkToken(Kind.IDENT, "O_f2W7z", new SourceLocation(1, 54), scanner.next());
		checkToken(Kind.IDENT, "____liu9mi", new SourceLocation(1, 62), scanner.next());
		checkToken(Kind.IDENT, "a__Q5x88i9ac_i8449", new SourceLocation(1, 73), scanner.next());
		checkToken(Kind.IDENT, "Se_CG__U_bjrt", new SourceLocation(2, 1), scanner.next());
		checkToken(Kind.IDENT, "BR_44y_0Yb_r", new SourceLocation(2, 15), scanner.next());
		checkToken(Kind.IDENT, "h_k_3746X", new SourceLocation(2, 28), scanner.next());
		checkToken(Kind.IDENT, "_", new SourceLocation(2, 38), scanner.next());
		checkToken(Kind.IDENT, "W6xSh3", new SourceLocation(2, 40), scanner.next());
		checkToken(Kind.IDENT, "_iMu_eny__hg__j", new SourceLocation(2, 47), scanner.next());
		checkToken(Kind.IDENT, "V_u__LRTm_", new SourceLocation(2, 63), scanner.next());
		checkToken(Kind.IDENT, "AX_R_8sNy9_9G0iq__", new SourceLocation(2, 74), scanner.next());
		checkToken(Kind.IDENT, "b1Y", new SourceLocation(3, 1), scanner.next());
		checkToken(Kind.IDENT, "C6_z_", new SourceLocation(3, 5), scanner.next());
		checkToken(Kind.IDENT, "_or5", new SourceLocation(3, 11), scanner.next());
		checkToken(Kind.IDENT, "Y_G4Oj3_ay", new SourceLocation(3, 16), scanner.next());
		checkToken(Kind.IDENT, "Hth2w_q43_6__1tdA", new SourceLocation(3, 27), scanner.next());
		checkToken(Kind.IDENT, "yl8WUL6qn_", new SourceLocation(3, 45), scanner.next());
		checkToken(Kind.IDENT, "aO_", new SourceLocation(3, 56), scanner.next());
		checkToken(Kind.IDENT, "fG__O_Wsy", new SourceLocation(4, 1), scanner.next());
		checkToken(Kind.IDENT, "e_S2T", new SourceLocation(4, 11), scanner.next());
		checkToken(Kind.IDENT, "G_6l", new SourceLocation(5, 1), scanner.next());
		checkToken(Kind.IDENT, "__3_6__D_Y91admK", new SourceLocation(5, 6), scanner.next());
		checkToken(Kind.IDENT, "q_R0GW_BwRK", new SourceLocation(5, 23), scanner.next());
		checkToken(Kind.IDENT, "_g9z", new SourceLocation(5, 35), scanner.next());
		checkToken(Kind.IDENT, "jmUMrkC1", new SourceLocation(5, 40), scanner.next());

	}

}
