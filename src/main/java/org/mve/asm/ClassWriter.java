package org.mve.asm;

import org.mve.asm.attribute.AttributeWriter;
import org.mve.asm.file.Class;
import org.mve.asm.file.Field;
import org.mve.asm.file.Method;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public class ClassWriter
{
	public int major;
	public int access;
	public String name;
	public String supers;
	public String[] interfaces = new String[0];
	public FieldWriter[] field = new FieldWriter[0];
	public MethodWriter[] method = new MethodWriter[0];
	public AttributeWriter[] attribute = new AttributeWriter[0];

	public ClassWriter set(int major, int accessFlag, String name, String superName, String[] interfaces)
	{
		this.major = major;
		this.access = accessFlag;
		this.name = name;
		this.supers = superName;
		this.interfaces = interfaces == null ? new String[0] : interfaces;
		return this;
	}

	public ClassWriter access(int access)
	{
		this.access = access;
		return this;
	}

	public ClassWriter major(int major)
	{
		this.major = major;
		return this;
	}

	public ClassWriter name(String name)
	{
		this.name = name;
		return this;
	}

	public ClassWriter supers(String type)
	{
		this.supers = type;
		return this;
	}

	public ClassWriter interfaces(String interfaces)
	{
		this.interfaces = Arrays.copyOf(this.interfaces, this.interfaces.length+1);
		this.interfaces[this.interfaces.length-1] = interfaces;
		return this;
	}

	public ClassWriter field(FieldWriter writer)
	{
		int i = this.field.length;
		this.field = Arrays.copyOf(this.field, i+1);
		this.field[i] = writer;
		return this;
	}

	public ClassWriter method(MethodWriter writer)
	{
		int i = this.method.length;
		this.method = Arrays.copyOf(this.method, i+1);
		this.method[i] = writer;
		return this;
	}

	public ClassWriter attribute(AttributeWriter writer)
	{
		int i = this.attribute.length;
		this.attribute = Arrays.copyOf(this.attribute, i+1);
		this.attribute[i] = writer;
		return this;
	}

	public ClassWriter reset()
	{
		this.major = 0;
		this.access = 0;
		this.name = null;
		this.supers = null;
		this.interfaces = new String[0];
		this.field = new FieldWriter[0];
		this.method = new MethodWriter[0];
		this.attribute = new AttributeWriter[0];
		return this;
	}

	public Class toClassFile()
	{
		Class file = new Class();
		ConstantArray pool = file.constant;

		file.magic = 0xCAFEBABE;
		file.major = this.major;
		file.access = this.access;
		file.self = ConstantPoolFinder.findClass(pool, this.name);
		file.supers = (this.access & AccessFlag.MODULE) != 0 ? 0 : ConstantPoolFinder.findClass(pool, this.supers);
		for (String str : this.interfaces)
		{
			file.interfaces(ConstantPoolFinder.findClass(pool, str));
		}

		for (FieldWriter writer : this.field)
		{
			Field field = new Field();
			field.access = writer.access;
			field.name = ConstantPoolFinder.findUTF8(pool, writer.name);
			field.type = ConstantPoolFinder.findUTF8(pool, writer.type);
			AttributeWriter[] attrs = writer.getAttribute();
			for (AttributeWriter attr : attrs) field.attribute(attr.getAttribute(pool));
			file.field(field);
		}

		for (MethodWriter writer : this.method)
		{
			Method method = new Method();
			method.access = writer.access;
			method.name = ConstantPoolFinder.findUTF8(pool, writer.name);
			method.type = ConstantPoolFinder.findUTF8(pool, writer.type);
			AttributeWriter[] attrs = writer.getAttribute();
			for (AttributeWriter attr : attrs)
			{
				method.attribute(attr.getAttribute(pool));
			}
			file.method(method);
		}

		for (AttributeWriter attr : this.attribute)
		{
			file.attribute(attr.getAttribute(pool));
		}

		return file;
	}

	public byte[] toByteArray()
	{
		return this.toClassFile().toByteArray();
	}
}
