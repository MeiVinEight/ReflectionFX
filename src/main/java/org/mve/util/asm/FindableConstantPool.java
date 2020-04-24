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
import org.mve.util.asm.file.ConstantPool;
import org.mve.util.asm.file.ConstantPoolElement;
import org.mve.util.asm.file.ConstantString;
import org.mve.util.asm.file.ConstantUTF8;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

public class FindableConstantPool extends ConstantPool
{
	public int findUTF8(String str)
	{
		int size = this.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = this.getConstantPoolElement(i);
			if (element instanceof ConstantUTF8 && ((ConstantUTF8)element).getUTF8().equals(str)) return i;
		}
		ConstantUTF8 constantUTF8 = new ConstantUTF8(str);
		this.addConstantPoolElement(constantUTF8);
		return (this.getConstantPoolSize() & 0XFFFF) - 1;
	}

	public int find(int num)
	{
		int size = this.getConstantPoolSize() & 0XFFFF;
		for (int i=0; i< size; i++)
		{
			ConstantPoolElement element = this.getConstantPoolElement(i);
			if (element instanceof ConstantInteger && ((ConstantInteger)element).getValue() == num) return i;
		}
		ConstantInteger integer = new ConstantInteger(num);
		this.addConstantPoolElement(integer);
		return size;
	}

	public int find(float num)
	{
		int size = this.getConstantPoolSize() & 0XFFFF;
		for (int i=0; i< size; i++)
		{
			ConstantPoolElement element = this.getConstantPoolElement(i);
			if (element instanceof ConstantFloat && ((ConstantFloat)element).getValue() == num) return i;
		}
		ConstantFloat integer = new ConstantFloat(num);
		this.addConstantPoolElement(integer);
		return size;
	}

	public int find(long num)
	{
		int size = this.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = this.getConstantPoolElement(i);
			if (element instanceof ConstantLong && ((ConstantLong)element).getValue() == num) return i;
		}
		ConstantLong constant = new ConstantLong(num);
		this.addConstantPoolElement(constant);
		return size;
	}

	public int find(double num)
	{
		int size = this.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = this.getConstantPoolElement(i);
			if (element instanceof ConstantDouble && ((ConstantDouble)element).getValue() == num) return i;
		}
		ConstantDouble constant = new ConstantDouble(num);
		this.addConstantPoolElement(constant);
		return size;
	}

	public int find(String string)
	{
		int size = this.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = this.getConstantPoolElement(i);
			if (element instanceof ConstantString && ((ConstantUTF8)this.getConstantPoolElement(((ConstantString)element).getStringIndex())).getUTF8().equals(string)) return i;
		}
		int utfIndex = this.findUTF8(string);
		ConstantString constantString = new ConstantString((short) utfIndex);
		this.addConstantPoolElement(constantString);
		return (this.getConstantPoolSize() & 0XFFFF) - 1;
	}

	public int find(java.lang.Class<?> clazz)
	{
		int size = this.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = this.getConstantPoolElement(i);
			if (element instanceof ConstantClass)
			{
				ConstantClass constant = (ConstantClass) element;
				int nameIndex = constant.getNameIndex() & 0XFFFF;
				String classname = Type.getName(clazz);
				if (((ConstantUTF8)this.getConstantPoolElement(nameIndex)).getUTF8().equals(classname)) return i;
			}
		}
		int nameIndex = this.findUTF8(Type.getName(clazz));
		ConstantClass constantClass = new ConstantClass((short) nameIndex);
		this.addConstantPoolElement(constantClass);
		return (this.getConstantPoolSize() & 0XFFFF) - 1;
	}

	public int find(NameAndType nameAndType)
	{
		int size = this.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = this.getConstantPoolElement(i);
			if (element instanceof ConstantNameAndType)
			{
				ConstantNameAndType constantNameAndType = (ConstantNameAndType) element;
				String name = ((ConstantUTF8)this.getConstantPoolElement(constantNameAndType.getNameIndex() & 0XFFFF)).getUTF8();
				String type = ((ConstantUTF8)this.getConstantPoolElement(constantNameAndType.getTypeIndex() & 0XFFFF)).getUTF8();
				if (name.equals(nameAndType.getName()) && type.equals(nameAndType.getType())) return i;
			}
		}
		int nameIndex = this.findUTF8(nameAndType.getName());
		int typeIndex = this.findUTF8(nameAndType.getType());
		ConstantNameAndType constantNameAndType = new ConstantNameAndType((short) nameIndex, (short) typeIndex);
		this.addConstantPoolElement(constantNameAndType);
		return (this.getConstantPoolSize() & 0XFFFF) - 1;
	}

	public int find(java.lang.reflect.Field field)
	{
		Class<?> clazz = field.getDeclaringClass();
		String name = field.getName();
		String type = Type.getDescription(field.getType());
		int size = this.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = this.getConstantPoolElement(i);
			if (element instanceof ConstantFieldReference)
			{
				ConstantFieldReference fieldReference = (ConstantFieldReference) element;
				int fieldClassIndex = fieldReference.getClassIndex() & 0XFFFF;
				int nameAndTypeIndex = fieldReference.getNameAndTypeIndex() & 0XFFFF;
				ConstantClass fieldClass = (ConstantClass) this.getConstantPoolElement(fieldClassIndex);
				ConstantNameAndType nameAndType = (ConstantNameAndType) this.getConstantPoolElement(nameAndTypeIndex);
				if (
					((ConstantUTF8)this.getConstantPoolElement(fieldClass.getNameIndex() & 0XFFFF)).getUTF8().equals(Type.getName(clazz)) &&
					(
						((ConstantUTF8)this.getConstantPoolElement(nameAndType.getNameIndex() & 0XFFFF)).getUTF8().equals(name) &&
						((ConstantUTF8)this.getConstantPoolElement(nameAndType.getTypeIndex() & 0XFFFF)).getUTF8().equals(type)
					)
				) return i;
			}
		}
		NameAndType nameAndType = new NameAndType(name, type);
		int classIndex = this.find(clazz);
		int nameAndTypeIndex = this.find(nameAndType);
		ConstantFieldReference fieldReference = new ConstantFieldReference((short) classIndex, (short) nameAndTypeIndex);
		this.addConstantPoolElement(fieldReference);
		return (this.getConstantPoolSize() & 0XFFFF) - 1;
	}

	public int find(java.lang.reflect.Method method)
	{
		boolean isAbstract = Modifier.isAbstract(method.getModifiers());
		Class<?> clazz = method.getDeclaringClass();
		String name = method.getName();
		String type = MethodType.methodType(method.getReturnType(), method.getParameterTypes()).toMethodDescriptorString();
		int size = this.getConstantPoolSize() & 0XFFFF;
		for (int i = 0; i < size; i++)
		{
			ConstantPoolElement element = this.getConstantPoolElement(i);
			if (isAbstract)
			{
				if (element instanceof ConstantMethodReference)
				{
					ConstantMethodReference methodReference = (ConstantMethodReference) element;
					int methodClassIndex = methodReference.getClassIndex() & 0XFFFF;
					int nameAndTypeIndex = methodReference.getNameAndTypeIndex() & 0XFFFF;
					ConstantClass methodClass = (ConstantClass) this.getConstantPoolElement(methodClassIndex);
					ConstantNameAndType nameAndType = (ConstantNameAndType) this.getConstantPoolElement(nameAndTypeIndex);
					if (
						((ConstantUTF8)this.getConstantPoolElement(methodClass.getNameIndex() & 0XFFFF)).getUTF8().equals(Type.getName(clazz)) &&
							(
								((ConstantUTF8)this.getConstantPoolElement(nameAndType.getNameIndex() & 0XFFFF)).getUTF8().equals(name) &&
								((ConstantUTF8)this.getConstantPoolElement(nameAndType.getTypeIndex() & 0XFFFF)).getUTF8().equals(type)
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
					ConstantClass methodClass = (ConstantClass) this.getConstantPoolElement(methodClassIndex);
					ConstantNameAndType nameAndType = (ConstantNameAndType) this.getConstantPoolElement(nameAndTypeIndex);
					if (
						((ConstantUTF8)this.getConstantPoolElement(methodClass.getNameIndex() & 0XFFFF)).getUTF8().equals(Type.getName(clazz)) &&
							(
								((ConstantUTF8)this.getConstantPoolElement(nameAndType.getNameIndex() & 0XFFFF)).getUTF8().equals(name) &&
								((ConstantUTF8)this.getConstantPoolElement(nameAndType.getTypeIndex() & 0XFFFF)).getUTF8().equals(type)
							)
					) return i;
				}
			}
		}
		NameAndType nameAndType = new NameAndType(name, type);
		int classIndex = this.find(clazz);
		int nameAndTypeIndex = this.find(nameAndType);
		ConstantPoolElement element = isAbstract ? new ConstantInterfaceMethodReference((short) classIndex, (short) nameAndTypeIndex) : new ConstantMethodReference((short) classIndex, (short) nameAndTypeIndex);
		this.addConstantPoolElement(element);
		return (this.getConstantPoolSize() & 0XFFFF) - 1;
	}
}
