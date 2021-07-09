package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.bootstrap.BootstrapMethod;
import org.mve.asm.constant.MethodHandle;
import org.mve.asm.constant.MethodType;
import org.mve.asm.constant.Type;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeBootstrapMethods;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public class BootstrapMethodWriter implements AttributeWriter
{
	public BootstrapMethod[] method = new BootstrapMethod[0];

	public BootstrapMethodWriter bootstrap(MethodHandle method, Object... argument)
	{
		return this.bootstrap(new BootstrapMethod(method, argument));
	}

	public BootstrapMethodWriter bootstrap(BootstrapMethod method)
	{
		this.method = Arrays.copyOf(this.method, this.method.length+1);
		this.method[this.method.length-1] = method;
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeBootstrapMethods attribute = new AttributeBootstrapMethods();
		attribute.name = ConstantPoolFinder.findUTF8(pool, AttributeType.BOOTSTRAP_METHODS.getName());
		for (BootstrapMethod method : this.method)
		{
			org.mve.asm.file.attribute.bootstrap.BootstrapMethod bm = new org.mve.asm.file.attribute.bootstrap.BootstrapMethod();
			bm.reference = ConstantPoolFinder.findMethodHandle(pool, method.method.kind, method.method.type, method.method.name, method.method.sign);
			bm.argument = new int[method.argument.length];
			for (int i = 0; i < method.argument.length; i++)
			{
				Object value = method.argument[i];
				if (value instanceof Type)
				{
					bm.argument[i] = ConstantPoolFinder.findClass(pool, ((Type)value).getType());
				}
				else if (value instanceof String)
				{
					bm.argument[i] = ConstantPoolFinder.findString(pool, (String) value);
				}
				else if (value instanceof Number)
				{
					if (value instanceof Float)
					{
						bm.argument[i] = ConstantPoolFinder.findFloat(pool, ((Number)value).floatValue());
					}
					else if (value instanceof Double)
					{
						bm.argument[i] = ConstantPoolFinder.findDouble(pool, ((Number)value).doubleValue());
					}
					else if (value instanceof Long)
					{
						bm.argument[i] = ConstantPoolFinder.findLong(pool, ((Number)value).longValue());
					}
					else
					{
						bm.argument[i] = ConstantPoolFinder.findInteger(pool, ((Number)value).intValue());
					}
				}
				else if (value instanceof MethodHandle)
				{
					MethodHandle handle = (MethodHandle) value;
					bm.argument[i] = ConstantPoolFinder.findMethodHandle(pool, handle.kind, handle.type, handle.name, handle.sign);
				}
				else if (value instanceof MethodType)
				{
					MethodType methodType = (MethodType) value;
					bm.argument[i] = ConstantPoolFinder.findMethodType(pool, methodType.type);
				}
			}

			attribute.bootstrap(bm);
		}
		return attribute;
	}
}
