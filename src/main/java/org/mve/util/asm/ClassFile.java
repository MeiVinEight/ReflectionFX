package org.mve.util.asm;

import com.sun.istack.internal.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Objects;

public class ClassFile
{
	/**
	 * 4 bytes magic value in file head
	 * always is 0xCAFEBABE
	 */
	private final int header;

	/**
	 * minor version of this class's compiler
	 */
	private final short minorVersion;

	/**
	 * major version of this class's compiler
	 * This number determines the minimum JRE
	 * version required to load this class
	 */
	private final short majorVersion;

	/**
	 * The constant pool of the class file
	 */
	private final ConstantPool constantPool;

	/**
	 * The value of the access_flags item is
	 * a mask of flags used to denote access
	 * permissions to and properties of this
	 * class or interface. The interpretation
	 * of each flag, when set, is specified in
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.1-200-E.1">Table 4.1-A.<a/>
	 *
	 *
	 * 									Table 4.1-A
	 *
	 * ACC_PUBLIC		0x0001		Declared public; may be accessed from outside its package.
	 * ACC_FINAL		0x0010		Declared final; no subclasses allowed.
	 * ACC_SUPER		0x0020		Treat superclass methods specially when invoked by the invokespecial instruction.
	 * ACC_INTERFACE	0x0200		Is an interface, not a class.
	 * ACC_ABSTRACT		0x0400		Declared abstract; must not be instantiated.
	 * ACC_SYNTHETIC	0x1000		Declared synthetic; not present in the source code.
	 * ACC_ANNOTATION	0x2000		Declared as an annotation type.
	 * ACC_ENUM			0x4000		Declared as an enum type.
	 *
	 *
	 * An interface is distinguished by the
	 * ACC_INTERFACE flag being set. If the
	 * ACC_INTERFACE flag is not set, this
	 * class file defines a class, not an interface.
	 *
	 * If the ACC_INTERFACE flag is set, the
	 * ACC_ABSTRACT flag must also be set, and
	 * the ACC_FINAL, ACC_SUPER, and ACC_ENUM
	 * flags set must not be set.
	 */
	private short accessFlag;
	private short thisClassIndex;
	private short interfaceCount;
	private short[] interfaces;
	private short fieldCount;
	private ClassField[] fields = new ClassField[0];
	private short methodCount;
	private ClassMethod[] methods = new ClassMethod[0];
	private short attributeCount;
	private Attribute[] attributes = new Attribute[0];

	public ClassFile(byte[] code)
	{
		ByteArrayInputStream in = null;
		try
		{
			in = new ByteArrayInputStream(code);
			DataInputStream datain = new DataInputStream(in);
			this.header = datain.readInt();
			this.minorVersion = datain.readShort();
			this.majorVersion = datain.readShort();
			short constantPoolSize = datain.readShort();
			this.constantPool = new ConstantPool();
			for (int i = 1; i < constantPoolSize; i++)
			{
				byte type = datain.readByte();
				switch (type)
				{
					case 1:
					{
						short length = datain.readShort();
						byte[] bytes = new byte[length];
						if (length != datain.read(bytes)) throw new ClassFormatError();
						this.constantPool.addConstantPoolElement(new ConstantUTF8(length, new String(bytes)));
						break;
					}
					case 3:
					{
						int value = datain.readInt();
						this.constantPool.addConstantPoolElement(new ConstantInteger(value));
						break;
					}
					case 4:
					{
						float value = datain.readFloat();
						this.constantPool.addConstantPoolElement(new ConstantFloat(value));
						break;
					}
					case 5:
					{
						long value = datain.readLong();
						this.constantPool.addConstantPoolElement(new ConstantLong(value));
						break;
					}
					case 6:
					{
						double value = datain.readDouble();
						this.constantPool.addConstantPoolElement(new ConstantDouble(value));
						break;
					}
					case 7:
					{
						short nameIndex = datain.readShort();
						this.constantPool.addConstantPoolElement(new ConstantClass(nameIndex));
						break;
					}
					case 8:
					{
						short stringIndex = datain.readShort();
						this.constantPool.addConstantPoolElement(new ConstantString(stringIndex));
						break;
					}
					case 9:
					{
						short classIndex = datain.readShort();
						short nameAndTypeIndex = datain.readShort();
						this.constantPool.addConstantPoolElement(new ConstantFieldReference(classIndex, nameAndTypeIndex));
						break;
					}
					case 10:
					{
						short classIndex = datain.readShort();
						short nameAndTypeIndex = datain.readShort();
						this.constantPool.addConstantPoolElement(new ConstantMethodReference(classIndex, nameAndTypeIndex));
						break;
					}
					case 11:
					{
						short classIndex = datain.readShort();
						short nameAndTypeIndex = datain.readShort();
						this.constantPool.addConstantPoolElement(new ConstantInterfaceMethodReference(classIndex, nameAndTypeIndex));
						break;
					}
					case 12:
					{
						short nameIndex = datain.readShort();
						short descriptorIndex = datain.readShort();
						this.constantPool.addConstantPoolElement(new ConstantNameAndType(nameIndex, descriptorIndex));
						break;
					}
					case 15:
					{
						byte kind = datain.readByte();
						short index = datain.readShort();
						this.constantPool.addConstantPoolElement(new ConstantMethodHandle(kind, index));
						break;
					}
					case 16:
					{
						short index = datain.readShort();
						this.constantPool.addConstantPoolElement(new ConstantMethodType(index));
						break;
					}
					case 18:
					{
						short bootstrapMethodAttributeIndex = datain.readShort();
						short nameAndTypeIndex = datain.readShort();
						this.constantPool.addConstantPoolElement(new ConstantInvokeDynamic(bootstrapMethodAttributeIndex, nameAndTypeIndex));
						break;
					}
					case 19:
					{
						short nameIndex = datain.readShort();
						this.constantPool.addConstantPoolElement(new ConstantModule(nameIndex));
						break;
					}
					case 20:
					{
						short index = datain.readShort();
						this.constantPool.addConstantPoolElement(new ConstantPackage(index));
						break;
					}
					default: break;
				}
			}
			this.accessFlag = datain.readShort();
			this.thisClassIndex = datain.readShort();
			this.interfaceCount = datain.readShort();
			short[] shorts = new short[interfaceCount];
			for (int i = 0; i < this.interfaceCount; i++) shorts[i] = datain.readShort();
			this.interfaces = shorts;
			int count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				ClassField field = new ClassField();
				field.setAccessFlag(datain.readShort());
				field.setNameIndex(datain.readShort());
				field.setDescriptorIndex(datain.readShort());
				int c = datain.readShort() & 0XFFFF;
				for (int j = 0; j < c; j++)
				{
					field.addAttribute(AttributeReader.read(this, datain));
				}
				this.addField(field);
			}
			count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				ClassMethod method = new ClassMethod();
				method.setAccessFlag(datain.readShort());
				method.setNameIndex(datain.readShort());
				method.setDescriptorIndex(datain.readShort());
				int c = datain.readShort() & 0XFFFF;
				for (int j = 0; j < c; j++)
				{
					method.addAttribute(AttributeReader.read(this, datain));
				}
				this.addMethod(method);
			}
			count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				this.addAttribute(AttributeReader.read(this, datain));
			}
			datain.close();
		}
		catch (Exception e)
		{
			throw new ClassParseException(e);
		}
		finally
		{
			if (in != null) try
			{
				in.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public int getHeader()
	{
		return header;
	}

	public short getMinorVersion()
	{
		return minorVersion;
	}

	public short getMajorVersion()
	{
		return majorVersion;
	}

	public ConstantPool getConstantPool()
	{
		return constantPool;
	}

	public short getAccessFlag()
	{
		return accessFlag;
	}

	public void setAccessFlag(short accessFlag)
	{
		this.accessFlag = accessFlag;
	}

	public short getThisClassIndex()
	{
		return thisClassIndex;
	}

	public void setThisClassIndex(short thisClassIndex)
	{
		this.thisClassIndex = thisClassIndex;
	}

	public short getInterfaceCount()
	{
		return interfaceCount;
	}

	public void addInterface(short cpIndex)
	{
		short[] arr = new short[this.interfaceCount+1];
		System.arraycopy(this.interfaces, 0, arr, 0, this.interfaceCount);
		arr[this.interfaceCount] = cpIndex;
		this.interfaces = arr;
		this.interfaceCount++;
	}

	public void setInterface(int index, short cpIndex)
	{
		this.interfaces[index] = cpIndex;
	}

	public short getInterface(int index)
	{
		return this.interfaces[index];
	}

	public short getFieldCount()
	{
		return 	this.fieldCount;
	}

	public void addField(ClassField field)
	{
		ClassField[] arr = new ClassField[this.fieldCount+1];
		System.arraycopy(this.fields, 0, arr, 0, this.fieldCount);
		arr[this.fieldCount] = Objects.requireNonNull(field);
		this.fields = arr;
		this.fieldCount++;
	}

	public void setField(int index, ClassField field)
	{
		this.fields[index] = Objects.requireNonNull(field);
	}

	public ClassField getField(int index)
	{
		return this.fields[index];
	}

	public short getMethodCount()
	{
		return methodCount;
	}

	public void addMethod(ClassMethod method)
	{
		ClassMethod[] arr = new ClassMethod[this.methodCount];
		System.arraycopy(this.methods, 0, arr, 0, this.methodCount);
		arr[this.methodCount] = Objects.requireNonNull(method);
		this.methods = arr;
		this.methodCount++;
	}

	public void setMethod(int index, ClassMethod method)
	{
		this.methods[index] = Objects.requireNonNull(method);
	}

	public ClassMethod getMethod(int index)
	{
		return this.methods[index];
	}

	public short getAttributeCount()
	{
		return attributeCount;
	}

	public Attribute getAttribute(int index)
	{
		return this.attributes[index];
	}

	public void setAttribute(int index, @NotNull Attribute attribute)
	{
		this.attributes[index] = Objects.requireNonNull(attribute);
	}

	public void addAttribute(Attribute attribute)
	{
		Attribute[] arr = new Attribute[this.attributeCount+1];
		System.arraycopy(this.attributes, 0, arr, 0, this.attributeCount);
		arr[this.attributeCount] = Objects.requireNonNull(attribute);
		this.attributes = arr;
		this.attributeCount++;
	}
}
