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
	
	private IType[] superInterfaces;

	public PADLClass(IClass padlType, PADLProject padlProject) {
		super(padlType, padlProject);
	}

	@Override
	public boolean isInterface() {
		return false;
	}
	
	public void initSuperInterfaces() {
		IClass padlClass = (IClass) padlType;
		ArrayList<IType> superInterfacesList = new ArrayList<IType>();
		final Iterator iterator = padlClass.getIteratorOnImplementedInterfaces();
		while (iterator.hasNext()) {
			IFirstClassEntity entity = (IFirstClassEntity) iterator.next();
			String[] splitPackages = entity.getDisplayPath().split("\\|");
			String entityName = splitPackages[splitPackages.length - 1];
			IType typeEntity = this.padlProject
					.findType(entityName);
			if (typeEntity != null) {
				superInterfacesList.add(typeEntity);
				superInterfacesList.addAll(Arrays.asList(typeEntity.getAllSupertypes()));
			}
		}
		this.superInterfaces = new IType[superInterfacesList.size()];
		for (int i = 0; i < superInterfacesList.size(); i++) {
			this.superInterfaces[i] = superInterfacesList.get(i);
		}
	}
	
	public IType[] getAllSuperInterfaces() {
		if(!this.isInitialized) {
			this.init();
		}
		return this.superInterfaces;
	}
	
	public void initSuperTypes() {
		this.initSuperInterfaces();
		List<IType> superTypesList = new ArrayList<IType>();
		superTypesList.addAll(Arrays.asList(this.getAllSuperInterfaces()));
		superTypesList.addAll(Arrays.asList(this.superTypes));
		this.superTypes =  superTypesList.toArray(new IType[0]);
	}
	
	
	@Override
	public void changeSuperclass(IType newSuperclass) {
		IType[] newSuperTypes = new IType[this.superTypes.length];
		for (int i = 0; i<this.superTypes.length; i++) {
			if (this.superTypes[i].isInterface()) {
				newSuperTypes[i] = this.superTypes[i];
			} else { // This should only happen once
				newSuperTypes[i] = newSuperclass;
			}
		}
	}

	@Override
	public IMethod[] getMethods() {
		Set<IMethod> allMethods = new HashSet<IMethod>();
		allMethods.addAll(Arrays.asList(this.getLocalMethods()));
		Set<IMethod> superTypeMethods = null;
		for (IType superType : this.getAllSupertypes()) {
			if (!superType.isInterface()) { // we already get the methods from the super interfaces in the local methods
				superTypeMethods = new HashSet<IMethod>(Arrays.asList(superType.getMethods()));
			}
		}
		for (IMethod method : superTypeMethods) {
			if ((method.isPublic() || method.isProtected()) && !method.isConstructor()) {
				allMethods.add(method);
			}
		}
		return (IMethod[]) allMethods.toArray();
	}
	
}
