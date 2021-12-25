package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeLineNumberTable;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.attribute.line.LineNumber;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public class LineNumberTableWriter implements AttributeWriter
{
	public Marker[] offset = new Marker[0];
	public int[] number = new int[0];

	public LineNumberTableWriter line(Marker offset, int number)
	{
		this.offset = Arrays.copyOf(this.offset, this.offset.length+1);
		this.offset[this.offset.length-1] = offset;

		this.number = Arrays.copyOf(this.number, this.number.length+1);
		this.number[this.number.length-1] = number;

		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeLineNumberTable table = new AttributeLineNumberTable();
		table.name = ConstantPoolFinder.findUTF8(pool, AttributeType.LINE_NUMBER_TABLE.getName());
		for (int i=0; i<this.offset.length; i++)
		{
			LineNumber line = new LineNumber();
			line.start = this.offset[i].address;
			line.line = this.number[i];
			table.line(line);
		}
		return table;
	}
}
