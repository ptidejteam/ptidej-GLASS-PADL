package glass.padl.ast.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import glass.ast.IType;
import glass.padl.ast.PADLProject;
import padl.kernel.IFirstClassEntity;

public class Utils {

	public static IType[] initArrayWithIterator(final Iterator iterator, final PADLProject project) {
		List<IType> typeList = new ArrayList<IType>();
		while (iterator.hasNext()) {
			IFirstClassEntity entity = (IFirstClassEntity) iterator.next();
			String[] splitPackages = entity.getDisplayPath().split("\\|");
			String entityName = splitPackages[splitPackages.length - 1];
			IType typeEntity = project.findType(entityName);
			if (typeEntity != null) {
				typeList.add(typeEntity);
			}
		}
		
		IType[] result = new IType[typeList.size()];
		for (int i = 0; i < typeList.size(); i++) {
			result[i] = typeList.get(i);
		}
		return result;
	}
}
