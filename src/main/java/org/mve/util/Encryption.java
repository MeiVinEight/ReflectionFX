package org.mve.util;

import org.mve.io.RandomAccessByteArray;
import org.mve.util.reflect.ReflectionFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.zip.CRC32;

public class Encryption
{
	public static long CRC32(File file) throws IOException
	{
		FileInputStream in = new FileInputStream(file);
		long crc = CRC32(in);
		in.close();
		return crc;
	}

	public static long CRC32(URL url) throws IOException
	{
		InputStream in = url.openStream();
		long crc = CRC32(in);
		in.close();
		return crc;
	}

	public static long CRC32(InputStream in) throws IOException
	{
		CRC32 crc32 = new CRC32();
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) crc32.update(buf, 0, len);
		return crc32.getValue();
	}

	public static long CRC32(byte[] b)
	{
		RandomAccessByteArray arr = new RandomAccessByteArray(b);
		CRC32 crc32 = new CRC32();
		byte[] buf = new byte[1024];
		int len;
		while ((len = arr.read(buf)) > 0) crc32.update(buf, 0, len);
		return crc32.getValue();
	}

	public static byte[] MD5(File file) throws IOException
	{
		FileInputStream in = new FileInputStream(file);
		byte[] md5 = MD5(in);
		in.close();
		return md5;
	}

	public static byte[] MD5(URL url) throws IOException
	{
		InputStream in = url.openStream();
		byte[] b = MD5(in);
		in.close();
		return b;
	}

	public static byte[] MD5(InputStream in) throws IOException
	{
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("MD5");
		}
		catch (Exception e)
		{
			ReflectionFactory.ACCESSOR.throwException(e);
			throw new RuntimeException();
		}
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) md.update(buf, 0, len);
		return md.digest();
	}

	public static byte[] MD5(byte[] b)
	{
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("MD5");
		}
		catch (Exception e)
		{
			ReflectionFactory.ACCESSOR.throwException(e);
			throw new RuntimeException();
		}
		RandomAccessByteArray arr = new RandomAccessByteArray(b);
		byte[] buf = new byte[1024];
		int len;
		while ((len = arr.read(buf)) > 0) md.update(buf, 0, len);
		return md.digest();
	}

	public static byte[] SHA1(File file) throws IOException
	{
		FileInputStream in = new FileInputStream(file);
		byte[] sha1 = SHA1(in);
		in.close();
		return sha1;
	}

	public static byte[] SHA1(URL url) throws IOException
	{
		InputStream in = url.openStream();
		byte[] b = SHA1(in);
		in.close();
		return b;
	}

	public static byte[] SHA1(InputStream in) throws IOException
	{
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("SHA-1");
		}
		catch (Exception e)
		{
			ReflectionFactory.ACCESSOR.throwException(e);
			throw new RuntimeException();
		}
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) md.update(buf, 0, len);
		return md.digest();
	}

	public static byte[] SHA1(byte[] b)
	{
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("SHA-1");
		}
		catch (Exception e)
		{
			ReflectionFactory.ACCESSOR.throwException(e);
			throw new RuntimeException();
		}
		RandomAccessByteArray arr = new RandomAccessByteArray(b);
		byte[] buf = new byte[1024];
		int len;
		while ((len = arr.read(buf)) > 0) md.update(buf, 0, len);
		return md.digest();
	}
}
