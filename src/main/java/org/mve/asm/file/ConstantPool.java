package org.mve.asm.file;

import java.util.Objects;

public class ConstantPool
{
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
		int size = this.elements.length;
		if ((size & 0XFFFF) == 65535) throw new ConstantPoolOverflowException();
		ConstantPoolElement[] arr = new ConstantPoolElement[size+1];
		System.arraycopy(this.elements, 0, arr, 0, size);
		arr[size] = Objects.requireNonNull(element);
		this.elements = arr;
	}

	public int size()
	{
		return this.elements.length;
	}
}
