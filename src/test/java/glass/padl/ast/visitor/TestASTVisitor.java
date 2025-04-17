package glass.padl.ast.visitor;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestASTVisitor extends TestSuite{
	
	public static Test suite() {
		final TestASTVisitor suite = new TestASTVisitor();
		
		suite.addTestSuite(TestSimpleClass.class);
		
		return suite;
	}
	
	public TestASTVisitor() {
	}
}
