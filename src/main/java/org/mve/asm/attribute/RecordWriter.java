package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.record.Record;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeRecord;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.attribute.record.RecordComponent;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public class RecordWriter implements AttributeWriter
{
	public Record[] record;

	public RecordWriter()
	{
		this.record = new Record[0];
	}

	public RecordWriter(Record... record)
	{
		this.record = record;
	}

	public RecordWriter record(Record record)
	{
		this.record = Arrays.copyOf(this.record, this.record.length+1);
		this.record[this.record.length-1] = record;
		return this;
	}
	public RecordWriter record(String name, String type, AttributeWriter... attribute)
	{
		return this.record(new Record(name, type, attribute));
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeRecord record = new AttributeRecord();
		record.name = ConstantPoolFinder.findUTF8(pool, AttributeType.RECORD.getName());
		for (Record r : this.record)
		{
			RecordComponent rc = new RecordComponent();
			rc.name = ConstantPoolFinder.findUTF8(pool, r.name);
			rc.type = ConstantPoolFinder.findUTF8(pool, r.type);
			for (AttributeWriter attribute : r.attribute)
			{
				rc.attribute(attribute.getAttribute(pool));
			}
			record.component(rc);
		}
		return record;
	}
}
