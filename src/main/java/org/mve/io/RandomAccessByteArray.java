package org.mve.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.UTFDataFormatException;
import java.util.Arrays;

public class RandomAccessByteArray implements ByteArrayAccessor, DataInput, DataOutput
{
	private static final int DEFAULT_EXPANSION_SIZE = 1 << 8;

	private final int expansionSize;
	private byte[] arr;
	private int length;
	private int pointer;

	public RandomAccessByteArray()
	{
		this(new byte[0], DEFAULT_EXPANSION_SIZE);
	}

	public RandomAccessByteArray(int expansionSize)
	{
		this(new byte[0], expansionSize);
	}

	public RandomAccessByteArray(byte[] b)
	{
		this(b, DEFAULT_EXPANSION_SIZE);
	}

	public RandomAccessByteArray(byte[] b, int expansionSize)
	{
		this.expansionSize = expansionSize;
		this.arr = b;
		this.length = arr.length;
		this.pointer = 0;
	}

	@Override
	public void readFully(byte[] b)
	{
		readFully(b, 0, b.length);
	}

	@Override
	public void readFully(byte[] b, int off, int len)
	{
		if (this.pointer + len > this.length || off + len > b.length || off < 0 || len < 0) throw new IndexOutOfBoundsException();
		System.arraycopy(this.arr, this.pointer, b, off, len);
		this.pointer += len;
	}

	@Override
	public int skipBytes(int n)
	{
		return this.skip(n);
	}

	@Override
	public boolean readBoolean()
	{
		return this.read() != 0;
	}

	@Override
	public byte readByte()
	{
		this.ensureAvailable(1);
		return this.arr[this.pointer++];
	}

	@Override
	public int readUnsignedByte()
	{
		return this.readByte() & 0XFF;
	}

	@Override
	public short readShort()
	{
		this.ensureAvailable(2);
		int i = 0;
		i = i | this.read();
		i = i << 8;
		i = i | this.read();
		return (short) i;
	}

	@Override
	public int readUnsignedShort()
	{
		return this.readShort() & 0XFFFF;
	}

	@Override
	public char readChar()
	{
		return (char) this.readShort();
	}

	@Override
	public int readInt()
	{
		this.ensureAvailable(4);
		int i=0;
		i = i | this.readShort();
		i = i << 16;
		i = i | this.readShort();
		return i;
	}

	@Override
	public long readLong()
	{
		this.ensureAvailable(8);
		long l = 0;
		l = l | this.readInt();
		l = l << 32;
		l = l | this.readInt();
		return l;
	}

	@Override
	public float readFloat()
	{
		return Float.intBitsToFloat(this.readInt());
	}

	@Override
	public double readDouble()
	{
		return Double.longBitsToDouble(this.readLong());
	}

	@Override
	public String readLine()
	{
		RandomAccessByteArray buf = new RandomAccessByteArray(256);
		loop: while (this.available() > 0)
		{
			int c = this.read();
			switch (c)
			{
				case '\n': break loop;
				case '\r':
				{
					if (this.available() > 0)
					{
						int c1 = this.read();
						if (c1 != '\n') this.skip(-1);
					}
					break loop;
				}
				default:
				{
					buf.write(c);
					break;
				}
			}
		}
		return new String(buf.toByteArray());
	}

	@Override
	public String readUTF()
	{
		int utflen = this.readUnsignedShort();
		if (this.available() < utflen)
		{
			this.skip(-2);
			throw new ArrayIndexOutOfBoundsException();
		}

		byte[] bytearr = new byte[utflen];
		char[] chararr = new char[utflen];

		int c, char2, char3;
		int count = 0;
		int chararrCount = 0;

		this.readFully(bytearr);

		while (count < utflen)
		{
			c = bytearr[count] & 0XFF;
			if (c > 127) break;
			count++;
			chararr[chararrCount++] = (char) c;
		}

		while (count < utflen)
		{
			c = bytearr[count] & 0XFF;
			switch (c >> 4)
			{
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				{
					count++;
					chararr[chararrCount++] = (char) c;
					break;
				}
				case 12:
				case 13:
				{
					count += 2;
					if (count > utflen) throw new RuntimeException(new UTFDataFormatException("malformed input: partial character at end"));
					char2 = bytearr[count-1];
					if ((char2 & 0XC0) != 0X80) throw new RuntimeException(new UTFDataFormatException("malformed input around byte " + count));
					chararr[chararrCount++] = (char) (((c & 0X1F) << 6) | (char2 & 0X3F));
					break;
				}
				case 14:
				{
					count+=3;
					if (count > utflen) throw new RuntimeException(new UTFDataFormatException("malformed input: partial character at end"));
					char2 = bytearr[count-2];
					char3 = bytearr[count-1];
					if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) throw new RuntimeException(new UTFDataFormatException("malformed input around byte " + (count-1)));
					chararr[chararrCount++] = (char) (((c & 0X0F) << 12) | ((char2 & 0X3F) << 6) | (char3 & 0X3F));
					break;
				}
				default:
				{
					throw new RuntimeException(new UTFDataFormatException("malformed input around byte " + count));
				}
			}
		}

		return new String(chararr, 0, chararrCount);
	}

	@Override
	public void seek(int n)
	{
		if (n > this.length || n < 0) throw new IndexOutOfBoundsException();
		this.pointer = n;
	}

	@Override
	public int skip(int n)
	{
		int skipped = Math.max(0, this.pointer + Math.min(this.length - this.pointer, n));
		this.pointer += skipped;
		return skipped;
	}

	@Override
	public void write(int b)
	{
		this.ensureCapacity(this.pointer + 1);
		this.arr[pointer++] = (byte) (b & 0XFF);
	}

	@Override
	public void write(byte[] b)
	{
		this.write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len)
	{
		if (off + len > b.length || off < 0 || len < 0) throw new ArrayIndexOutOfBoundsException();
		this.ensureCapacity(this.pointer + len);
		System.arraycopy(b, off, this.arr, this.pointer, len);
		this.pointer += len;
	}

	@Override
	public int read()
	{
		return this.length > this.pointer ? this.arr[this.pointer++] : -1;
	}

	@Override
	public int read(byte[] b)
	{
		 return this.read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len)
	{
		if (off + len > b.length || off < 0 || len < 0) throw new ArrayIndexOutOfBoundsException();
		int readLength = Math.min(len, this.length - this.pointer);
		System.arraycopy(this.arr, this.pointer, b, off, readLength);
		pointer+=readLength;
		return readLength;
	}

	@Override
	public void writeBoolean(boolean v)
	{
		this.ensureCapacity(this.pointer + 1);
		this.arr[this.pointer++] = (byte) (v ? 1 : 0);
	}

	@Override
	public void writeByte(int v)
	{
		this.ensureCapacity(this.pointer + 1);
		this.arr[this.pointer++] = (byte) v;
	}

	@Override
	public void writeShort(int v)
	{
		this.ensureCapacity(this.pointer + 2);
		byte[] b = {(byte) ((v >>> 8) & 0XFF), (byte) (v & 0XFF)};
		this.write(b);
	}

	@Override
	public void writeChar(int v)
	{
		this.writeShort(v);
	}

	@Override
	public void writeInt(int v)
	{
		this.ensureCapacity(this.pointer + 4);
		byte[] b = {
			(byte) ((v >>> 24) & 0XFF),
			(byte) ((v >>> 16) & 0XFF),
			(byte) ((v >>> 8) & 0XFF),
			(byte) (v & 0XFF)
		};
		this.write(b);
	}

	@Override
	public void writeLong(long v)
	{
		this.ensureCapacity(this.pointer + 8);
		byte[] b = {
			(byte) ((v >>> 56) & 0XFF),
			(byte) ((v >>> 48) & 0XFF),
			(byte) ((v >>> 40) & 0XFF),
			(byte) ((v >>> 32) & 0XFF),
			(byte) ((v >>> 24) & 0XFF),
			(byte) ((v >>> 16) & 0XFF),
			(byte) ((v >>> 8) & 0XFF),
			(byte) (v & 0XFF)
		};
		this.write(b);
	}

	@Override
	public void writeFloat(float v)
	{
		this.writeInt(Float.floatToIntBits(v));
	}

	@Override
	public void writeDouble(double v)
	{
		this.writeLong(Double.doubleToLongBits(v));
	}

	@Override
	public void writeBytes(String s)
	{
		char[] chs = s.toCharArray();
		for (char c : chs) this.write(c & 0XFF);
	}

	@Override
	public void writeChars(String s)
	{
		this.ensureCapacity(this.pointer + s.length());
		char[] ch = s.toCharArray();
		for (char c : ch) this.writeChar(c);
	}

	@Override
	public void writeUTF(String s)
	{
		int strlen = s.length();
		int utflen = 0;
		int c;

		for (int i=0; i<strlen; i++)
		{
			c = s.charAt(i);
			if ((c >= 0X0001) && (c <= 0X007F)) utflen++;
			else if (c > 0X07FF) utflen += 3;
			else  utflen += 2;
		}

		if (utflen > 65535) throw new RuntimeException(new UTFDataFormatException("encoded string too long: " + utflen + " bytes"));

		this.writeShort(utflen);

		int i;
		for (i=0; i<strlen; i++)
		{
			c = s.charAt(i);
			if ((c >= 0X0001) && (c <= 0X007F)) this.write(c);
			else if (c > 0X07FF)
			{
				this.write(0XE0 | ((c >> 12) & 0X0F));
				this.write(0X80 | ((c >>  6) & 0X3F));
				this.write(0X80 | ((c)       & 0X3F));
			}
			else
			{
				this.write(0XC0 | ((c >> 6) & 0X1F));
				this.write(0X80 | ((c)      & 0X3F));
			}
		}
	}

	@Override
	public void insert(int i)
	{
		this.shiftRight(this.pointer, 1);
		this.arr[this.pointer++] = (byte) (i & 0XFF);
	}

	@Override
	public void insert(byte[] b)
	{
		this.insert(b, 0, b.length);
	}

	@Override
	public void insert(byte[] b, int off, int len)
	{
		if (off + len > b.length || off < 0 || len < 0) throw new ArrayIndexOutOfBoundsException();
		this.shiftRight(this.pointer, len);
		System.arraycopy(b, off, this.arr, this.pointer, len);
		this.pointer += len;
	}

	@Override
	public int delete()
	{
		int b = this.arr[this.pointer] & 0XFF;
		this.shiftLeft(this.pointer+1, 1);
		return b;
	}

	@Override
	public int delete(byte[] b)
	{
		return this.delete(b, 0, b.length);
	}

	@Override
	public int delete(byte[] b, int off, int len)
	{
		if (off + len > b.length || off < 0 || len < 0) throw new ArrayIndexOutOfBoundsException();
		int deletedSize = Math.min(this.length - this.pointer, len);
		System.arraycopy(this.arr, this.pointer, b, off, deletedSize);
		this.shiftLeft(this.pointer + len, len);
		return deletedSize;
	}

	@Override
	public int available()
	{
		return this.length - this.pointer;
	}

	@Override
	public int length()
	{
		return this.length;
	}

	@Override
	public byte[] toByteArray()
	{
		return Arrays.copyOf(this.arr, this.length);
	}

	private void shiftRight(int start, int off)
	{
		int dataSize = this.length - this.pointer;
		this.ensureCapacity(this.length + off);
		for (int i = dataSize-1; i > -1; i--) this.arr[start + i + off] = this.arr[start + i];
	}

	private void shiftLeft(int start, int off)
	{
		int dataSize = this.length - start;
		for (int i=0; i<dataSize; i++) this.arr[start + i] = this.arr[start + i + off];
	}

	private void ensureCapacity(int minCapacity)
	{
		if (minCapacity > this.arr.length)
		{
			expand(minCapacity);
		}
		this.length = Math.max(this.length, minCapacity);
	}

	private void expand(int minCapacity)
	{
		int oldSize = this.arr.length;
		int expSize = minCapacity - oldSize;
		int newSize = Math.max(expSize, this.expansionSize);
		this.arr = Arrays.copyOf(this.arr, newSize);
	}

	private void ensureAvailable(int len)
	{
		if (this.length - this.pointer < len) throw new ArrayIndexOutOfBoundsException();
	}
}
