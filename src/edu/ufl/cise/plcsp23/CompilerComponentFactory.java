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

import edu.ufl.cise.plcsp23.ast.ASTVisitor;

public class CompilerComponentFactory {
	public static IScanner makeScanner(String input) {
		//Add statement to return an instance of your scanner
		return new Scanner(input);
	}


	public static IParser makeAssignment2Parser(String input) throws LexicalException {
		Scanner scanner = new Scanner(input);
		return new Parser(scanner);
	}

	public static IParser makeParser(String input) throws LexicalException{
		Scanner scanner = new Scanner(input);
		return new Parser(scanner);
	}

	public static ASTVisitor makeTypeChecker(){
		return new TypeChecker();
	}

	public static ASTVisitor makeCodeGenerator(String param) {
		// code to instantiate a return an ASTVisitor for code generation
		return new CodeGeneration(param);
	}

}

