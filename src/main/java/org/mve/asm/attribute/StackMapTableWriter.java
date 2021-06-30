package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeStackMapTable;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.constant.ConstantArray;
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
	public StackMapFrame[] frames = new StackMapFrame[0];

	public StackMapTableWriter frame(StackMapFrame frame)
	{
		int i = this.frames.length;
		this.frames = Arrays.copyOf(this.frames, i + 1);
		this.frames[i] = frame;
		return this;
	}

	public StackMapTableWriter sameFrame(Marker marker)
	{
		return this.frame(new SameFrame().mark(marker));
	}

	public StackMapTableWriter sameLocals1StackItemFrame(Marker marker, Verification verification)
	{
		return this.frame(new SameLocals1StackItemFrame().mark(marker).verification(verification));
	}

	public StackMapTableWriter sameLocals1StackItemFrameExtended(Marker marker, Verification verification)
	{
		return this.frame(new SameLocals1StackItemFrameExtended().mark(marker).verification(verification));
	}

	public StackMapTableWriter chopFrame(Marker marker, int k)
	{
		ChopFrame frame = new ChopFrame();
		frame.marker = marker;
		return this.frame(frame.chop(k));
	}

	public StackMapTableWriter sameFrameExtended(Marker marker)
	{
		return this.frame(new SameFrameExtended().mark(marker));
	}

	public StackMapTableWriter appendFrame(Marker marker, Verification... verifications)
	{
		AppendFrame frame = new AppendFrame();
		frame.marker = marker;
		frame.verification = verifications;
		return this.frame(frame);
	}

	public StackMapTableWriter fullFrame(Marker marker, Verification[] local, Verification[] stack)
	{
		FullFrame frame = new FullFrame();
		frame.marker = marker;
		frame.local = local;
		frame.stack = stack;
		return this.frame(frame);
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeStackMapTable smt = new AttributeStackMapTable();
		smt.name = ConstantPoolFinder.findUTF8(pool, AttributeType.STACK_MAP_TABLE.getName());
		int offset = 0;
		for (StackMapFrame frame : this.frames)
		{
			smt.frame(frame.transform(offset, pool));
			offset = (frame.mark() + 1);
		}
		return smt;
	}
}
