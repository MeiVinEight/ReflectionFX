package org.mve.util.asm;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ElementValueReader
{
	public static ElementValue read(ClassFile file, InputStream input) throws IOException
	{
		DataInputStream in = new DataInputStream(input);
		byte tag = in.readByte();
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
				value.setConstantValueIndex(in.readShort());
				return value;
			}
			case 'e':
			{
				ElementValueEnum value = new ElementValueEnum(tag);
				value.setTypeNameIndex(in.readShort());
				value.setConstNameIndex(in.readShort());
				return value;
			}
			case 'c':
			{
				ElementValueClass value = new ElementValueClass(tag);
				value.setClassIndex(in.readShort());
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
				short count = in.readShort();
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
