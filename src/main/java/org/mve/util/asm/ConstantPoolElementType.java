package org.mve.util.asm;

public enum ConstantPoolElementType
{
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
}
