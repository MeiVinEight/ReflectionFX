package org.mve.util.asm.file;

import org.mve.util.Binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * A class file consists of a stream of 8-bit bytes.
 * 16-bit and 32-bit quantities are constructed by
 * reading in two and four consecutive 8-bit bytes,
 * respectively. Multibyte data items are always stored
 * in big-endian order, where the high bytes come first.
 * This chapter defines the data types u1, u2, and u4 to
 * represent an unsigned one-, two-, or four-byte quantity,
 * respectively.
 *
 * In the Java SE Platform API, the class file format is
 * supported by interfaces java.io.DataInput and java.io.DataOutput
 * and classes such as java.io.DataInputStream and java.io.DataOutputStream.
 * For example, values of the types u1, u2, and u4 may be
 * read by methods such as readUnsignedByte, readUnsignedShort,
 * and readInt of the interface java.io.DataInput.
 *
 * This chapter presents the class file format using
 * pseudostructures written in a C-like structure notation.
 * To avoid confusion with the fields of classes and class
 * instances, etc., the contents of the structures describing
 * the class file format are referred to as items. Successive
 * items are stored in the class file sequentially, without
 * padding or alignment.
 *
 * Tables, consisting of zero or more variable-sized items,
 * are used in several class file structures. Although we
 * use C-like array syntax to refer to table items, the fact
 * that tables are streams of varying-sized structures means
 * that it is not possible to translate a table index directly
 * to a byte offset into the table.
 *
 * Where we refer to a data structure as an array, it consists
 * of zero or more contiguous fixed-sized items and can be indexed like an array.
 *
 * Reference to an ASCII character in this chapter should be
 * interpreted to mean the Unicode code point corresponding to the ASCII character.
 */
public class ClassFile implements Binary
{
	/**
	 * The magic item supplies the magic number identifying
	 * the class file format; it has the value 0xCAFEBABE.
	 */
	private int header;

	/**
	 * The values of the minor_version and major_version items
	 * are the minor and major version numbers of this class file.
	 * Together, a major and a minor version number determine the
	 * version of the class file format. If a class file has major
	 * version number M and minor version number m, we denote the
	 * version of its class file format as M.m.
	 *
	 * A Java Virtual Machine implementation which conforms to Java
	 * SE N must support exactly the major versions of the class file
	 * format specified in the third column of <a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.1-200-B.2">Table 4.1-A<a/>, "Supported
	 * major versions". The notation A .. B means major versions A
	 * through B, inclusive of both A and B. The second column,
	 * "Corresponding major version", shows the major version introduced
	 * by each Java SE release, that is, the first release that could have
	 * accepted a class file containing that major_version item. For very
	 * early releases, the JDK version is shown instead of the Java SE release.
	 *
	 *
	 * 				Table 4.1-A. class file format major versions
	 *
	 * 	Java SE			Corresponding major version			Supported major versions
	 * 	1.0.2						45								45
	 * 	1.1							45								45
	 * 	1.2							46								45..46
	 * 	1.3							47								45..47
	 * 	1.4							48								45..48
	 * 	5.0							49								45..49
	 * 	6							50								45..50
	 * 	7							51								45..51
	 * 	8							52								45..52
	 * 	9							53								45..53
	 * 	10							54								45..54
	 * 	11							55								45..55
	 * 	12							56								45..56
	 * 	13							57								45..57
	 * 	14							58								45..58
	 *
	 * 	For a class file whose major_version is 56 or above, the minor_version must be 0 or 65535.
	 *
	 * 	For a class file whose major_version is between 45 and 55 inclusive, the minor_version may be any value.
	 *
	 * 	A historical perspective is warranted on JDK support for class file format
	 * 	versions. JDK 1.0.2 supported versions 45.0 through 45.3 inclusive. JDK 1.1
	 * 	supported versions 45.0 through 45.65535 inclusive. When JDK 1.2 introduced
	 * 	support for major version 46, the only minor version supported under that
	 * 	major version was 0. Later JDKs continued the practice of introducing support
	 * 	for a new major version (47, 48, etc) but supporting only a minor version of
	 * 	0 under the new major version. Finally, the introduction of preview features
	 * 	in Java SE 12 (see below) motivated a standard role for the minor version of
	 * 	the class file format, so JDK 12 supported minor versions of 0 and 65535 under
	 * 	major version 56. Subsequent JDKs introduce support for N.0 and N.65535 where
	 * 	N is the corresponding major version of the implemented Java SE Platform.
	 * 	For example, JDK 13 supports 57.0 and 57.65535.
	 *
	 */
	private short minorVersion;

	/**
	 * major version of this class's compiler
	 * This number determines the minimum JRE
	 * version required to load this class
	 */
	private short majorVersion;

	/**
	 * Java Virtual Machine instructions do not rely on the run-time layout of classes,
	 * interfaces, class instances, or arrays. Instead, instructions refer to symbolic
	 * information in the constant_pool table.
	 *
	 * All constant_pool table entries have the following general format:
	 * 			cp_info {
	 * 				u1 tag;
	 * 				u1 info[];
	 * 			}
	 *
	 * Each entry in the constant_pool table must begin with a 1-byte tag indicating
	 * the kind of constant denoted by the entry. There are 17 kinds of constant, listed
	 * in <a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4-140">Table 4.4-A<a/> with their corresponding tags, and ordered by their section number
	 * in this chapter. Each tag byte must be followed by two or more bytes giving information
	 * about the specific constant. The format of the additional information depends on the tag
	 * byte, that is, the content of the info array varies with the value of tag.
	 *
	 *
	 * 				Table 4.4-A. Constant pool tags (by section)
	 * 			Constant Kind				Tag 			Section
	 * 			CONSTANT_Class 				7				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.1">§4.4.1<a/>
	 * 			CONSTANT_Fieldref 			9				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.2">§4.4.2<a/>
	 * 			CONSTANT_Methodref 			10				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.2">§4.4.2<a/>
	 * 			CONSTANT_InterfaceMethodref 11				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.2">§4.4.2<a/>
	 * 			CONSTANT_String 			8				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.3">§4.4.3<a/>
	 * 			CONSTANT_Integer 			3				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.4">§4.4.4<a/>
	 * 			CONSTANT_Float				4				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.4">§4.4.4<a/>
	 * 			CONSTANT_Long				5				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.5">§4.4.5<a/>
	 * 			CONSTANT_Double				6				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.5">§4.4.5<a/>
	 * 			CONSTANT_NameAndType		12				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.6">§4.4.6<a/>
	 * 			CONSTANT_Utf8				1				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.7">§4.4.7<a/>
	 * 			CONSTANT_MethodHandle		15				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.8">§4.4.8<a/>
	 * 			CONSTANT_MethodType			16				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.9">§4.4.9<a/>
	 * 			CONSTANT_Dynamic			17				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.10">§4.4.10<a/>
	 * 			CONSTANT_InvokeDynamic		18				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.10">§4.4.10<a/>
	 * 			CONSTANT_Module				19				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.11">§4.4.11<a/>
	 * 			CONSTANT_Package			20				<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.4.12">§4.4.12<a/>
	 *
	 *
	 */
	private final ConstantPool constantPool = new ConstantPool();

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
	private short superClassIndex;
	private short interfaceCount;
	private short[] interfaces;
	private short fieldCount;
	private ClassField[] fields = new ClassField[0];
	private short methodCount;
	private ClassMethod[] methods = new ClassMethod[0];
	private short attributeCount;
	private Attribute[] attributes = new Attribute[0];

	public ClassFile(){}

	public ClassFile(byte[] code)
	{
		ByteArrayInputStream in = null;
		try
		{
			in = new ByteArrayInputStream(code);
			DataInputStream datain = new DataInputStream(in);
			this.header = datain.readInt();
			if (this.header != 0XCAFEBABE) throw new ClassFormatError("Invalid file head magic value "+this.header);
			this.minorVersion = datain.readShort();
			this.majorVersion = datain.readShort();
			short constantPoolSize = datain.readShort();
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
						i++;
						long value = datain.readLong();
						this.constantPool.addConstantPoolElement(new ConstantLong(value));
						this.constantPool.addConstantPoolElement(new ConstantNull());
						break;
					}
					case 6:
					{
						i++;
						double value = datain.readDouble();
						this.constantPool.addConstantPoolElement(new ConstantDouble(value));
						this.constantPool.addConstantPoolElement(new ConstantNull());
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
					default: throw new ClassFormatError("Unknown constant tag "+type);
				}
			}
			this.accessFlag = datain.readShort();
			this.thisClassIndex = datain.readShort();
			this.superClassIndex = datain.readShort();
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

	public void setMinorVersion(short minorVersion)
	{
		this.minorVersion = minorVersion;
	}

	public void setMajorVersion(short majorVersion)
	{
		this.majorVersion = majorVersion;
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

	public short getSuperClassIndex()
	{
		return superClassIndex;
	}

	public void setSuperClassIndex(short superClassIndex)
	{
		this.superClassIndex = superClassIndex;
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
		ClassMethod[] arr = new ClassMethod[this.methodCount+1];
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

	public void setAttribute(int index, Attribute attribute)
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

	@Override
	public byte[] toByteArray()
	{
		try
		{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bout);
			out.writeInt(this.header);
			out.writeShort(this.minorVersion);
			out.writeShort(this.majorVersion);
			out.writeShort(this.constantPool.getConstantPoolSize());
			return bout.toByteArray();
		}
		catch (IOException e)
		{
			throw new ClassSerializeException("Can not serialize class", e);
		}
	}
}
