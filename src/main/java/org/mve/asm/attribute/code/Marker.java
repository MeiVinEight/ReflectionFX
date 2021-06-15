package org.mve.asm.attribute.code;

import org.mve.asm.file.ConstantPool;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class Marker implements Element
{
	public int address = 0;

	@Override
	public void consume(ConstantPool pool, RandomAccessByteArray array, boolean[] wide, Map<Integer, Marker> marker)
	{
		this.address = array.position();
	}
}
