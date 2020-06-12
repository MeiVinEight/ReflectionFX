package org.mve.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import sun.reflect.MagicAccessorImpl;

public final class ReflectionMagicAccessor extends MagicAccessorImpl implements MagicAccessor {
	private static final SecurityManager 0 = new SecurityManager();

	public final void setAccessible(AccessibleObject var1, boolean var2) {
		var1.override = var2;
	}

	public final Class<?> forName(String var1) {
		Class var10002 = this.getCallerClass();
		return Class.forName0(var1, true, var10002.classLoader, var10002);
	}

	public final Class<?> forName(String var1, boolean var2, ClassLoader var3) {
		return Class.forName0(var1, var2, var3, (Class)null);
	}

	public final Class<?> defineClass(ClassLoader var1, byte[] var2) {
		return var1.defineClass((String)null, var2, 0, var2.length);
	}

	public final Class<?> getCallerClass() {
		return 0.getClassContext()[2];
	}

	public final Class<?>[] getClassContext() {
		Class[] var10000 = 0.getClassContext();
		return (Class[])Arrays.copyOfRange(var10000, 1, var10000.length);
	}

	public final <T> T construct(Class<?> var1) {
		Constructor var10000 = var1.getDeclaredConstructor();
		var10000.override = true;
		return var10000.newInstance();
	}

	public final <T> T construct(Class<?> var1, Class<?> var2, Object[] var3) {
		Constructor var10000 = var1.getDeclaredConstructor(var2);
		var10000.override = true;
		return var10000.newInstance(var3);
	}

	public Object invokeMethodHandle(MethodHandle var1, Object... var2) {
		return var1.invokeWithArguments(var2);
	}

	public Field getField(Class<?> var1, String var2) {
		Field var10001 = Class.searchFields(var1.getDeclaredFields0(false), var2);
		if (var10001 == null) {
			throw new NoSuchFieldException(var2);
		} else {
			return var10001;
		}
	}

	public Method getMethod(Class<?> var1, String var2, Class<?>... var3) {
		Method var10000 = Class.searchMethods(var1.getDeclaredMethods0(false), var2, var3);
		if (var10000 == null) {
			throw new NoSuchMethodException(var1.getName() + "." + var2 + Class.argumentTypesToString(var3));
		} else {
			return var10000;
		}
	}

	public <T> Constructor<T> getConstructor(Class<?> var1, Class<?>... var2) {
		Constructor[] var3;
		int var10000 = (var3 = var1.getDeclaredConstructors0(false)).length;

		for(int var4 = 0; var10000 > var4; ++var4) {
			Constructor var10001 = var3[var4];
			if (Class.arrayContentsEq(var2, var3[var4].getParameterTypes())) {
				return var10001.copy();
			}
		}

		throw new NoSuchMethodException(var1.getName() + ".<init>" + Class.argumentTypesToString(var2));
	}

	public Field[] getFields(Class<?> var1) {
		return var1.getDeclaredFields0(false);
	}

	public Method[] getMethods(Class<?> var1) {
		return var1.getDeclaredMethods0(false);
	}

	public <T> Constructor<T>[] getConstructors(Class<?> var1) {
		return var1.getDeclaredConstructors0(false);
	}

	public void throwException(Throwable var1) {
		throw var1;
	}

	public void initialize(Object var1) {
		var1.<init>();
	}
}
