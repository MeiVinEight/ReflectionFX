package org.mve.io;

public interface ByteArrayAccessor
{
	void seek(int n);

	int skip(int n);

	void write(int i);

	void write(byte[] b);

	void write(byte[] b, int off, int len);

	int read();

	int read(byte[] b);

	int read(byte[] b, int off, int len);

	void insert(int i);

	void insert(byte[] b);

	void insert(byte[] b, int off, int len);

	void insertByte(int b);

	void insertShort(int s);

	void insertInt(int i);

	void insertLong(long l);

	void insertFloat(float f);

	void insertDouble(double d);

	void insertBoolean(boolean b);

	void insertChar(char c);

	void insertUTF(String str);

	int delete();

	int delete(byte[] b);

	int delete(byte[] b, int off, int len);

	int available();

	int length();

	int offset();

	byte[] toByteArray();
}
