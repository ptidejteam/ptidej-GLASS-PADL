package glass.padl.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import glass.ast.IMethod;
import glass.ast.IType;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IInterface;

public class PADLInterface extends PADLType{
	
	private padl.kernel.IInterface padlInterface;
	private IType[] directImplClasses;
	private IType[] directSubInterfaces;
	private IType[] directSuperInterfaces;

	public PADLInterface(IInterface padlType, PADLProject padlProject) {
		super(padlType, padlProject);
		this.padlInterface = padlType;
	}

	@Override
	public boolean isInterface() {
		return true;
	}
	
	@Override
	public IType[] getImplementingClasses() {
		return this.directImplClasses;
	}
	
	public void initImplementingClasses() {
		ArrayList<IType> implClassesList = new ArrayList<IType>();
		final Iterator iterator = this.padlInterface.getIteratorOnImplementingClasses();
		while (iterator.hasNext()) {
			IFirstClassEntity entity = (IFirstClassEntity) iterator.next();
			String[] splitPackages = entity.getDisplayPath().split("\\|");
			String entityName = splitPackages[splitPackages.length-1];
			IType typeEntity = this.padlProject.findType(entityName);
			if (typeEntity != null) {
				implClassesList.add(typeEntity);
			}
		}
		
		this.directImplClasses = new IType[implClassesList.size()];
		for (int i = 0; i<implClassesList.size(); i++) {
			this.directImplClasses[i] = implClassesList.get(i);
		}
	}
	

	@Override
	public void changeSuperclass(IType newSuperclass) {
		// do nothing, should throw exception in the future?
	}

	@Override
	public IMethod[] getMethods() {
		Set<IMethod> allMethods = new HashSet<IMethod>();
		allMethods.addAll(Arrays.asList(this.getLocalMethods()));
		for (IType superType : this.getAllSupertypes()) { //All our super types should be interfaces
			IMethod[] superTypeMethods = superType.getMethods();
			if (superTypeMethods != null) {
				allMethods.addAll(Arrays.asList(superTypeMethods));	
			}
		}
		return allMethods.toArray(new IMethod[allMethods.size()]);
	}
	
	private void initDirectSuperInterfaces() {
		List<IType> superTypesList = new ArrayList<IType>();
		final Iterator iterator = this.padlType
				.getIteratorOnInheritedEntities();
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
	
	private void initDirectSubInterfaces() {
		List<IType> subInterfacesList = new ArrayList<IType>();
		final Iterator iterator = this.padlType
				.getIteratorOnInheritingEntities();
		while (iterator.hasNext()) {
			IFirstClassEntity entity = (IFirstClassEntity) iterator.next();
			String[] splitPackages = entity.getDisplayPath().split("\\|");
			String entityName = splitPackages[splitPackages.length - 1];
			IType typeEntity = this.padlProject.findType(entityName);
			if (typeEntity != null) {
				subInterfacesList.add(typeEntity);
			}
		}

		this.directSubInterfaces = new IType[subInterfacesList.size()];
		for (int i = 0; i < subInterfacesList.size(); i++) {
			this.directSubInterfaces[i] = subInterfacesList.get(i);
		}
	}
	
	private void initDirectImplClasses() {
		List<IType> implClassesList = new ArrayList<IType>();
		final Iterator iterator = this.padlInterface
				.getIteratorOnImplementingClasses();
		while (iterator.hasNext()) {
			IFirstClassEntity entity = (IFirstClassEntity) iterator.next();
			String[] splitPackages = entity.getDisplayPath().split("\\|");
			String entityName = splitPackages[splitPackages.length - 1];
			IType typeEntity = this.padlProject.findType(entityName);
			if (typeEntity != null) {
				implClassesList.add(typeEntity);
			}
		}
		
		this.directImplClasses = new IType[implClassesList.size()];
		for (int i = 0; i < implClassesList.size(); i++) {
			this.directImplClasses[i] = implClassesList.get(i);
		}
	}

	@Override
	public void init() {
		this.initDirectImplClasses();
		this.initDirectSubInterfaces();
		this.initDirectSuperInterfaces();
	}

	@Override
	public IType[] getAllSubtypes() {
		Set<IType> allSubTypes = new HashSet<IType>(Arrays.asList(this.directImplClasses));
		allSubTypes.addAll(Arrays.asList(this.directSubInterfaces));
		for (IType implClass : this.directImplClasses) {
			IType[] implSubTypes = implClass.getAllSubtypes();
			if (implSubTypes != null) {
				allSubTypes.addAll(Arrays.asList(implSubTypes));	
			}
		}
		for (IType subInterface : this.directSubInterfaces) {
			IType[] recSubTypes = subInterface.getAllSubtypes();
			if (recSubTypes != null) {
				allSubTypes.addAll(Arrays.asList(recSubTypes));	
			}
		}
		return allSubTypes.toArray(new IType[allSubTypes.size()]);
	}

	@Override
	public IType[] getAllSupertypes() {
		Set<IType> allSuperTypes = new HashSet<IType>(Arrays.asList(this.directSuperInterfaces));
		for (IType superInterface : this.directSuperInterfaces) {
			IType[] recSuperTypes = superInterface.getAllSupertypes();
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
		if (subType.isInterface()) {
			this.addToArray(this.directSubInterfaces, subType);
		} else {
			this.addToArray(this.directImplClasses, subType);
		}
	}
	
	@Override
	public IType[] getDirectSubTypes() {
		return Stream.concat(Arrays.stream(this.directImplClasses), Arrays.stream(this.directSubInterfaces))
				.toArray(IType[]::new);
	}

}
