package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.Attribute;
import org.mve.asm.file.AttributeStackMapTable;
import org.mve.asm.file.AttributeType;
import org.mve.asm.file.ConstantPool;
import org.mve.asm.attribute.code.stack.AppendFrame;
import org.mve.asm.attribute.code.stack.ChopFrame;
import org.mve.asm.attribute.code.stack.FullFrame;
import org.mve.asm.attribute.code.stack.SameFrame;
import org.mve.asm.attribute.code.stack.SameFrameExtended;
import org.mve.asm.attribute.code.stack.SameLocals1StackItemFrame;
import org.mve.asm.attribute.code.stack.SameLocals1StackItemFrameExtended;
import org.mve.asm.attribute.code.stack.StackMapFrame;
import org.mve.asm.attribute.code.stack.verification.Verification;

import java.util.Arrays;

public class StackMapTableWriter implements AttributeWriter
{
	private StackMapFrame[] frames = new StackMapFrame[0];

	public StackMapTableWriter frame(StackMapFrame frame)
	{
		int i = this.frames.length;
		this.frames = Arrays.copyOf(this.frames, i + 1);
		this.frames[i] = frame;
		return this;
	}

	public StackMapTableWriter sameFrame(Marker marker)
	{
		return this.frame(new SameFrame(marker));
	}

	public StackMapTableWriter sameLocals1StackItemFrame(Marker marker, Verification verification)
	{
		return this.frame(new SameLocals1StackItemFrame(marker, verification));
	}

	public StackMapTableWriter sameLocals1StackItemFrameExtended(Marker marker, Verification verification)
	{
		return this.frame(new SameLocals1StackItemFrameExtended(marker, verification));
	}

	public StackMapTableWriter chopFrame(Marker marker, int k)
	{
		return this.frame(new ChopFrame(marker, k));
	}

	public StackMapTableWriter sameFrameExtended(Marker marker)
	{
		return this.frame(new SameFrameExtended(marker));
	}

	public StackMapTableWriter appendFrame(Marker marker, Verification... verifications)
	{
		return this.frame(new AppendFrame(marker, verifications));
	}

	public StackMapTableWriter fullFrame(Marker marker, Verification[] local, Verification[] stack)
	{
		return this.frame(new FullFrame(marker, local, stack));
	}

	@Override
	public Attribute getAttribute(ConstantPool pool)
	{
		AttributeStackMapTable smt = new AttributeStackMapTable((short) ConstantPoolFinder.findUTF8(pool, AttributeType.STACK_MAP_TABLE.getName()));
		int offset = 0;
		for (StackMapFrame frame : this.frames)
		{
			smt.addStackMapFrame(frame.transform(offset, pool));
			offset = (frame.mark() + 1);
		}
		return smt;
	}
}
