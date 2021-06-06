package org.mve.asm.file;

import org.mve.util.Binary;

import java.util.Objects;

public class StructTypeAnnotation implements Binary
{
	private byte targetType;
	private TypeAnnotationTarget target;
	private StructTypeAnnotationPath targetPath;
	private short typeIndex;
	private short elementValuePairsCount;
	private StructElementValuePairs[] elementValuePairs = new StructElementValuePairs[0];

	public byte getTargetType()
	{
		return targetType;
	}

	public TypeAnnotationTarget getTarget()
	{
		return target;
	}

	public void setTypeAnnotationTarget(byte targetType, TypeAnnotationTarget target)
	{
		this.targetType = targetType;
		this.target = target;
	}

	public StructTypeAnnotationPath getTargetPath()
	{
		return targetPath;
	}

	public void setTargetPath(StructTypeAnnotationPath targetPath)
	{
		this.targetPath = targetPath;
	}

	public short getTypeIndex()
	{
		return typeIndex;
	}

	public void setTypeIndex(short typeIndex)
	{
		this.typeIndex = typeIndex;
	}

	public short getElementValuePairsCount()
	{
		return elementValuePairsCount;
	}

	public void addElementValuePairs(StructElementValuePairs pairs)
	{
		StructElementValuePairs[] arr = new StructElementValuePairs[this.elementValuePairsCount+1];
		System.arraycopy(this.elementValuePairs, 0, arr, 0, this.elementValuePairsCount);
		arr[this.elementValuePairsCount] = Objects.requireNonNull(pairs);
		this.elementValuePairs = arr;
		this.elementValuePairsCount++;
	}

	public void setElementValuePair(int index, StructElementValuePairs pairs)
	{
		this.elementValuePairs[index] = Objects.requireNonNull(pairs);
	}

	public StructElementValuePairs getElementValuePairs(int index)
	{
		return this.elementValuePairs[index];
	}

	public int getLength()
	{
		int len = 5 + target.getLength() + targetPath.getLength();
		for (StructElementValuePairs s : this.elementValuePairs) len += s.getLength();
		return len;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		int index = 0;
		byte[] b = new byte[len];
		b[index++] = this.targetType;
		int l = this.target.getLength();
		System.arraycopy(this.target.toByteArray(), 0, b, index, l);
		index += l;
		l = this.targetPath.getLength();
		System.arraycopy(this.targetPath.toByteArray(), 0, b, index, l);
		index+=l;
		b[index++] = (byte) ((this.typeIndex >>> 8) & 0XFF);
		b[index++] = (byte) (this.typeIndex & 0XFF);
		b[index++] = (byte) ((this.elementValuePairsCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.elementValuePairsCount & 0XFF);
		for (StructElementValuePairs s : this.elementValuePairs)
		{
			l = s.getLength();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
