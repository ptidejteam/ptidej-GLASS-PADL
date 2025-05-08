package glass.padl.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import glass.ast.IField;
import glass.ast.IMethod;
import glass.ast.IType;
import padl.kernel.IFirstClassEntity;

public abstract class PADLType implements IType {

	protected IFirstClassEntity padlType;
	private Collection<IField> fields;
	private Collection <IMethod> methods;
	protected PADLProject padlProject;
	private IType superClass;
	protected boolean isInitialized = false;
	

	public PADLType(IFirstClassEntity padlType, PADLProject padlProject) {
		this.padlType = padlType;
		this.fields = new ArrayList<IField>();
		this.methods = new ArrayList<IMethod>();
		this.padlProject = padlProject;
	}

	public void addField(IField field) {
		this.fields.add(field);
	}

	public void addMethod(IMethod method) {
		this.methods.add(method);
	}
	
	public abstract void init();


	@Override
	public boolean isAnonymous() {
		// We shouldn't detect anonymous types.
		return false;
	}

	@Override
	abstract public boolean isInterface();

	@Override
	public abstract IMethod[] getMethods();

	@Override
	public IField[] getFields() {
		return this.fields.toArray(new IField[this.fields.size()]);
	}

	@Override
	public String getElementName() {
		return padlType.getDisplayName();
	}

	@Override
	public String getFullyQualifiedName() {
		String[] splitPackages = this.padlType.getDisplayPath().split("\\|");
		return splitPackages[splitPackages.length - 1];
	}

	@Override
	public String getFullyQualifiedParameterizedName() {
		return this.getFullyQualifiedName();
	}

	@Override
	public String[][] resolveType(String typeName) {
		return null;	// shouldn't be used since PADL already takes care of the resolution, should probably throw exception
	}
	
	@Override
	public abstract IType[] getAllSubtypes();

	@Override
	public abstract IType[] getAllSupertypes();
	
	public IType getSuperClass() {
		return this.superClass;
	}

	@Override
	public IType[] getImplementingClasses() {
		return null; // only used for interfaces, should throw exception
	}
	

	public boolean containsSameEntity(IFirstClassEntity entity) {
		String[] splitPackages = entity.getDisplayPath().split("\\|");
		String entityName = splitPackages[splitPackages.length - 1];
		return this.getFullyQualifiedName().equals(entityName);
	}
	
	@Override
	public String toString() {
		return this.getElementName();
	}

	@Override
	public String getPackage() {
		String qualifiedName = this.getFullyQualifiedName();
		String[] splitPackages = qualifiedName.split("\\.");
		StringBuilder packageName = new StringBuilder();
		for (int i=0; i<splitPackages.length-2; i++) {
			packageName.append(splitPackages[i]+".");
		}
		packageName.append(splitPackages[splitPackages.length-2]);
		return packageName.toString();
	}

	@Override
	public abstract void addSuperInterface(IType superInterface);

	
	@Override
	public void changeSuperclass(IType newSuperClass) {
		this.superClass = newSuperClass;
	}
	

	@Override
	public abstract void addSubType(IType subType);

	@Override
	public boolean hasSamePublicInterface(IType comparedType) {
		Set<String> publicComparedMethods = Stream.of(comparedType.getMethods()).
				filter(m -> m.isPublic()).
				map(m -> m.getSignature()).
				collect(Collectors.toSet());
		
		String[] publicLocalMethods = Stream.of(this.getMethods()).
				filter(m -> m.isPublic()).
				map(m -> m.getSignature()).
				toArray(String[]::new);
		
		int nbPublicMethods = publicLocalMethods.length;
		
		if (nbPublicMethods != publicComparedMethods.size()) {
			return false;
		}
		
		int i = 0;
		while (i<nbPublicMethods && publicComparedMethods.contains(publicLocalMethods[i])) {
			i++;
		}
		
		return i == nbPublicMethods;
		
	}
	
	protected IType[] addToArray(IType[] typeArray, IType newType) {
		IType[] newArray = null;
		if (typeArray == null) {
			newArray = new IType[1];
			newArray[0] = newType;
		} else {
			newArray = new IType[typeArray.length + 1];
			newArray[0] = newType;
			System.arraycopy(typeArray, 0, newArray, 1, newArray.length);
		}
		return newArray;
	}

	@Override
	public IMethod[] getLocalMethods() {
		return this.methods.toArray(new IMethod[this.methods.size()]);
	}

}
