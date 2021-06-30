package org.mve.asm.file.attribute.stack.verification;

import org.mve.asm.file.Class;
import org.mve.io.RandomAccessByteArray;

public abstract class Verification
{
	public abstract VerificationType type();

	public abstract byte[] toByteArray();
	
	public static Verification read(Class file, RandomAccessByteArray input)
	{
		int typeTag = input.readUnsignedByte();
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
			ver.type = (short) input.readUnsignedShort();
			return ver;
		}
		else if (type == VerificationType.ITEM_UNINITIALIZED)
		{
			VerificationUninitializedVariable ver = new VerificationUninitializedVariable();
			ver.offset = input.readUnsignedShort();
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
