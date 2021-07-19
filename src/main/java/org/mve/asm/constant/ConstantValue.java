package org.mve.asm.constant;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.constant.ConstantArray;

public class ConstantValue
{
	public static int constant(ConstantArray array, Object value)
	{
		if (value instanceof Number)
		{
			if (value instanceof Long)
			{
				long val = ((Number) value).longValue();
				return ConstantPoolFinder.findLong(array, val);
			}
			else if (value instanceof Double)
			{
				double val = ((Number) value).doubleValue();
				return ConstantPoolFinder.findDouble(array, val);
			}
			else if (value instanceof Float)
			{
				float val = ((Number) value).floatValue();
				return ConstantPoolFinder.findFloat(array, val);
			}
			else
			{
				int val = ((Number) value).intValue();
				return ConstantPoolFinder.findInteger(array, val);
			}
		}
		else if (value instanceof String)
		{
			String str = value.toString();
			return ConstantPoolFinder.findString(array, str);
		}
		else if (value instanceof Type)
		{
			String type = ((Type) value).getType();
			return ConstantPoolFinder.findClass(array, type);
		}
		else if (value instanceof Class)
		{
			Class<?> clazz = (Class<?>) value;
			return ConstantPoolFinder.findClass(array, clazz.getTypeName().replace('.', '/'));
		}
		else if (value instanceof MethodHandle)
		{
			MethodHandle handle = (MethodHandle) value;
			return ConstantPoolFinder.findMethodHandle(array, handle.kind, handle.type, handle.name, handle.sign);

		}
		else if (value instanceof MethodType)
		{
			MethodType methodType = (MethodType) value;
			return ConstantPoolFinder.findMethodType(array, methodType.type);
		}
		else
		{
			return 0;
		}
	}
}
