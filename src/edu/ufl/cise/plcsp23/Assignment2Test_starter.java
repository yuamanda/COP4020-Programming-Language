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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import edu.ufl.cise.plcsp23.IToken.Kind;
import edu.ufl.cise.plcsp23.ast.AST;
import edu.ufl.cise.plcsp23.ast.BinaryExpr;
import edu.ufl.cise.plcsp23.ast.ConditionalExpr;
import edu.ufl.cise.plcsp23.ast.Expr;
import edu.ufl.cise.plcsp23.ast.IdentExpr;
import edu.ufl.cise.plcsp23.ast.NumLitExpr;
import edu.ufl.cise.plcsp23.ast.RandomExpr;
import edu.ufl.cise.plcsp23.ast.StringLitExpr;
import edu.ufl.cise.plcsp23.ast.UnaryExpr;
import edu.ufl.cise.plcsp23.ast.ZExpr;


class Assignment2Test_starter {
	void checkIdent(String expectedChars, IToken t) {
		checkToken(Kind.IDENT, t);
		assertEquals(expectedChars.intern(), t.getTokenString().intern());
		;
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

	void checkString(String expectedValue, IToken t) {
		assertTrue(t instanceof IStringLitToken);
		assertEquals(expectedValue, ((IStringLitToken) t).getValue());
	}

	void checkString(String expectedChars, String expectedValue, IToken.SourceLocation expectedLocation, IToken t) {
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

	void checkNUM_LIT(int expectedValue, IToken.SourceLocation expectedLocation, IToken t) {
		checkToken(Kind.NUM_LIT, t);
		int value = ((INumLitToken) t).getValue();
		assertEquals(expectedValue, value);
		assertEquals(expectedLocation, t.getSourceLocation());
	}

	// check that this token has the expected kind
	void checkToken(Kind expectedKind, IToken t) {
		assertEquals(expectedKind, t.getKind());
	}

	void checkToken(Kind expectedKind, String expectedChars, IToken.SourceLocation expectedLocation, IToken t) {
		assertEquals(expectedKind, t.getKind());
		assertEquals(expectedChars, t.getTokenString());
		assertEquals(expectedLocation, t.getSourceLocation());
		;
	}


	// check that this token is the EOF token
	void checkEOF(IToken t) {
		checkToken(Kind.EOF, t);
	}


	/** Indicates whether show should generate output*/
	static final boolean VERBOSE = true;

	/**
	 * Prints obj to console if VERBOSE.  This is easier to type than System.out.println and makes it easy to disable output.
	 *
	 * @param obj
	 */
	void show(Object obj) {
		if (VERBOSE) {
			System.out.println(obj);
		}
	}

	/** Constructs a scanner and parser for the given input string, scans and parses the input and returns and AST.
	 *
	 * @param input   String representing program to be tested
	 * @return  AST representing the program
	 * @throws PLCException
	 */
	AST getAST(String input) throws  PLCException {
		return  CompilerComponentFactory.makeAssignment2Parser(input).parse();
	}

	/**
	 * Checks that the given AST e has type NumLitExpr with the indicated value.  Returns the given AST cast to NumLitExpr.
	 *
	 * @param e
	 * @param value
	 * @return
	 */
	NumLitExpr checkNumLit(AST e, int value) {
		assertThat("",e, instanceOf( NumLitExpr.class));
		NumLitExpr ne = (NumLitExpr)e;
		assertEquals(value, ne.getValue());
		return ne;
	}

	/**
	 *  Checks that the given AST e has type StringLitExpr with the given String value.  Returns the given AST cast to StringLitExpr.
	 * @param e
	 * @param name
	 * @return
	 */
	StringLitExpr checkStringLit(AST e, String value) {
		assertThat("",e, instanceOf( StringLitExpr.class));
		StringLitExpr se = (StringLitExpr)e;
		assertEquals(value,se.getValue());
		return se;
	}

	/**
	 *  Checks that the given AST e has type UnaryExpr with the given operator.  Returns the given AST cast to UnaryExpr.
	 * @param e
	 * @param op  Kind of expected operator
	 * @return
	 */
	private UnaryExpr checkUnary(AST e, Kind op) {
		assertThat("",e, instanceOf( UnaryExpr.class));
		assertEquals(op, ((UnaryExpr)e).getOp());
		return (UnaryExpr)e;
	}


	/**
	 *  Checks that the given AST e has type ConditionalExpr.  Returns the given AST cast to ConditionalExpr.
	 * @param e
	 * @return
	 */
	private ConditionalExpr checkConditional(AST e) {
		assertThat("",e, instanceOf( ConditionalExpr.class));
		return (ConditionalExpr)e;
	}

	/**
	 *  Checks that the given AST e has type BinaryExpr with the given operator.  Returns the given AST cast to BinaryExpr.
	 *
	 * @param e
	 * @param op  Kind of expected operator
	 * @return
	 */
	BinaryExpr checkBinary(AST e, Kind expectedOp) {
		assertThat("",e, instanceOf(BinaryExpr.class));
		BinaryExpr be = (BinaryExpr)e;
		assertEquals(expectedOp, be.getOp());
		return be;
	}

/**
 * Checks that the given AST e has type IdentExpr with the given name.  Returns the given AST cast to IdentExpr.
 * @param e
 * @param name
 * @return
 */
	IdentExpr checkIdent(AST e, String name) {
		assertThat("",e, instanceOf( IdentExpr.class));
		IdentExpr ident = (IdentExpr)e;
		assertEquals(name,ident.getName());
		return ident;
	}

	//PASSED
	@Test
	void emptyProgram() throws PLCException {
		String input = "";  //no empty expressions, this program should throw a SyntaxException
		assertThrows(SyntaxException.class, () -> {
			getAST(input);
		});
	}

	//PASSED
	@Test
	void numLit() throws PLCException {
		String input= "3";
		checkNumLit(getAST(input),3);
	}

	//PASSED
	@Test
	void stringLit() throws PLCException {
		String input= "\"Go Gators\" ";
		checkStringLit(getAST(input), "Go Gators");
	}


	//PASSED
	@Test
	void Z() throws PLCException {
		String input = " Z  ";
		AST e = getAST(input);
		assertThat("",e, instanceOf( ZExpr.class));
	}

	//PASSED
	@Test
	void rand() throws PLCException {
		String input = "  rand";
		Expr e = (Expr) getAST(input);
		assertEquals(1,e.getLine());
		assertEquals(3, e.getColumn());
		assertThat("",e, instanceOf( RandomExpr.class));
	}

	//PASSED
	@Test
	void primary() throws PLCException {
		String input = " (3) ";
		Expr e = (Expr) getAST(input);
		checkNumLit(e,3);
	}

	@Test
	void primary1() throws PLCException {
		String input = " (b) "; // Ident
		Expr e = (Expr) getAST(input);
		checkIdent(e, "b");
	}


	//PASSED
@Test
void unary1()
	throws PLCException {
		String input = " -3 ";
		UnaryExpr ue = checkUnary(getAST(input), Kind.MINUS);
		checkNumLit(ue.getE(),3);
	}


	//PASSED
@Test
void unary2()
	throws PLCException {
		String input = " cos atan ! - \"hello\" ";
		UnaryExpr ue0 = checkUnary(getAST(input), Kind.RES_cos);
		UnaryExpr ue1 = checkUnary(ue0.getE(), Kind.RES_atan);
		UnaryExpr ue2 = checkUnary(ue1.getE(),Kind.BANG);
		UnaryExpr ue3 = checkUnary(ue2.getE(), Kind.MINUS);
		checkStringLit(ue3.getE(), "hello");
	}

	@Test
	void unary3() throws PLCException {
		String input = "cos(3)";
		UnaryExpr ue = checkUnary(getAST(input), Kind.RES_cos);
		checkNumLit(ue.getE(), 3);
	}

	//PASSED
@Test void ident() throws PLCException {
	String input = "b";
	checkIdent(getAST(input),"b");
}

	//PASSED
@Test void binary0() throws PLCException {
	String input = "b+2";
	BinaryExpr binary = checkBinary(getAST(input),Kind.PLUS);
	checkIdent(binary.getLeft(),"b");
	checkNumLit(binary.getRight(),2);
}

	//PASSED
@Test void binary1() throws PLCException {
	String input = "1-2+3*4/5%6";  //   (1-2) +  (((3  * 4)  /  5) % 6)

	BinaryExpr be0 = checkBinary(getAST(input), Kind.PLUS); // (1-2) + (3*4/5%6)

	BinaryExpr be0l = checkBinary(be0.getLeft(),Kind.MINUS); // 1-2
	checkNumLit(be0l.getLeft(),1);
	checkNumLit(be0l.getRight(),2);

	BinaryExpr be0r = checkBinary(be0.getRight(),Kind.MOD);  //(3*4/5)%6
	checkNumLit(be0r.getRight(),6);

	BinaryExpr be0rl = checkBinary(be0r.getLeft(),Kind.DIV );  //(3*4)/5
	checkNumLit(be0rl.getRight(),5);  // 5

    BinaryExpr be0rll = checkBinary(be0rl.getLeft(), Kind.TIMES); // 3*4
	checkNumLit(be0rll.getLeft(),3);
	checkNumLit(be0rll.getRight(),4);
}

	@Test
	void binary2() throws PLCException {
		String input = "2 + 3 * 4 - 5 / 6"; // 2 + (3 * 4) - (5 / 6)
		BinaryExpr be0 = checkBinary(getAST(input), Kind.MINUS);
		BinaryExpr be0l = checkBinary(be0.getLeft(), Kind.PLUS);
		checkNumLit(be0l.getLeft(), 2);
		BinaryExpr be0r = checkBinary(be0.getRight(), Kind.DIV);
		checkNumLit(be0r.getLeft(), 5);
		checkNumLit(be0r.getRight(), 6);
		BinaryExpr be0ll = checkBinary(be0l.getRight(), Kind.TIMES);
		checkNumLit(be0ll.getLeft(), 3);
		checkNumLit(be0ll.getRight(), 4);
	}

	@Test
	void binary3() throws PLCException {
		String input = "b + c - d * e"; // Variables in expression [(b + c) - (d * e)]
		BinaryExpr be0 = checkBinary(getAST(input), Kind.MINUS);
		BinaryExpr be0l = checkBinary(be0.getLeft(), Kind.PLUS);
		checkIdent(be0l.getLeft(), "b");
		checkIdent(be0l.getRight(), "c");
		BinaryExpr be0r = checkBinary(be0.getRight(), Kind.TIMES);
		checkIdent(be0r.getLeft(), "d");
		checkIdent(be0r.getRight(), "e");
	}

	@Test
	void binary4() throws PLCException {
		String input = "2 * (3 + 4)"; // Parentheses present
		BinaryExpr be = checkBinary(getAST(input), Kind.TIMES);
		checkNumLit(be.getLeft(), 2);
		BinaryExpr beR = checkBinary(be.getRight(), Kind.PLUS);
		checkNumLit(beR.getLeft(), 3);
		checkNumLit(beR.getRight(), 4);
	}

	@Test
	void binary6() throws PLCException {
		String input = "true && false || true"; // AND an OR [(true && false || true)]
		BinaryExpr be0 = checkBinary(getAST(input), Kind.OR);
		BinaryExpr be0l = checkBinary(be0.getLeft(), Kind.AND);
		checkIdent(be0l.getLeft(), "true");
		checkIdent(be0l.getRight(), "false");
		checkIdent(be0.getRight(), "true");
	}

	@Test
	void binary7() throws PLCException {
		String input = "3 & 2 | 1"; // BITAND and BITOR [(3 & 2) | 1]
		BinaryExpr be0 = checkBinary(getAST(input), Kind.BITOR);
		BinaryExpr be0l = checkBinary(be0.getLeft(), Kind.BITAND);
		checkNumLit(be0l.getLeft(), 3);
		checkNumLit(be0l.getRight(), 2);
		checkNumLit(be0.getRight(), 1);
	}

	@Test
	void binary8() throws PLCException {
		String input = "3 & 2 && 1"; // BITAND and AND [(3 & 2) && 1]
		BinaryExpr be0 = checkBinary(getAST(input), Kind.AND);
		BinaryExpr be0l = checkBinary(be0.getLeft(), Kind.BITAND);
		checkNumLit(be0l.getLeft(), 3);
		checkNumLit(be0l.getRight(), 2);
		checkNumLit(be0.getRight(), 1);
	}

	@Test
	void binary9() throws PLCException {
		String input = "1 || 2 | 3 & 4 && 5 && 6"; // (1||2) | (((3 & 4) && 5) && 6)

		BinaryExpr be0 = checkBinary(getAST(input), Kind.BITOR); // (1 || 2) | (3 & 4 && 5 &&6)
		BinaryExpr be0l = checkBinary(be0.getLeft(), Kind.OR); // 1 || 2
		checkNumLit(be0l.getLeft(), 1);
		checkNumLit(be0l.getRight(), 2);

		BinaryExpr be0r = checkBinary(be0.getRight(), Kind.AND); // (3 & 4 && 5) && 6
		checkNumLit(be0r.getRight(), 6);

		BinaryExpr be0rl = checkBinary(be0r.getLeft(), Kind.AND); // (3 & 4) && 5
		checkNumLit(be0rl.getRight(), 5); // 5

		BinaryExpr be0rll = checkBinary(be0rl.getLeft(), Kind.BITAND); // 3 & 4
		checkNumLit(be0rll.getLeft(), 3);
		checkNumLit(be0rll.getRight(), 4);
	}

	@Test
	void binary10() throws PLCException {
		String input = "5 || 3 & 2 & 1 & 4 | 2"; // OR and BITAND [(5 || (((3 & 2) & 1) & 4)) | 2]
		BinaryExpr be0 = checkBinary(getAST(input), Kind.BITOR);
		BinaryExpr be1 = checkBinary(be0.getLeft(), Kind.OR);
		BinaryExpr be2 = checkBinary(be1.getRight(), Kind.BITAND);
		BinaryExpr be3 = checkBinary(be2.getLeft(), Kind.BITAND);
		BinaryExpr be4 = checkBinary(be3.getLeft(), Kind.BITAND);
		checkNumLit(be1.getLeft(), 5);
		checkNumLit(be0.getRight(), 2);
		checkNumLit(be2.getRight(), 4);
		checkNumLit(be3.getRight(), 1);
		checkNumLit(be4.getRight(), 2);
		checkNumLit(be4.getLeft(), 3);
	}

	@Test
	void binary11() throws PLCException {
		String input = "2 & 3 | 4 && 5 & 6 & 7"; // BITAND, BITOR, and AND [((2 & 3) | ((4 && 5) & 6) & 7)]
		BinaryExpr be0 = checkBinary(getAST(input), Kind.BITOR);
		BinaryExpr be1 = checkBinary(be0.getLeft(), Kind.BITAND);
		BinaryExpr be2 = checkBinary(be0.getRight(), Kind.BITAND);
		BinaryExpr be3 = checkBinary(be2.getLeft(), Kind.BITAND);
		BinaryExpr be4 = checkBinary(be3.getLeft(), Kind.AND);
		checkNumLit(be1.getLeft(), 2);
		checkNumLit(be1.getRight(), 3);
		checkNumLit(be2.getRight(), 7);
		checkNumLit(be3.getRight(), 6);
		checkNumLit(be4.getLeft(), 4);
		checkNumLit(be4.getRight(), 5);
	}

	@Test
	void powerExpression1() throws PLCException {
		String input = "2 ** 3 - 1 * 5"; // [2 ** (3 - (1 * 5))]
		BinaryExpr be = checkBinary(getAST(input), Kind.EXP);
		BinaryExpr be0 = checkBinary(be.getRight(), Kind.MINUS);
		BinaryExpr be1 = checkBinary(be0.getRight(), Kind.TIMES);
		checkNumLit(be.getLeft(), 2);
		checkNumLit(be0.getLeft(), 3);
		checkNumLit(be1.getLeft(), 1);
		checkNumLit(be1.getRight(), 5);
	}

	//PASSED
@Test void conditional0() throws PLCException {
	String input = " if d ? e ? f";
	ConditionalExpr ce = checkConditional(getAST(input));
	checkIdent(ce.getGuard(),"d");
	checkIdent(ce.getTrueCase(),"e");
	checkIdent(ce.getFalseCase(),"f");
}


	//PASSED
@Test void conditional1() throws PLCException {
	String input = """
			if if 3 ? 4 ? 5 ? if 6 ? 7 ? 8 ? if 9 ? 10 ? 11
			""";
	ConditionalExpr ce = checkConditional(getAST(input));
	ConditionalExpr guard = checkConditional(ce.getGuard());
	ConditionalExpr trueCase = checkConditional(ce.getTrueCase());
	ConditionalExpr falseCase = checkConditional(ce.getFalseCase());

	checkNumLit(guard.getGuard(),3);
	checkNumLit(guard.getTrueCase(),4);
	checkNumLit(guard.getFalseCase(),5);

	checkNumLit(trueCase.getGuard(),6);
	checkNumLit(trueCase.getTrueCase(),7);
	checkNumLit(trueCase.getFalseCase(),8);

	checkNumLit(falseCase.getGuard(),9);
	checkNumLit(falseCase.getTrueCase(),10);
	checkNumLit(falseCase.getFalseCase(),11);
}

	@Test
	void conditional2() throws PLCException {
		String input = " if 3-1 ? 5*2 ? 4+3";
		ConditionalExpr ce = checkConditional(getAST(input));

		BinaryExpr be0 = checkBinary(ce.getGuard(), Kind.MINUS);
		BinaryExpr be1 = checkBinary(ce.getTrueCase(), Kind.TIMES);
		BinaryExpr be2 = checkBinary(ce.getFalseCase(), Kind.PLUS);
		checkNumLit(be0.getLeft(), 3);
		checkNumLit(be0.getRight(), 1);
		checkNumLit(be1.getLeft(), 5);
		checkNumLit(be1.getRight(), 2);
		checkNumLit(be2.getLeft(), 4);
		checkNumLit(be2.getRight(), 3);
	}


	//PASSED
@Test void error0() throws PLCException {
	String input = "b + + 2";
	assertThrows(SyntaxException.class, () -> {
		getAST(input);
	});
}

	//PASSED
@Test void error1() throws PLCException {
	String input = "3 @ 4"; //this should throw a LexicalException
	assertThrows(LexicalException.class, () -> {
		getAST(input);
	});
}

	@Test
	void error2() throws PLCException {
		String input = "(3 + 4"; // No closing parenthesis
		assertThrows(SyntaxException.class, () -> {
			getAST(input);
		});
	}

	@Test
	void error3() throws PLCException {
		String input = " if d ? e"; // Incomplete conditional expression
		assertThrows(SyntaxException.class, () -> {
			getAST(input);
		});
	}

// ASSIGNMENT 1 TEST CASES (80 TESTS)

	//PASSED
	// test 1: empty program
	@Test
	void emptyProg() throws LexicalException {
		String input = "";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkEOF(scanner.next());
	}

	//PASSED
	// test 2: only white space
	@Test
	void onlyWhiteSpace() throws LexicalException {
		String input = " \t \r\n \f \n";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkEOF(scanner.next());
		checkEOF(scanner.next());  //repeated invocations of next after end reached should return EOF token
	}

	//PASSED
	// test 3: number literals 1
	@Test
	void numLits1() throws LexicalException {
		String input = """
				123
				05 240
				1+2
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkNUM_LIT(123, scanner.next());
		checkNUM_LIT(0, scanner.next());
		checkNUM_LIT(5, scanner.next());
		checkNUM_LIT(240, scanner.next());

		checkNUM_LIT(1, scanner.next());
		checkToken(Kind.PLUS,scanner.next());
		checkNUM_LIT(2, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 4: equals
	@Test
	void equals() throws LexicalException{
		String input = """
    ==
    == ==
    ==-==
    ====
    """;
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.MINUS,scanner.next());
		checkToken(Kind.EQ,scanner.next());
		checkToken(Kind.EQ,scanner.next());
	}

	//PASSED
	// test 5: single character tokens 0
	@Test
	void singleCharTokens() throws LexicalException{
		String input = "+00";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.PLUS,scanner.next());
		checkToken(Kind.NUM_LIT,scanner.next());
		checkToken(Kind.NUM_LIT,scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 6: single character tokens 1
	@Test
	void singleCharTokens1() throws LexicalException{
		String input = ".,?:()[]{}";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.DOT,scanner.next());
		checkToken(Kind.COMMA,scanner.next());
		checkToken(Kind.QUESTION,scanner.next());
		checkToken(Kind.COLON,scanner.next());
		checkToken(Kind.LPAREN,scanner.next());
		checkToken(Kind.RPAREN,scanner.next());
		checkToken(Kind.LSQUARE,scanner.next());
		checkToken(Kind.RSQUARE,scanner.next());
		checkToken(Kind.LCURLY,scanner.next());
		checkToken(Kind.RCURLY,scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 7: single character tokens 2
	@Test
	void singleCharTokens2() throws LexicalException{
		String input = "+-/%";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.PLUS,scanner.next());
		checkToken(Kind.MINUS,scanner.next());
		checkToken(Kind.DIV,scanner.next());
		checkToken(Kind.MOD,scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 8: single character tokens with white space
	@Test
	void singleCharTokensWithWhiteSpace() throws LexicalException{
		String input = """
    + %
    0
    0
    """;
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.PLUS,scanner.next());
		checkToken(Kind.MOD,scanner.next());
		checkToken(Kind.NUM_LIT,scanner.next());
		checkToken(Kind.NUM_LIT,scanner.next());
		checkEOF(scanner.next());
	}

	// test 9: identifiers 0
	@Test
	void idents0() throws LexicalException {
		String input = """
				i0
				i1
				""";

		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.IDENT,"i0", new IToken.SourceLocation(1,1), scanner.next());
		checkToken(Kind.IDENT, "i1",new IToken.SourceLocation(2,1), scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 10: identifiers and reserved
	@Test
	void identsAndReserved() throws LexicalException {
		String input = """
				i0
				  i1  x ~~~2 spaces at beginning and after il
				y Y
				""";

		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.IDENT,"i0", new IToken.SourceLocation(1,1), scanner.next());
		checkToken(Kind.IDENT, "i1",new IToken.SourceLocation(2,3), scanner.next());
		checkToken(Kind.RES_x, "x", new IToken.SourceLocation(2,7), scanner.next());
		checkToken(Kind.RES_y, "y", new IToken.SourceLocation(3,1), scanner.next());
		checkToken(Kind.RES_Y, "Y", new IToken.SourceLocation(3,3), scanner.next());
		checkEOF(scanner.next());
	}

	//UNFINISHED, needs to be modified
	// test 11: identifiers and reserved 2
	@Test
	void identsAndReserved2() throws LexicalException {
		String input = """
				i0
				  i1  x ~~~2 spaces at beginning and after il
				  ~comment~
				  "hello there"
				sin cos
				""";

		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.IDENT,"i0", new IToken.SourceLocation(1,1), scanner.next());
		checkToken(Kind.IDENT, "i1",new IToken.SourceLocation(2,3), scanner.next());
		checkToken(Kind.RES_x, "x", new IToken.SourceLocation(2,7), scanner.next());
		checkString(input.substring(63, 76),"hello there", new IToken.SourceLocation(4,3), scanner.next());


		checkToken(Kind.RES_sin, scanner.next());
		checkToken(Kind.RES_cos, scanner.next());
		//checkToken(Kind.IDENT,"sin", new IToken.SourceLocation(5,1), scanner.next());
		//checkToken(Kind.IDENT,"cos", new IToken.SourceLocation(5,4), scanner.next());
		//checkToken(Kind.RES_sin,"sin", new IToken.SourceLocation(5,1), scanner.next());
		//checkToken(Kind.RES_cos,"cos", new IToken.SourceLocation(5,4), scanner.next());
	}

	//PASSED
	// test 12: identifiers with underscores
	@Test
	void identsWithUnderscore() throws LexicalException {
		String input = """
				i0
				i1
				_
				__
				a_b_c
				""";

		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.IDENT,"i0", new IToken.SourceLocation(1,1), scanner.next());
		checkToken(Kind.IDENT, "i1",new IToken.SourceLocation(2,1), scanner.next());

		checkToken(Kind.IDENT,"_", new IToken.SourceLocation(3,1), scanner.next());
		checkToken(Kind.IDENT, "__",new IToken.SourceLocation(4,1), scanner.next());

		checkToken(Kind.IDENT,"a_b_c", new IToken.SourceLocation(5,1), scanner.next());
	}

	//PASSED
	// test 13: operators 0
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
	// test 14: operators 1
	@Test
	void operators1() throws LexicalException {
		String input = """
				**** *
				& && &&&
				| || |||
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.EXP, scanner.next());
		checkToken(Kind.EXP, scanner.next());
		checkToken(Kind.TIMES, scanner.next());
		checkToken(Kind.BITAND, scanner.next());
		checkToken(Kind.AND, scanner.next());
		checkToken(Kind.AND, scanner.next());
		checkToken(Kind.BITAND, scanner.next());
		checkToken(Kind.BITOR, scanner.next());
		checkToken(Kind.OR, scanner.next());
		checkToken(Kind.OR, scanner.next());
		checkToken(Kind.BITOR, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 15: string literals 0, same as test case 22
	@Test
	void stringLiterals0() throws LexicalException {
		String input = """
				"hello"
				"\t"
				"\\""
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString(input.substring(0, 7),"hello", new IToken.SourceLocation(1,1), scanner.next());
		checkString(input.substring(8, 11), "\t", new IToken.SourceLocation(2,1), scanner.next());
		checkString(input.substring(12, 16), "\"",  new IToken.SourceLocation(3,1), scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 16: comment 0
	@Test
	void comment0() throws LexicalException {
		String input = """
				==
				+ ~reandomcharse!@#$%W%$^#%&$
				/
				====
				=
				~comment at begining of line
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
	// test 17: illegal escape
	@Test
	void illegalEscape() throws LexicalException {
		String input = """
				"\\t"
				"\\k"
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString("\"\\t\"","\t", new IToken.SourceLocation(1,1), scanner.next());
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}

	//PASSED
	// test 18: illegal line term in string literal
	@Test
	void illegalLineTermInStringLiteral() throws LexicalException {
		String input = """
				"\\n"  ~ this one passes the escape sequence--it is OK
				"\n"   ~ this on passes the LF, it is illegal.
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString("\"\\n\"","\n", new IToken.SourceLocation(1,1), scanner.next());
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}

	//PASSED
	// test 19: less than greater than exchange
	@Test
	void lessThanGreaterThanExchange() throws LexicalException {
		String input = """
				<->>>>=
				<<=<
				""";
		checkTokens(input, Kind.EXCHANGE, Kind.GT, Kind.GT, Kind.GE, Kind.LT, Kind.LE, Kind.LT, Kind.EOF);
	}

	//PASSED
	// test 20: illegal character
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

	//PASSED
	// test 21: number literal too big
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
	// test 22: string literals 1, same as test case 15
	@Test
	void stringLiterals1() throws LexicalException {
		String input = """
				"hello"
				"\t"
				"\\""
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString(input.substring(0, 7),"hello", new IToken.SourceLocation(1,1), scanner.next());
		checkString(input.substring(8, 11), "\t", new IToken.SourceLocation(2,1), scanner.next());
		checkString(input.substring(12, 16), "\"",  new IToken.SourceLocation(3,1), scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 23: incomplete exchange throws exception
	/** The Scanner should not backtrack so this input should throw an exception */
	@Test
	void incompleteExchangeThrowsException() throws LexicalException {
		String input = " <- ";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}

	//test 24: string escape
	@Test
	void stringEscape() throws LexicalException {
		String input = """
    "\\b \\t \\n \\r \\" \\\\"
    """;
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString(input.substring(0,19),"\b \t \n \r \" \\", new IToken.SourceLocation(1,1), scanner.next());
		checkEOF(scanner.next());
	}

	//UNFINISHED, needs to be modified
	//test 25: escape outside string
	@Test
	void escapeOutsideString() throws LexicalException {
		String input = """
				\\b \\t \\n \\r \\" \\\\
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});

	}

	//PASSED
	// test 26: operator 1
	@Test
	void operator1() throws LexicalException {
		String input = """
				.
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.DOT, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 27: operator 2
	@Test
	void operator2() throws LexicalException {
		String input = """
				,
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.COMMA, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 28: operator 3
	@Test
	void operator3() throws LexicalException {
		String input = """
				?
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.QUESTION, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 29: operator 4
	@Test
	void operator4() throws LexicalException {
		String input = """
				:
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.COLON, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 30: operator 5
	@Test
	void operator5() throws LexicalException {
		String input = """
				(
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.LPAREN, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 31: operator 6
	@Test
	void operator6() throws LexicalException {
		String input = """
				)
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RPAREN, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 32: operator 7
	@Test
	void operator7() throws LexicalException {
		String input = """
				<
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.LT, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 33: operator 8
	@Test
	void operator8() throws LexicalException {
		String input = """
				>
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.GT, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 34: operator 9
	@Test
	void operator9() throws LexicalException {
		String input = """
				[
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.LSQUARE, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 35: operator 10
	@Test
	void operator10() throws LexicalException {
		String input = """
				]
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RSQUARE, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 36: operator 11
	@Test
	void operator11() throws LexicalException {
		String input = """
				{
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.LCURLY, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 37: operator 12
	@Test
	void operator12() throws LexicalException {
		String input = """
				}
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RCURLY, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 38: operator 13
	@Test
	void operator13() throws LexicalException {
		String input = """
				=
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.ASSIGN, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 39: operator 14
	@Test
	void operator14() throws LexicalException {
		String input = """
				==
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.EQ, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 40: operator 15
	@Test
	void operator15() throws LexicalException {
		String input = """
				<->
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.EXCHANGE, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 41: operator 16
	@Test
	void operator16() throws LexicalException {
		String input = """
				<=
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.LE, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 42: operator 17
	@Test
	void operator17() throws LexicalException {
		String input = """
				>=
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.GE, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 43: operator 18
	@Test
	void operator18() throws LexicalException {
		String input = """
				!
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.BANG, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 44: operator 19
	@Test
	void operator19() throws LexicalException {
		String input = """
				&
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.BITAND, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 45: operator 20
	@Test
	void operator20() throws LexicalException {
		String input = """
				&&
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.AND, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 46: operator 21
	@Test
	void operator21() throws LexicalException {
		String input = """
				|
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.BITOR, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 47: operator 22
	@Test
	void operator22() throws LexicalException {
		String input = """
				||
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.OR, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 48: operator 23
	@Test
	void operator23() throws LexicalException {
		String input = """
				+
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.PLUS, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 49: operator 24
	@Test
	void operator24() throws LexicalException {
		String input = """
				-
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.MINUS, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 50: operator 25
	@Test
	void operator25() throws LexicalException {
		String input = """
				*
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.TIMES, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 51: operator 26
	@Test
	void operator26() throws LexicalException {
		String input = """
				**
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.EXP, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 52: operator 27
	@Test
	void operator27() throws LexicalException {
		String input = """
				/
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.DIV, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 53: operator 28
	@Test
	void operator28() throws LexicalException {
		String input = """
				%
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.MOD, scanner.next());
		checkEOF(scanner.next());
	}

	//PASSED
	// test 54: reserved 1
	@Test
	void reserved1() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				image
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_image, scanner.next());

	}

	//PASSED
	// test 55: reserved 2
	@Test
	void reserved2() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				pixel
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_pixel, scanner.next());

	}

	//PASSED
	// test 56: reserved 3
	@Test
	void reserved3() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				int
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_int, scanner.next());

	}

	//PASSED
	// test 57: reserved 4
	@Test
	void reserved4() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				string
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_string, scanner.next());

	}

	//PASSED
	// test 58: reserved 5
	@Test
	void reserved5() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				void
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_void, scanner.next());

	}

	//PASSED
	// test 59: reserved 6
	@Test
	void reserved6() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				nil
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_nil, scanner.next());

	}

	//PASSED
	// test 60: reserved 7
	@Test
	void reserved7() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				load
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_load, scanner.next());

	}

	//PASSED
	// test 61: reserved 8
	@Test
	void reserved8() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				display
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_display, scanner.next());

	}

	//PASSED
	// test 62: reserved 9
	@Test
	void reserved9() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				write
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_write, scanner.next());

	}

	//PASSED
	// test 63: reserved 10
	@Test
	void reserved10() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				x
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_x, scanner.next());

	}

	//PASSED
	// test 64: reserved 11
	@Test
	void reserved11() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				y
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_y, scanner.next());

	}

	//PASSED
	// test 65: reserved 12
	@Test
	void reserved12() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				a
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_a, scanner.next());

	}

	//PASSED
	// test 66: reserved 13
	@Test
	void reserved13() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				r
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_r, scanner.next());

	}

	//PASSED
	// test 67: reserved 14
	@Test
	void reserved14() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				X
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_X, scanner.next());

	}

	//PASSED
	// test 68: reserved 15
	@Test
	void reserved15() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				Y
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_Y, scanner.next());

	}

	//PASSED
	// test 69: reserved 16
	@Test
	void reserved16() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				Z
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_Z, scanner.next());

	}

	//PASSED
	// test 70: reserved 17
	@Test
	void reserved17() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				x_cart
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_x_cart, scanner.next());

	}

	//PASSED
	// test 71: reserved 18
	@Test
	void reserved18() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				y_cart
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_y_cart, scanner.next());

	}

	//PASSED
	// test 72: reserved 19
	@Test
	void reserved19() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				a_polar
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_a_polar, scanner.next());

	}

	//PASSED
	// test 73: reserved 20
	@Test
	void reserved20() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				r_polar
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_r_polar, scanner.next());

	}

	//PASSED
	// test 74: reserved 21
	@Test
	void reserved21() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				rand
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_rand, scanner.next());

	}

	//PASSED
	// test 75: reserved 22
	@Test
	void reserved22() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				sin
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_sin, scanner.next());

	}

	//PASSED
	// test 76: reserved 23
	@Test
	void reserved23() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				cos
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_cos, scanner.next());

	}

	//PASSED
	// test 77: reserved 24
	@Test
	void reserved24() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				atan
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_atan, scanner.next());

	}

	//PASSED
	// test 78: reserved 25
	@Test
	void reserved25() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				if
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_if, scanner.next());

	}

	//PASSED
	// test 79: reserved 26
	@Test
	void reserved26() throws LexicalException {
		/* reserved words: image | pixel | int | string | void | nil | load | display | write | x | y | a | r | X  | Y | Z |
          x_cart | y_cart | a_polar | r_polar | rand | sin | cos | atan  | if | while  */
		String input = """
				while
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.RES_while, scanner.next());

	}

	//UNFINISHED, needs to be modified
	//test 80: non terminated string
	@Test
	void nonTerminatedString() throws LexicalException {
		String input = """
				"abc
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}

	//---------------------------TEST CASES FROM DISCORD-----------------------------------------

	@Test
	void andPowerExpressions() throws PLCException {
		String input = " 2 ** 3 ** 5 "; // 2 ** (3 ** 5)
		BinaryExpr be0 = checkBinary(getAST(input), Kind.EXP);
		checkNumLit(be0.getLeft(), 2);
		BinaryExpr be1 = checkBinary(be0.getRight(), Kind.EXP);
		checkNumLit(be1.getLeft(), 3);
		checkNumLit(be1.getRight(), 5);
	}

	@Test
	void andParentheses() throws PLCException {
		String input = " ( 7 ** 11 ) ** 2 ** 3 ** 5 "; // ( 7 ** 11 ) ** (2 ** (3 ** 5))
		BinaryExpr be0 = checkBinary(getAST(input), Kind.EXP);
		BinaryExpr bel1 = checkBinary(be0.getLeft(), Kind.EXP);
		checkNumLit(bel1.getLeft(), 7);
		checkNumLit(bel1.getRight(), 11);
		BinaryExpr ber1 = checkBinary(be0.getRight(), Kind.EXP);
		checkNumLit(ber1.getLeft(), 2);
		BinaryExpr berr2 = checkBinary(ber1.getRight(), Kind.EXP);
		checkNumLit(berr2.getLeft(), 3);
		checkNumLit(berr2.getRight(), 5);
	}

	@Test
	void andMismatchedParentheses() throws PLCException {
		String input = " (((oh)) ";
		assertThrows(SyntaxException.class, () -> {
			getAST(input);
		});
	}

	@Test
	void andDeepParentheses() throws PLCException {
		String input = " ((((((((1)))))))) ";
		AST e = getAST(input);
		checkNumLit(e, 1);
	}

	@Test
	void andUnaryChain() throws PLCException {
		String input = " !-atan!--!!cos sin love";
		UnaryExpr u1 = checkUnary(getAST(input), Kind.BANG);
		UnaryExpr u2 = checkUnary(u1.getE(), Kind.MINUS);
		UnaryExpr u3 = checkUnary(u2.getE(), Kind.RES_atan);
		UnaryExpr u4 = checkUnary(u3.getE(), Kind.BANG);
		UnaryExpr u5 = checkUnary(u4.getE(), Kind.MINUS);
		UnaryExpr u6 = checkUnary(u5.getE(), Kind.MINUS);
		UnaryExpr u7 = checkUnary(u6.getE(), Kind.BANG);
		UnaryExpr u8 = checkUnary(u7.getE(), Kind.BANG);
		UnaryExpr u9 = checkUnary(u8.getE(), Kind.RES_cos);
		UnaryExpr u10 = checkUnary(u9.getE(), Kind.RES_sin);
		checkIdent(u10.getE(), "love");
	}

	@Test
	void andAMixOfOperators() throws PLCException {
		String input = " !1 + -2 - -3 * atan 4 ** 5";
		BinaryExpr e0 = checkBinary(getAST(input), Kind.EXP);
		checkNumLit(e0.getRight(), 5);
		BinaryExpr el1 = checkBinary(e0.getLeft(), Kind.MINUS);
		BinaryExpr ell2 = checkBinary(el1.getLeft(), Kind.PLUS);
		UnaryExpr elll3 = checkUnary(ell2.getLeft(), Kind.BANG);
		checkNumLit(elll3.getE(), 1);
		UnaryExpr ellr3 = checkUnary(ell2.getRight(), Kind.MINUS);
		checkNumLit(ellr3.getE(), 2);
		BinaryExpr elr2 = checkBinary(el1.getRight(), Kind.TIMES);
		UnaryExpr elrl3 = checkUnary(elr2.getLeft(), Kind.MINUS);
		checkNumLit(elrl3.getE(), 3);
		UnaryExpr elrr3 = checkUnary(elr2.getRight(), Kind.RES_atan);
		checkNumLit(elrr3.getE(), 4);
	}

	@Test
	void andLogicalOperators() throws PLCException {
		String input = "1 || (if 2 && 3 ? 4 || 5 ? 6 || 7 && 8 && 9) && 10";
		BinaryExpr e0 = checkBinary(getAST(input), Kind.OR);
		checkNumLit(e0.getLeft(), 1);
		BinaryExpr er1 = checkBinary(e0.getRight(), Kind.AND);
		checkNumLit(er1.getRight(), 10);
		ConditionalExpr erl2 = checkConditional(er1.getLeft());
		BinaryExpr erlg3 = checkBinary(erl2.getGuard(), Kind.AND);
		checkNumLit(erlg3.getLeft(), 2);
		checkNumLit(erlg3.getRight(), 3);
		BinaryExpr erlt3 = checkBinary(erl2.getTrueCase(), Kind.OR);
		checkNumLit(erlt3.getLeft(), 4);
		checkNumLit(erlt3.getRight(), 5);
		BinaryExpr erlf3 = checkBinary(erl2.getFalseCase(), Kind.OR);
		checkNumLit(erlf3.getLeft(), 6);
		BinaryExpr erlfr4 = checkBinary(erlf3.getRight(), Kind.AND);
		checkNumLit(erlfr4.getRight(), 9);
		BinaryExpr erlfrl5 = checkBinary(erlfr4.getLeft(), Kind.AND);
		checkNumLit(erlfrl5.getLeft(), 7);
		checkNumLit(erlfrl5.getRight(), 8);
	}

	@Test
	void andSomeSentence() throws PLCException {
		String input = """
			if youre- atan person | see?
			you & me? ~us?
			we- sin together ~<3
			""";
		ConditionalExpr c = checkConditional(getAST(input));
		BinaryExpr cg = checkBinary(c.getGuard(), Kind.BITOR);
		checkIdent(cg.getRight(), "see");
		BinaryExpr cgl = checkBinary(cg.getLeft(), Kind.MINUS);
		checkIdent(cgl.getLeft(), "youre");
		UnaryExpr cglr = checkUnary(cgl.getRight(), Kind.RES_atan);
		checkIdent(cglr.getE(), "person");
		BinaryExpr ct = checkBinary(c.getTrueCase(), Kind.BITAND);
		checkIdent(ct.getLeft(), "you");
		checkIdent(ct.getRight(), "me");
		BinaryExpr cf = checkBinary(c.getFalseCase(), Kind.MINUS);
		checkIdent(cf.getLeft(), "we");
		UnaryExpr cfr = checkUnary(cf.getRight(), Kind.RES_sin);
		checkIdent(cfr.getE(), "together");
	}

	//--------------------------SHARED TEST CASES BATCH 2---------------------------------

	@Test
	void powerExpression() throws PLCException {
		String input = "2**3"; // Power expression
		BinaryExpr be = checkBinary(getAST(input), Kind.EXP);
		checkNumLit(be.getLeft(), 2);
		checkNumLit(be.getRight(), 3);
	}

	@Test
	void comparisonOperators() throws PLCException {
		String input1 = "1 < 2";
		BinaryExpr be1 = checkBinary(getAST(input1), Kind.LT);
		checkNumLit(be1.getLeft(), 1);
		checkNumLit(be1.getRight(), 2);

		String input2 = "3 > 4";
		BinaryExpr be2 = checkBinary(getAST(input2), Kind.GT);
		checkNumLit(be2.getLeft(), 3);
		checkNumLit(be2.getRight(), 4);

		String input3 = "5 <= 6";
		BinaryExpr be3 = checkBinary(getAST(input3), Kind.LE);
		checkNumLit(be3.getLeft(), 5);
		checkNumLit(be3.getRight(), 6);

		String input4 = "7 >= 8";
		BinaryExpr be4 = checkBinary(getAST(input4), Kind.GE);
		checkNumLit(be4.getLeft(), 7);
		checkNumLit(be4.getRight(), 8);
	}


}

