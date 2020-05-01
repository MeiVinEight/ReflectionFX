package org.mve.util.asm.instruction;

import org.mve.util.asm.Marker;

public class JumpInstruction extends Instruction
{
	public final Marker marker;

	public JumpInstruction(int opcode, Marker marker)
	{
		super(opcode);
		this.marker = marker;
	}
}
