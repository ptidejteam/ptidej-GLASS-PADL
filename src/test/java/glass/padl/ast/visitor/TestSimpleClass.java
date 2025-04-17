package glass.padl.ast.visitor;

import java.util.Collection;

import org.junit.Test;

import glass.ast.IMethod;
import glass.ast.IType;
import glass.padl.ast.PADLProject;
import junit.framework.TestCase;

public class TestSimpleClass extends TestCase{
	
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
		IType type = project.findType(fullClassName);
		//assertTrue(ghostTypes.isEmpty()); never True because of Object, String, etc...
		assertEquals(1, definedTypes.size());
		assertEquals("SimpleClass", type.getElementName());
		assertEquals("glass.example.ast.SimpleClass", type.getPackage());
		assertEquals(fullClassName, type.getFullyQualifiedName());
		
		IMethod[] methods = type.getLocalMethods();
		assertEquals(3, methods.length);
		
		IMethod firstMethod = methods[0];
		assertEquals("compute", firstMethod.getElementName());
		assertEquals("void", firstMethod.getReturnType());
		assertEquals("void compute(int, java.lang.String)", firstMethod.getSignature());
		assertFalse(firstMethod.isPublic());
		assertFalse(firstMethod.isConstructor());
		assertFalse(firstMethod.isSimilar(methods[1]));
		
		String[] paramNames = firstMethod.getParameterNames();
		assertEquals(2, firstMethod.getParameterNames().length);
		assertEquals("int", paramNames[0]);
		assertEquals("java.lang.String", paramNames[1]);
		 
	}

}
