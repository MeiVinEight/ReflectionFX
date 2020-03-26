package org.mve.util.asm;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StackMapFrameReader
{
	public static StackMapFrame read(ClassFile file, InputStream input) throws IOException
	{
		DataInputStream in = new DataInputStream(input);
		byte tag = in.readByte();
		StackMapFrameType type = StackMapFrameType.getType(tag);
		if (type == StackMapFrameType.STACK_MAP_SAME_FRAME)
		{
			return new StackMapSameFrame(tag);
		}
		else if (type == StackMapFrameType.STACK_MAP_SAME_LOCALS_1_ITEM_FRAME)
		{
			StackMapSameLocals1StackItemFrame frame = new StackMapSameLocals1StackItemFrame(tag);
			Verification ver = VerificationReader.read(file, in);
			frame.setVerification(ver);
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_SAME_LOCALS_1_ITEM_FRAME_EXTENDED)
		{
			StackMapSameLocals1StackItemFrameExtended frame = new StackMapSameLocals1StackItemFrameExtended(tag);
			frame.setOffsetDelta(in.readShort());
			frame.setVerification(VerificationReader.read(file, in));
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_CHOP_FRAME)
		{
			StackMapChopFrame frame = new StackMapChopFrame(tag);
			frame.setOffsetDelta(in.readShort());
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_SAME_FRAME_EXTENDED)
		{
			StackMapSameFrameExtended frame = new StackMapSameFrameExtended(tag);
			frame.setOffsetDelta(in.readShort());
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_APPEND_FRAME)
		{
			StackMapAppendFrame frame = new StackMapAppendFrame(tag);
			int count = (tag & 0XFF)-251;
			frame.setOffsetDelta(in.readShort());
			for (int i=0; i<count; i++)
			{
				frame.setVerification(i, VerificationReader.read(file, in));
			}
			return frame;
		}
		else if (type == StackMapFrameType.STACK_MAP_FULL_FRAME)
		{
			StackMapFullFrame frame = new StackMapFullFrame(tag);
			frame.setOffsetDelta(in.readShort());
			int locals = (in.readShort() & 0XFFFF);
			for (int i = 0; i < locals; i++)
			{
				frame.addLocal(VerificationReader.read(file, in));
			}
			int stackItems = in.readShort() & 0XFFFF;
			for (int i = 0; i < stackItems; i++)
			{
				frame.addStackItem(VerificationReader.read(file, in));
			}
			return frame;
		}
		else throw new ClassFormatError();
	}
}
