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
import glass.padl.ast.util.Utils;
import padl.kernel.IFirstClassEntity;

/**
 * In PADL, ghosts and interfaces are essentially the same thing
 */
public class PADLGhost extends PADLType implements IType{
	
	private padl.kernel.IGhost padlGhost;
	private IType[] directSuperGhosts;
	private IType[] directSubTypes;
	

	public PADLGhost(padl.kernel.IGhost padlType, PADLProject padlProject) {
		super(padlType, padlProject);
		this.padlGhost = padlType;
	}
	
	@Override
	public boolean isGhost() {
		return true;
	}

	@Override
	public boolean isInterface() {
		return false; // can PADL identify interfaces from ghost types?
	}

	@Override
	public void changeSuperclass(IType newSuperclass) {
		return; // shouldn't do anything for types outside the project
	}

	@Override
	public IMethod[] getMethods() {
		Set<IMethod> allMethods = new HashSet<IMethod>();
		allMethods.addAll(Arrays.asList(this.getLocalMethods()));
		IType[] allSuperTypes = this.getAllSupertypes();
		for (IType superType : allSuperTypes) {
			if (superType.isInterface()) {
				IMethod[] superTypeMethods = superType.getLocalMethods();
				if (superTypeMethods != null) {
					allMethods.addAll(Arrays.asList(superTypeMethods));	
				}
			}
			else {
				for (IMethod method : superType.getLocalMethods()) {
					if ((method.isPublic() || method.isProtected()) && !method.isConstructor()) {
						allMethods.add(method); // Our class doesn't inherit private methods and constructors
					}
				}
			}
		}
		
		return allMethods.toArray(new IMethod[allMethods.size()]);
	}

	@Override
	public void init() {
		this.initDirectSubTypes();
		this.initDirectSuperTypes();
		this.isInitialized = true;
	}
	
	private void initDirectSubTypes() {
		final Iterator iteratorImplClasses = this.padlGhost.getIteratorOnImplementingClasses();
		IType[] directImplClasses = Utils.initArrayWithIterator(iteratorImplClasses, this.padlProject);
		
		final Iterator iteratorSubInterfaces = this.padlGhost.getIteratorOnInheritingEntities();
		IType[] directSubInterfaces = Utils.initArrayWithIterator(iteratorSubInterfaces, this.padlProject);
		
		this.directSubTypes = Stream.concat(Arrays.stream(directImplClasses), Arrays.stream(directSubInterfaces))
				.toArray(IType[]::new);
	}
	
	private void initDirectSuperTypes() {
		final Iterator iteratorSuperTypes = this.padlGhost.getIteratorOnInheritedEntities();
		IType[] directSuperTypes = Utils.initArrayWithIterator(iteratorSuperTypes, this.padlProject);
		
		final Iterator iteratorImplInterfaces = this.padlGhost.getIteratorOnImplementedInterfaces();
		IType[] directImplInterfaces = Utils.initArrayWithIterator(iteratorImplInterfaces, this.padlProject);
		
		this.directSuperGhosts = Stream.concat(Arrays.stream(directSuperTypes), Arrays.stream(directImplInterfaces))
				.toArray(IType[]::new);
	}

	@Override
	public IType[] getAllSubtypes() {
		IType[] directSubTypes = this.getDirectSubTypes();
		Set<IType> allSubTypes = new HashSet<IType>(Arrays.asList(directSubTypes));
		for (IType subType : directSubTypes) {
			IType[] recSubTypes = subType.getAllSubtypes();
			if (recSubTypes != null) {
				allSubTypes.addAll(Arrays.asList(recSubTypes));
			}
		}
		return allSubTypes.toArray(new IType[allSubTypes.size()]);
	}

	@Override
	public IType[] getAllSupertypes() {
		Set<IType> allSuperTypes = new HashSet<IType>(Arrays.asList(this.directSuperGhosts));
		for (IType superGhost : this.directSuperGhosts) {
			IType[] recSuperGhost = superGhost.getAllSupertypes();
			if (recSuperGhost != null) {
				allSuperTypes.addAll(Arrays.asList(recSuperGhost));
			}
		}
		return allSuperTypes.toArray(new IType[allSuperTypes.size()]);
	}

	@Override
	public void addSuperInterface(IType superInterface) {
		// shouldn't do anything because ghost can't change
	}

	@Override
	public void addSubType(IType subType) {
		// could change, because sub-type can be in project
		// for now, do nothing
	}

	@Override
	public IType[] getDirectSubTypes() {
		return this.directSubTypes;
	}

}
