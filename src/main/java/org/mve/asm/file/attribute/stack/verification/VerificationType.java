package org.mve.asm.file.attribute.stack.verification;

public enum VerificationType
{
	ITEM_TOP((byte)0, 1),
	ITEM_INTEGER((byte)1, 1),
	ITEM_FLOAT((byte)2, 1),
	ITEM_DOUBLE((byte)3, 1),
	ITEM_LONG((byte)4, 1),
	ITEM_NULL((byte)5, 1),
	ITEM_UNINITIALIZED_THIS((byte)6, 1),
	ITEM_OBJECT((byte)7, 3),
	ITEM_UNINITIALIZED((byte)8, 3);

	private final byte type;
	private final int length;

	private VerificationType(byte type, int length)
	{
		this.type = type;
		this.length = length;
	}

	public byte type()
	{
		return type;
	}

	public int length()
	{
		return length;
	}

	public static VerificationType getType(int type)
	{
		switch (type)
		{
			case 0: return ITEM_TOP;
			case 1: return ITEM_INTEGER;
			case 2: return ITEM_FLOAT;
			case 3: return ITEM_DOUBLE;
			case 4: return ITEM_LONG;
			case 5: return ITEM_NULL;
			case 6: return ITEM_UNINITIALIZED_THIS;
			case 7: return ITEM_OBJECT;
			case 8: return ITEM_UNINITIALIZED;
			default: return null;
		}
	}
}
