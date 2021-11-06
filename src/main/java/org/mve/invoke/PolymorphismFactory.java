package org.mve.invoke;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.common.JavaVM;
import org.mve.invoke.common.polymorphism.PolymorphismEnumHelperGenerator;
import org.mve.invoke.common.polymorphism.PolymorphismGenerator;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.polymorphism.MagicPolymorphismConstructGenerator;
import org.mve.invoke.common.polymorphism.MagicPolymorphismFieldGenerator;
import org.mve.invoke.common.polymorphism.MagicPolymorphismInstantiationGenerator;
import org.mve.invoke.common.polymorphism.MagicPolymorphismMethodGenerator;
import org.mve.invoke.common.polymorphism.NativePolymorphismConstructGenerator;
import org.mve.invoke.common.polymorphism.NativePolymorphismFieldGenerator;
import org.mve.invoke.common.polymorphism.NativePolymorphismInstantiationGenerator;
import org.mve.invoke.common.polymorphism.NativePolymorphismMethodGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PolymorphismFactory<T>
{
	private static final Unsafe UNSAFE = ReflectionFactory.UNSAFE;
	private static final MagicAccessor ACCESSOR = ReflectionFactory.ACCESSOR;
	private final Class<T> accessor;
	private final Map<MethodKind, String> names = new HashMap<>();
	private final Map<MethodKind, Class<?>> objectives = new HashMap<>();
	private final Map<Class<?>, List<PolymorphismGenerator>> generators = new HashMap<>();
	private final Map<Class<?>, List<MethodKind>> kinds = new HashMap<>();

	public PolymorphismFactory(Class<T> accessor)
	{
		this.accessor = accessor;
	}

	public PolymorphismFactory<T> method(Class<?> objective, MethodKind implementation, MethodKind invocation, int kind)
	{
		List<PolymorphismGenerator> generators = this.generators.computeIfAbsent(objective, obj -> new LinkedList<>());

		PolymorphismGenerator generator;
		String name = hex(generators.size());
		MethodKind bridge = new MethodKind(name, implementation.type());
		this.kinds.computeIfAbsent(objective, obj -> new LinkedList<>()).add(bridge);
		if (Generator.anonymous(objective))
		{
			generator = new NativePolymorphismMethodGenerator(objective, bridge, invocation, kind);
		}
		else
		{
			generator = new MagicPolymorphismMethodGenerator(objective, new MethodKind(hex(generators.size()), implementation.type()), invocation, kind);
		}
		generators.add(generator);
		this.names.put(implementation, name);
		this.objectives.put(implementation, objective);

		return this;
	}

	public PolymorphismFactory<T> field(Class<?> objective, MethodKind implementation, String operation, int kind)
	{
		List<PolymorphismGenerator> generators = this.generators.computeIfAbsent(objective, obj -> new LinkedList<>());

		PolymorphismGenerator generator;
		String name = hex(generators.size());
		MethodKind bridge = new MethodKind(name, implementation.type());
		this.kinds.computeIfAbsent(objective, obj -> new LinkedList<>()).add(bridge);
		if (Generator.anonymous(objective))
		{
			generator = new NativePolymorphismFieldGenerator(objective, bridge, operation, kind);
		}
		else
		{
			generator = new MagicPolymorphismFieldGenerator(objective, new MethodKind(hex(generators.size()), implementation.type()), operation, kind);
		}
		generators.add(generator);
		this.names.put(implementation, name);
		this.objectives.put(implementation, objective);

		return this;
	}

	public PolymorphismFactory<T> instantiate(Class<?> objective, MethodKind implementation)
	{
		List<PolymorphismGenerator> generators = this.generators.computeIfAbsent(objective, obj -> new LinkedList<>());

		PolymorphismGenerator generator;
		String name = hex(generators.size());
		MethodKind bridge = new MethodKind(name, implementation.type());
		this.kinds.computeIfAbsent(objective, obj -> new LinkedList<>()).add(bridge);
		if (Generator.anonymous(objective))
		{
			generator = new NativePolymorphismInstantiationGenerator(objective, bridge);
		}
		else
		{
			generator = new MagicPolymorphismInstantiationGenerator(objective, new MethodKind(hex(generators.size()), implementation.type()));
		}
		generators.add(generator);
		this.names.put(implementation, name);
		this.objectives.put(implementation, objective);

		return this;
	}

	public PolymorphismFactory<T> construct(Class<?> objective, MethodKind implementation, MethodKind invocation)
	{
		List<PolymorphismGenerator> generators = this.generators.computeIfAbsent(objective, obj -> new LinkedList<>());

		PolymorphismGenerator generator;
		String name = hex(generators.size());
		MethodKind bridge = new MethodKind(name, implementation.type());
		this.kinds.computeIfAbsent(objective, obj -> new LinkedList<>()).add(bridge);
		if (Generator.anonymous(objective))
		{
			generator = new NativePolymorphismConstructGenerator(objective, bridge, invocation);
		}
		else
		{
			generator = new MagicPolymorphismConstructGenerator(objective, new MethodKind(hex(generators.size()), implementation.type()), invocation);
		}
		generators.add(generator);
		this.names.put(implementation, name);
		this.objectives.put(implementation, objective);

		return this;
	}

	public PolymorphismFactory<T> enumHelper(Class<?> objective)
	{
		List<PolymorphismGenerator> generators = this.generators.computeIfAbsent(objective, obj -> new LinkedList<>());
		List<MethodKind> kinds = this.kinds.computeIfAbsent(objective, obj -> new LinkedList<>());
		generators.add(new PolymorphismEnumHelperGenerator(objective));

		this.check(kinds, objective, "construct", Object.class, String.class);
		this.check(kinds, objective, "construct", objective, String.class);
		this.check(kinds, objective, "construct", Object.class, String.class, int.class);
		this.check(kinds, objective, "construct", objective, String.class, int.class);
		this.check(kinds, objective, "values", Object[].class);
		this.check(kinds, objective, "values", ACCESSOR.forName("[" + Generator.signature(objective).replace('/', '.')));
		this.check(kinds, objective, "values", void.class, Object[].class);
		this.check(kinds, objective, "values", void.class, ACCESSOR.forName("[" + Generator.signature(objective).replace('/', '.')));
		this.check(kinds, objective, "add", void.class, Object.class);
		this.check(kinds, objective, "add", void.class, objective);
		this.check(kinds, objective, "remove", void.class, int.class);

		return this;
	}

	public T allocate()
	{
		ClassWriter cw = new ClassWriter()
			.set(
				0x34,
				0x21,
				Generator.name(),
				this.accessor.isInterface() ? "java/lang/Object" : Generator.type(this.accessor),
				this.accessor.isInterface() ? new String[]{Generator.type(this.accessor)} : null
			);

		int cid = 0;

		Map<Class<?>, String> cids = new HashMap<>();
		Map<Class<?>, Class<?>> accessors = new HashMap<>();
		List<Class<?>> cidList = new ArrayList<>(this.generators.entrySet().size());
		Map<String, Object> implement = new HashMap<>();

		for (Map.Entry<Class<?>, List<PolymorphismGenerator>> entry : this.generators.entrySet())
		{
			Class<?> objective = entry.getKey();
			List<PolymorphismGenerator> generators = entry.getValue();

			String name = Generator.name();
			Class<?> accessor;

			{
				ClassWriter bridge = new ClassWriter()
					.set(
						0x34,
						AccessFlag.PUBLIC | AccessFlag.INTERFACE | AccessFlag.ABSTRACT,
						name,
						"java/lang/Object",
						null
					);
				List<MethodKind> kinds = this.kinds.get(objective);
				for (MethodKind kind : kinds)
				{
					bridge.method(new MethodWriter().set(AccessFlag.PUBLIC | AccessFlag.ABSTRACT, kind.name(), kind.type().toMethodDescriptorString()));
				}
				byte[] code = bridge.toByteArray();
				accessor = UNSAFE.defineClass(name, code, 0, code.length, null, null);
			}

			{
				ClassWriter invoke = new ClassWriter()
					.set(
						0x34,
						AccessFlag.PUBLIC | AccessFlag.SUPER,
						Generator.name(),
						JavaVM.CONSTANT[0],
						new String[]{name}
					);
				for (PolymorphismGenerator generator : generators)
				{
					generator.generate(invoke);
				}
				byte[] code = invoke.toByteArray();
				implement.put(hex(cid), UNSAFE.allocateInstance(UNSAFE.defineAnonymousClass(objective, code, null)));
			}

			accessors.put(objective, accessor);
			cidList.add(accessor);
			cids.put(accessor, hex(cid));
			cid++;
		}

		for (Class<?> accessor : cidList)
		{
			cw.field(new FieldWriter().set(AccessFlag.PRIVATE | AccessFlag.STATIC | AccessFlag.FINAL, cids.get(accessor), Generator.signature(accessor)));
		}

		for (Map.Entry<MethodKind, Class<?>> entry : this.objectives.entrySet())
		{
			MethodKind implementation = entry.getKey();
			Class<?> objective = entry.getValue();
			Class<?> accessor = accessors.get(objective);
			String name = cids.get(accessor);
			String invoke = this.names.get(implementation);
			MethodWriter method;
			CodeWriter code;
			cw.method(method = new MethodWriter()
				.set(AccessFlag.PUBLIC, implementation.name(), implementation.type().toMethodDescriptorString())
				.attribute(code = new CodeWriter()
					.field(Opcodes.GETSTATIC, cw.name, name, Generator.signature(accessor))
				)
			);
			Class<?>[] parameters = implementation.type().parameterArray();
			int local = 1;
			for (Class<?> param : parameters)
			{
				Generator.load(param, code, local);
				local += Generator.typeSize(param);
			}
			code.method(Opcodes.INVOKEINTERFACE, Generator.type(accessor), invoke, implementation.type().toMethodDescriptorString(), true);
			Generator.returner(implementation.type().returnType(), code);
			code.max(local, local);
			Generator.inline(method);
		}

		byte[] code = cw.toByteArray();
		Class<?> c = UNSAFE.defineAnonymousClass(this.accessor, code, null);
		for (Map.Entry<String, Object> entry : implement.entrySet())
		{
			String name = entry.getKey();
			Object field = entry.getValue();
			UNSAFE.putObject(c, UNSAFE.staticFieldOffset(ACCESSOR.getField(c, name)), field);
		}
		Object o = UNSAFE.allocateInstance(c);

		@SuppressWarnings("unchecked")
		T val = (T) o;
		return val;
	}

	private static String hex(int i)
	{
		byte[] ch = "0123456789ABCDEF".getBytes();
		byte[] val = new byte[4];
		val[0] = ch[(i >> 12) & 0xF];
		val[1] = ch[(i >> 8) & 0xF];
		val[2] = ch[(i >> 4) & 0xF];
		val[3] = ch[i & 0xF];
		return new String(val);
	}

	private void check(List<MethodKind> kinds, Class<?> objective, String name, Class<?> returnType, Class<?>... parameterTypes)
	{
		try
		{
			if (ACCESSOR.getMethod(this.accessor, name, returnType, parameterTypes) != null)
			{
				MethodKind kind = new MethodKind(name, returnType, parameterTypes);
				this.names.put(kind, kind.name());
				this.objectives.put(kind, objective);
				kinds.add(kind);
			}
		}
		catch (Throwable ignored)
		{
		}
	}
}
