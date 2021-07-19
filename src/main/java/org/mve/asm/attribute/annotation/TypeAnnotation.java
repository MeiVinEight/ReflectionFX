package org.mve.asm.attribute.annotation;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.annotation.type.TypeAnnotationValue;
import org.mve.asm.attribute.annotation.type.location.Location;
import org.mve.asm.file.attribute.element.ElementNameAndValue;
import org.mve.asm.file.constant.ConstantArray;

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



	/* type annotation location declaration */

	/**
	 * Annotation is deeper in an array type
	 */
	public static final int ARRAY = 0;

	/**
	 * Annotation is deeper in a nested type
	 */
	public static final int INNER_TYPE = 1;

	/**
	 * Annotation is on the bound of a wildcard type argument of a parameterized type
	 */
	public static final int WILDCARD = 2;

	/**
	 * Annotation is on a type argument of a parameterized type
	 */
	public static final int TYPE_ARGUMENT = 3;

	public TypeAnnotationValue<?> value;
	public Location[] location = new Location[0];
	public String type;
	public NameAndValue[] element = new NameAndValue[0];

	public TypeAnnotation value(TypeAnnotationValue<?> value)
	{
		this.value = value;
		return this;
	}

	public TypeAnnotation location(int kind, int argument)
	{
		this.location = Arrays.copyOf(this.location, this.location.length+1);
		this.location[this.location.length-1] = new Location(kind, argument);
		return this;
	}

	public TypeAnnotation type(String type)
	{
		this.type = type;
		return this;
	}

	public TypeAnnotation element(String name, Object value)
	{
		return this.element(new NameAndValue(name, value));
	}

	public TypeAnnotation element(NameAndValue value)
	{
		this.element = Arrays.copyOf(this.element, this.element.length+1);
		this.element[this.element.length-1] = value;
		return this;
	}

	public org.mve.asm.file.attribute.annotation.TypeAnnotation annotation(ConstantArray array)
	{
		org.mve.asm.file.attribute.annotation.TypeAnnotation annotation = new org.mve.asm.file.attribute.annotation.TypeAnnotation();
		annotation.kind = this.value.type;
		annotation.value = this.value.value(array);
		annotation.type = ConstantPoolFinder.findClass(array, this.type);
		for (NameAndValue value : this.element)
		{
			ElementNameAndValue element = new ElementNameAndValue();
			element.name = ConstantPoolFinder.findUTF8(array, value.name);
			element.value = NameAndValue.value(array, value);
			annotation.element(element);
		}
		return annotation;
	}
}
