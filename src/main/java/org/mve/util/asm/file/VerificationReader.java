package org.mve.util.asm.file;

import org.mve.io.RandomAccessByteArray;

public class VerificationReader
{
	public static Verification read(ClassFile file, RandomAccessByteArray input)
	{
		byte typeTag = input.readByte();
		VerificationType type = VerificationType.getType(typeTag);
		if (type == VerificationType.ITEM_TOP)
		{
			return new VerificationTopVariable();
		}
		else if (type == VerificationType.ITEM_INTEGER)
		{
			return new VerificationIntegerVariable();
		}
		else if (type == VerificationType.ITEM_FLOAT)
		{
			return new VerificationFloatVariable();
		}
		else if (type == VerificationType.ITEM_NULL)
		{
			return new VerificationNullVariable();
		}
		else if (type == VerificationType.ITEM_UNINITIALIZED_THIS)
		{
			return new VerificationUninitializedThisVariable();
		}
		else if (type == VerificationType.ITEM_OBJECT)
		{
			VerificationObjectVariable ver = new VerificationObjectVariable();
			ver.setConstantPoolIndex(input.readShort());
			return ver;
		}
		else if (type == VerificationType.ITEM_UNINITIALIZED)
		{
			VerificationUninitializedVariable ver = new VerificationUninitializedVariable();
			ver.setOffset(input.readShort());
			return ver;
		}
		else if (type == VerificationType.ITEM_LONG)
		{
			return new VerificationLongVariable();
		}
		else if (type == VerificationType.ITEM_DOUBLE)
		{
			return new VerificationDoubleVariable();
		}
		else throw new ClassFormatError();
	}
}
