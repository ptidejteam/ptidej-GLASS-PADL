package glass.padl.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import glass.ast.IMethod;
import glass.ast.IType;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IInterface;

public class PADLInterface extends PADLType{
	
	private padl.kernel.IInterface padlInterface;
	private IType[] implClasses;

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
		if (!this.isInitialized) {
			this.init();
		}
		return this.implClasses;
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
				implClassesList.addAll(Arrays.asList(typeEntity.getAllSubtypes()));
			}
		}
		
		this.implClasses = new IType[implClassesList.size()];
		for (int i = 0; i<implClassesList.size(); i++) {
			this.implClasses[i] = implClassesList.get(i);
		}
	}
	
	@Override
	public void initSubtypes() {
		this.initImplementingClasses();
		List<IType> subTypesList = new ArrayList<IType>();
		subTypesList.addAll(Arrays.asList(super.getAllSubtypes()));
		subTypesList.addAll(Arrays.asList(this.implClasses));
		this.subTypes = subTypesList.toArray(new IType[0]);
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
			allMethods.addAll(Arrays.asList(superType.getMethods()));
		}
		return (IMethod[]) allMethods.toArray();
	}

}
