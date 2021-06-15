package org.mve.asm.attribute.code;

import org.mve.asm.file.ConstantPool;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public interface Element
{
	void consume(ConstantPool pool, RandomAccessByteArray array, boolean[] wide, Map<Integer, Marker> marker);
}
