package org.mve.asm.attribute;

import org.mve.io.RandomAccessByteArray;
import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.Marker;
import org.mve.asm.Opcodes;
import org.mve.asm.Type;
import org.mve.asm.file.Attribute;
import org.mve.asm.file.AttributeCode;
import org.mve.asm.file.AttributeType;
import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.StructExceptionTable;
import org.mve.asm.instruction.ConstantInstruction;
import org.mve.asm.instruction.FieldInstruction;
import org.mve.asm.instruction.IincInstruction;
import org.mve.asm.instruction.Instruction;
import org.mve.asm.instruction.InterfaceMethodInstruction;
import org.mve.asm.instruction.JumpInstruction;
import org.mve.asm.instruction.LocalVariableInstruction;
import org.mve.asm.instruction.MethodInstruction;
import org.mve.asm.instruction.NumberInstruction;
import org.mve.asm.instruction.SimpleInstruction;
import org.mve.asm.instruction.TypeInstruction;

import java.util.Arrays;

public class CodeWriter implements AttributeWriter
{
	private int maxStack;
	private int maxLocals;
	private int addr;
	private Instruction[] instructions = new Instruction[0];
	private StructExceptionTable[] exceptionTables = new StructExceptionTable[0];
	private AttributeWriter[] attributes = new AttributeWriter[0];

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
		boolean wide = this.instructions.length > 0 && this.instructions[this.instructions.length - 1].opcode == Opcodes.WIDE;

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
		return this.addInstruction(new ConstantInstruction(value));
	}

	public CodeWriter addConstantInstruction(Object value)
	{
		return this.addInstruction(new ConstantInstruction(value));
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

	public CodeWriter addAttribute(AttributeWriter writer)
	{
		int i = this.attributes.length;
		this.attributes = Arrays.copyOf(this.attributes, i+1);
		this.attributes[i] = writer;
		return this;
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
			boolean wide = i > 0 && this.instructions[i - 1].opcode == Opcodes.WIDE;
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
					arr.skip(-1);
					ConstantInstruction insn = (ConstantInstruction) instruction;
					Object value = insn.value;
					if (value instanceof Number)
					{
						if (value instanceof Long)
						{
							long val = ((Number) value).longValue();
							int index = ConstantPoolFinder.findLong(pool, val);
							arr.write(Opcodes.LDC2_W);
							arr.writeShort(index);
						}
						else if (value instanceof Double)
						{
							double val = ((Number) value).doubleValue();
							int index = ConstantPoolFinder.findDouble(pool, val);
							arr.write(Opcodes.LDC2_W);
							arr.writeShort(index);
						}
						else if (value instanceof Float)
						{
							float val = ((Number) value).floatValue();
							int index = ConstantPoolFinder.findFloat(pool, val);
							if (index > 255)
							{
								arr.write(Opcodes.LDC_W);
								arr.writeShort(index);
							}
							else
							{
								arr.write(Opcodes.LDC);
								arr.write(index);
							}
						}
						else
						{
							int val = ((Number) value).intValue();
							int index = ConstantPoolFinder.findInteger(pool, val);
							if (index > 255)
							{
								arr.write(Opcodes.LDC_W);
								arr.writeShort(index);
							}
							else
							{
								arr.write(Opcodes.LDC);
								arr.write(index);
							}
						}
					}
					else if (value instanceof String)
					{
						String str = value.toString();
						int index = ConstantPoolFinder.findString(pool, str);
						if (index > 255)
						{
							arr.write(Opcodes.LDC_W);
							arr.writeShort(index);
						}
						else
						{
							arr.write(Opcodes.LDC);
							arr.write(index);
						}
					}
					else if (value instanceof Type)
					{
						String type = ((Type) value).getType();
						int index = ConstantPoolFinder.findClass(pool, type);
						if (index > 255)
						{
							arr.write(Opcodes.LDC_W);
							arr.writeShort(index);
						}
						else
						{
							arr.write(Opcodes.LDC);
							arr.write(index);
						}
					}
					else if (value instanceof Class)
					{
						Class<?> clazz = (Class<?>) value;
						int index = ConstantPoolFinder.findClass(pool, clazz.getTypeName().replace('.', '/'));
						if (index > 255)
						{
							arr.write(Opcodes.LDC_W);
							arr.writeShort(index);
						}
						else
						{
							arr.write(Opcodes.LDC);
							arr.write(index);
						}
					}
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
