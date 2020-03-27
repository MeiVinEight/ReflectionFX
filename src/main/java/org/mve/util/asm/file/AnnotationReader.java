package org.mve.util.asm.file;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AnnotationReader
{
	public static StructAnnotation read(ClassFile file, InputStream input) throws IOException
	{
		DataInputStream in = new DataInputStream(input);
		StructAnnotation annotation = new StructAnnotation();
		annotation.setTypeIndex(in.readShort());
		short count = in.readShort();
		for (int i = 0; i < count; i++)
		{
			StructElementValuePairs pairs = new StructElementValuePairs();
			pairs.setElementNameIndex(in.readShort());
			pairs.setElementValue(ElementValueReader.read(file, input));
			annotation.addElementValuePairs(pairs);
		}
		return annotation;
	}
}
