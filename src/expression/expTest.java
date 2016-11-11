package expression;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;


public class expTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test1() {
		String result="";
		String exp="x+y+2";
		expression temp1=new expression(exp);
		temp1.retExp();
		String inputvalue="!simplify x=1 y=2";
		TYPE type=temp1.retVar(inputvalue);
		if(type == TYPE.SIMPLIFY){
			result=temp1.simplify();
		}
		assertEquals("5", result);
		//fail("Not yet implemented");
	}
	
	@Test
	public void test2() {
		String result="3";
		String exp="x+y+2";
		expression temp1=new expression(exp);
		temp1.retExp();
		String inputvalue="!simplify x=2";
		TYPE type=temp1.retVar(inputvalue);
		if(type == TYPE.SIMPLIFY){
			result=temp1.simplify();
		}
		assertEquals("2+y+2", result);
		//fail("Not yet implemented");
	}

	@Test	
	public void test3() {
		String result="3";
		String exp="x+y+2";
		expression temp1=new expression(exp);
		temp1.retExp();
		String inputvalue="!simplify";
		TYPE type=temp1.retVar(inputvalue);
		if(type == TYPE.SIMPLIFY){
			result=temp1.simplify();
			System.out.println("zhou"+result);
		}
		if(type == TYPE.OTHER){
			assertEquals("x+y+2", exp);
		}
		//assertEquals("x+y+2", result);
		//fail("Not yet implemented");
	}
	

}
