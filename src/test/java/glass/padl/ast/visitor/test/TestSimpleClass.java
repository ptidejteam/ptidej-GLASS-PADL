package glass.padl.ast.visitor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import glass.ast.IMethod;
import glass.ast.IType;
import glass.padl.ast.PADLProject;

public class TestSimpleClass{
	
	private static PADLProject project;
	private static IType type;
	private static String fullClassName = "glass.example.ast.SimpleClass.SimpleClass";
	
	@BeforeClass
	public static void setUpProject() {
		String filePath = "..\\ptidej-GLASS\\target\\test-classes\\glass\\example\\ast\\SimpleClass\\SimpleClass.class";
		project = new PADLProject(filePath);
		type = project.findType(fullClassName);
	}
	
	@Test
	public void testSingleClass() {
		
		Collection<IType> definedTypes = project.getDefinedTypes();
		Collection<IType> ghostTypes = project.getGhostTypes();
		
		assertFalse(ghostTypes.isEmpty()); // never True because of Object, String, etc...
		assertEquals(1, definedTypes.size());
		assertEquals("SimpleClass", type.getElementName());
		assertEquals("glass.example.ast.SimpleClass", type.getPackage());
		assertEquals(this.fullClassName, type.getFullyQualifiedName());
		
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
