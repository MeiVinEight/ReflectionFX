package org.mve.asm.stack;

import org.mve.asm.Marker;
import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.StackMapAppendFrame;
import org.mve.asm.file.StackMapFrameType;
import org.mve.asm.stack.verification.Verification;

public class AppendFrame extends StackMapFrame
{
	/**
	 * length = k
	 * k = frame_type - 251
	 */
	private final Verification[] verifications;

	public AppendFrame(Marker marker, Verification[] verifications)
	{
		super(marker);
		this.verifications = verifications;
	}

	@Override
	public org.mve.asm.file.StackMapFrame transform(int previous, ConstantPool pool)
	{
		StackMapAppendFrame frame = new StackMapAppendFrame((byte) (StackMapFrameType.STACK_MAP_APPEND_FRAME.getLow() + this.verifications.length - 1));
		frame.setOffsetDelta((short) (this.marker.get() - previous));
		for (int i = 0; i < this.verifications.length; i++)
		{
			frame.setVerification(i, this.verifications[i].transform(pool));
		}
		return frame;
	}
}
