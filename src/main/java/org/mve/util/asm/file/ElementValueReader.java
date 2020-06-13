package org.mve.util.asm.file;

import org.mve.io.RandomAccessByteArray;

public class ElementValueReader
{
	public static ElementValue read(ClassFile file, RandomAccessByteArray input)
	{
		byte tag = input.readByte();
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
				ElementValueConstant value = new ElementValueConstant(tag);
				value.setConstantValueIndex(input.readShort());
				return value;
			}
			case 'e':
			{
				ElementValueEnum value = new ElementValueEnum(tag);
				value.setTypeNameIndex(input.readShort());
				value.setConstNameIndex(input.readShort());
				return value;
			}
			case 'c':
			{
				ElementValueClass value = new ElementValueClass(tag);
				value.setClassIndex(input.readShort());
				return value;
			}
			case '@':
			{
				ElementValueAnnotation value = new ElementValueAnnotation(tag);
				value.setAnnotation(AnnotationReader.read(file, input));
				return value;
			}
			case '[':
			{
				ElementValueArray value = new ElementValueArray(tag);
				short count = input.readShort();
				for (int i = 0; i < count; i++)
				{
					value.addElementValue(ElementValueReader.read(file, input));
				}
				return value;
			}
			default: throw new ClassFormatError();
		}
	}
}
