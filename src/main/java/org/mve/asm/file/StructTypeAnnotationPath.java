package org.mve.asm.file;

import org.mve.util.Binary;

import java.util.Arrays;
import java.util.Objects;

public class StructTypeAnnotationPath implements Binary
{
	private byte pathLength;
	private StructPath[] paths = new StructPath[0];

	public byte getPathLength()
	{
		return pathLength;
	}

	public void addPath(StructPath path)
	{
		this.paths = Arrays.copyOf(this.paths, this.pathLength+1);
		this.paths[this.pathLength] = Objects.requireNonNull(path);
		this.pathLength++;
	}

	public void setPath(int index, StructPath path)
	{
		this.paths[index] = Objects.requireNonNull(path);
	}

	public StructPath getPath(int index)
	{
		return this.paths[index];
	}

	public int getLength()
	{
		return 1 + (2 * this.pathLength);
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		b[0] = this.pathLength;
		int index = 1;
		for (StructPath s : this.paths)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 2);
			index+=2;
		}
		return b;
	}
}
