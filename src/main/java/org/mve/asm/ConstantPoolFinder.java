package org.mve.asm;

import org.mve.asm.file.constant.ConstantClass;
import org.mve.asm.file.constant.ConstantDouble;
import org.mve.asm.file.constant.ConstantFieldReference;
import org.mve.asm.file.constant.ConstantFloat;
import org.mve.asm.file.constant.ConstantInteger;
import org.mve.asm.file.constant.ConstantInterfaceMethodReference;
import org.mve.asm.file.constant.ConstantLong;
import org.mve.asm.file.constant.ConstantMethodReference;
import org.mve.asm.file.constant.ConstantModule;
import org.mve.asm.file.constant.ConstantNameAndType;
import org.mve.asm.file.constant.ConstantNull;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.constant.Constant;
import org.mve.asm.file.constant.ConstantPackage;
import org.mve.asm.file.constant.ConstantString;
import org.mve.asm.file.constant.ConstantUTF8;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class ConstantPoolFinder
{
	public static int findUTF8(ConstantArray array, String str)
	{
		byte[] bytes =  str.getBytes(StandardCharsets.UTF_8);
		int size = array.element.length & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			Constant element = array.element[i];
			if (element instanceof ConstantUTF8 && Arrays.equals(((ConstantUTF8) element).value, bytes)) return i;
		}
		ConstantUTF8 constantUTF8 = new ConstantUTF8(bytes);
		array.add(constantUTF8);
		return (array.element.length & 0XFFFF) - 1;
	}

	public static int findInteger(ConstantArray array, int num)
	{
		int size = array.element.length & 0XFFFF;
		for (int i=0; i< size; i++)
		{
			Constant element = array.element[i];
			if (element instanceof ConstantInteger && ((ConstantInteger) element).value == num) return i;
		}
		ConstantInteger integer = new ConstantInteger(num);
		array.add(integer);
		return size;
	}

	public static int findFloat(ConstantArray array, float num)
	{
		int size = array.element.length & 0XFFFF;
		for (int i=0; i< size; i++)
		{
			Constant element = array.element[i];
			if (element instanceof ConstantFloat && ((ConstantFloat) element).value == num) return i;
		}
		ConstantFloat integer = new ConstantFloat(num);
		array.add(integer);
		return size;
	}

	public static int findLong(ConstantArray array, long num)
	{
		int size = array.element.length & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			Constant element = array.element[i];
			if (element instanceof ConstantLong && ((ConstantLong) element).value == num) return i;
		}
		ConstantLong constant = new ConstantLong(num);
		array.add(constant);
		int ret = array.element.length & 0XFFFF;
		array.add(new ConstantNull());
		return ret - 1;
	}

	public static int findDouble(ConstantArray array, double num)
	{
		int size = array.element.length & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			Constant element = array.element[i];
			if (element instanceof ConstantDouble && ((ConstantDouble)element).value == num) return i;
		}
		ConstantDouble constant = new ConstantDouble(num);
		array.add(constant);
		int ret = array.element.length & 0XFFFF;
		array.add(new ConstantNull());
		return ret - 1;
	}

	public static int findString(ConstantArray array, String string)
	{
		byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
		int size = array.element.length & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			Constant element = array.element[i];
			if (element instanceof ConstantString && Arrays.equals(((ConstantUTF8)array.element[((ConstantString) element).value]).value, bytes)) return i;
		}
		int utfIndex = findUTF8(array, string);
		ConstantString constantString = new ConstantString((short) utfIndex);
		array.add(constantString);
		return (array.element.length & 0XFFFF) - 1;
	}

	public static int findClass(ConstantArray array, String type)
	{
		byte[] bytes = type.getBytes(StandardCharsets.UTF_8);
		int size = array.element.length & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			Constant element = array.element[i];
			if (element instanceof ConstantClass)
			{
				ConstantClass constant = (ConstantClass) element;
				int nameIndex = constant.name & 0XFFFF;
				if (Arrays.equals(((ConstantUTF8)array.element[nameIndex]).value, bytes)) return i;
			}
		}
		int nameIndex = findUTF8(array, type);
		ConstantClass constantClass = new ConstantClass((short) nameIndex);
		array.add(constantClass);
		return (array.element.length & 0XFFFF) - 1;
	}

	public static int findNameAndType(ConstantArray array, String name, String type)
	{
		Objects.requireNonNull(name);
		Objects.requireNonNull(type);
		int size = array.element.length & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			Constant element = array.element[i];
			if (element instanceof ConstantNameAndType)
			{
				ConstantNameAndType constantNameAndType = (ConstantNameAndType) element;
				String name1 = new String(((ConstantUTF8)array.element[constantNameAndType.name & 0XFFFF]).value, StandardCharsets.UTF_8);
				String type1 = new String(((ConstantUTF8)array.element[constantNameAndType.type & 0XFFFF]).value, StandardCharsets.UTF_8);
				if (name1.equals(name) && type1.equals(type)) return i;
			}
		}
		int nameIndex = findUTF8(array, name);
		int typeIndex = findUTF8(array, type);
		ConstantNameAndType constantNameAndType = new ConstantNameAndType((short) nameIndex, (short) typeIndex);
		array.add(constantNameAndType);
		return (array.element.length & 0XFFFF) - 1;
	}

	public static int findField(ConstantArray array, String type, String name, String sign)
	{
		byte[] typeValue = type.getBytes(StandardCharsets.UTF_8);
		byte[] nameValue = name.getBytes(StandardCharsets.UTF_8);
		byte[] signValue = sign.getBytes(StandardCharsets.UTF_8);
		int size = array.element.length & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			Constant element = array.element[i];
			if (element instanceof ConstantFieldReference)
			{
				ConstantFieldReference fieldReference = (ConstantFieldReference) element;
				int fieldClassIndex = fieldReference.clazz & 0XFFFF;
				int nameAndTypeIndex = fieldReference.nameAndType & 0XFFFF;
				ConstantClass fieldClass = (ConstantClass) array.element[fieldClassIndex];
				ConstantNameAndType nameAndType = (ConstantNameAndType) array.element[nameAndTypeIndex];
				if (
					Arrays.equals(((ConstantUTF8)array.element[fieldClass.name & 0XFFFF]).value, typeValue) &&
						(
							Arrays.equals(((ConstantUTF8)array.element[nameAndType.name & 0XFFFF]).value, nameValue) &&
							Arrays.equals(((ConstantUTF8)array.element[nameAndType.type & 0XFFFF]).value, signValue)
						)
				) return i;
			}
		}
		int classIndex = findClass(array, type);
		int nameAndTypeIndex = findNameAndType(array, name, sign);
		ConstantFieldReference fieldReference = new ConstantFieldReference((short) classIndex, (short) nameAndTypeIndex);
		array.add(fieldReference);
		return (array.element.length & 0XFFFF) - 1;
	}

	public static int findMethod(ConstantArray array, String type, String name, String sign, boolean isAbstract)
	{
		byte[] typeValue = type.getBytes(StandardCharsets.UTF_8);
		byte[] nameValue = name.getBytes(StandardCharsets.UTF_8);
		byte[] signValue = sign.getBytes(StandardCharsets.UTF_8);
		int size = array.element.length & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			Constant element = array.element[i];
			if (isAbstract)
			{
				if (element instanceof ConstantMethodReference)
				{
					ConstantMethodReference methodReference = (ConstantMethodReference) element;
					int methodClassIndex = (short) methodReference.clazz;
					int nameAndTypeIndex = (short) methodReference.nameAndType;
					ConstantClass methodClass = (ConstantClass) array.element[methodClassIndex];
					ConstantNameAndType nameAndType = (ConstantNameAndType) array.element[nameAndTypeIndex];
					if (
						Arrays.equals(((ConstantUTF8)array.element[methodClass.name & 0XFFFF]).value, typeValue) &&
							(
								Arrays.equals(((ConstantUTF8)array.element[nameAndType.name & 0XFFFF]).value, nameValue) &&
								Arrays.equals(((ConstantUTF8)array.element[nameAndType.type & 0XFFFF]).value, signValue)
							)
					) return i;
				}
			}
			else
			{
				if (element instanceof ConstantInterfaceMethodReference)
				{
					ConstantInterfaceMethodReference methodReference = (ConstantInterfaceMethodReference) element;
					int methodClassIndex = (short) methodReference.clazz;
					int nameAndTypeIndex = (short) methodReference.nameAndType;
					ConstantClass methodClass = (ConstantClass) array.element[methodClassIndex];
					ConstantNameAndType nameAndType = (ConstantNameAndType) array.element[nameAndTypeIndex];
					if (
						Arrays.equals(((ConstantUTF8)array.element[methodClass.name & 0XFFFF]).value, typeValue) &&
							(
								Arrays.equals(((ConstantUTF8)array.element[nameAndType.name & 0XFFFF]).value, nameValue) &&
								Arrays.equals(((ConstantUTF8)array.element[nameAndType.type & 0XFFFF]).value, signValue)
							)
					) return i;
				}
			}
		}
		int classIndex = findClass(array, type);
		int nameAndTypeIndex = findNameAndType(array, name, sign);
		Constant element = isAbstract ? new ConstantInterfaceMethodReference((short) classIndex, (short) nameAndTypeIndex) : new ConstantMethodReference((short) classIndex, (short) nameAndTypeIndex);
		array.add(element);
		return (array.element.length & 0XFFFF) - 1;
	}

	public static int findPackage(ConstantArray array, String name)
	{
		byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
		int size = array.element.length & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			Constant element = array.element[i];
			if (element instanceof ConstantPackage)
			{
				ConstantPackage constant = (ConstantPackage) element;
				int nameIndex = constant.name & 0XFFFF;
				if (Arrays.equals(((ConstantUTF8)array.element[nameIndex]).value, bytes)) return i;
			}
		}
		int nameIndex = findUTF8(array, name);
		ConstantPackage constant = new ConstantPackage((short) nameIndex);
		array.add(constant);
		return (array.element.length & 0XFFFF) - 1;
	}

	public static int findModule(ConstantArray array, String name)
	{
		byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
		int size = array.element.length & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			Constant element = array.element[i];
			if (element instanceof ConstantModule)
			{
				ConstantModule constant = (ConstantModule) element;
				int nameIndex = constant.name & 0XFFFF;
				if (Arrays.equals(((ConstantUTF8)array.element[nameIndex]).value, bytes)) return i;
			}
		}
		int nameIndex = findUTF8(array, name);
		ConstantModule constant = new ConstantModule((short) nameIndex);
		array.add(constant);
		return (array.element.length & 0XFFFF) - 1;
	}
}
