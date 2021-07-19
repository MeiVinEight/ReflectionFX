package org.mve.asm.file.attribute.element;

import org.mve.asm.file.Class;
import org.mve.asm.file.attribute.annotation.Annotation;
import org.mve.io.RandomAccessByteArray;

public abstract class ElementValue
{
	public int type;

	public abstract int length();

	public abstract byte[] toByteArray();

	public static ElementValue read(Class file, RandomAccessByteArray input)
	{
		int tag = input.readUnsignedByte();
		switch (tag)
		{
			case ElementValueType.BYTE:
			case ElementValueType.CHAR:
			case ElementValueType.DOUBLE:
			case ElementValueType.FLOAT:
			case ElementValueType.INT:
			case ElementValueType.LONG:
			case ElementValueType.SHORT:
			case ElementValueType.BOOLEAN:
			case ElementValueType.STRING:
			{
				ElementConstantValue value = new ElementConstantValue();
				value.type = tag;
				value.value = input.readUnsignedShort();
				return value;
			}
			case ElementValueType.ENUM:
			{
				ElementEnumValue value = new ElementEnumValue();
				value.type = tag;
				value.name = input.readUnsignedShort();
				value.value = input.readUnsignedShort();
				return value;
			}
			case ElementValueType.CLASS:
			{
				ElementClassValue value = new ElementClassValue();
				value.type = tag;
				value.clazz = input.readUnsignedShort();
				return value;
			}
			case ElementValueType.ANNOTATION:
			{
				ElementAnnotationValue value = new ElementAnnotationValue();
				value.type = tag;
				value.annotation = Annotation.read(file, input);
				return value;
			}
			case ElementValueType.ARRAY:
			{
				ElementArrayValue value = new ElementArrayValue();
				value.type = tag;
				int count = input.readUnsignedShort();
				for (int i = 0; i < count; i++)
				{
					value.value(read(file, input));
				}
				return value;
			}
			default: throw new ClassFormatError();
		}
	}
}
