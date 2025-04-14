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

}
