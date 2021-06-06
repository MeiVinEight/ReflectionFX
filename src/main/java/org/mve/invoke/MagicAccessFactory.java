package org.mve.invoke;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.file.AccessFlag;
import org.mve.invoke.common.Generator;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.UUID;

public class MagicAccessFactory
{
	private static final Unsafe UNSAFE = ReflectionFactory.UNSAFE;
	private static final MagicAccessor ACCESSOR = ReflectionFactory.ACCESSOR;

	public static <T> T access(Class<T> accessor)
	{
		Method[] methods = ACCESSOR.getMethods(accessor);
		ClassWriter writer = new ClassWriter().set(0x34, 0x21, UUID.randomUUID().toString().toUpperCase(), Generator.CONSTANT_POOL[0], new String[]{Generator.getType(accessor)});
		for (Method method : methods)
		{
			MagicAccess access = method.getDeclaredAnnotation(MagicAccess.class);
			if (access != null)
			{
				int accessType = access.access();

				Class<?>[] parameters = method.getParameterTypes();
				Class<?> returnType = method.getReturnType();

				MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, method.getName(), MethodType.methodType(returnType, parameters).toMethodDescriptorString());
				CodeWriter cw = new CodeWriter();
				mw.addAttribute(cw);

				switch (accessType)
				{
					case MagicAccess.METHOD:
					{
						Class<?> c = access.objective();
						String name = access.name();
						Class<?>[] type = access.type();
						Class<?> value = access.value();
						int kind = access.kind();
						int invoke = access.kind() + 0xB6;

						if (name.isEmpty())
						{
							throw new IllegalArgumentException("Argument 'name' is required and can not be empty");
						}

						int stack = 1;
						for (Class<?> clazz : parameters)
						{
							Generator.load(clazz, cw, stack);
							stack += Generator.typeSize(clazz);
						}
						cw.addMethodInstruction(invoke, Generator.getType(c), name, MethodType.methodType(value, type).toMethodDescriptorString(), kind == ReflectionFactory.KIND_INVOKE_INTERFACE);
						if (value != void.class)
						{
							if (returnType != void.class)
							{
								Generator.returner(returnType, cw);
							}
							else
							{
								cw.addInstruction(Generator.typeSize(returnType) == 1 ? Opcodes.POP : Opcodes.POP2)
									.addInstruction(Opcodes.RETURN);
							}
						}
						cw.setMaxs(Math.max(stack-1, Generator.typeSize(value)), stack);

						break;
					}
					case MagicAccess.FIELD:
					{
						Class<?> objective = access.objective();
						String name = access.name();
						Class<?> value = access.value();
						int kind = access.kind();

						if (name.isEmpty())
						{
							throw new IllegalArgumentException("Argument 'name' is required and can not be empty");
						}

						if (value == void.class)
						{
							throw new IllegalArgumentException("Argument 'value' is required and can not be void");
						}

						switch (kind)
						{
							case ReflectionFactory.KIND_GET:
							{
								switch (parameters.length)
								{
									case 0:
									{
										cw.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(objective), name, Generator.getSignature(value));
										break;
									}
									case 1:
									{
										cw.addInstruction(Opcodes.ALOAD_1)
											.addTypeInstruction(Opcodes.CHECKCAST, Generator.getType(objective))
											.addFieldInstruction(Opcodes.GETFIELD, Generator.getType(objective), name, Generator.getSignature(value));
										break;
									}
									default:
									{
										throw new IllegalArgumentException("Wrong argument list of method" + method);
									}
								}
								if (!returnType.isPrimitive())
								{
									cw.addTypeInstruction(Opcodes.CHECKCAST, Generator.getType(returnType));
								}
								Generator.returner(method.getReturnType(), cw);
								cw.setMaxs(Generator.typeSize(returnType), 1 + Generator.parameterSize(parameters));

								break;
							}
							case ReflectionFactory.KIND_PUT:
							{
								switch (parameters.length)
								{
									case 1:
									{
										Generator.load(parameters[0], cw, 1);
										cw.addFieldInstruction(Opcodes.PUTSTATIC, Generator.getType(objective), name, Generator.getSignature(value));
										break;
									}
									case 2:
									{
										cw.addInstruction(Opcodes.ALOAD_1)
											.addTypeInstruction(Opcodes.CHECKCAST, Generator.getType(objective));
										Generator.load(parameters[1], cw, 2);
										cw.addFieldInstruction(Opcodes.GETFIELD, Generator.getType(objective), name, Generator.getSignature(value));
										break;
									}
									default:
									{
										throw new IllegalArgumentException("Wrong argument list of method" + method);
									}
								}
								cw.addInstruction(Opcodes.RETURN)
									.setMaxs(Generator.parameterSize(parameters), 1 + Generator.parameterSize(parameters));

								break;
							}
							default:
							{
								throw new IllegalArgumentException("Argument 'kind' must be ReflectionFactory.KIND_GET or ReflectionFactory.KIND_PUT");
							}
						}

						break;
					}
					case MagicAccess.CONSTRUCT:
					{
						Class<?> objective = access.objective();
						Class<?>[] type = access.type();

						cw.addTypeInstruction(Opcodes.NEW, Generator.getType(objective))
							.addInstruction(Opcodes.DUP);

						int local = 1;
						for (Class<?> parameter : parameters)
						{
							Generator.load(parameter, cw, local);
							local += Generator.typeSize(parameter);
						}
						cw.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(objective), "<init>", MethodType.methodType(void.class, type).toMethodDescriptorString(), false)
							.addTypeInstruction(Opcodes.CHECKCAST, Generator.getType(returnType))
							.addInstruction(Opcodes.ARETURN)
							.setMaxs(local + 1, local);

					}
					case MagicAccess.INSTANTIATE:
					{
						Class<?> objective = access.objective();

						cw.addTypeInstruction(Opcodes.NEW, Generator.getType(objective))
							.addInstruction(Opcodes.ARETURN);
					}
					default:
					{
						throw new IllegalArgumentException("Argument 'access' must be MagicAccess.METHOD, MagicAccess.FIELD MagicAccess.CONSTRUCT or MagicAccess.INSTANTIATE");
					}
				}

				Generator.inline(mw);
				writer.addMethod(mw);
			}
		}

		byte[] classcode = writer.toByteArray();
		@SuppressWarnings("unchecked")
		T val = (T) UNSAFE.allocateInstance(ReflectionFactory.UNSAFE.defineAnonymousClass(accessor, classcode, null));
		ACCESSOR.initialize(val);
		return val;
	}
}
