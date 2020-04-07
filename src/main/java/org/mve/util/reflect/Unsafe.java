package org.mve.util.reflect;

import java.lang.reflect.Field;
import java.security.ProtectionDomain;

public interface Unsafe
{
	byte getByte(long offset);

	byte getByte(Object obj, long offset);

	byte getByteVolatile(Object obj, long offset);

	void putByte(long offset, byte b);

	void putByte(Object obj, long offset, byte b);

	void putByteVolatile(Object obj, long offset, byte b);

	short getShort(long offset);

	short getShort(Object obj, long offset);

	short getShortVolatile(Object obj, long offset);

	void putShort(long offset, short s);

	void putShort(Object obj, long offset, short s);

	void putShortVolatile(Object obj, long offset, short s);

	int getInt(long offset);

	int getInt(Object obj, long offset);

	int getIntVolatile(Object obj, long offset);

	void putInt(long offset, int i);

	void putInt(Object obj, long offset, int i);

	void putIntVolatile(Object obj, long offset, int i);

	long getLong(long offset);

	long getLong(Object obj, long offset);

	long getLongVolatile(Object obj, long offset);

	void putLong(long offset, long l);

	void putLong(Object obj, long offset, long l);

	void putLongVolatile(Object obj, long offset, long l);

	float getFloat(long offset);

	float getFloat(Object obj, long offset);

	float getFloatVolatile(Object obj, long offset);

	void putFloat(long offset, float f);

	void putFloat(Object obj, long offset, float f);

	void putFloatVolatile(Object obj, long offset, float f);

	double getDouble(long offset);

	double getDouble(Object obj, long offset);

	double getDoubleVolatile(Object obj, long offset);

	void putDouble(long offset, double d);

	void putDouble(Object obj, long offset, double d);

	void putDoubleVolatile(Object obj, long offset, double d);

	boolean getBoolean(long offset);

	boolean getBoolean(Object obj, long offset);

	boolean getBooleanVolatile(Object obj, long offset);

	void putBoolean(long offset, boolean b);

	void putBoolean(Object obj, long offset, boolean b);

	void putBooleanVolatile(Object obj, long offset, boolean b);

	char getChar(long offset);

	char getChar(Object obj, long offset);

	char getCharVolatile(Object obj, long offset);

	void putChar(long offset, char c);

	void putChar(Object obj, long offset, char c);

	void putCharVolatile(Object obj, long offset, char c);

	Object getObject(Object obj, long offset);

	Object getObjectVolatile(Object obj, long offset);

	void putObject(Object obj, long offset, Object value);

	void putObjectVolatile(Object obj, long offset, Object value);

	long getAddress(long address);

	void putAddress(long address, long value);

	long allocateMemory(long length);

	long reallocateMemory(long address, long length);

	void setMemory(Object o, long offset, long bytes, byte value);

	void setMemory(long address, long bytes, byte value);

	void copyMemory(Object src, long secOff, Object dest, long destOff, long length);

	void copyMemory(long sec, long dest, long length);

	void freeMemory(long address);

	long staticFieldOffset(Field f);

	long objectFieldOffset(Field f);

	Object staticFieldBase(Field f);

	boolean shouldBeInitialized(Class<?> c);

	void ensureClassInitialized(Class<?> c);

	int arrayBaseOffset(Class<?> c);

	int arrayIndexScale(Class<?> c);

	int addressSize();

	int pageSize();

	Class<?> defineClass(String name, byte[] code, int offset, int length, ClassLoader loader, ProtectionDomain protectionDomain);

	Class<?> defineAnonymousClass(Class<?> hostClass, byte[] data, Object[] cpPatches);

	Object allocateInstance(Class<?> c) throws InstantiationException;

	void throwException(Throwable t);

	boolean compareAndSwapInt(Object obj, long offset, int expected, int value);

	boolean compareAndSwapLong(Object obj, long offset, long expected, long value);

	boolean compareAndSwapObject(Object obj, long offset, Object expected, Object value);

	void putOrderedInt(Object o, long offset, int x);

	void putOrderedLong(Object o, long offset, long x);

	void putOrderedObject(Object o, long offset, Object x);

	void unpark(Object thread);

	void park(boolean isAbsolute, long time);

	int getLoadAverage(double[] loadavg, int nelems);

	int getAndAddInt(Object o, long offset, int delta);

	int getAndSetInt(Object o, long offset, int newValue);

	long getAndAddLong(Object o, long offset, long delta);

	long getAndSetLong(Object o, long offset, long newValue);

	Object getAndSetObject(Object o, long offset, Object newValue);

	void loadFence();

	void storeFence();

	void fullFence();
}
