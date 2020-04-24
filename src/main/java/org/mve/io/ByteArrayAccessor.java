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

	int delete();

	int delete(byte[] b);

	int delete(byte[] b, int off, int len);

	int available();

	int length();

	byte[] toByteArray();
}
