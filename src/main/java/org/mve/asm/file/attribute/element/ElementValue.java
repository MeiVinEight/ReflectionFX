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
			case 'B':
			case 'C':
			case 'D':
			case 'F':
			case 'I':
			case 'J':
			case 'S':
			case 'Z':
			case 's':
			{
				ElementConstantValue value = new ElementConstantValue();
				value.type = tag;
				value.value = input.readUnsignedShort();
				return value;
			}
			case 'e':
			{
				ElementEnumValue value = new ElementEnumValue();
				value.type = tag;
				value.name = input.readUnsignedShort();
				value.value = input.readUnsignedShort();
				return value;
			}
			case 'c':
			{
				ElementClassValue value = new ElementClassValue();
				value.type = tag;
				value.clazz = input.readUnsignedShort();
				return value;
			}
			case '@':
			{
				ElementAnnotationValue value = new ElementAnnotationValue();
				value.type = tag;
				value.annotation = Annotation.read(file, input);
				return value;
			}
			case '[':
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
