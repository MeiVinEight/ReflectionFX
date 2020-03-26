package org.mve.util.asm;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.LinkedList;

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
	private final short accessFlag;
	private final short thisClassIndex;
	private final short interfaceCount;
	private final short[] interfaces;
	private final short fieldCount;
//	private final LinkedList<FieldParser> fields;
//	private final short methodCount;
//	private final LinkedList<MethodParser> methods;
//	private final short attributeCount;
	private final LinkedList<Attribute> attributes = new LinkedList<>();

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
						datain.read(bytes);
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
			this.fieldCount = datain.readShort();
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

	public ConstantPool getConstantPool()
	{
		return constantPool;
	}
}
