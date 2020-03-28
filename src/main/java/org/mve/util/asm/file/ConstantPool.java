package org.mve.util.asm.file;

import java.util.Objects;

public class ConstantPool
{
	/**
	 * The value of the constantPoolSize item
	 * is equal to the number of entries in the
	 * elements table plus one. An elements
	 * index is considered valid if it is greater than
	 * zero and less than constantPoolSize, with the
	 * exception for constants of type long and double
	 * noted in <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.5">§4.4.5.<a/>
	 */
	private short constantPoolSize = 1;

	/**
	 * The elements is a table of structures <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4">(§4.4)<a/>
	 * representing various string constants, class and
	 * interface names, field names, and other constants
	 * that are referred to within the ClassFile structure
	 * and its substructures. The format of each elements
	 * table entry is indicated by its first "tag" byte.
	 */
	private ConstantPoolElement[] elements = new ConstantPoolElement[1];

	public ConstantPoolElement getConstantPoolElement(int index)
	{
		return elements[index];
	}

	public void setConstantPoolElement(short index, ConstantPoolElement element)
	{
		elements[index] = Objects.requireNonNull(element);
	}

	public void addConstantPoolElement(ConstantPoolElement element)
	{
		ConstantPoolElement[] arr = new ConstantPoolElement[this.constantPoolSize+1];
		System.arraycopy(this.elements, 0, arr, 0, this.constantPoolSize);
		arr[this.constantPoolSize] = Objects.requireNonNull(element);
		this.elements = arr;
		this.constantPoolSize++;
	}

	public short getConstantPoolSize()
	{
		return constantPoolSize;
	}
}
