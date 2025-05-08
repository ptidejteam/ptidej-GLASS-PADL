package glass.padl.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import glass.ast.IMethod;
import glass.ast.IType;
import padl.kernel.IClass;
import padl.kernel.IFirstClassEntity;


public class PADLClass extends PADLType{
	
	private IClass padlClass;
	private IType[] directSubClasses;
	private IType[] directSuperInterfaces;

	public PADLClass(IClass padlType, PADLProject padlProject) {
		super(padlType, padlProject);
		this.padlClass = padlType;
	}
	
	@Override
	public void init() {
		this.initDirectSuperClass();
		this.initDirectSuperInterfaces();
		this.initDirectSubClasses();
		this.isInitialized = true;
	}

	@Override
	public boolean isInterface() {
		return false;
	}

	@Override
	public IMethod[] getMethods() {
		Set<IMethod> allMethods = new HashSet<IMethod>();
		allMethods.addAll(Arrays.asList(this.getLocalMethods()));
		Set<IMethod> superTypeMethods = null;
		// we already get the methods from the super interfaces in the local methods
		superTypeMethods = new HashSet<IMethod>(Arrays.asList(this.getSuperClass().getMethods()));
		
		for (IMethod method : superTypeMethods) {
			if ((method.isPublic() || method.isProtected()) && !method.isConstructor()) {
				allMethods.add(method);
			}
		}
		return (IMethod[]) allMethods.toArray();
	}
	
	private void initDirectSuperClass() {
		List<IType> superTypesList = new ArrayList<IType>();
		final Iterator iterator = this.padlType
				.getIteratorOnInheritedEntities(); // Only goes through the class that the current type extends
		while (iterator.hasNext()) { 
			IFirstClassEntity entity = (IFirstClassEntity) iterator.next();
			String[] splitPackages = entity.getDisplayPath().split("\\|");
			String entityName = splitPackages[splitPackages.length - 1];
			IType typeEntity = this.padlProject.findType(entityName);
			if (typeEntity != null) {
				this.changeSuperclass(typeEntity); // There should only be 1 super class
			}
		}
		
	}
	
	private void initDirectSuperInterfaces() {
		List<IType> superTypesList = new ArrayList<IType>();
		final Iterator iterator = this.padlClass.getIteratorOnImplementedInterfaces();
		while (iterator.hasNext()) {
			IFirstClassEntity entity = (IFirstClassEntity) iterator.next();
			String[] splitPackages = entity.getDisplayPath().split("\\|");
			String entityName = splitPackages[splitPackages.length - 1];
			IType typeEntity = this.padlProject.findType(entityName);
			if (typeEntity != null) {
				superTypesList.add(typeEntity);
			}
		}
		
		this.directSuperInterfaces = new IType[superTypesList.size()];
		for (int i = 0; i < superTypesList.size(); i++) {
			this.directSuperInterfaces[i] = superTypesList.get(i);
		}
	}
	
	private void initDirectSubClasses() {
		List<IType> subTypesList = new ArrayList<IType>();
		final Iterator iterator = this.padlType
				.getIteratorOnInheritingEntities();
		while (iterator.hasNext()) {
			IFirstClassEntity entity = (IFirstClassEntity) iterator.next();
			String[] splitPackages = entity.getDisplayPath().split("\\|");
			String entityName = splitPackages[splitPackages.length - 1];
			IType typeEntity = this.padlProject.findType(entityName);
			if (typeEntity != null) {
				subTypesList.add(typeEntity);
			}
		}

		this.directSubClasses = new IType[subTypesList.size()];
		for (int i = 0; i < subTypesList.size(); i++) {
			this.directSubClasses[i] = subTypesList.get(i);
		}
	}
	
	public IType[] getDirectSubClasses() {
		return this.directSubClasses;
	}

	@Override
	public IType[] getAllSubtypes() {
		Set<IType> allSubTypes = new HashSet<IType>(Arrays.asList(this.directSubClasses));
		for (IType subClass : this.directSubClasses) {
			IType[] recSubTypes = subClass.getAllSubtypes();
			if (recSubTypes != null) {
				allSubTypes.addAll(Arrays.asList(recSubTypes));	
			}
		}
		return allSubTypes.toArray(new IType[allSubTypes.size()]);
	}

	public IType[] getDirectSuperTypes() {
		if (this.getSuperClass() == null) { // can be null because we don't consider ghost types
			return this.directSuperInterfaces;
		}
		IType[] allSuperTypes = new IType[this.directSuperInterfaces.length+1];
		for (int i=0; i<this.directSuperInterfaces.length; i++) {
			allSuperTypes[i] = this.directSuperInterfaces[i];
		}
		allSuperTypes[allSuperTypes.length-1] = this.getSuperClass();
		return allSuperTypes;
	}
	
	@Override
	public IType[] getAllSupertypes() {
		IType[] directSuperTypes = this.getDirectSuperTypes();
		Set<IType> allSuperTypes = new HashSet<IType>(Arrays.asList(directSuperTypes));
		for (IType superType : directSuperTypes) {
			IType[] recSuperTypes = superType.getAllSupertypes();
			if (recSuperTypes != null) {
				allSuperTypes.addAll(Arrays.asList(recSuperTypes));
			}
		}
		return allSuperTypes.toArray(new IType[allSuperTypes.size()]);
	}

	@Override
	public void addSuperInterface(IType superInterface) {
		this.addToArray(this.directSuperInterfaces, superInterface);
	}

	@Override
	public void addSubType(IType subType) {
		this.addToArray(this.directSubClasses, subType);
	}
	
}
