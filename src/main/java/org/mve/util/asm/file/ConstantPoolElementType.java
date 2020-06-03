package org.mve.util.asm.file;

public enum ConstantPoolElementType
{
	CONSTANT_NULL((byte)0),
	CONSTANT_UTF8((byte)1),
	CONSTANT_INTEGER((byte)3),
	CONSTANT_FLOAT((byte)4),
	CONSTANT_LONG((byte)5),
	CONSTANT_DOUBLE((byte)6),
	CONSTANT_CLASS((byte)7),
	CONSTANT_STRING((byte)8),
	CONSTANT_FIELD_REFERENCE((byte)9),
	CONSTANT_METHOD_REFERENCE((byte)10),
	CONSTANT_INTERFACE_METHOD_REFERENCE((byte)11),
	CONSTANT_NAME_AND_TYPE((byte)12),
	CONSTANT_METHOD_HANDLE((byte)15),
	CONSTANT_METHOD_TYPE((byte)16),
	CONSTANT_DYNAMIC((byte)17),
	CONSTANT_INVOKE_DYNAMIC((byte)18),
	CONSTANT_MODULE((byte)19),
	CONSTANT_PACKAGE((byte)20);

	private final byte code;

	private ConstantPoolElementType(byte code)
	{
		this.code = code;
	}

	public byte getCode()
	{
		return this.code;
	}

	public static ConstantPoolElementType getType(int tag)
	{
		switch (tag)
		{
			case 1: return CONSTANT_UTF8;
			case 3: return CONSTANT_INTEGER;
			case 4: return CONSTANT_FLOAT;
			case 5: return CONSTANT_LONG;
			case 6: return CONSTANT_DOUBLE;
			case 7: return CONSTANT_CLASS;
			case 8: return CONSTANT_STRING;
			case 9: return CONSTANT_FIELD_REFERENCE;
			case 10: return CONSTANT_METHOD_REFERENCE;
			case 11: return CONSTANT_INTERFACE_METHOD_REFERENCE;
			case 12: return CONSTANT_NAME_AND_TYPE;
			case 15: return CONSTANT_METHOD_HANDLE;
			case 16: return CONSTANT_METHOD_TYPE;
			case 18: return CONSTANT_INVOKE_DYNAMIC;
			case 19: return CONSTANT_MODULE;
			case 20: return CONSTANT_PACKAGE;
			default: throw new IllegalArgumentException("Unknown constant tag "+tag);
		}
	}
}
