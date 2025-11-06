package glass.padl.ast;


import glass.ast.IMethod;
import glass.ast.IType;
import padl.kernel.IGhost;

public class PADLGhost extends PADLType{

	public PADLGhost(IGhost padlType, PADLProject padlProject) {
		super(padlType, padlProject);
	}

	@Override
	public boolean isInterface() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void changeSuperclass(IType newSuperclass) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IMethod[] getMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IType[] getAllSubtypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType[] getAllSupertypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addSuperInterface(IType superInterface) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSubType(IType subType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IType[] getDirectSubTypes() {
		// TODO Auto-generated method stub
		return null;
	}

}
