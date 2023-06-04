////package edu.ufl.cise.plcsp23;
////
////import static org.junit.jupiter.api.Assertions.assertEquals;
////
////import java.awt.image.BufferedImage;
////
////import org.junit.jupiter.api.Test;
////
////import edu.ufl.cise.plcsp23.ast.AST;
////import edu.ufl.cise.plcsp23.ast.Program;
////import edu.ufl.cise.plcsp23.runtime.ConsoleIO;
////import edu.ufl.cise.plcsp23.javaCompilerClassLoader.DynamicClassLoader;
////import edu.ufl.cise.plcsp23.javaCompilerClassLoader.DynamicCompiler;
////
////class Assignment5Test_starter {
////
////	Object genCodeAndRun(String input, String mypackage, Object[] params) throws Exception{
////		show("**** Input ****");
////		show(input);
////		AST ast = CompilerComponentFactory.makeParser(input).parse();
////		ast.visit(CompilerComponentFactory.makeTypeChecker(), null);
////		String name = ((Program)ast).getIdent().getName();
////		String packageName = "";
////		String code = (String) ast.visit(CompilerComponentFactory.makeCodeGenerator(packageName),null);
////		show("**** Generated Code ****");
////		show(code);
////		show("**** Output ****");
////		byte[] byteCode = DynamicCompiler.compile(name, code);
////		Object result = DynamicClassLoader.loadClassAndRunMethod(byteCode, name, "apply", params);
////		show("**** Returned Result ****");
////		show(result);
////		return result;
////	}
////
////
////
////	// makes it easy to turn output on and off (and less typing thanSystem.out.println)
////	static final boolean VERBOSE = true;
////	void show(Object obj) {
////		if (VERBOSE) {
////			System.out.println(obj);
////		}
////	}
////
////
////	@Test
////	//Demonstrates dynamic compilation and execution using a small valid Java class.
////	void testDynamicCompileAndRun() throws Exception {
////		String code = """
////				public class Class1 {
////				   public static int f(int x){
////				     return x+1;
////				   }
////				 }
////				""";
////		String className = "Class1";
////		byte[] byteCode = DynamicCompiler.compile(className, code);
////		int paramVal = 3;
////		Object[] params = {paramVal};
////		Object result = DynamicClassLoader.loadClassAndRunMethod(byteCode, className, "f", params);
////		System.out.println(result);
////		assertEquals(paramVal+1, ((Integer)result).intValue());
////	}
////
////	@Test
////	void cg0() throws Exception {
////		String input = "void f(){}";
////		Object[] params = {};
////		Object result = genCodeAndRun(input, "", params );
////		assertEquals(null, result);
////	}
////
////	@Test
////	void cg1() throws Exception {
////		String input = "int f(){:3.}";
////		Object[] params = {};
////		Object result = genCodeAndRun(input, "", params );
////		assertEquals(3,((Integer)result).intValue());
////	}
////
////	@Test
////	void cg2() throws Exception {
////		String input = """
////				int f(int aa, int bb){
////				   :aa+bb.
////				}""";
////		int aa = 5;
////		int bb = 7;
////		Object[] params = {aa,bb};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(aa+bb,((Integer)result).intValue());
////	}
////
////	@Test
////	void cg3() throws Exception {
////		String input = """
////				string f(string aa, string bb){
////				   :aa+bb.
////				}""";
////		String aa = "aa";
////		String bb = "bb";
////		Object[] params = {aa,bb};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(aa+bb, result);
////	}
////
////	@Test
////	void cg5a() throws Exception{
////		String input= """
////				string c(int val){
////				   string ss = if val > 0 ? "greater" ? "not greater".
////				   :ss.
////				   }
////				""";
////		int v = -2;
////		Object[] params = {v};
////		Object result = genCodeAndRun(input,"",params);
////		assertEquals(v>0?"greater":"not greater", result);
////	}
////
////	@Test
////	void cg5b() throws Exception{
////		String input= """
////				string c(int val){
////				   string ss = if val > 0 ? "greater" ? "not greater".
////				   :ss.
////				   }
////				""";
////		int v = 2;
////		Object[] params = {v};
////		Object result = genCodeAndRun(input,"",params);
////		assertEquals(v>0?"greater":"not greater", result);
////	}
////
////	@Test
////	void cg5c() throws Exception{
////		String input= """
////				string c(int val){
////				   string ss = if val > 0 ? "greater" ? "not greater".
////				   :ss.
////				   }
////				""";
////		int v = 0;
////		Object[] params = {v};
////		Object result = genCodeAndRun(input,"",params);
////		assertEquals(v>0?"greater":"not greater", result);
////	}
////
////	@Test
////	void cg6() throws Exception{
////		String input= """
////				string c(int val){
////				   string ss = if val > 0 ? "greater" ? "not greater".
////				   :ss.
////				   }
////				""";
////		int v = 2;
////		Object[] params = {v};
////		Object result = genCodeAndRun(input,"",params);
////		show(result);
////		assertEquals("greater", result);
////	}
////
////	@Test
////	void cg7() throws Exception{
////		String input = """
////				void d(int val){
////				int vv = val/2.
////				write vv.
////				}
////				""";
////		int v = 4;
////		Object[] params = {v};
////		Object result = genCodeAndRun(input,"",params);
////		//should write 2 to OUTPUT
////		assertEquals(null,result);
////	}
////
////	@Test
////	void cg8() throws Exception{
////		String input = """
////			int d(int val){
////			int aaa = val.
////			int bbb = aaa.
////			while (bbb>0) {
////			   write bbb.
////			   bbb = bbb - 1.
////			   }.
////			   :bbb.
////			   }
////				""";
////		int v = 4;
////		Object[] params = {v};
////		int result = (Integer)genCodeAndRun(input,"",params);
////		//should write
////		// 4
////		// 3
////		// 2
////		// 1
////		// to OUTPUT
////		assertEquals(0, result);
////	}
////
////	@Test
////	void cg9a() throws Exception{
////		String input = """
////				int BBoolean(int xx){
////				int yy = xx>=0.
////				int z = yy+1.
////				z = z-1.
////				: if z ? z ? 0.
////				}
////				""";
////		int v = 1;
////		Object[] params = {v};
////		int result = (Integer)genCodeAndRun(input,"",params);
////		assertEquals(1,result);
////	}
////
////	@Test
////	void cg9b() throws Exception{
////		String input = """
////				int BBoolean(int xx){
////				int yy = xx>=0.
////				int z = yy+1.
////				z = z-1.
////				: if z ? z ? 0.
////				}
////				""";
////		int v = -1;
////		Object[] params = {v};
////		int result = (Integer)genCodeAndRun(input,"",params);
////		assertEquals(0,result);
////	}
////
////	//Faulty Test
//////	@Test
//////	void cg10() throws Exception{
//////		String input = """
//////				int testWhile(int val){
//////				int aa = val.
//////				int g = aa.
//////				write val.
//////				while (!(g == 0)) {
//////				  int aa = val/2.
//////				  write "outer loop:  aa=".
//////				  write aa.
//////				  g = (aa%2==0).
//////				  val = val-1.
//////				  while (val > 0){
//////				     int aa = val/5.
//////				     write "inner loop:  aa=".
//////				     write aa.
//////				     val = 0.
//////				  }.
//////				  write "outer loop after inner loop:  aa=".
//////				  write aa.
//////				}.
//////				: aa.
//////				}
//////				""";
//////		int v = 100;
//////		Object[] params = {v};
//////		Object result = genCodeAndRun(input,"",params);
//////	}
////
////	@Test
////	void cg10Updated() throws Exception{
////		String input = """
////				int testWhile(int val){
////				int aa = val.
////				int g = aa.
////				write val.
////				while (g == 1) {
////				  int aa = val/2.
////				  write "outer loop:  aa=".
////				  write aa.
////				  g = (aa%2==0).
////				  val = val-1.
////				  while (val > 0){
////				     int aa = val/5.
////				     write "inner loop:  aa=".
////				     write aa.
////				     val = 0.
////				  }.
////				  write "outer loop after inner loop:  aa=".
////				  write aa.
////				}.
////				: aa.
////				}
////				""";
////		int v = 100;
////		Object[] params = {v};
////		Object result = genCodeAndRun(input,"",params);
////		assertEquals(100,result);
////
////	}
////
////	@Test
////	void exponentiation() throws Exception{
////		String input = """
////				int t(int val){
////				int vv = val.
////				int yy = 3.
////				int zz = 2**3.
////				:zz.
////				}
////				""";
////		int v = 2;
////		Object[] params = {v};
////		int result = (Integer)genCodeAndRun(input,"",params);
////		assertEquals(8,result);
////	}
////
////	@Test
////	void randomAndZ() throws Exception{
////		String input = """
////				int t(int val){
////				int vv = val.
////				int yy = Z.
////				int zz = rand.
////				write zz.
////				:yy.
////				}
////				""";
////		int v = 2;
////		Object[] params = {v};
////		int result = (Integer)genCodeAndRun(input,"",params);
////		//should write
////		// integer from 0 to 255
////		// to OUTPUT
////		assertEquals(255,result);
////	}
////
////	@Test
////	void cg11() throws Exception{
////		String input = """
//// int testWhile(int val){
//// int aa = val.
//// int g = aa.
//// write val.
//// while (g > 0) {
//// int aa = val/2.
//// write "outer loop: aa=".
//// write aa.
//// g = (aa%2==0).
//// val = val-1.
//// while (val > 0){
//// int aa = val/5.
//// write "inner loop: aa=".
//// write aa.
//// val = 0.
//// }.
//// write "outer loop after inner loop: aa=".
//// write aa.
//// }.
//// : aa.
//// }
////""";
////		int v = 100;
////		Object[] params = {v};
////		int result = (int) genCodeAndRun(input,"",params);
////		assertEquals(v, (Integer)result);
////
////	}
////
////
////	@Test
////	void andWhatIsUp() throws Exception {
////		String input = "int up(int up){ :up. }";
////		Object[] params = { 1 };
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(1, result);
////	}
////
////	@Test
////	void andReturnString() throws Exception {
////		String input = """
////				string fun() { :"Hello, World!". }
////				""";
////		Object[] params = {};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals("Hello, World!", (String) result);
////	}
////
////	@Test
////	void andAddingStrings() throws Exception {
////		String input = """
////				string fun(string start) {
////					string end = ", World!".
////					:start + end. }
////				""";
////		Object[] params = { "Hello" };
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals("Hello, World!", (String) result);
////		params[0] = "Goodbye";
////		result = genCodeAndRun(input, "", params);
////		assertEquals("Goodbye, World!", (String) result);
////	}
////
////	@Test
////	void andBasicMath() throws Exception {
////		String input = """
////				int fun(int num1, int num2) {
////					int num3 = num1 + num2.
////					: num3.
////				}
////				""";
////		int num1 = 5;
////		int num2 = 2;
////		Object[] params = { num1, num2 };
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(num1 + num2, result);
////		input = """
////				int fun(int num1, int num2) {
////					int num3 = num1 - num2.
////					: num3.
////				}
////				""";
////		result = genCodeAndRun(input, "", params);
////		assertEquals(num1 - num2, result);
////		input = """
////				int fun(int num1, int num2) {
////					int num3 = num1 * num2.
////					: num3.
////				}
////				""";
////		result = genCodeAndRun(input, "", params);
////		assertEquals(num1 * num2, result);
////		input = """
////				int fun(int num1, int num2) {
////					int num3 = num1 / num2.
////					: num3.
////				}
////				""";
////		result = genCodeAndRun(input, "", params);
////		assertEquals(num1 / num2, result);
////		input = """
////				int fun(int num1, int num2) {
////					int num3 = num1 % num2.
////					: num3.
////				}
////				""";
////		result = genCodeAndRun(input, "", params);
////		assertEquals(num1 % num2, result);
////		input = """
////				int fun(int num1, int num2) {
////					int num3 = num1 ** num2.
////					: num3.
////				}
////				""";
////		result = genCodeAndRun(input, "", params);
////		assertEquals((int) Math.pow(num1, num2), result);
////	}
////
////	@Test
////	void andLogicalOps() throws Exception {
////		String input = """
////				int fun(int num1, int num2) {
////					int num3 = num1 || num2.
////					: num3.
////				}
////				""";
////		Object[] params = { 1, 0 };
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(1, result);
////		input = """
////				int fun(int num1, int num2) {
////					int num3 = num1 && num2.
////					: num3.
////				}
////				""";
////		result = genCodeAndRun(input, "", params);
////		assertEquals(0, result);
////	}
////
////	@Test
////	void andComparisonOps() throws Exception {
////		String input = """
////				int fun() {	: 1 > 0. }
////				""";
////		Object[] params = {};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(1, result);
////		input = """
////				int fun() {	: 1 < 0. }
////				""";
////		result = genCodeAndRun(input, "", params);
////		assertEquals(0, result);
////		input = """
////				int fun() {	: 1 <= 0. }
////				""";
////		result = genCodeAndRun(input, "", params);
////		assertEquals(0, result);
////		input = """
////				int fun() {	: 1 >= 0. }
////				""";
////		result = genCodeAndRun(input, "", params);
////		assertEquals(1, result);
////		input = """
////				int fun() {	: 0 <= 0. }
////				""";
////		result = genCodeAndRun(input, "", params);
////		assertEquals(1, result);
////		input = """
////				int fun() {	: 0 >= 0. }
////				""";
////		result = genCodeAndRun(input, "", params);
////		assertEquals(1, result);
////		input = """
////				int fun() {	: 0 < 0. }
////				""";
////		result = genCodeAndRun(input, "", params);
////		assertEquals(0, result);
////		input = """
////				int fun() {	: 0 > 0. }
////				""";
////		result = genCodeAndRun(input, "", params);
////		assertEquals(0, result);
////	}
////
////	@Test
////	void andIfExpr() throws Exception {
////		String input = """
////				string fun(int num) { : if num ? "true" ? "false". }
////				""";
////		Object[] params = { 1 };
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals("true", (String) result);
////		params[0] = 0;
////		result = genCodeAndRun(input, "", params);
////		assertEquals("false", (String) result);
////		params[0] = -1;
////		result = genCodeAndRun(input, "", params);
////		assertEquals("true", (String) result);
////		params[0] = 123456;
////		result = genCodeAndRun(input, "", params);
////		assertEquals("true", (String) result);
////	}
////
////	@Test
////	void andMultipleDeclarations() throws Exception {
////		String input = """
////				void fun(int fun) {
////					fun = 0.
////					while (fun) {
////						int fun = 0.
////						while (fun) {
////							int fun = 0.
////							while (fun) {
////								string fun = "".
////							}.
////						}.
////						fun = 0.
////						while (fun) {
////							string fun = "".
////						}.
////					}.
////
////				}
////				""";
////		Object[] params = { 0 };
////		genCodeAndRun(input, "", params);
////	}
////
////	@Test
////	void andSeparateVariables() throws Exception {
////		String input = """
////				int fun() {
////					int val = 5.
////					int i = 0.
////					while (i < 1) {
////						int val = 3.
////						i = i + 1.
////					}.
////					: val.
////				}
////				""";
////		Object[] params = {};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(5, result);
////	}
////
////	@Test
////	void andAddingInALoop() throws Exception {
////		String input = """
////				int fun() {
////					int val = 0.
////					int i = 0.
////					while (i <= 6) {
////						val = val + i.
////						i = i + 1.
////					}.
////					: val.
////				}
////				""";
////		Object[] params = {};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(21, result);
////	}
////
////	@Test
////	void andItsBinary() throws Exception {
////		String input = """
////				string fun(int num) {
////					string result = "".
////					while (num > 0) {
////						result = (if num % 2 == 0 ? "0" ? "1") + result.
////						num = num / 2.
////					}.
////					: result.
////				}
////				""";
////		int num = 3563;
////		Object[] params = { num };
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(Integer.toBinaryString(num), (String) result);
////	}
////
////	@Test
////	void andReturnIntAsString() throws Exception {
////		String input = """
////				string fun() { :1. }
////				""";
////		Object[] params = {};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals("1", (String) result);
////	}
////
////	@Test
////	void andAssignIntToString() throws Exception {
////		String input = """
////				string fun() {
////					string result.
////					result = 1.
////					: result.
////				}
////				""";
////		Object[] params = {};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals("1", (String) result);
////	}
////
////	@Test
////	void andDeclareStringWithInt() throws Exception {
////		String input = """
////				string fun() {
////					string result = 1.
////					: result.
////				}
////				""";
////		Object[] params = {};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals("1", (String) result);
////	}
////
////	@Test
////	void custom2() throws Exception {
////		String input = """
////                int test(int val){
////                int aa = 4.
////                while (aa > 0) {
////                    aa = aa - 1.
////                    val = val * 2.
////                }.
////                : val.
////                }
////                """;
////		int v = 5;
////		Object[] params = {v};
////		int result = (int) genCodeAndRun(input, "", params);
////		assertEquals(80, (Integer) result);
////	}
////
////	@Test
////	void custom3() throws Exception {
////		String input = """
////                int test(int val){
////                int aa = 4.
////                int bb = 4.
////                while (aa > 0) {
////                    aa = aa - 1.
////                    while (bb > 0) {
////                        val = val * bb.
////                        bb = bb - 1.
////                    }.
////                    bb = 4.
////                }.
////                write val * 3.
////                : val.
////                }
////                """;
////		int v = 5;
////		Object[] params = {v};
////		int result = (int) genCodeAndRun(input, "", params);
////		assertEquals(1658880, (Integer) result);
////	}
////
////	@Test
////	void custom4() throws Exception {
////		String input = """
////                int test(int val,int val2){
////                int out.
////                : val ** val2.
////                }
////                """;
////		int v = 5;
////		int v2 = 4;
////		Object[] params = {v, v2};
////		int result = (int) genCodeAndRun(input, "", params);
////		assertEquals(625, (Integer) result);
////	}
////
////	@Test
////	void custom5a() throws Exception {
////		String input = """
////                int test(int val,int val2){
////                :if (val > val2) ? val ? val2.
////                }
////                """;
////		int v = 5;
////		int v2 = 4;
////		Object[] params = {v, v2};
////		int result = (int) genCodeAndRun(input, "", params);
////		assertEquals(5, (Integer) result);
////	}
////
////	@Test
////	void custom5b() throws Exception {
////		String input = """
////                int test(int val,int val2){
////                :if (val > val2) ? val ? val2.
////                }
////                """;
////		int v = 5;
////		int v2 = 6;
////		Object[] params = {v, v2};
////		int result = (int) genCodeAndRun(input, "", params);
////		assertEquals(6, (Integer) result);
////	}
////
////	@Test
////	void custom5c() throws Exception {
////		String input = """
////                int test(int val,int val2){
////                :if (val == val2) ? val ? val2.
////                }
////                """;
////		int v = 5;
////		int v2 = 10;
////		Object[] params = {v, v2};
////		int result = (int) genCodeAndRun(input, "", params);
////		assertEquals(10, (Integer) result);
////	}
////
////	@Test
////	void custom6() throws Exception {
////		String input = """
////                string test(string val){
////                :val + "test".
////                }
////                """;
////		String v = "yurr";
////		Object[] params = {v};
////		String result = (String) genCodeAndRun(input, "", params);
////		assertEquals("yurrtest",  result);
////	}
////
////	@Test
////	void custom7() throws Exception {
////		String input = """
////                int test(){
////                int aa = rand.
////                write aa.
////                :aa.
////                }
////                """;
////		Object[] params = {};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(Integer.class, result.getClass());
////	}
////
////	@Test
////	void custom8() throws Exception {
////		//ZExpr always returns 255 per the spec
////		String input = """
////                int test(){
////                :Z.
////                }
////                """;
////		Object[] params = {};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(255, (Integer) result);
////	}
////
////	@Test
////	void custom9a() throws Exception {
////		//ZExpr always returns 255 per the spec
////		String input = """
////                int test(int val,int val2){
////                :if (val > 0 && val2 > 0) ? val ? val2.
////                }
////                """;
////		int a = 1;
////		int b = 2;
////		Object[] params = {a, b};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(1, (Integer) result);
////	}
////
////	@Test
////	void custom9b() throws Exception {
////		//ZExpr always returns 255 per the spec
////		String input = """
////                int test(int val,int val2){
////                :if (val > 0 && val2 == 0) ? val ? val2.
////                }
////                """;
////		int a = 1;
////		int b = 2;
////		Object[] params = {a, b};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(2, (Integer) result);
////	}
////
////	@Test
////	void custom9c() throws Exception {
////		//ZExpr always returns 255 per the spec
////		String input = """
////                int test(int val,int val2){
////                :if (val == 0 && val2 == 0) ? val ? val2+ val.
////                }
////                """;
////		int a = 1;
////		int b = 2;
////		Object[] params = {a, b};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(3, (Integer) result);
////	}
////
////	@Test
////	void custom10a() throws Exception {
////		String input = """
////                int test(string val){
////                :val == "nottest".
////                }
////                """;
////		String a = "test";
////		Object[] params = {a};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(0,(Integer) result);
////	}
////
////	@Test
////	void custom10b() throws Exception {
////		String input = """
////                int test(string val){
////                :val == "test".
////                }
////                """;
////		String a = "test";
////		Object[] params = {a};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals(1,(Integer) result);
////	}
////
////	@Test
////	void custom10c() throws Exception {
////		String input = """
////                string test(string val){
////                string ok = if (val == "test") ? "mhm" ? "idk".
////                :ok.
////                }
////                """;
////		String a = "test";
////		Object[] params = {a};
////		Object result = genCodeAndRun(input, "", params);
////		assertEquals("mhm",(String) result);
////	}
////
////
////}
//////
//package edu.ufl.cise.plcsp23;
//
//import edu.ufl.cise.plcsp23.ast.AST;
//import edu.ufl.cise.plcsp23.ast.Program;
//import edu.ufl.cise.plcsp23.javaCompilerClassLoader.DynamicClassLoader;
//import edu.ufl.cise.plcsp23.javaCompilerClassLoader.DynamicCompiler;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class Assignment5Test_starter {
//
//	Object genCodeAndRun(String input, String mypackage, Object[] params) throws Exception {
//		show("**** Input ****");
//		show(input);
//		AST ast = CompilerComponentFactory.makeParser(input).parse();
//		ast.visit(CompilerComponentFactory.makeTypeChecker(), null);
//		String name = ((Program) ast).getIdent().getName();
//		String packageName = "";
//		String code = (String) ast.visit(CompilerComponentFactory.makeCodeGenerator(packageName), null);
//		show("**** Generated Code ****");
//		show(code);
//		show("**** Output ****");
//		byte[] byteCode = DynamicCompiler.compile(name, code);
//		Object result = DynamicClassLoader.loadClassAndRunMethod(byteCode, name, "apply", params);
//		show("**** Returned Result ****");
//		show(result);
//		return result;
//	}
//
//	// makes it easy to turn output on and off (and less typing
//	// thanSystem.out.println)
//	static final boolean VERBOSE = true;
//
//	void show(Object obj) {
//		if (VERBOSE) {
//			System.out.println(obj);
//		}
//	}
//
//	@Test
//		// Demonstrates dynamic compilation and execution using a small valid Java
//		// class.
//	void testDynamicCompileAndRun() throws Exception {
//		String code = """
//				public class Class1 {
//				   public static int f(int x){
//				     return x+1;
//				   }
//				 }
//				""";
//		String className = "Class1";
//		byte[] byteCode = DynamicCompiler.compile(className, code);
//		int paramVal = 3;
//		Object[] params = { paramVal };
//		Object result = DynamicClassLoader.loadClassAndRunMethod(byteCode, className, "f", params);
//		System.out.println(result);
//		assertEquals(paramVal + 1, ((Integer) result).intValue());
//	}
//
//	@Test
//	void cg0() throws Exception {
//		String input = "void f(){}";
//		Object[] params = {};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(null, result);
//	}
//
//	@Test
//	void cg1() throws Exception {
//		String input = "int f(){:3.}";
//		Object[] params = {};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(3, ((Integer) result).intValue());
//	}
//
//	@Test
//	void cg2() throws Exception {
//		String input = """
//				int f(int aa, int bb){
//				   :aa+bb.
//				}""";
//		int aa = 5;
//		int bb = 7;
//		Object[] params = { aa, bb };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(aa + bb, ((Integer) result).intValue());
//	}
//
//	@Test
//	void cg3() throws Exception {
//		String input = """
//				string f(string aa, string bb){
//				   :aa+bb.
//				}""";
//		String aa = "aa";
//		String bb = "bb";
//		Object[] params = { aa, bb };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(aa + bb, result);
//	}
//
//	@Test
//	void cg5a() throws Exception {
//		String input = """
//				string c(int val){
//				   string ss = if val > 0 ? "greater" ? "not greater".
//				   :ss.
//				   }
//				""";
//		int v = -2;
//		Object[] params = { v };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(v > 0 ? "greater" : "not greater", result);
//	}
//
//	@Test
//	void cg5b() throws Exception {
//		String input = """
//				string c(int val){
//				   string ss = if val > 0 ? "greater" ? "not greater".
//				   :ss.
//				   }
//				""";
//		int v = 2;
//		Object[] params = { v };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(v > 0 ? "greater" : "not greater", result);
//	}
//
//	@Test
//	void cg5c() throws Exception {
//		String input = """
//				string c(int val){
//				   string ss = if val > 0 ? "greater" ? "not greater".
//				   :ss.
//				   }
//				""";
//		int v = 0;
//		Object[] params = { v };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(v > 0 ? "greater" : "not greater", result);
//	}
//
//	@Test
//	void cg6() throws Exception {
//		String input = """
//				string c(int val){
//				   string ss = if val > 0 ? "greater" ? "not greater".
//				   :ss.
//				   }
//				""";
//		int v = 2;
//		Object[] params = { v };
//		Object result = genCodeAndRun(input, "", params);
//		show(result);
//		assertEquals("greater", result);
//	}
//
//	@Test
//	void cg7() throws Exception {
//		String input = """
//				void d(int val){
//				int vv = val/2.
//				write vv.
//				}
//				""";
//		int v = 4;
//		Object[] params = { v };
//		Object result = genCodeAndRun(input, "", params);
//		// should write 2 to OUTPUT
//		assertEquals(null, result);
//	}
//
//	@Test
//	void cg8() throws Exception {
//		String input = """
//				int d(int val){
//				int aaa = val.
//				int bbb = aaa.
//				while (bbb>0) {
//				   write bbb.
//				   bbb = bbb - 1.
//				   }.
//				   :bbb.
//				   }
//					""";
//		int v = 4;
//		Object[] params = { v };
//		int result = (Integer) genCodeAndRun(input, "", params);
//		// should write
//		// 4
//		// 3
//		// 2
//		// 1
//		// to OUTPUT
//		assertEquals(0, result);
//	}
//
//	@Test
//	void cg9a() throws Exception {
//		String input = """
//				int BBoolean(int xx){
//				int yy = xx>=0.
//				int z = yy+1.
//				z = z-1.
//				: if z ? z ? 0.
//				}
//				""";
//		int v = 1;
//		Object[] params = { v };
//		int result = (Integer) genCodeAndRun(input, "", params);
//		assertEquals(1, result);
//	}
//
//	@Test
//	void cg9b() throws Exception {
//		String input = """
//				int BBoolean(int xx){
//				int yy = xx>=0.
//				int z = yy+1.
//				z = z-1.
//				: if z ? z ? 0.
//				}
//				""";
//		int v = -1;
//		Object[] params = { v };
//		int result = (Integer) genCodeAndRun(input, "", params);
//		assertEquals(0, result);
//	}
//
//	@Test
//	void cg11() throws Exception {
//		String input = """
//				 int testWhile(int val){
//				 int aa = val.
//				 int g = aa.
//				 write val.
//				 while (g > 0) {
//				 int aa = val/2.
//				 write "outer loop: aa=".
//				 write aa.
//				 g = (aa%2==0).
//				 val = val-1.
//				 while (val > 0){
//				 int aa = val/5.
//				 write "inner loop: aa=".
//				 write aa.
//				 val = 0.
//				 }.
//				 write "outer loop after inner loop: aa=".
//				 write aa.
//				 }.
//				 : aa.
//				 }
//				""";
//		int v = 100;
//		Object[] params = { v };
//		int result = (int) genCodeAndRun(input, "", params);
//		assertEquals(v, (Integer) result);
//
//	}
//
//	@Test
//	void pow() throws Exception {
//		String input = """
//				int pow(){:2**3.}
//				""";
//		Object[] params = {};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(8, ((Integer) result).intValue());
//	}
//
//	// @Test
//	// void custom1() throws Exception { // Not used in assignment 5
//	// String input = """
//	// int test(int val){
//	// int aa = val * 2.
//	// aa = aa + 1.
//	// aa = aa * 4.
//	// aa = aa - 1.
//	// aa = aa + -1.
//	// : aa.
//	// }
//	// """;
//	// int v = 100;
//	// Object[] params = {v};
//	// int result = (int) genCodeAndRun(input, "", params);
//	// assertEquals(((v * 2) + 1) * 4 - 2, (Integer) result);
//	// }
//
//	@Test
//	void custom2() throws Exception {
//		String input = """
//				int test(int val){
//				int aa = 4.
//				while (aa > 0) {
//				    aa = aa - 1.
//				    val = val * 2.
//				}.
//				: val.
//				}
//				""";
//		int v = 5;
//		Object[] params = { v };
//		int result = (int) genCodeAndRun(input, "", params);
//		assertEquals(80, (Integer) result);
//	}
//
//	@Test
//	void custom3() throws Exception {
//		String input = """
//				int test(int val){
//				int aa = 4.
//				int bb = 4.
//				while (aa > 0) {
//				    aa = aa - 1.
//				    while (bb > 0) {
//				        val = val * bb.
//				        bb = bb - 1.
//				    }.
//				    bb = 4.
//				}.
//				write val * 3.
//				: val.
//				}
//				""";
//		int v = 5;
//		Object[] params = { v };
//		int result = (int) genCodeAndRun(input, "", params);
//		assertEquals(1658880, (Integer) result);
//	}
//
//	@Test
//	void custom4() throws Exception {
//		String input = """
//				int test(int val,int val2){
//				int out.
//				: val ** val2.
//				}
//				""";
//		int v = 5;
//		int v2 = 4;
//		Object[] params = { v, v2 };
//		int result = (int) genCodeAndRun(input, "", params);
//		assertEquals(625, (Integer) result);
//	}
//
//	@Test
//	void custom5a() throws Exception {
//		String input = """
//				int test(int val,int val2){
//				:if (val > val2) ? val ? val2.
//				}
//				""";
//		int v = 5;
//		int v2 = 4;
//		Object[] params = { v, v2 };
//		int result = (int) genCodeAndRun(input, "", params);
//		assertEquals(5, (Integer) result);
//	}
//
//	@Test
//	void custom5b() throws Exception {
//		String input = """
//				int test(int val,int val2){
//				:if (val > val2) ? val ? val2.
//				}
//				""";
//		int v = 5;
//		int v2 = 6;
//		Object[] params = { v, v2 };
//		int result = (int) genCodeAndRun(input, "", params);
//		assertEquals(6, (Integer) result);
//	}
//
//	@Test
//	void custom5c() throws Exception {
//		String input = """
//				int test(int val,int val2){
//				:if (val == val2) ? val ? val2.
//				}
//				""";
//		int v = 5;
//		int v2 = 10;
//		Object[] params = { v, v2 };
//		int result = (int) genCodeAndRun(input, "", params);
//		assertEquals(10, (Integer) result);
//	}
//
//	@Test
//	void custom6() throws Exception {
//		String input = """
//				string test(string val){
//				:val + "test".
//				}
//				""";
//		String v = "yurr";
//		Object[] params = { v };
//		String result = (String) genCodeAndRun(input, "", params);
//		assertEquals("yurrtest", result);
//	}
//
//	@Test
//	void custom7() throws Exception {
//		String input = """
//				int test(){
//				int aa = rand.
//				write aa.
//				:aa.
//				}
//				""";
//		Object[] params = {};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(Integer.class, result.getClass());
//	}
//
//	@Test
//	void custom8() throws Exception {
//		// ZExpr always returns 255 per the spec
//		String input = """
//				int test(){
//				:Z.
//				}
//				""";
//		Object[] params = {};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(255, (Integer) result);
//	}
//
//	@Test
//	void custom9a() throws Exception {
//		String input = """
//				int test(int val,int val2){
//				:if (val > 0 && val2 > 0) ? val ? val2.
//				}
//				""";
//		int a = 1;
//		int b = 2;
//		Object[] params = { a, b };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(1, (Integer) result);
//	}
//
//	@Test
//	void custom9b() throws Exception {
//		String input = """
//				int test(int val,int val2){
//				:if (val > 0 && val2 == 0) ? val ? val2.
//				}
//				""";
//		int a = 1;
//		int b = 2;
//		Object[] params = { a, b };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(2, (Integer) result);
//	}
//
//	@Test
//	void custom9c() throws Exception {
//		String input = """
//				int test(int val,int val2){
//				:if (val == 0 && val2 == 0) ? val ? val2+ val.
//				}
//				""";
//		int a = 1;
//		int b = 2;
//		Object[] params = { a, b };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(3, (Integer) result);
//	}
//
//	@Test
//	void custom10a() throws Exception {
//		String input = """
//				int test(string val){
//				:val == "nottest".
//				}
//				""";
//		String a = "test";
//		Object[] params = { a };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(0, (Integer) result);
//	}
//
//	@Test
//	void custom10b() throws Exception {
//		String input = """
//				int test(string val){
//				:val == "test".
//				}
//				""";
//		String a = "test";
//		Object[] params = { a };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(1, (Integer) result);
//	}
//
//	@Test
//	void custom10c() throws Exception {
//		String input = """
//				string test(string val){
//				string ok = if (val == "test") ? "mhm" ? "idk".
//				:ok.
//				}
//				""";
//		String a = "test";
//		Object[] params = { a };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals("mhm", (String) result);
//	}
//
//	@Test
//	void custom10d() throws Exception {
//		String input = """
//                string test(string val){
//                string ok = val + "test".
//                :ok.
//                }
//                """;
//		String a = "wut";
//		Object[] params = {a};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals("wuttest", (String) result);
//	}
//
//	@Test
//	void custom11() throws Exception {
//		String input = """
//				int f(int n) {
//					int i = 0.
//					int j = 1.
//					int k = 0.
//					while k < n {
//						int t = i + j.
//						i = j.
//						j = t.
//						k = k + 1.
//					}.
//					:i.
//				}
//				""";
//		int n = 10;
//		Object[] params = { n };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(55, (Integer) result);
//	}
//
//	@Test
//	void gcd() throws Exception {
//		String input = """
//				int f(int aa, int bb) {
//					int i = if (aa < bb) ? aa ? bb.
//					int ret = 1.
//					while i > 1 {
//						ret = if ((aa % i == 0) && (bb % i == 0)) ? i ? 1.
//						i = if (ret > 1) ? 1 ? i.
//						i = i - 1.
//					}.
//					:ret.
//				}
//				""";
//		int aa = 65;
//		int bb = 52;
//		Object[] params = { aa, bb };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(13, (Integer) result);
//	}
//
//
//	@Test
//	void andWhatIsUp() throws Exception {
//		String input = "int up(int up){ :up. }";
//		Object[] params = { 1 };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(1, result);
//	}
//
//	@Test
//	void andReturnString() throws Exception {
//		String input = """
//				string fun() { :"Hello, World!". }
//				""";
//		Object[] params = {};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals("Hello, World!", (String) result);
//	}
//
//	@Test
//	void andAddingStrings() throws Exception {
//		String input = """
//				string fun(string start) {
//					string end = ", World!".
//					:start + end. }
//				""";
//		Object[] params = { "Hello" };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals("Hello, World!", (String) result);
//		params[0] = "Goodbye";
//		result = genCodeAndRun(input, "", params);
//		assertEquals("Goodbye, World!", (String) result);
//	}
//
//	@Test
//	void andBasicMath() throws Exception {
//		String input = """
//				int fun(int num1, int num2) {
//					int num3 = num1 + num2.
//					: num3.
//				}
//				""";
//		int num1 = 5;
//		int num2 = 2;
//		Object[] params = { num1, num2 };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(num1 + num2, result);
//		input = """
//				int fun(int num1, int num2) {
//					int num3 = num1 - num2.
//					: num3.
//				}
//				""";
//		result = genCodeAndRun(input, "", params);
//		assertEquals(num1 - num2, result);
//		input = """
//				int fun(int num1, int num2) {
//					int num3 = num1 * num2.
//					: num3.
//				}
//				""";
//		result = genCodeAndRun(input, "", params);
//		assertEquals(num1 * num2, result);
//		input = """
//				int fun(int num1, int num2) {
//					int num3 = num1 / num2.
//					: num3.
//				}
//				""";
//		result = genCodeAndRun(input, "", params);
//		assertEquals(num1 / num2, result);
//		input = """
//				int fun(int num1, int num2) {
//					int num3 = num1 % num2.
//					: num3.
//				}
//				""";
//		result = genCodeAndRun(input, "", params);
//		assertEquals(num1 % num2, result);
//		input = """
//				int fun(int num1, int num2) {
//					int num3 = num1 ** num2.
//					: num3.
//				}
//				""";
//		result = genCodeAndRun(input, "", params);
//		assertEquals((int) Math.pow(num1, num2), result);
//	}
//
//	@Test
//	void andLogicalOps() throws Exception {
//		String input = """
//				int fun(int num1, int num2) {
//					int num3 = num1 || num2.
//					: num3.
//				}
//				""";
//		Object[] params = { 1, 0 };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(1, result);
//		input = """
//				int fun(int num1, int num2) {
//					int num3 = num1 && num2.
//					: num3.
//				}
//				""";
//		result = genCodeAndRun(input, "", params);
//		assertEquals(0, result);
//	}
//
//	@Test
//	void andComparisonOps() throws Exception {
//		String input = """
//				int fun() {	: 1 > 0. }
//				""";
//		Object[] params = {};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(1, result);
//		input = """
//				int fun() {	: 1 < 0. }
//				""";
//		result = genCodeAndRun(input, "", params);
//		assertEquals(0, result);
//		input = """
//				int fun() {	: 1 <= 0. }
//				""";
//		result = genCodeAndRun(input, "", params);
//		assertEquals(0, result);
//		input = """
//				int fun() {	: 1 >= 0. }
//				""";
//		result = genCodeAndRun(input, "", params);
//		assertEquals(1, result);
//		input = """
//				int fun() {	: 0 <= 0. }
//				""";
//		result = genCodeAndRun(input, "", params);
//		assertEquals(1, result);
//		input = """
//				int fun() {	: 0 >= 0. }
//				""";
//		result = genCodeAndRun(input, "", params);
//		assertEquals(1, result);
//		input = """
//				int fun() {	: 0 < 0. }
//				""";
//		result = genCodeAndRun(input, "", params);
//		assertEquals(0, result);
//		input = """
//				int fun() {	: 0 > 0. }
//				""";
//		result = genCodeAndRun(input, "", params);
//		assertEquals(0, result);
//	}
//
//	@Test
//	void andIfExpr() throws Exception {
//		String input = """
//				string fun(int num) { : if num ? "true" ? "false". }
//				""";
//		Object[] params = { 1 };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals("true", (String) result);
//		params[0] = 0;
//		result = genCodeAndRun(input, "", params);
//		assertEquals("false", (String) result);
//		params[0] = -1;
//		result = genCodeAndRun(input, "", params);
//		assertEquals("true", (String) result);
//		params[0] = 123456;
//		result = genCodeAndRun(input, "", params);
//		assertEquals("true", (String) result);
//	}
//
//	@Test
//	void andMultipleDeclarations() throws Exception {
//		String input = """
//				void fun(int fun) {
//					fun = 0.
//					while (fun) {
//						int fun = 0.
//						while (fun) {
//							int fun = 0.
//							while (fun) {
//								string fun = "".
//							}.
//						}.
//						fun = 0.
//						while (fun) {
//							string fun = "".
//						}.
//					}.
//
//				}
//				""";
//		Object[] params = { 0 };
//		genCodeAndRun(input, "", params);
//	}
//
//	@Test
//	void andSeparateVariables() throws Exception {
//		String input = """
//				int fun() {
//					int val = 5.
//					int i = 0.
//					while (i < 1) {
//						int val = 3.
//						i = i + 1.
//					}.
//					: val.
//				}
//				""";
//		Object[] params = {};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(5, result);
//	}
//
//	@Test
//	void andAddingInALoop() throws Exception {
//		String input = """
//				int fun() {
//					int val = 0.
//					int i = 0.
//					while (i <= 6) {
//						val = val + i.
//						i = i + 1.
//					}.
//					: val.
//				}
//				""";
//		Object[] params = {};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(21, result);
//	}
//
//	@Test
//	void andItsBinary() throws Exception {
//		String input = """
//				string fun(int num) {
//					string result = "".
//					while (num > 0) {
//						result = (if num % 2 == 0 ? "0" ? "1") + result.
//						num = num / 2.
//					}.
//					: result.
//				}
//				""";
//		int num = 3563;
//		Object[] params = { num };
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(Integer.toBinaryString(num), (String) result);
//	}
//
//	@Test
//	void andReturnIntAsString() throws Exception {
//		String input = """
//				string fun() { :1. }
//				""";
//		Object[] params = {};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals("1", (String) result);
//	}
//
//	@Test
//	void andAssignIntToString() throws Exception {
//		String input = """
//				string fun() {
//					string result.
//					result = 1.
//					: result.
//				}
//				""";
//		Object[] params = {};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals("1", (String) result);
//	}
//
//	@Test
//	void andDeclareStringWithInt() throws Exception {
//		String input = """
//				string fun() {
//					string result = 1.
//					: result.
//				}
//				""";
//		Object[] params = {};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals("1", (String) result);
//	}
//	@Test
//	void custom12() throws Exception {
//		String input = """
//                int test(int val){
//                while ( val > 0) {
//                :val -1.
//                }.
//                :val.
//                }
//                """;
//		int a = 21;
//		Object[] params = {a};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(20,(Integer) result);
//	}
//
//	@Test
//	void custom13() throws Exception {
//		String input = """
//                int test(int val){
//                int out.
//                out = if val ? 2 ? 3.
//                :out.
//                }
//                """;
//		int a = 1;
//		Object[] params = {a};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(2,(Integer) result);
//	}
//
//	@Test
//	void custom14() throws Exception {
//		String input = """
//                int test(int val){
//                int out = 45.
//                out = out % val.
//                :out.
//                }
//                """;
//		int a = 21;
//		Object[] params = {a};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(3,(Integer) result);
//	}
//
//	@Test
//	void custom15() throws Exception {
//		String input = """
//                string test(int val){
//                string out.
//                out = val.
//                :out.
//                }
//                """;
//		int a = 21;
//		Object[] params = {a};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals("21",(String) result);
//	}
//
//	@Test
//	void custom16() throws Exception {
//		String input = """
//                string test(int val){
//                string out=  val.
//                :out.
//                }
//                """;
//		int a = 21;
//		Object[] params = {a};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals("21",(String) result);
//	}
//
//	@Test
//	void custom16a() throws Exception {
//		String input = """
//                int test(int val, int val2){
//                int out = val && val2.
//                :out.
//                }
//                """;
//		int a = 21;
//		int b = 0;
//		Object[] params = {a,b};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(0,(Integer) result);
//	}
//
//	@Test
//	void custom16b() throws Exception {
//		String input = """
//                int test(int val, int val2){
//                int out = val && val2.
//                :out.
//                }
//                """;
//		int a = 21;
//		int b = 16;
//		Object[] params = {a,b};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(1,(Integer) result);
//	}
//
//	@Test
//	void custom16c() throws Exception {
//		String input = """
//                int test(int val, int val2){
//                int out = val || val2.
//                :out.
//                }
//                """;
//		int a = 21;
//		int b = 0;
//		Object[] params = {a,b};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(1,(Integer) result);
//		a = 0;
//		params = new Object[]{a,b};
//		result = genCodeAndRun(input, "", params);
//		assertEquals(0,(Integer) result);
//	}
//
//	@Test
//	void custom17() throws Exception {
//		//Tests basic arithmetic
//		String input = """
//                int test(int val){
//                int one = val + 1.
//                int two = one - 2.
//                int three = two * 3.
//                one = three / 4.
//                two = one % 99.
//                :two ** 2.
//                }
//                """;
//		int a = 21;
//		Object[] params = {a};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(225,(Integer) result);
//	}
//
//	@Test
//	void custom18() throws Exception {
//		//Tests basic arithmetic
//		String input = """
//                int test(int val, int val2){
//                int one = val > val2.
//                int two = val < val2.
//                int three = val == val2.
//                int four = val >= val2.
//                int five = val <= val2.
//                write one.
//                write two.
//                write three.
//                write four.
//                write five.
//                :one + two + three + four + five.
//                }
//                """;
//		int a = 21;
//		int b = 21;
//		Object[] params = {a,b};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(3,(Integer) result);
//	}
//
//	@Test
//	void custom19() throws Exception {
//		//Tests basic arithmetic
//		String input = """
//                int test(int val){
//                    while (Z*Z) {
//                        write val.
//                        :val - 1.
//                    }.
//                }
//                """;
//		int a = 21;
//		Object[] params = {a};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(20,(Integer) result);
//	}
//
//	@Test
//	void custom20() throws Exception {
//		//Tests basic arithmetic
//		String input = """
//                int test(int val){
//                    while (val + val * val) {
//                        write val.
//                        :val - 1.
//                    }.
//                    :2.
//                }
//                """;
//		int a = 21;
//		Object[] params = {a};
//		Object result = genCodeAndRun(input, "", params);
//		assertEquals(20,(Integer) result);
//	}
//}
//

package edu.ufl.cise.plcsp23;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;

import org.junit.jupiter.api.Test;

import edu.ufl.cise.plcsp23.ast.AST;
import edu.ufl.cise.plcsp23.ast.Program;
import edu.ufl.cise.plcsp23.javaCompilerClassLoader.DynamicClassLoader;
import edu.ufl.cise.plcsp23.javaCompilerClassLoader.DynamicCompiler;
import edu.ufl.cise.plcsp23.runtime.ConsoleIO;

import static org.junit.jupiter.api.Assertions.*;

class Assignment5Test_grading {
	static final int TIMEOUT_MILLIS = 1000;

	Object genCodeAndRun(String input, String mypackage, Object[] params) throws Exception {
//		show("**** Input ****");
//		show(input);
		AST ast = CompilerComponentFactory.makeParser(input).parse();
		ast.visit(CompilerComponentFactory.makeTypeChecker(), null);
		String name = ((Program) ast).getIdent().getName();
		String packageName = "";
		String code = (String) ast.visit(CompilerComponentFactory.makeCodeGenerator(packageName), null);
//		show("**** Generated Code ****");
//		show(code);
//		show("**** Output ****");
		byte[] byteCode = DynamicCompiler.compile(name, code);
		Object result = DynamicClassLoader.loadClassAndRunMethod(byteCode, name, "apply", params);
//		show("**** Returned Result ****");
//		show(result);
		return result;
	}

	// makes it easy to turn output on and off (and less typing
	// thanSystem.out.println)
	static final boolean VERBOSE = true;

	void show(Object obj) {
		if (VERBOSE) {
			System.out.println(obj);
		}
	}

	@Test
	void cg0() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = "void f(){}";
			Object[] params = {};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(null, result);
		});
	}

	@Test
	void cg1() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = "int f(){:3.}";
			Object[] params = {};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(3, ((Integer) result).intValue());
		});
	}

	@Test
	void cg2() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int aa, int bb){
                       :aa+bb.
                    }""";
			int aa = 5;
			int bb = 7;
			Object[] params = {aa, bb};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(aa + bb, ((Integer) result).intValue());

		});
	}

	@Test
	void cg3() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string f(string aa, string bb){
                       :aa+bb.
                    }""";
			String aa = "aa";
			String bb = "bb";
			Object[] params = {aa, bb};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(aa + bb, result);
		});
	}

	@Test
	void cg5a() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string c(int val){
                       string ss = if val > 0 ? "greater" ? "not greater".
                       :ss.
                       }
                    """;
			int v = -2;
			Object[] params = {v};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(v > 0 ? "greater" : "not greater", result);
		});
	}

	@Test
	void cg5b() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string c(int val){
                       string ss = if val > 0 ? "greater" ? "not greater".
                       :ss.
                       }
                    """;
			int v = 2;
			Object[] params = {v};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(v > 0 ? "greater" : "not greater", result);
		});
	}

	@Test
	void cg5c() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string c(int val){
                       string ss = if val > 0 ? "greater" ? "not greater".
                       :ss.
                       }
                    """;
			int v = 0;
			Object[] params = {v};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(v > 0 ? "greater" : "not greater", result);
		});
	}

	@Test
	void cg6() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string c(int val){
                       string ss = if val > 0 ? if 1 ? "greater" ? "1" ? "not greater".
                       :ss.
                       }
                    """;
			int v = 2;
			Object[] params = {v};
			Object result = genCodeAndRun(input, "", params);
			show(result);
			assertEquals("greater", result);
		});
	}

	@Test
	void cg7() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    void d(int val){
                    int vv = val/2.
                    write vv.
                    }
                    """;
			int v = 4;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream test = new PrintStream(baos);
			ConsoleIO.setConsole(test);
			Object[] params = {v};
			Object result = genCodeAndRun(input, "", params);
			String output = baos.toString();
			// should write 2 to OUTPUT
			assertEquals(null, result);
			assertTrue(output.equals("2\n") || output.equals("2\r\n"));

		});
	}

	@Test
	void cg8() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int d(int val){
                    int aaa = val.
                    int bbb = aaa.
                    while (bbb>0) {
                       write bbb.
                       bbb = bbb - 1.
                       }.
                       :bbb.
                       }
                    	""";
			int v = 4;
			Object[] params = {v};
			int result = (Integer) genCodeAndRun(input, "", params);
			// should write
			// 4
			// 3
			// 2
			// 1
			// to OUTPUT
			assertEquals(0, result);
		});
	}

	@Test
	void cg9() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int testWhile(int val){
                    int aa = val.
                    int g = aa.
                    write val.
                    while ((g > 0)) {
                      int aa = val/2.
                      write "outer loop:  aa=".
                      write aa.
                      g = (aa%2==0).
                      val = val-1.
                      while (val > 0){
                         int aa = val/5.
                         write "inner loop:  aa=".
                         write aa.
                         val = 0.
                      }.
                      write "outer loop after inner loop:  aa=".
                      write aa.
                    }.
                    : aa.
                    }
                    """;
			int v = 100;
			Object[] params = {v};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(100, result);
		});
	}

	@Test
	void cg10() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int xx, string ss){
                    	:xx.
                    }
                    """;
			int v = 2;
			Object[] params = {v, "ss"};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(v, result);
		});
	}

	@Test
	void cg11() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string f(string s0, string s1){
                    	:s0 + " " + s1.
                    }
                    """;
			int v = 2;
			Object[] params = {"aa", "ss"};
			Object result = genCodeAndRun(input, "", params);
			assertEquals("aa ss", result);
		});
	}

	@Test
	void cg12() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string f(string s0, string s1){
                    	int i = 3.
                    	while i {
                    		string s0 = "bb".
                    		:s0 + " " + s1.
                    	}.
                    	: "cc".
                    }
                    """;
			int v = 2;
			Object[] params = {"aa", "ss"};
			Object result = genCodeAndRun(input, "", params);
			assertEquals("bb ss", result);
		});
	}

	@Test
	void cg13() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int i(int i0, int i1){
                    	int i = 12/(4*3).
                    	while i {
                    		int i0 = 10.
                    		:i0 + i1/3.
                    	}.
                    	: 12**4.
                    }
                    """;
			int v = 2;
			Object[] params = {1, 24};
			Object result = genCodeAndRun(input, "", params);
			int expected = (int)Math.round(Math.pow(12,4));
			assertEquals(18, result);
		});
	}

	@Test
	void cg14() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	int i2 = i0 && i1.
                    	: i2.
                    }
                    """;
			int v = 2;
			Object[] params = {1, 5};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(1, result);
		});
	}

	@Test
	void cg15() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	int i2 = i0 && i1.
                    	: i2.
                    }
                    """;
			int v = 2;
			Object[] params = {1, 0};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(0, result);
		});
	}

	@Test
	void cg16() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	int i2 = i0 || i1.
                    	: i2.
                    }
                    """;
			int v = 2;
			Object[] params = {1, 5};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(1, result);
		});
	}

	@Test
	void cg17() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	int i2 = i0 || i1.
                    	: i2.
                    }
                    """;
			int v = 2;
			Object[] params = {1, 0};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(1, result);
		});
	}

	@Test
	void cg18() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	int i2 = (((i0 > 0 || i1 > 0)*5)<=5)-1.
                    	: i2.
                    }
                    """;
			int v = 2;
			Object[] params = {1, 0};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(0, result);
		});
	}

	@Test
	void cg20() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string f(int i0, int i1){
                    	int i2 = if (i0 > 0 || i1 > 0)?10?100.
                    	: i2.
                    }
                    """;
			int v = 2;
			Object[] params = {1, 0};
			Object result = genCodeAndRun(input, "", params);
			assertEquals("10", result);
		});
	}

	@Test
	void cg22() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	int i2 = i0 ** i1.
                    	: i2.
                    }
                    """;
			int v = 2;
			Object[] params = {5, 4};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(625, result);
		});
	}

	@Test
	void cg23() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	int i2 = (i0 % i1).
                    	: i2.
                    }
                    """;
			int v = 2;
			Object[] params = {5, 1};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(0, result);
		});
	}

	@Test
	void cg24() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	int i2.
                    	i2 = i0 && i1.
                    	: i2.
                    }
                    """;
			int v = 2;
			Object[] params = {1, 5};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(1, result);
		});
	}

	@Test
	void cg25() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	int i2.
                    	i2 = i0 && i1.
                    	: i2.
                    }
                    """;
			int v = 2;
			Object[] params = {1, 0};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(0, result);
		});
	}

	@Test
	void cg26() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	int i2.
                    	i2 = i0 > 0 || i1 > 0.
                    	: i2.
                    }
                    """;
			int v = 2;
			Object[] params = {1, 5};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(1, result);
		});
	}

	@Test
	void cg27() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	int i2.
                    	i2 = i0 || i1.
                    	: i2.
                    }
                    """;
			int v = 2;
			Object[] params = {1, 0};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(1, result);
		});
	}

	@Test
	void cg29() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string f(int i0, int i1){
                    	string i2.
                    	i2 = if (i0 > 0 || i1 > 0)?"aa"?"bb".
                    	: i2.
                    }
                    """;
			int v = 2;
			Object[] params = {1, 0};
			Object result = genCodeAndRun(input, "", params);
			assertEquals("aa", result);
		});
	}

	@Test
	void cg30() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	int i2.
                    	i2 = if (i0 > 0 || i1 > 0)?10?100.
                    	: i2.
                    }
                    """;
			int v = 2;
			Object[] params = {1, 0};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(10, result);
		});
	}

	@Test
	void cg31() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string f(int i0, int i1){
                    	while (i0 > 0 && i1 > 0){
                    		: "aa".
                    	}.
                    	: "bb".
                    }
                    """;
			int v = 2;
			Object[] params = {1, 0};
			Object result = genCodeAndRun(input, "", params);
			assertEquals("bb", result);
		});
	}

	@Test
	void cg32() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string f(int i0, int i1){
                        : if Z ? "aa" ? "bb".
                    }
                    """;
			int v = 2;
			Object[] params = {1, 0};
			Object result = genCodeAndRun(input, "", params);
			assertEquals("aa", result);
		});
	}

	@Test
	void cg33() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string f(int i0, int i1){
                    	while (i0 ** i1){
                    		: "aa".
                    	}.
                    	: "bb".
                    }
                    """;
			int v = 2;
			Object[] params = {1, 10};
			Object result = genCodeAndRun(input, "", params);
			assertEquals("aa", result);
		});
	}

	@Test
	void cg34() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string f(int i0, int i1){
                    	while (i0 ** i1){
                    		: "aa".
                    	}.
                    	: "bb".
                    }
                    """;
			int v = 2;
			Object[] params = {0, 10};
			Object result = genCodeAndRun(input, "", params);
			assertEquals("bb", result);
		});
	}

	@Test
	void cg35() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string f(int i0, int i1){
                    	: if (i0 && i1) ? "aa" ? "bb".
                    }
                    """;
			int v = 2;
			Object[] params = {1, 0};
			Object result = genCodeAndRun(input, "", params);
			assertEquals("bb", result);
		});
	}

	@Test
	void cg36() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string f(int i0, int i1){
                    	: if (i0 ** i1) ? "aa" ? "bb".
                    }
                    """;
			int v = 2;
			Object[] params = {1, 10};
			Object result = genCodeAndRun(input, "", params);
			assertEquals("aa", result);
		});
	}

	@Test
	void cg37() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    string f(int i0, int i1){
                    	: if (i0 ** i1) ? "aa" ? "bb".
                    }
                    """;
			int v = 2;
			Object[] params = {0, 10};
			Object result = genCodeAndRun(input, "", params);
			assertEquals("bb", result);
		});
	}

	@Test
	void cg38() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	: i0 + i0 * i1 / i0 ** 3 % 6 .
                    }
                    """;
			int v = 2;
			Object[] params = {3, 8};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(1331, result);
		});
	}

	@Test
	void cg39() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	: (i0 + i0 * i1 / i0 ** 3 % 6)>3.
                    }
                    """;
			int v = 2;
			Object[] params = {3, 8};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(1, result);
		});
	}

	@Test
	void cg40() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	: (i0 + i0 * i1 / i0 ** 3 % 6)>=3.
                    }
                    """;
			int v = 2;
			Object[] params = {3, 8};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(1, result);
		});
	}

	@Test
	void cg41() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	: (i0 + i0 * i1 / i0 ** 3 % 6)==3.
                    }
                    """;
			int v = 2;
			Object[] params = {1, 8};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(0, result);
		});
	}

	@Test
	void cg42() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	: (i0 + i0 * i1 / i0 ** 3 % 6)<5.
                    }
                    """;
			int v = 2;
			Object[] params = {3, 8};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(0, result);
		});
	}

	@Test
	void cg43() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	: (i0 + i0 * i1 / i0 ** 3 % 6)<=3.
                    }
                    """;
			int v = 2;
			Object[] params = {3, 8};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(0, result);
		});
	}

	@Test
	void cg44() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	while i0{
                    		int i0 = i1.
                    		while i0 {
                    		    int i0 = 2.
                    		    :i0.
                    		}.
                    		:i0.
                    	}.
                    	:5.
                    }
                    """;
			int v = 2;
			Object[] params = {3, 8};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(2, result);
		});
	}

	@Test
	void cg45() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	while i0{
                    		int i2 = i1.
                    		:i2.
                    	}.
                    	:5.
                    }
                    """;
			int v = 2;
			Object[] params = {3, 8};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(8, result);
		});
	}

	@Test
	void cg46() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1){
                    	while i0 > 0{
                    		int i1 = 0.
                    		i0 = i0/2.
                    	}.
                    	while (i0 > 0|| 3 > 0){
                    		:i1.
                    	}.
                    	:5.
                    }
                    """;
			int v = 2;
			Object[] params = {3, 8};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(8, result);
		});
	}

	@Test
	void cg47() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    int f(int i0, int i1, int i2, int i3, int i4, int i5, int i6, int q_, int w_, int e_, int r_, int s_, int t_){
                    	int zz = 32.
                    	while i0{
                    		int q_ = 0.
                    		i0 = i0 - 1.
                    		while i1{
                    			int w_ = 0.
                    			i1 = i1 - 1.
                    			while i2{
                    				int e_ = 0.
                    				i2 = i2 - 1.
                    			}.
                    			while i3{
                    				int r_ = 0.
                    				i3 = i3 - 1.
                    			}.
                    		}.
                    		while i4{
                    			int s_ = 0.
                    			i4 = i4 - 1.
                    		}.
                    		while i5{
                    			int t_ = 0.
                    			i5 = i5 - 1.
                    		}.
                    	}.
                    	while i6{
                    		t_ = 1.
                    		i6 = i6 - 1.
                    	}.
                    	: zz*q_*w_*e_*r_*s_*t_.

                    }
                    """;
			int v = 2;
			Object[] params = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0};
			Object result = genCodeAndRun(input, "", params);
			assertEquals(32, result);
		});
	}

	@Test
	void cg48() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    void d(string val){
                    write val.
                    }
                    """;
			int v = 4;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream test = new PrintStream(baos);
			ConsoleIO.setConsole(test);
			Object[] params = {"abc"};
			Object result = genCodeAndRun(input, "", params);
			String output = baos.toString();
			// should write 2 to OUTPUT
			assertEquals(null, result);
			assertTrue(output.equals("abc\n") || output.equals("abc\r\n"));

		});
	}

	@Test
	void cg49() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    void d(string val){
                    write val + "cc\\t".
                    }
                    """;
			int v = 4;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream test = new PrintStream(baos);
			ConsoleIO.setConsole(test);
			Object[] params = {"abc"};
			Object result = genCodeAndRun(input, "", params);
			String output = baos.toString();
			// should write 2 to OUTPUT
			assertEquals(null, result);
			assertTrue(output.equals("abccc\t\n") || output.equals("abccc\t\r\n"));

		});
	}

	@Test
	void cg50() throws Exception {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
                    void d(int val){
                    string aa = val.
                    write aa + "cc\\t".
                    }
                    """;
			int v = 4;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream test = new PrintStream(baos);
			ConsoleIO.setConsole(test);
			Object[] params = {12};
			Object result = genCodeAndRun(input, "", params);
			String output = baos.toString();
			// should write 2 to OUTPUT
			assertEquals(null, result);
			assertTrue(output.equals("12cc\t\n") || output.equals("12cc\t\r\n"));

		});
	}

}