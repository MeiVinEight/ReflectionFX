package org.mve.util.asm;

import java.util.Objects;

public class StructTypeAnnotationPath
{
	private byte pathLength;
	private StructPath[] paths = new StructPath[0];

	public byte getPathLength()
	{
		return pathLength;
	}

	public void addPath(StructPath path)
	{
		StructPath[] arr = new StructPath[this.pathLength];
		System.arraycopy(this.paths, 0, arr, 0, this.pathLength);
		arr[this.pathLength] = Objects.requireNonNull(path);
		this.paths = arr;
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
}
