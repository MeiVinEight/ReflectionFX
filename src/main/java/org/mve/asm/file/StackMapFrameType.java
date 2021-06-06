package org.mve.asm.file;

public enum StackMapFrameType
{
	STACK_MAP_SAME_FRAME((byte)0, (byte)63),
	STACK_MAP_SAME_LOCALS_1_ITEM_FRAME((byte)64, (byte)127),
	STACK_MAP_SAME_LOCALS_1_ITEM_FRAME_EXTENDED((byte)247),
	STACK_MAP_CHOP_FRAME((byte)248, (byte)250),
	STACK_MAP_SAME_FRAME_EXTENDED((byte)251),
	STACK_MAP_APPEND_FRAME((byte)252, (byte)254),
	STACK_MAP_FULL_FRAME((byte)255);
	private final byte low;
	private final byte high;

	private StackMapFrameType(byte low, byte high)
	{
		this.low = low;
		this.high = high;
	}

	private StackMapFrameType(byte type)
	{
		this(type, type);
	}

	public byte getLow()
	{
		return low;
	}

	public byte getHigh()
	{
		return high;
	}

	public static StackMapFrameType getType(int type)
	{
		if (type >=0 && type <= 63) return STACK_MAP_SAME_FRAME;
		else if (type >= 64 && type <= 127) return STACK_MAP_SAME_LOCALS_1_ITEM_FRAME;
		else if (type == 247) return STACK_MAP_SAME_LOCALS_1_ITEM_FRAME_EXTENDED;
		else if (type >= 248 && type <= 250) return STACK_MAP_CHOP_FRAME;
		else if (type == 251) return STACK_MAP_SAME_FRAME_EXTENDED;
		else if (type >= 252 && type <= 254) return STACK_MAP_APPEND_FRAME;
		else if (type == 255) return STACK_MAP_FULL_FRAME;
		else return null;
	}
}
