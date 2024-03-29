package org.mve.invoke;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.Opcodes;
import org.mve.invoke.common.JavaVM;
import org.mve.invoke.common.polymorphism.PolymorphismConstructGenerator;
import org.mve.invoke.common.polymorphism.PolymorphismFieldGenerator;
import org.mve.invoke.common.polymorphism.PolymorphismInstantiationGenerator;
import org.mve.invoke.common.polymorphism.PolymorphismMethodGenerator;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.polymorphism.MagicPolymorphismConstructGenerator;
import org.mve.invoke.common.polymorphism.MagicPolymorphismFieldGenerator;
import org.mve.invoke.common.polymorphism.MagicPolymorphismInstantiationGenerator;
import org.mve.invoke.common.polymorphism.MagicPolymorphismMethodGenerator;
import org.mve.invoke.common.polymorphism.NativePolymorphismConstructGenerator;
import org.mve.invoke.common.polymorphism.NativePolymorphismFieldGenerator;
import org.mve.invoke.common.polymorphism.NativePolymorphismInstantiationGenerator;
import org.mve.invoke.common.polymorphism.NativePolymorphismMethodGenerator;

import java.lang.reflect.Method;

public class MagicAccessFactory
{
	public static <T> T access(Class<T> accessor)
	{
		Method[] methods = MagicAccessor.accessor.getMethods(accessor);
		ClassWriter writer = new ClassWriter()
			.set(
				Opcodes.version(8),
				AccessFlag.PUBLIC | AccessFlag.SUPER,
				JavaVM.randomAnonymous(accessor),
				JavaVM.CONSTANT[JavaVM.CONSTANT_MAGIC],
				Generator.type(accessor)
			);
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

						PolymorphismMethodGenerator generator;
						if (Generator.anonymous(c))
						{
							generator = new NativePolymorphismMethodGenerator(c, new MethodKind(method.getName(), returnType, parameters), new MethodKind(name, value, type), kind);
						}
						else
						{
							generator = new MagicPolymorphismMethodGenerator(c, new MethodKind(method.getName(), returnType, parameters), new MethodKind(name, value, type), kind);
						}

						generator.generate(writer);

						break;
					}
					case MagicAccess.FIELD:
					{
						Class<?> objective = access.objective();
						String name = access.name();
						int kind = access.kind();

						PolymorphismFieldGenerator generator;
						if(Generator.anonymous(objective))
						{
							generator = new NativePolymorphismFieldGenerator(objective, new MethodKind(method.getName(), method.getReturnType(), method.getParameterTypes()), name, kind);
						}
						else
						{
							generator = new MagicPolymorphismFieldGenerator(objective, new MethodKind(method.getName(), method.getReturnType(), method.getParameterTypes()), name, kind);
						}
						generator.generate(writer);

						break;
					}
					case MagicAccess.CONSTRUCT:
					{
						Class<?> objective = access.objective();
						Class<?>[] type = access.type();

						PolymorphismConstructGenerator generator;
						if (Generator.anonymous(objective))
						{
							generator = new NativePolymorphismConstructGenerator(objective, new MethodKind(method.getName(), method.getReturnType(), method.getParameterTypes()), new MethodKind("<init>", void.class, type));
						}
						else
						{
							generator = new MagicPolymorphismConstructGenerator(objective, new MethodKind(method.getName(), method.getReturnType(), method.getParameterTypes()), new MethodKind("<init>", void.class, type));
						}
						generator.generate(writer);

					}
					case MagicAccess.INSTANTIATE:
					{
						Class<?> objective = access.objective();

						PolymorphismInstantiationGenerator generator;
						if (Generator.anonymous(objective))
						{
							generator = new NativePolymorphismInstantiationGenerator(objective, new MethodKind(method.getName(), method.getReturnType(), method.getParameterTypes()));
						}
						else
						{
							generator = new MagicPolymorphismInstantiationGenerator(objective, new MethodKind(method.getName(), method.getReturnType(), method.getParameterTypes()));
						}
						generator.generate(writer);
					}
				}
			}
		}

		byte[] classcode = writer.toByteArray();
		@SuppressWarnings("unchecked")
		T val = (T) Unsafe.unsafe.allocateInstance(Unsafe.unsafe.defineAnonymousClass(accessor, classcode, null));
		MagicAccessor.accessor.initialize(val);
		return val;
	}
}
