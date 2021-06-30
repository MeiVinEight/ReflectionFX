package org.mve.asm.file;

import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.constant.Constant;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.constant.ConstantClass;
import org.mve.asm.file.constant.ConstantDouble;
import org.mve.asm.file.constant.ConstantFieldReference;
import org.mve.asm.file.constant.ConstantFloat;
import org.mve.asm.file.constant.ConstantInteger;
import org.mve.asm.file.constant.ConstantInterfaceMethodReference;
import org.mve.asm.file.constant.ConstantInvokeDynamic;
import org.mve.asm.file.constant.ConstantLong;
import org.mve.asm.file.constant.ConstantMethodHandle;
import org.mve.asm.file.constant.ConstantMethodReference;
import org.mve.asm.file.constant.ConstantMethodType;
import org.mve.asm.file.constant.ConstantModule;
import org.mve.asm.file.constant.ConstantNameAndType;
import org.mve.asm.file.constant.ConstantNull;
import org.mve.asm.file.constant.ConstantPackage;
import org.mve.asm.file.constant.ConstantType;
import org.mve.asm.file.constant.ConstantString;
import org.mve.asm.file.constant.ConstantUTF8;
import org.mve.io.RandomAccessByteArray;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Class
{
	public int magic;
	public int minor;
	public int major;
	public ConstantArray constant = new ConstantArray();
	public int access;
	public int self;
	public int supers;
	public int[] interfaces = new int[0];
	public Field[] field = new Field[0];
	public Method[] method = new Method[0];
	public Attribute[] attribute = new Attribute[0];

	public Class(){}

	public Class(byte[] code)
	{
		RandomAccessByteArray codeAccessor = new RandomAccessByteArray(code);
		this.magic = codeAccessor.readInt();
		if (this.magic != 0XCAFEBABE) throw new ClassFormatError("Invalid file head magic value "+this.magic);
		this.minor = codeAccessor.readUnsignedShort();
		this.major = codeAccessor.readUnsignedShort();
		int constantPoolSize = codeAccessor.readUnsignedShort();
		for (int i = 1; i < constantPoolSize; i++)
		{
			int type = codeAccessor.readUnsignedByte();
			switch (type)
			{
				case 1:
				{
					int length = codeAccessor.readUnsignedShort();
					byte[] bytes = new byte[length];
					if (length != codeAccessor.read(bytes)) throw new ClassFormatError();
					this.constant.add(new ConstantUTF8(bytes));
					break;
				}
				case 3:
				{
					this.constant.add(new ConstantInteger(codeAccessor.readInt()));
					break;
				}
				case 4:
				{
					this.constant.add(new ConstantFloat(codeAccessor.readFloat()));
					break;
				}
				case 5:
				{
					i++;
					this.constant.add(new ConstantLong(codeAccessor.readLong()));
					this.constant.add(new ConstantNull());
					break;
				}
				case 6:
				{
					i++;
					this.constant.add(new ConstantDouble(codeAccessor.readDouble()));
					this.constant.add(new ConstantNull());
					break;
				}
				case 7:
				{
					this.constant.add(new ConstantClass(codeAccessor.readUnsignedShort()));
					break;
				}
				case 8:
				{
					this.constant.add(new ConstantString(codeAccessor.readUnsignedShort()));
					break;
				}
				case 9:
				{
					this.constant.add(new ConstantFieldReference(codeAccessor.readUnsignedShort(), codeAccessor.readUnsignedShort()));
					break;
				}
				case 10:
				{
					this.constant.add(new ConstantMethodReference(codeAccessor.readUnsignedShort(), codeAccessor.readUnsignedShort()));
					break;
				}
				case 11:
				{
					this.constant.add(new ConstantInterfaceMethodReference(codeAccessor.readUnsignedShort(), codeAccessor.readUnsignedShort()));
					break;
				}
				case 12:
				{
					this.constant.add(new ConstantNameAndType(codeAccessor.readUnsignedShort(), codeAccessor.readUnsignedShort()));
					break;
				}
				case 15:
				{
					this.constant.add(new ConstantMethodHandle(codeAccessor.readUnsignedByte(), codeAccessor.readUnsignedShort()));
					break;
				}
				case 16:
				{
					this.constant.add(new ConstantMethodType(codeAccessor.readUnsignedShort()));
					break;
				}
				case 17:
				{
				}
				case 18:
				{
					this.constant.add(new ConstantInvokeDynamic(codeAccessor.readUnsignedShort(), codeAccessor.readUnsignedShort()));
					break;
				}
				case 19:
				{
					this.constant.add(new ConstantModule(codeAccessor.readUnsignedShort()));
					break;
				}
				case 20:
				{
					this.constant.add(new ConstantPackage(codeAccessor.readUnsignedShort()));
					break;
				}
				default: throw new ClassFormatError("Unknown constant tag "+type);
			}
		}
		this.access = codeAccessor.readUnsignedShort();
		this.self = codeAccessor.readUnsignedShort();
		this.supers = codeAccessor.readUnsignedShort();
		int interfaceCount = codeAccessor.readUnsignedShort();
		int[] shorts = new int[interfaceCount];
		for (int i = 0; i < interfaceCount; i++) shorts[i] = codeAccessor.readUnsignedShort();
		this.interfaces = shorts;
		int count = codeAccessor.readUnsignedShort() & 0XFFFF;
		for (int i = 0; i < count; i++)
		{
			Field field = new Field();
			field.access = codeAccessor.readUnsignedShort();
			field.name = codeAccessor.readUnsignedShort();
			field.type = codeAccessor.readUnsignedShort();
			int c = codeAccessor.readUnsignedShort() & 0XFFFF;
			for (int j = 0; j < c; j++)
			{
				field.attribute(Attribute.read(this, codeAccessor));
			}
			this.field(field);
		}
		count = codeAccessor.readUnsignedShort() & 0XFFFF;
		for (int i = 0; i < count; i++)
		{
			Method method = new Method();
			method.access = codeAccessor.readUnsignedShort();
			method.name = codeAccessor.readUnsignedShort();
			method.type = codeAccessor.readUnsignedShort();
			int c = codeAccessor.readUnsignedShort() & 0XFFFF;
			for (int j = 0; j < c; j++)
			{
				method.attribute(Attribute.read(this, codeAccessor));
			}
			this.method(method);
		}
		count = codeAccessor.readUnsignedShort() & 0XFFFF;
		for (int i = 0; i < count; i++)
		{
			this.attribute(Attribute.read(this, codeAccessor));
		}
	}

	public void interfaces(int i)
	{
		this.interfaces = Arrays.copyOf(this.interfaces, this.interfaces.length + 1);
		this.interfaces[this.interfaces.length-1] = i;
	}

	public void field(Field field)
	{
		this.field = Arrays.copyOf(this.field, this.field.length + 1);
		this.field[this.field.length-1] = field;
	}

	public void method(Method method)
	{
		this.method = Arrays.copyOf(this.method, this.method.length+1);
		this.method[this.method.length-1] = method;
	}

	public void attribute(Attribute attribute)
	{
		this.attribute = Arrays.copyOf(this.attribute, this.attribute.length+1);
		this.attribute[this.attribute.length-1] = attribute;
	}

	public byte[] toByteArray()
	{
		try
		{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bout);

			out.writeInt(this.magic);
			out.writeShort(this.minor);
			out.writeShort(this.major);

			out.writeShort(this.constant.element.length);
			for (int i = 1; i < this.constant.element.length; i++)
			{
				Constant element = this.constant.element[i];
				if (element.type() == ConstantType.CONSTANT_NULL) continue;
				out.write(element.toByteArray());
			}

			out.writeShort(this.access);
			out.writeShort(this.self);
			out.writeShort(this.supers);

			out.writeShort(this.interfaces.length);
			for (int anInterface : this.interfaces)
			{
				out.writeShort(anInterface);
			}

			out.writeShort(this.field.length);
			for (Field field : this.field)
			{
				out.write(field.toByteArray());
			}
			out.writeShort(this.method.length);
			for (Method method : this.method)
			{
				out.write(method.toByteArray());
			}

			out.writeShort(this.attribute.length);
			for (Attribute a : this.attribute)
			{
				out.write(a.toByteArray());
			}

			return bout.toByteArray();
		}
		catch (IOException e)
		{
			throw new ClassSerializeException("Can not serialize class", e);
		}
	}
}
