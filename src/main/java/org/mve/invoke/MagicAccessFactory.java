package org.mve.invoke;

import org.mve.asm.ClassWriter;
import org.mve.invoke.common.DynamicBindConstructGenerator;
import org.mve.invoke.common.DynamicBindFieldGenerator;
import org.mve.invoke.common.DynamicBindInstantiationGenerator;
import org.mve.invoke.common.DynamicBindMethodGenerator;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.MagicDynamicBindConstructGenerator;
import org.mve.invoke.common.MagicDynamicBindFieldGenerator;
import org.mve.invoke.common.MagicDynamicBindInstantiationGenerator;
import org.mve.invoke.common.MagicDynamicBindMethodGenerator;
import org.mve.invoke.common.NativeDynamicBindConstructGenerator;
import org.mve.invoke.common.NativeDynamicBindFieldGenerator;
import org.mve.invoke.common.NativeDynamicBindInstantiationGenerator;
import org.mve.invoke.common.NativeDynamicBindMethodGenerator;

import java.lang.reflect.Method;
import java.util.UUID;

public class MagicAccessFactory
{
	private static final Unsafe UNSAFE = ReflectionFactory.UNSAFE;
	private static final MagicAccessor ACCESSOR = ReflectionFactory.ACCESSOR;

	public static <T> T access(Class<T> accessor)
	{
		Method[] methods = ACCESSOR.getMethods(accessor);
		ClassWriter writer = new ClassWriter().set(0x34, 0x21, UUID.randomUUID().toString().toUpperCase(), Generator.CONSTANT_POOL[0], new String[]{Generator.type(accessor)});
		for (Method method : methods)
		{
			MagicAccess access = method.getDeclaredAnnotation(MagicAccess.class);
			if (access != null)
			{
				int accessType = access.access();

				Class<?>[] parameters = method.getParameterTypes();
				Class<?> returnType = method.getReturnType();

				switch (accessType)
				{
					case MagicAccess.METHOD:
					{
						Class<?> c = access.objective();
						String name = access.name();
						Class<?>[] type = access.type();
						Class<?> value = access.value();
						int kind = access.kind();

						DynamicBindMethodGenerator generator;
						if (Generator.anonymous(c))
						{
							generator = new NativeDynamicBindMethodGenerator(c, new MethodKind(method.getName(), returnType, parameters), new MethodKind(name, value, type), kind);
						}
						else
						{
							generator = new MagicDynamicBindMethodGenerator(c, new MethodKind(method.getName(), returnType, parameters), new MethodKind(name, value, type), kind);
						}

						generator.generate(writer);

						break;
					}
					case MagicAccess.FIELD:
					{
						Class<?> objective = access.objective();
						String name = access.name();
						int kind = access.kind();

						DynamicBindFieldGenerator generator;
						if(Generator.anonymous(objective))
						{
							generator = new NativeDynamicBindFieldGenerator(objective, new MethodKind(method.getName(), method.getReturnType(), method.getParameterTypes()), name, kind);
						}
						else
						{
							generator = new MagicDynamicBindFieldGenerator(objective, new MethodKind(method.getName(), method.getReturnType(), method.getParameterTypes()), name, kind);
						}
						generator.generate(writer);

						break;
					}
					case MagicAccess.CONSTRUCT:
					{
						Class<?> objective = access.objective();
						Class<?>[] type = access.type();

						DynamicBindConstructGenerator generator;
						if (Generator.anonymous(objective))
						{
							generator = new NativeDynamicBindConstructGenerator(objective, new MethodKind(method.getName(), method.getReturnType(), method.getParameterTypes()), new MethodKind("<init>", void.class, type));
						}
						else
						{
							generator = new MagicDynamicBindConstructGenerator(objective, new MethodKind(method.getName(), method.getReturnType(), method.getParameterTypes()), new MethodKind("<init>", void.class, type));
						}
						generator.generate(writer);

					}
					case MagicAccess.INSTANTIATE:
					{
						Class<?> objective = access.objective();

						DynamicBindInstantiationGenerator generator;
						if (Generator.anonymous(objective))
						{
							generator = new NativeDynamicBindInstantiationGenerator(objective, new MethodKind(method.getName(), method.getReturnType(), method.getParameterTypes()));
						}
						else
						{
							generator = new MagicDynamicBindInstantiationGenerator(objective, new MethodKind(method.getName(), method.getReturnType(), method.getParameterTypes()));
						}
						generator.generate(writer);
					}
				}
			}
		}

		byte[] classcode = writer.toByteArray();
		@SuppressWarnings("unchecked")
		T val = (T) UNSAFE.allocateInstance(ReflectionFactory.UNSAFE.defineAnonymousClass(accessor, classcode, null));
		ACCESSOR.initialize(val);
		return val;
	}
}
