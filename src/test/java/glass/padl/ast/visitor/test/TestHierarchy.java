package glass.padl.ast.visitor.test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.BeforeClass;
import org.junit.Test;

import glass.ast.IMethod;
import glass.ast.IType;
import glass.padl.ast.PADLProject;

public class TestHierarchy{
	
	private static PADLProject project;
	private static IType[] typeArray;
	private static final String[] classNames = {"IOtherInterface", "IRoot", "ISubInterface",
			"IAdhocInterface", "RootClass", "SubClass", "TopClass"};
	private static final String packageName = "glass.example.ast.HierarchyTest";
	
	@BeforeClass
	public static void beforeAll() {
		String filePath = "..\\ptidej-GLASS\\target\\test-classes\\glass\\example\\ast\\HierarchyTest";
		project = new PADLProject(filePath);
		typeArray = new IType[project.getDefinedTypes().size()];
		for (int i=0; i<typeArray.length; i++) {
			typeArray[i] = project.findType(packageName + "." + classNames[i]);
		}
	}
	
	@Test
	public void testHierarchyClasses() {
		Collection<IType> definedTypes = project.getDefinedTypes();
		assertEquals(7, definedTypes.size());
		for (int i=0; i<this.typeArray.length; i++) {
			assertEquals(this.classNames[i], this.typeArray[i].getElementName());
		}
	}
	
	@Test
	public void testHierarchyRelationsSubClass() {
		IType subClass = typeArray[5];
		
		IType[] superTypes = subClass.getAllSupertypes();
		assertEquals(4, superTypes.length);
		assertEquals(0, subClass.getAllSubtypes().length);
	}
	
	@Test
	public void testHierarchyRelationsSubInterface() {
		IType subInterface = typeArray[2];
		
		assertEquals(1, subInterface.getAllSupertypes().length);
		assertEquals(3, subInterface.getAllSubtypes().length);
	}
	
	@Test
	public void testMethods() {
		IType topClass = typeArray[6];
		IMethod[] allMethods = topClass.getMethods();
		IMethod[] localMethods = topClass.getLocalMethods();
		assertTrue(allMethods.length == localMethods.length);
		
		IType subInterface = typeArray[2];
		IMethod[] allMethodsInt = subInterface.getMethods();
		IMethod[] localMethodsInt = subInterface.getLocalMethods();
		assertEquals(2, allMethodsInt.length);
		assertEquals(1, localMethodsInt.length);
	}
}
