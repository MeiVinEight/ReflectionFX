package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeModuleMainClass;
import org.mve.asm.file.constant.ConstantArray;

public class ModuleMainWriter implements AttributeWriter
{
	public String name;

	public ModuleMainWriter(String name)
	{
		this.name = name;
	}

	public ModuleMainWriter()
	{
	}

	public ModuleMainWriter name(String name)
	{
		this.name = name;
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeModuleMainClass attribute = new AttributeModuleMainClass();
		attribute.name = ConstantPoolFinder.findUTF8(pool, attribute.type().getName());
		attribute.main = ConstantPoolFinder.findClass(pool, this.name);
		return attribute;
	}
}
