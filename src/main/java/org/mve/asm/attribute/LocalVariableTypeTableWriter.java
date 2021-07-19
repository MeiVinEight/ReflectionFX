package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.AttributeLocalVariableTable;
import org.mve.asm.file.attribute.AttributeLocalVariableTypeTable;
import org.mve.asm.file.constant.ConstantArray;

public class LocalVariableTypeTableWriter extends LocalVariableTableWriter
{
	@Override
	public AttributeLocalVariableTable getAttribute(ConstantArray pool)
	{
		AttributeLocalVariableTable table = super.getAttribute(pool);
		AttributeLocalVariableTypeTable t1 = new AttributeLocalVariableTypeTable();
		t1.name = ConstantPoolFinder.findUTF8(pool, t1.type().getName());
		t1.local = table.local;
		return t1;
	}
}
