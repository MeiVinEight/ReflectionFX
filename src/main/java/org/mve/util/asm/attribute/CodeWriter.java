package org.mve.util.asm.attribute;

import org.mve.io.RandomAccessByteArray;
import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.ConstantPoolFinder;
import org.mve.util.asm.Marker;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.Type;
import org.mve.util.asm.file.Attribute;
import org.mve.util.asm.file.AttributeCode;
import org.mve.util.asm.file.AttributeType;
import org.mve.util.asm.file.ConstantPool;
import org.mve.util.asm.file.StructExceptionTable;
import org.mve.util.asm.instruction.ConstantInstruction;
import org.mve.util.asm.instruction.FieldInstruction;
import org.mve.util.asm.instruction.IincInstruction;
import org.mve.util.asm.instruction.Instruction;
import org.mve.util.asm.instruction.InterfaceMethodInstruction;
import org.mve.util.asm.instruction.JumpInstruction;
import org.mve.util.asm.instruction.LocalVariableInstruction;
import org.mve.util.asm.instruction.MethodInstruction;
import org.mve.util.asm.instruction.NumberInstruction;
import org.mve.util.asm.instruction.SimpleInstruction;
import org.mve.util.asm.instruction.TypeInstruction;

import java.util.Arrays;

public class CodeWriter implements AttributeWriter
{
	private final ClassWriter classWriter;
	private int maxStack;
	private int maxLocals;
	private int addr;
	private Instruction[] instructions = new Instruction[0];
	private StructExceptionTable[] exceptionTables = new StructExceptionTable[0];
	private AttributeWriter[] attributes = new AttributeWriter[0];

	public CodeWriter(ClassWriter cw)
	{
		this.classWriter = cw;
	}

	public ClassWriter getClassWriter()
	{
		return classWriter;
	}

	public int getMaxStack()
	{
		return maxStack;
	}

	public void setMaxStack(int maxStack)
	{
		this.maxStack = maxStack;
	}

	public int getMaxLocals()
	{
		return maxLocals;
	}

	public void setMaxLocals(int maxLocals)
	{
		this.maxLocals = maxLocals;
	}

	public CodeWriter setMaxs(int stack, int locals)
	{
		this.maxStack = stack;
		this.maxLocals = locals;
		return this;
	}

	public CodeWriter mark(Marker marker)
	{
		marker.mark(this.addr);
		return this;
	}

	private CodeWriter addInstruction(Instruction insn)
	{
		boolean wide = false;
		if (this.instructions.length > 0 && this.instructions[this.instructions.length-1].opcode == Opcodes.WIDE) wide = true;

		int i = this.instructions.length;
		this.instructions = Arrays.copyOf(this.instructions, i+1);
		this.instructions[i] = insn;

		this.addr++;
		if (insn instanceof NumberInstruction) addr += (insn.opcode == Opcodes.BIPUSH ? 1 : 2);
		else if (insn instanceof ConstantInstruction) addr += (insn.opcode == Opcodes.LDC ? 1 : 2);
		else if (insn instanceof JumpInstruction) addr += (insn.opcode == Opcodes.GOTO_W ? 4 : 2);
		else if (insn instanceof LocalVariableInstruction) addr += (wide ? 2 : 1);
		else if (insn instanceof TypeInstruction) addr += 2;
		else if (insn instanceof FieldInstruction) addr += 2;
		else if (insn instanceof MethodInstruction)
		{
			addr += 2;
			if (insn instanceof InterfaceMethodInstruction) addr += 2;
		}
		else if (insn instanceof IincInstruction) addr += wide ? 4 : 2;
		return this;
	}

	public CodeWriter addConstantInstruction(int opcode, Object value)
	{
		return this.addInstruction(new ConstantInstruction(opcode, value));
	}

	public CodeWriter addFieldInstruction(int opcode, String type, String name, String desc)
	{
		return this.addInstruction(new FieldInstruction(opcode, type, name, desc));
	}

	public CodeWriter addJumpInstruction(int opcode, Marker marker)
	{
		return this.addInstruction(new JumpInstruction(opcode, marker));
	}

	public CodeWriter addLocalVariableInstruction(int opcode, int index)
	{
		return this.addInstruction(new LocalVariableInstruction(opcode, index));
	}

	public CodeWriter addMethodInstruction(int opcode, String type, String name, String desc, boolean isAbstract)
	{
		if (isAbstract) return this.addInstruction(new InterfaceMethodInstruction(opcode, type, name, desc, Type.getArgumentsAndReturnSizes(desc) >> 2));
		else return this.addInstruction(new MethodInstruction(opcode, type, name, desc, isAbstract));
	}

	public CodeWriter addNumberInstruction(int opcode, int num)
	{
		return this.addInstruction(new NumberInstruction(opcode, num));
	}

	public CodeWriter addInstruction(int opcode)
	{
		return this.addInstruction(new SimpleInstruction(opcode));
	}

	public CodeWriter addTypeInstruction(int opcode, String type)
	{
		return this.addInstruction(new TypeInstruction(opcode, type));
	}

	public CodeWriter addIincInstruction(int indexbyte, int constbyte)
	{
		return this.addInstruction(new IincInstruction(indexbyte, constbyte));
	}

	public void addAttribute(AttributeWriter writer)
	{
		int i = this.attributes.length;
		this.attributes = Arrays.copyOf(this.attributes, i+1);
		this.attributes[i] = writer;
	}

	@Override
	public Attribute getAttribute(ConstantPool pool)
	{
		AttributeCode code = new AttributeCode((short) ConstantPoolFinder.findUTF8(pool, AttributeType.CODE.getName()));
		code.setMaxStack((short) this.maxStack);
		code.setMaxLocals((short) this.maxLocals);

		RandomAccessByteArray arr = new RandomAccessByteArray();
		for (int i = 0; i < this.instructions.length; i++)
		{
			boolean wide = false;
			if (i > 0 && this.instructions[i-1].opcode == Opcodes.WIDE) wide = true;
			Instruction instruction = this.instructions[i];
			arr.write(instruction.opcode);

			{
				if (instruction instanceof NumberInstruction)
				{
					NumberInstruction insn = (NumberInstruction) instruction;
					if (insn.opcode == Opcodes.BIPUSH) arr.write(insn.num);
					else if (insn.opcode == Opcodes.SIPUSH) arr.writeShort(insn.num);
				}
				else if (instruction instanceof ConstantInstruction)
				{
					ConstantInstruction insn = (ConstantInstruction) instruction;
					Object value = insn.value;
					int index = 0;
					if (value instanceof Number)
					{
						Number number = (Number) value;
						if (number instanceof Long) index = ConstantPoolFinder.findLong(pool, number.longValue());
						else if (number instanceof Double) index = ConstantPoolFinder.findDouble(pool, number.doubleValue());
						else if (number instanceof Float) index = ConstantPoolFinder.findFloat(pool, number.floatValue());
						else index = ConstantPoolFinder.findInteger(pool, number.intValue());
					}
					else if (value instanceof String) index = ConstantPoolFinder.findString(pool, value.toString());
					else if (value instanceof Type) index = ConstantPoolFinder.findClass(pool, ((Type)value).getType());
					if (insn.opcode == Opcodes.LDC) arr.write(index);
					else arr.writeShort(index);
				}
				else if (instruction instanceof LocalVariableInstruction)
				{
					LocalVariableInstruction insn = (LocalVariableInstruction) instruction;
					if (wide) arr.writeShort(insn.index);
					else arr.write(insn.index);
				}
				else if (instruction instanceof TypeInstruction)
				{
					arr.writeShort(ConstantPoolFinder.findClass(pool, ((TypeInstruction)instruction).type));
				}
				else if (instruction instanceof FieldInstruction)
				{
					FieldInstruction insn = (FieldInstruction) instruction;
					arr.writeShort(ConstantPoolFinder.findField(pool, insn.type, insn.name, insn.desc));
				}
				else if (instruction instanceof MethodInstruction)
				{
					MethodInstruction insn = (MethodInstruction) instruction;
					arr.writeShort(ConstantPoolFinder.findMethod(pool, insn.type, insn.name, insn.desc, insn.isAbstract));
					if (insn instanceof InterfaceMethodInstruction)
					{
						arr.write(((InterfaceMethodInstruction)insn).count);
						arr.write(0);
					}
				}
				else if (instruction instanceof JumpInstruction)
				{
					JumpInstruction insn = (JumpInstruction) instruction;
					short addr = (short) (arr.length()-1);
					short toaddr = (short) insn.marker.get();
					int off = toaddr - addr;
					if (insn.opcode == Opcodes.GOTO_W) arr.writeInt(off);
					else arr.writeShort(off);
				}
				else if (instruction instanceof IincInstruction)
				{
					IincInstruction insn = (IincInstruction) instruction;
					if (wide)
					{
						arr.writeShort(insn.indexbyte);
						arr.writeShort(insn.constbyte);
					}
					else
					{
						arr.write(insn.indexbyte);
						arr.write(insn.constbyte);
					}
				}
			}
		}

		code.setCode(arr.toByteArray());

		for (StructExceptionTable table : this.exceptionTables) code.addExceptionTable(table);
		for (AttributeWriter writer : this.attributes) code.addAttribute(writer.getAttribute(pool));

		return code;
	}
}
