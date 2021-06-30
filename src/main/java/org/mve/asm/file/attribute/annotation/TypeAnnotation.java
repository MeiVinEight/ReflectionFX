package org.mve.asm.file.attribute.annotation;

import org.mve.asm.file.Class;
import org.mve.asm.file.attribute.annotation.type.path.Path;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationPath;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationCatchValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationEmptyValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationFormalParameterValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationLocalVariableValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationOffsetValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationSupertypeValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationTypeArgumentValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationTargetTypeParameter;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationTypeParameterBoundValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationThrowsValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationValue;
import org.mve.asm.file.attribute.annotation.type.local.LocalVariableValue;
import org.mve.asm.file.attribute.element.ElementNameAndValue;
import org.mve.asm.file.attribute.element.ElementValue;
import org.mve.io.RandomAccessByteArray;

import java.util.Arrays;

public class TypeAnnotation
{
	public int kind;
	public TypeAnnotationValue value;
	public TypeAnnotationPath path;
	public int type;
	public ElementNameAndValue[] element = new ElementNameAndValue[0];

	public void element(ElementNameAndValue value)
	{
		this.element = Arrays.copyOf(this.element, this.element.length + 1);
		this.element[this.element.length-1] = value;
	}

	public int length()
	{
		int len = 5 + value.length() + path.length();
		for (ElementNameAndValue s : this.element) len += s.length();
		return len;
	}

	public byte[] toByteArray()
	{
		int len = this.length();
		int index = 0;
		byte[] b = new byte[len];
		b[index++] = (byte) this.kind;
		int l = this.value.length();
		System.arraycopy(this.value.toByteArray(), 0, b, index, l);
		index += l;
		l = this.path.length();
		System.arraycopy(this.path.toByteArray(), 0, b, index, l);
		index+=l;
		b[index++] = (byte) ((this.type >>> 8) & 0XFF);
		b[index++] = (byte) (this.type & 0XFF);
		b[index++] = (byte) ((this.element.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.element.length & 0XFF);
		for (ElementNameAndValue s : this.element)
		{
			l = s.length();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}

	public static TypeAnnotation read(Class file, RandomAccessByteArray input)
	{
		TypeAnnotation annotation = new TypeAnnotation();
		int targetType = input.readUnsignedByte();
		switch (targetType)
		{
			case 0X00:
			case 0X01:
			{
				TypeAnnotationTargetTypeParameter target = new TypeAnnotationTargetTypeParameter();
				target.type = targetType;
				target.parameter = input.readUnsignedByte();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case 0X10:
			{
				TypeAnnotationSupertypeValue target = new TypeAnnotationSupertypeValue();
				target.type = targetType;
				target.supertype = input.readUnsignedShort();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case 0X11:
			case 0X12:
			{
				TypeAnnotationTypeParameterBoundValue target = new TypeAnnotationTypeParameterBoundValue();
				target.type = targetType;
				target.parameter = input.readUnsignedByte();
				target.bound = input.readUnsignedByte();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case 0X13:
			case 0X14:
			case 0X15:
			{
				TypeAnnotationEmptyValue target = new TypeAnnotationEmptyValue();
				target.type = targetType;
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case 0X16:
			{
				TypeAnnotationFormalParameterValue target = new TypeAnnotationFormalParameterValue();
				target.type = targetType;
				target.parameter = input.readUnsignedByte();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case 0X17:
			{
				TypeAnnotationThrowsValue target = new TypeAnnotationThrowsValue();
				target.type = targetType;
				target.throwsType = input.readUnsignedShort();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case 0X40:
			case 0X41:
			{
				TypeAnnotationLocalVariableValue target = new TypeAnnotationLocalVariableValue();
				target.type = targetType;
				int count = input.readUnsignedShort() & 0XFFFF;
				for (int i = 0; i < count; i++)
				{
					LocalVariableValue table = new LocalVariableValue();
					table.start = input.readUnsignedShort();
					table.length = input.readUnsignedShort();
					table.slot = input.readUnsignedShort();
					target.local(table);
				}
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case 0X42:
			{
				TypeAnnotationCatchValue target = new TypeAnnotationCatchValue();
				target.type = targetType;
				target.exception = input.readUnsignedShort();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case 0X43:
			case 0X44:
			case 0X45:
			case 0X46:
			{
				TypeAnnotationOffsetValue target = new TypeAnnotationOffsetValue();
				target.type = targetType;
				target.offset = input.readUnsignedShort();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case 0X47:
			case 0X48:
			case 0X49:
			case 0X4A:
			case 0X4B:
			{
				TypeAnnotationTypeArgumentValue target = new TypeAnnotationTypeArgumentValue();
				target.type = targetType;
				target.offset = input.readUnsignedShort();
				target.argument = input.readUnsignedByte();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
		}
		TypeAnnotationPath path = new TypeAnnotationPath();
		int count = input.readUnsignedByte() & 0XFF;
		for (int i = 0; i < count; i++)
		{
			Path p = new Path();
			p.kind = input.readUnsignedByte();
			p.argument = input.readUnsignedByte();
			path.path(p);
		}
		annotation.path = path;
		annotation.type = input.readUnsignedShort();
		count = input.readUnsignedShort() & 0XFFFF;
		for (int i = 0; i < count; i++)
		{
			ElementNameAndValue pairs = new ElementNameAndValue();
			pairs.name = input.readUnsignedShort();
			pairs.value = ElementValue.read(file, input);
			annotation.element(pairs);
		}
		return annotation;
	}
}
