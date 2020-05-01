package org.mve.util.asm;

import org.mve.util.asm.file.ConstantClass;
import org.mve.util.asm.file.ConstantDouble;
import org.mve.util.asm.file.ConstantFieldReference;
import org.mve.util.asm.file.ConstantFloat;
import org.mve.util.asm.file.ConstantInteger;
import org.mve.util.asm.file.ConstantInterfaceMethodReference;
import org.mve.util.asm.file.ConstantLong;
import org.mve.util.asm.file.ConstantMethodReference;
import org.mve.util.asm.file.ConstantNameAndType;
import org.mve.util.asm.file.ConstantNull;
import org.mve.util.asm.file.ConstantPool;
import org.mve.util.asm.file.ConstantPoolElement;
import org.mve.util.asm.file.ConstantString;
import org.mve.util.asm.file.ConstantUTF8;

import java.util.Objects;

public class ConstantPoolFinder
{
	public static int findUTF8(ConstantPool pool, String str)
	{
		int size = pool.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = pool.getConstantPoolElement(i);
			if (element instanceof ConstantUTF8 && ((ConstantUTF8)element).getUTF8().equals(str)) return i;
		}
		ConstantUTF8 constantUTF8 = new ConstantUTF8(str);
		pool.addConstantPoolElement(constantUTF8);
		return (pool.getConstantPoolSize() & 0XFFFF) - 1;
	}

	public static int findInteger(ConstantPool pool, int num)
	{
		int size = pool.getConstantPoolSize() & 0XFFFF;
		for (int i=0; i< size; i++)
		{
			ConstantPoolElement element = pool.getConstantPoolElement(i);
			if (element instanceof ConstantInteger && ((ConstantInteger)element).getValue() == num) return i;
		}
		ConstantInteger integer = new ConstantInteger(num);
		pool.addConstantPoolElement(integer);
		return size;
	}

	public static int findFloat(ConstantPool pool, float num)
	{
		int size = pool.getConstantPoolSize() & 0XFFFF;
		for (int i=0; i< size; i++)
		{
			ConstantPoolElement element = pool.getConstantPoolElement(i);
			if (element instanceof ConstantFloat && ((ConstantFloat)element).getValue() == num) return i;
		}
		ConstantFloat integer = new ConstantFloat(num);
		pool.addConstantPoolElement(integer);
		return size;
	}

	public static int findLong(ConstantPool pool, long num)
	{
		int size = pool.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = pool.getConstantPoolElement(i);
			if (element instanceof ConstantLong && ((ConstantLong)element).getValue() == num) return i;
		}
		ConstantLong constant = new ConstantLong(num);
		pool.addConstantPoolElement(constant);
		int ret = pool.getConstantPoolSize() & 0XFFFF;
		pool.addConstantPoolElement(new ConstantNull());
		return ret - 1;
	}

	public static int findDouble(ConstantPool pool, double num)
	{
		int size = pool.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = pool.getConstantPoolElement(i);
			if (element instanceof ConstantDouble && ((ConstantDouble)element).getValue() == num) return i;
		}
		ConstantDouble constant = new ConstantDouble(num);
		pool.addConstantPoolElement(constant);
		int ret = pool.getConstantPoolSize() & 0XFFFF;
		pool.addConstantPoolElement(new ConstantNull());
		return ret - 1;
	}

	public static int findString(ConstantPool pool, String string)
	{
		int size = pool.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = pool.getConstantPoolElement(i);
			if (element instanceof ConstantString && ((ConstantUTF8)pool.getConstantPoolElement(((ConstantString)element).getStringIndex())).getUTF8().equals(string)) return i;
		}
		int utfIndex = findUTF8(pool, string);
		ConstantString constantString = new ConstantString((short) utfIndex);
		pool.addConstantPoolElement(constantString);
		return (pool.getConstantPoolSize() & 0XFFFF) - 1;
	}

	public static int findClass(ConstantPool pool, String type)
	{
		int size = pool.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = pool.getConstantPoolElement(i);
			if (element instanceof ConstantClass)
			{
				ConstantClass constant = (ConstantClass) element;
				int nameIndex = constant.getNameIndex() & 0XFFFF;
				if (((ConstantUTF8)pool.getConstantPoolElement(nameIndex)).getUTF8().equals(type)) return i;
			}
		}
		int nameIndex = findUTF8(pool, type);
		ConstantClass constantClass = new ConstantClass((short) nameIndex);
		pool.addConstantPoolElement(constantClass);
		return (pool.getConstantPoolSize() & 0XFFFF) - 1;
	}

	public static int findNameAndType(ConstantPool pool, String name, String type)
	{
		Objects.requireNonNull(name);
		Objects.requireNonNull(type);
		int size = pool.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = pool.getConstantPoolElement(i);
			if (element instanceof ConstantNameAndType)
			{
				ConstantNameAndType constantNameAndType = (ConstantNameAndType) element;
				String name1 = ((ConstantUTF8)pool.getConstantPoolElement(constantNameAndType.getNameIndex() & 0XFFFF)).getUTF8();
				String type1 = ((ConstantUTF8)pool.getConstantPoolElement(constantNameAndType.getTypeIndex() & 0XFFFF)).getUTF8();
				if (name1.equals(name) && type1.equals(type)) return i;
			}
		}
		int nameIndex = findUTF8(pool, name);
		int typeIndex = findUTF8(pool, type);
		ConstantNameAndType constantNameAndType = new ConstantNameAndType((short) nameIndex, (short) typeIndex);
		pool.addConstantPoolElement(constantNameAndType);
		return (pool.getConstantPoolSize() & 0XFFFF) - 1;
	}

	public static int findField(ConstantPool pool, String type, String name, String desc)
	{
		int size = pool.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = pool.getConstantPoolElement(i);
			if (element instanceof ConstantFieldReference)
			{
				ConstantFieldReference fieldReference = (ConstantFieldReference) element;
				int fieldClassIndex = fieldReference.getClassIndex() & 0XFFFF;
				int nameAndTypeIndex = fieldReference.getNameAndTypeIndex() & 0XFFFF;
				ConstantClass fieldClass = (ConstantClass) pool.getConstantPoolElement(fieldClassIndex);
				ConstantNameAndType nameAndType = (ConstantNameAndType) pool.getConstantPoolElement(nameAndTypeIndex);
				if (
					((ConstantUTF8)pool.getConstantPoolElement(fieldClass.getNameIndex() & 0XFFFF)).getUTF8().equals(type) &&
						(
							((ConstantUTF8)pool.getConstantPoolElement(nameAndType.getNameIndex() & 0XFFFF)).getUTF8().equals(name) &&
								((ConstantUTF8)pool.getConstantPoolElement(nameAndType.getTypeIndex() & 0XFFFF)).getUTF8().equals(desc)
						)
				) return i;
			}
		}
		int classIndex = findClass(pool, type);
		int nameAndTypeIndex = findNameAndType(pool, name, desc);
		ConstantFieldReference fieldReference = new ConstantFieldReference((short) classIndex, (short) nameAndTypeIndex);
		pool.addConstantPoolElement(fieldReference);
		return (pool.getConstantPoolSize() & 0XFFFF) - 1;
	}

	public static int findMethod(ConstantPool pool, String type, String name, String desc, boolean isAbstract)
	{
		int size = pool.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = pool.getConstantPoolElement(i);
			if (isAbstract)
			{
				if (element instanceof ConstantMethodReference)
				{
					ConstantMethodReference methodReference = (ConstantMethodReference) element;
					int methodClassIndex = methodReference.getClassIndex() & 0XFFFF;
					int nameAndTypeIndex = methodReference.getNameAndTypeIndex() & 0XFFFF;
					ConstantClass methodClass = (ConstantClass) pool.getConstantPoolElement(methodClassIndex);
					ConstantNameAndType nameAndType = (ConstantNameAndType) pool.getConstantPoolElement(nameAndTypeIndex);
					if (
						((ConstantUTF8)pool.getConstantPoolElement(methodClass.getNameIndex() & 0XFFFF)).getUTF8().equals(type) &&
							(
								((ConstantUTF8)pool.getConstantPoolElement(nameAndType.getNameIndex() & 0XFFFF)).getUTF8().equals(name) &&
									((ConstantUTF8)pool.getConstantPoolElement(nameAndType.getTypeIndex() & 0XFFFF)).getUTF8().equals(desc)
							)
					) return i;
				}
			}
			else
			{
				if (element instanceof ConstantInterfaceMethodReference)
				{
					ConstantInterfaceMethodReference methodReference = (ConstantInterfaceMethodReference) element;
					int methodClassIndex = methodReference.getClassIndex() & 0XFFFF;
					int nameAndTypeIndex = methodReference.getNameAndTypeIndex() & 0XFFFF;
					ConstantClass methodClass = (ConstantClass) pool.getConstantPoolElement(methodClassIndex);
					ConstantNameAndType nameAndType = (ConstantNameAndType) pool.getConstantPoolElement(nameAndTypeIndex);
					if (
						((ConstantUTF8)pool.getConstantPoolElement(methodClass.getNameIndex() & 0XFFFF)).getUTF8().equals(type) &&
							(
								((ConstantUTF8)pool.getConstantPoolElement(nameAndType.getNameIndex() & 0XFFFF)).getUTF8().equals(name) &&
									((ConstantUTF8)pool.getConstantPoolElement(nameAndType.getTypeIndex() & 0XFFFF)).getUTF8().equals(desc)
							)
					) return i;
				}
			}
		}
		int classIndex = findClass(pool, type);
		int nameAndTypeIndex = findNameAndType(pool, name, desc);
		ConstantPoolElement element = isAbstract ? new ConstantInterfaceMethodReference((short) classIndex, (short) nameAndTypeIndex) : new ConstantMethodReference((short) classIndex, (short) nameAndTypeIndex);
		pool.addConstantPoolElement(element);
		return (pool.getConstantPoolSize() & 0XFFFF) - 1;
	}
}
