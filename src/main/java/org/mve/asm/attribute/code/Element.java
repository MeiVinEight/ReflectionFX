package org.mve.asm.attribute.code;

import org.mve.asm.file.constant.ConstantArray;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public interface Element
{
	void consume(ConstantArray pool, RandomAccessByteArray array, boolean[] wide, Map<Integer, Marker> marker);
}
