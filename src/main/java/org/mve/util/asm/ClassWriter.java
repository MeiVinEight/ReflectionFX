package org.mve.util.asm;

import org.mve.util.asm.attribute.AttributeSignatureWriter;
import org.mve.util.asm.attribute.AttributeSourceWriter;
import org.mve.util.asm.attribute.AttributeWriter;
import org.mve.util.asm.file.ClassField;
import org.mve.util.asm.file.ClassFile;
import org.mve.util.asm.file.ClassMethod;
import org.mve.util.asm.file.ConstantPool;

import java.util.Arrays;

public class ClassWriter
{
	private int accessFlag;
	private int majorVersion;
	private String name;
	private String superName;
	private String[] interfaces;
	private FieldWriter[] fields = new FieldWriter[0];
	private MethodWriter[] methods = new MethodWriter[0];
	private AttributeWriter[] attributes = new AttributeWriter[0];

	public void set(int major, int accessFlag, String name, String superName, String[] interfaces)
	{
		this.majorVersion = major;
		this.accessFlag = accessFlag;
		this.name = name;
		this.superName = superName;
		this.interfaces = interfaces;
	}

	public int getAccessFlag()
	{
		return accessFlag;
	}

	public void setAccessFlag(int accessFlag)
	{
		this.accessFlag = accessFlag;
	}

	public int getMajorVersion()
	{
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion)
	{
		this.majorVersion = majorVersion;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSuperName()
	{
		return superName;
	}

	public void setSuperName(String superName)
	{
		this.superName = superName;
	}

	public String[] getInterfaces()
	{
		return interfaces;
	}

	public void setInterfaces(String[] interfaces)
	{
		this.interfaces = interfaces;
	}

	public void addField(FieldWriter writer)
	{
		int i = this.fields.length;
		this.fields = Arrays.copyOf(this.fields, i+1);
		this.fields[i] = writer;
	}

	public FieldWriter addField(int accessFlag, String name, String desc)
	{
		FieldWriter fw = new FieldWriter();
		fw.set(accessFlag, name, desc);
		this.addField(fw);
		return fw;
	}

	public void addMethod(MethodWriter writer)
	{
		int i = this.methods.length;
		this.methods = Arrays.copyOf(this.methods, i+1);
		this.methods[i] = writer;
	}

	public MethodWriter addMethod(int accessFlag, String name, String desc)
	{
		MethodWriter writer = new MethodWriter();
		writer.set(accessFlag, name, desc);
		this.addMethod(writer);
		return writer;
	}

	public void addAttribute(AttributeWriter writer)
	{
		int i = this.attributes.length;
		this.attributes = Arrays.copyOf(this.attributes, i+1);
		this.attributes[i] = writer;
	}

	public void addSignature(String signature)
	{
		this.addAttribute(new AttributeSignatureWriter(signature));
	}

	public AttributeSourceWriter addSource(String name)
	{
		if (name == null) return null;
		AttributeSourceWriter writer = new AttributeSourceWriter(name);
		this.addAttribute(writer);
		return writer;
	}

	public ClassFile toClassFile()
	{
		ClassFile file = new ClassFile();
		ConstantPool pool = file.getConstantPool();
		file.setHeader(0xCAFEBABE);
		file.setMajorVersion((short) this.majorVersion);
		file.setAccessFlag((short) this.accessFlag);
		file.setThisClassIndex((short) ConstantPoolFinder.findClass(pool, this.name));
		file.setSuperClassIndex((short) ConstantPoolFinder.findClass(pool, this.superName));
		if (this.interfaces != null) for (String str : this.interfaces) file.addInterface((short) ConstantPoolFinder.findClass(pool, str));
		for (FieldWriter writer : this.fields)
		{
			ClassField field = new ClassField();
			field.setAccessFlag((short) writer.getAccessFlag());
			field.setNameIndex((short) ConstantPoolFinder.findUTF8(pool, writer.getName()));
			field.setDescriptorIndex((short) ConstantPoolFinder.findUTF8(pool, writer.getDesc()));
			AttributeWriter[] attrs = writer.getAttributes();
			for (AttributeWriter attr : attrs) field.addAttribute(attr.getAttribute(pool));
			file.addField(field);
		}
		for (MethodWriter writer : this.methods)
		{
			ClassMethod method = new ClassMethod();
			method.setAccessFlag((short) writer.getAccessFlag());
			method.setNameIndex((short) ConstantPoolFinder.findUTF8(pool, writer.getName()));
			method.setDescriptorIndex((short) ConstantPoolFinder.findUTF8(pool, writer.getDesc()));
			AttributeWriter[] attrs = writer.getAttributes();
			for (AttributeWriter attr : attrs) method.addAttribute(attr.getAttribute(pool));
			file.addMethod(method);
		}
		for (AttributeWriter attr : this.attributes) file.addAttribute(attr.getAttribute(pool));

		return file;
	}

	public byte[] toByteArray()
	{
		return this.toClassFile().toByteArray();
	}
}
