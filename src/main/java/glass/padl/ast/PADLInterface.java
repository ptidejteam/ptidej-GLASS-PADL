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
		ArrayList<IType> implClasses = new ArrayList<IType>();
		final Iterator iterator = this.padlInterface.getIteratorOnImplementingClasses();
		while (iterator.hasNext()) {
			IFirstClassEntity entity = (IFirstClassEntity) iterator.next();
			String[] splitPackages = entity.getDisplayPath().split("\\|");
			String entityName = splitPackages[splitPackages.length-1];
			IType typeEntity = this.padlProject.findType(entityName);
			if (typeEntity != null) {
				implClasses.add(typeEntity);
				implClasses.addAll(Arrays.asList(typeEntity.getAllSubtypes()));
			}
		}
		
		IType[] res = new IType[implClasses.size()];
		for (int i = 0; i<implClasses.size(); i++) {
			res[i] = implClasses.get(i);
		}
		return res;
	}
	
	@Override
	public IType[] getAllSubtypes() {
		List<IType> subTypes = new ArrayList<IType>();
		subTypes.addAll(Arrays.asList(super.getAllSubtypes()));
		subTypes.addAll(Arrays.asList(this.getImplementingClasses()));
		return subTypes.toArray(new IType[0]);
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
