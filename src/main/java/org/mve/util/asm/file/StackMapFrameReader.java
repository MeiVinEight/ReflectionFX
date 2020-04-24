package org.mve.util.asm.file;

import org.mve.io.RandomAccessByteArray;

public class StackMapFrameReader
{
	public static StackMapFrame read(ClassFile file, RandomAccessByteArray input)
	{
		byte tag = input.readByte();
		int unsignedTag = tag & 0XFF;
		StackMapFrameType type = StackMapFrameType.getType(unsignedTag);
		if (type == StackMapFrameType.STACK_MAP_SAME_FRAME)
		{
			return new StackMapSameFrame(tag);
		}
		else if (type == StackMapFrameType.STACK_MAP_SAME_LOCALS_1_ITEM_FRAME)
		{
			StackMapSameLocals1StackItemFrame frame = new StackMapSameLocals1StackItemFrame(tag);
			Verification ver = VerificationReader.read(file, input);
			frame.setVerification(ver);
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_SAME_LOCALS_1_ITEM_FRAME_EXTENDED)
		{
			StackMapSameLocals1StackItemFrameExtended frame = new StackMapSameLocals1StackItemFrameExtended(tag);
			frame.setOffsetDelta(input.readShort());
			frame.setVerification(VerificationReader.read(file, input));
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_CHOP_FRAME)
		{
			StackMapChopFrame frame = new StackMapChopFrame(tag);
			frame.setOffsetDelta(input.readShort());
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_SAME_FRAME_EXTENDED)
		{
			StackMapSameFrameExtended frame = new StackMapSameFrameExtended(tag);
			frame.setOffsetDelta(input.readShort());
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_APPEND_FRAME)
		{
			StackMapAppendFrame frame = new StackMapAppendFrame(tag);
			int count = (tag & 0XFF)-251;
			frame.setOffsetDelta(input.readShort());
			for (int i=0; i<count; i++)
			{
				frame.setVerification(i, VerificationReader.read(file, input));
			}
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_FULL_FRAME)
		{
			StackMapFullFrame frame = new StackMapFullFrame(tag);
			frame.setOffsetDelta(input.readShort());
			int locals = (input.readShort() & 0XFFFF);
			for (int i = 0; i < locals; i++)
			{
				frame.addLocal(VerificationReader.read(file, input));
			}
			int stackItems = input.readShort() & 0XFFFF;
			for (int i = 0; i < stackItems; i++)
			{
				frame.addStackItem(VerificationReader.read(file, input));
			}
			return frame;
		}
		else throw new ClassFormatError();
	}
}
