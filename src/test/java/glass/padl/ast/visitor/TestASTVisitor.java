package glass.padl.ast.visitor;

import java.util.Collection;

import org.junit.After;
import org.junit.Test;

import glass.ast.IType;
import glass.padl.ast.PADLProject;
import junit.framework.TestCase;

public class TestASTVisitor extends TestCase{

	private PADLProject project;
	
	@Override
	public void setUp() {
		
	}
	
	@Test
	public void testSingleClass() {
		String filePathTest = "..\\ptidej-GLASS\\target\\test-classes\\glass\\example\\ast\\SimpleClass\\SimpleClass.class";
		String filePath = "..\\ptidej-GLASS\\src\\test\\java\\glass\\example\\ast\\SimpleClass";
		String fullClassName = "glass.example.ast.SimpleClass.SimpleClass";
		this.project = new PADLProject(filePathTest);
		Collection<IType> definedTypes = this.project.getDefinedTypes();
		Collection<IType> ghostTypes = this.project.getGhostTypes();
		IType type = project.findType("glass.example.ast.SimpleClass.SimpleClass");
		//assertTrue(ghostTypes.isEmpty()); never True because of Object, String, etc...
		assertEquals(definedTypes.size(), 1);
		assertEquals(type.getElementName(), "SimpleClass");
		assertEquals(type.getPackage(), "glass.example.ast.SimpleClass");
		assertEquals(type.getFullyQualifiedName(), "glass.example.ast.SimpleClass.SimpleClass");
	}
}
