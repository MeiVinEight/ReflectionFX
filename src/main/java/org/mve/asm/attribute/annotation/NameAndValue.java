package org.mve.asm.attribute.annotation;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.annotation.value.Array;
import org.mve.asm.attribute.annotation.value.Enum;
import org.mve.asm.constant.Type;
import org.mve.asm.file.attribute.element.ElementAnnotationValue;
import org.mve.asm.file.attribute.element.ElementArrayValue;
import org.mve.asm.file.attribute.element.ElementClassValue;
import org.mve.asm.file.attribute.element.ElementConstantValue;
import org.mve.asm.file.attribute.element.ElementEnumValue;
import org.mve.asm.file.attribute.element.ElementValue;
import org.mve.asm.file.attribute.element.ElementValueType;
import org.mve.asm.file.constant.ConstantArray;

public class NameAndValue
{
	public String name;
	public Object value;

	public NameAndValue(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}

	public static ElementValue value(ConstantArray array, Object value)
	{
		int tag;
		if (value instanceof Byte)
		{
			tag = ElementValueType.BYTE;
		}
		else if (value instanceof Short)
		{
			tag = ElementValueType.SHORT;
		}
		else if (value instanceof Integer)
		{
			tag = ElementValueType.INT;
		}
		else if (value instanceof Boolean)
		{
			tag = ElementValueType.BOOLEAN;
		}
		else if (value instanceof Character)
		{
			tag = ElementValueType.CHAR;
		}
		else if (value instanceof Float)
		{
			tag = ElementValueType.FLOAT;
		}
		else if (value instanceof Double)
		{
			tag = ElementValueType.DOUBLE;
		}
		else if (value instanceof Long)
		{
			tag = ElementValueType.LONG;
		}
		else if (value instanceof String)
		{
			tag = ElementValueType.STRING;
		}
		else if (value instanceof Enum)
		{
			tag = ElementValueType.ENUM;
		}
		else if (value instanceof Type)
		{
			tag = ElementValueType.CLASS;
		}
		else if (value instanceof Annotation)
		{
			tag = ElementValueType.ANNOTATION;
		}
		else if (value instanceof Array)
		{
			tag = ElementValueType.ARRAY;
		}
		else
		{
			tag = 0;
		}

		switch (tag)
		{
			case ElementValueType.BYTE:
			case ElementValueType.SHORT:
			case ElementValueType.INT:
			case ElementValueType.BOOLEAN:
			case ElementValueType.CHAR:
			{
				ElementConstantValue v = new ElementConstantValue();
				v.type = tag;
				v.value = ConstantPoolFinder.findInteger(array, ((Number)value).intValue());
				return v;
			}
			case ElementValueType.LONG:
			{
				ElementConstantValue v = new ElementConstantValue();
				v.type = tag;
				v.value = ConstantPoolFinder.findLong(array, ((Number)value).longValue());
				return v;
			}
			case ElementValueType.FLOAT:
			{
				ElementConstantValue v = new ElementConstantValue();
				v.type = tag;
				v.value = ConstantPoolFinder.findFloat(array, ((Number)value).floatValue());
				return v;
			}
			case ElementValueType.DOUBLE:
			{
				ElementConstantValue v = new ElementConstantValue();
				v.type = tag;
				v.value = ConstantPoolFinder.findDouble(array, ((Number)value).doubleValue());
				return v;
			}
			case ElementValueType.STRING:
			{
				ElementConstantValue v = new ElementConstantValue();
				v.type = tag;
				v.value = ConstantPoolFinder.findUTF8(array, (String) value);
				return v;
			}
			case ElementValueType.ENUM:
			{
				ElementEnumValue v = new ElementEnumValue();
				v.type = tag;
				Enum e = (Enum) value;
				v.name = ConstantPoolFinder.findUTF8(array, e.name);
				v.value = ConstantPoolFinder.findUTF8(array, e.value);
				return v;
			}
			case ElementValueType.CLASS:
			{
				ElementClassValue v = new ElementClassValue();
				v.type = tag;
				v.clazz = ConstantPoolFinder.findClass(array, ((Type)value).getType());
				return v;
			}
			case ElementValueType.ANNOTATION:
			{
				ElementAnnotationValue v = new ElementAnnotationValue();
				v.type = tag;
				v.annotation = ((Annotation)value).annotation(array);
				return v;
			}
			case ElementValueType.ARRAY:
			{
				Array arr = (Array) value;
				ElementArrayValue v = new ElementArrayValue();
				v.type = tag;
				v.value = new ElementValue[arr.value.length];
				for (int i=0; i<arr.value.length; i++)
				{
					v.value[i] = value(array, arr.value[i]);
				}
				return v;
			}
			default: return null;
		}
	}
}
