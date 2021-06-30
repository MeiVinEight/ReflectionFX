package org.mve.asm.file.attribute.stack;

import org.mve.asm.file.Class;
import org.mve.asm.file.attribute.stack.verification.Verification;
import org.mve.io.RandomAccessByteArray;

public abstract class StackMapFrame
{
	public int type;

	public abstract StackMapFrameType type();

	public abstract int length();

	public abstract byte[] toByteArray();

	public static StackMapFrame read(Class file, RandomAccessByteArray input)
	{
		int tag = input.readUnsignedByte();
		int unsignedTag = tag & 0XFF;
		StackMapFrameType type = StackMapFrameType.getType(unsignedTag);
		if (type == StackMapFrameType.STACK_MAP_SAME_FRAME)
		{
			StackMapSameFrame frame = new StackMapSameFrame();
			frame.type = tag;
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_SAME_LOCALS_1_ITEM_FRAME)
		{
			StackMapSameLocals1StackItemFrame frame = new StackMapSameLocals1StackItemFrame();
			frame.type = tag;
			frame.verification = Verification.read(file, input);
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_SAME_LOCALS_1_ITEM_FRAME_EXTENDED)
		{
			StackMapSameLocals1StackItemFrameExtended frame = new StackMapSameLocals1StackItemFrameExtended();
			frame.type = tag;
			frame.offset = input.readUnsignedShort();
			frame.verification = Verification.read(file, input);
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_CHOP_FRAME)
		{
			StackMapChopFrame frame = new StackMapChopFrame();
			frame.type = tag;
			frame.offset = input.readUnsignedShort();
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_SAME_FRAME_EXTENDED)
		{
			StackMapSameFrameExtended frame = new StackMapSameFrameExtended();
			frame.type = tag;
			frame.offset = input.readUnsignedShort();
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_APPEND_FRAME)
		{
			StackMapAppendFrame frame = new StackMapAppendFrame();
			frame.type = tag;
			frame.verification = new Verification[tag - 251];
			int count = (tag & 0XFF)-251;
			frame.offset = input.readUnsignedShort();
			for (int i=0; i<count; i++)
			{
				frame.verification[i] = Verification.read(file, input);
			}
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_FULL_FRAME)
		{
			StackMapFullFrame frame = new StackMapFullFrame();
			frame.type = tag;
			frame.offset = input.readUnsignedShort();
			int locals = (input.readUnsignedShort() & 0XFFFF);
			for (int i = 0; i < locals; i++)
			{
				frame.local(Verification.read(file, input));
			}
			int stackItems = input.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < stackItems; i++)
			{
				frame.stack(Verification.read(file, input));
			}
			return frame;
		}
		else throw new ClassFormatError();
	}
}
