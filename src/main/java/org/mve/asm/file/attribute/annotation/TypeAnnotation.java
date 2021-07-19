package org.mve.asm.file.attribute.annotation;

import org.mve.asm.file.Class;
import org.mve.asm.file.attribute.annotation.type.location.Location;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationLocation;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationCatchValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationEmptyValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationFormalParameterValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationLocalVariableValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationOffsetValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationSupertypeValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationTypeArgumentValue;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationTypeParameterValue;
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
	/* type constants declaration */

	/**
	 * type parameter declaration of generic class or interface
	 */
	public static final int CLASS_TYPE_PARAMETER = 0x00;

	/**
	 * type parameter declaration of generic method or constructor
	 */
	public static final int METHOD_TYPE_PARAMETER = 0x01;

	/**
	 * type in extends or implements clause of class declaration
	 * (including the direct superclass or direct superinterface
	 * of an anonymous class declaration), or in extends clause of
	 * interface declaration
	 */
	public static final int CLASS_EXTENDS = 0x10;

	/**
	 * type in bound of type parameter declaration of generic class
	 * or interface
	 */
	public static final int CLASS_TYPE_PARAMETER_BOUND = 0x11;

	/**
	 * type in bound of type parameter declaration of generic method
	 * or constructor
	 */
	public static final int METHOD_TYPE_PARAMETER_BOUND = 0x12;

	/**
	 * type in field declaration
	 */
	public static final int FIELD = 0x13;

	/**
	 * return type of method, or type of newly constructed object
	 */
	public static final int METHOD_RETURN = 0x14;

	/**
	 * receiver type of method or constructor
	 */
	public static final int METHOD_RECEIVER = 0x15;

	/**
	 * type in formal parameter declaration of method, constructor,
	 * or lambda expression
	 */
	public static final int METHOD_FORMAL_PARAMETER = 0x16;

	/**
	 * type in throws clause of method or constructor
	 */
	public static final int THROWS = 0x17;

	/**
	 * type in local variable declaration
	 */
	public static final int LOCAL_VARIABLE = 0x40;

	/**
	 * type in resource variable declaration
	 */
	public static final int RESOURCE_VARIABLE = 0x41;

	/**
	 * type in exception parameter declaration
	 */
	public static final int EXCEPTION_PARAMETER = 0x42;

	/**
	 * type in instanceof expression
	 */
	public static final int INSTANCEOF = 0x43;

	/**
	 * type in new expression
	 */
	public static final int NEW = 0x44;

	/**
	 * type in method reference expression using ::new
	 */
	public static final int CONSTRUCTOR_REFERENCE = 0x45;

	/**
	 * type in method reference expression using ::Identifier
	 */
	public static final int METHOD_REFERENCE = 0x46;

	/**
	 * type in cast expression
	 */
	public static final int CAST = 0x47;

	/**
	 * type argument for generic constructor in new expression
	 * or explicit constructor invocation statement
	 */
	public static final int CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT = 0x48;

	/**
	 * type argument for generic method in method invocation expression
	 */
	public static final int METHOD_INVOCATION_TYPE_ARGUMENT = 0x49;

	/**
	 * type argument for generic constructor in method reference expression using ::new
	 */
	public static final int CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT = 0x4A;

	/**
	 * type argument for generic method in method reference expression using ::Identifier
	 */
	public static final int METHOD_REFERENCE_TYPE_ARGUMENT = 0x4B;

	public int kind;
	public TypeAnnotationValue value;
	public TypeAnnotationLocation location;
	public int type;
	public ElementNameAndValue[] element = new ElementNameAndValue[0];

	public void element(ElementNameAndValue value)
	{
		this.element = Arrays.copyOf(this.element, this.element.length + 1);
		this.element[this.element.length-1] = value;
	}

	public int length()
	{
		int len = 5 + value.length() + location.length();
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
		l = this.location.length();
		System.arraycopy(this.location.toByteArray(), 0, b, index, l);
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
			case CLASS_TYPE_PARAMETER:
			case METHOD_TYPE_PARAMETER:
			{
				TypeAnnotationTypeParameterValue target = new TypeAnnotationTypeParameterValue();
				target.type = targetType;
				target.parameter = input.readUnsignedByte();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case CLASS_EXTENDS:
			{
				TypeAnnotationSupertypeValue target = new TypeAnnotationSupertypeValue();
				target.type = targetType;
				target.supertype = input.readUnsignedShort();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case CLASS_TYPE_PARAMETER_BOUND:
			case METHOD_TYPE_PARAMETER_BOUND:
			{
				TypeAnnotationTypeParameterBoundValue target = new TypeAnnotationTypeParameterBoundValue();
				target.type = targetType;
				target.parameter = input.readUnsignedByte();
				target.bound = input.readUnsignedByte();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case FIELD:
			case METHOD_RETURN:
			case METHOD_RECEIVER:
			{
				TypeAnnotationEmptyValue target = new TypeAnnotationEmptyValue();
				target.type = targetType;
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case METHOD_FORMAL_PARAMETER:
			{
				TypeAnnotationFormalParameterValue target = new TypeAnnotationFormalParameterValue();
				target.type = targetType;
				target.parameter = input.readUnsignedByte();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case THROWS:
			{
				TypeAnnotationThrowsValue target = new TypeAnnotationThrowsValue();
				target.type = targetType;
				target.thrown = input.readUnsignedShort();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case LOCAL_VARIABLE:
			case RESOURCE_VARIABLE:
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
			case EXCEPTION_PARAMETER:
			{
				TypeAnnotationCatchValue target = new TypeAnnotationCatchValue();
				target.type = targetType;
				target.exception = input.readUnsignedShort();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case INSTANCEOF:
			case NEW:
			case CONSTRUCTOR_REFERENCE:
			case METHOD_REFERENCE:
			{
				TypeAnnotationOffsetValue target = new TypeAnnotationOffsetValue();
				target.type = targetType;
				target.offset = input.readUnsignedShort();
				annotation.kind = targetType;
				annotation.value = target;
				break;
			}
			case CAST:
			case CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
			case METHOD_INVOCATION_TYPE_ARGUMENT:
			case CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
			case METHOD_REFERENCE_TYPE_ARGUMENT:
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
		TypeAnnotationLocation path = new TypeAnnotationLocation();
		int count = input.readUnsignedByte() & 0XFF;
		for (int i = 0; i < count; i++)
		{
			Location p = new Location();
			p.kind = input.readUnsignedByte();
			p.argument = input.readUnsignedByte();
			path.location(p);
		}
		annotation.location = path;
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
