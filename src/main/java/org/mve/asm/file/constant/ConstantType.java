package org.mve.asm.file.constant;

public enum ConstantType
{
	CONSTANT_NULL(0x00),
	CONSTANT_UTF8(0x01),
	CONSTANT_INTEGER(0x03),
	CONSTANT_FLOAT(0x04),
	CONSTANT_LONG(0x05),
	CONSTANT_DOUBLE(0x06),
	CONSTANT_CLASS(0x07),
	CONSTANT_STRING(0x08),
	CONSTANT_FIELD_REFERENCE(0x09),
	CONSTANT_METHOD_REFERENCE(0x0A),
	CONSTANT_INTERFACE_METHOD_REFERENCE(0x0B),
	CONSTANT_NAME_AND_TYPE(0x0C),
	CONSTANT_METHOD_HANDLE(0x0E),
	CONSTANT_METHOD_TYPE(0x10),
	CONSTANT_DYNAMIC(0x11),
	CONSTANT_INVOKE_DYNAMIC(0x12),
	CONSTANT_MODULE(0x13),
	CONSTANT_PACKAGE(0x14);

	private final byte code;

	private ConstantType(int code)
	{
		this.code = (byte) code;
	}

	public byte code()
	{
		return this.code;
	}

	public static ConstantType type(int tag)
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
