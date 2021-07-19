package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeModulePackages;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public class ModulePackageWriter implements AttributeWriter
{
	public String[] packages;

	public ModulePackageWriter(String... packages)
	{
		this.packages = packages;
	}

	public ModulePackageWriter()
	{
		this(new String[0]);
	}

	public ModulePackageWriter packages(String name)
	{
		this.packages = Arrays.copyOf(this.packages, this.packages.length+1);
		this.packages[this.packages.length-1] = name;
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeModulePackages attribute = new AttributeModulePackages();
		attribute.name = ConstantPoolFinder.findUTF8(pool, attribute.type().getName());
		return attribute;
	}
}
