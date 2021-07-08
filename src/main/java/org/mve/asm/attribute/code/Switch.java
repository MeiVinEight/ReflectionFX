package org.mve.asm.attribute.code;

import org.mve.asm.Opcodes;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.io.RandomAccessByteArray;

import java.util.Arrays;
import java.util.Map;

public class Switch extends Instruction
{
	public Marker defaults;
	public int[] cases;
	public Marker[] offset;

	public Switch(int opcode, Marker def, int[] cases, Marker[] offsets)
	{
		super(opcode);
		this.defaults = def;
		this.cases = cases;
		this.offset = offsets;
	}

	@Override
	public void consume(ConstantArray pool, RandomAccessByteArray array, boolean[] wide, Map<int[], Marker> marker)
	{
		super.consume(pool, array, wide, marker);

		int pos = array.position() + 1;
		int pad = (4 - (pos % 4)) % 4;
		array.write(new byte[pad]);

		marker.put(new int[]{array.position(), this.opcode}, this.defaults);
		array.writeInt(0);

		switch (this.opcode)
		{
			case Opcodes.LOOKUPSWITCH:
			{
				array.writeInt(this.cases.length);
				for (int i = 0; i < this.cases.length; i++)
				{
					int cas = this.cases[i];
					Marker off = this.offset[i];
					array.writeInt(cas);
					marker.put(new int[]{array.position(), this.opcode}, off);
					array.writeInt(0);
				}
			}
			case Opcodes.TABLESWITCH:
			{
				for (int i = 0; i < this.cases.length-1; i++)
				{
					int n = i;

					for (int j = i+1; j < this.cases.length; j++)
					{
						if (this.cases[j] < this.cases[n])
						{
							n = j;
						}
					}

					Marker off = this.offset[n];
					this.offset[n] = this.offset[i];
					this.offset[i] = off;

					int m = this.cases[n];
					this.cases[n] = this.cases[i];
					this.cases[i] = m;
				}

				int low = this.cases[0];
				int high = this.cases[this.cases.length-1];
				array.writeInt(low);
				array.writeInt(high);

				int c = high - low + 1;

				for (int i = 0; i < c; i++)
				{
					int n = low + i;
					int j = Arrays.binarySearch(this.cases, n);
					if (j > -1)
					{
						marker.put(new int[]{array.position(), this.opcode}, this.offset[j]);
					}
					else
					{
						marker.put(new int[]{array.position(), this.opcode}, this.defaults);
					}
					array.writeInt(0);
				}
			}
		}
	}
}
