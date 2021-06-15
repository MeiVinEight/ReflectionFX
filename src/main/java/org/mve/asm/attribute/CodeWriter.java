package org.mve.asm.attribute;

import org.mve.asm.attribute.code.Element;
import org.mve.io.RandomAccessByteArray;
import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.Opcodes;
import org.mve.asm.Type;
import org.mve.asm.file.Attribute;
import org.mve.asm.file.AttributeCode;
import org.mve.asm.file.AttributeType;
import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.StructExceptionTable;
import org.mve.asm.attribute.code.ConstantInstruction;
import org.mve.asm.attribute.code.FieldInstruction;
import org.mve.asm.attribute.code.IincInstruction;
import org.mve.asm.attribute.code.Instruction;
import org.mve.asm.attribute.code.InterfaceMethodInstruction;
import org.mve.asm.attribute.code.JumpInstruction;
import org.mve.asm.attribute.code.LocalVariableInstruction;
import org.mve.asm.attribute.code.MethodInstruction;
import org.mve.asm.attribute.code.NumberInstruction;
import org.mve.asm.attribute.code.SimpleInstruction;
import org.mve.asm.attribute.code.TypeInstruction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CodeWriter implements AttributeWriter
{
	private int stack;
	private int local;
	private Element[] elements = new Element[0];
	private StructExceptionTable[] exceptionTables = new StructExceptionTable[0];
	private AttributeWriter[] attributes = new AttributeWriter[0];

	public int stack()
	{
		return stack;
	}

	public CodeWriter stack(int stack)
	{
		this.stack = stack;
		return this;
	}

	public int local()
	{
		return local;
	}

	public CodeWriter local(int local)
	{
		this.local = local;
		return this;
	}

	public CodeWriter max(int stack, int local)
	{
		this.stack = stack;
		this.local = local;
		return this;
	}

	public CodeWriter mark(Marker marker)
	{
		this.element(marker);
		return this;
	}

	private CodeWriter element(Element element)
	{
		int i = this.elements.length;
		this.elements = Arrays.copyOf(this.elements, i+1);
		this.elements[i] = element;
		return this;
	}

	public CodeWriter constant(int opcode, Object value)
	{
		return this.element(new ConstantInstruction(value));
	}

	public CodeWriter constant(Object value)
	{
		return this.element(new ConstantInstruction(value));
	}

	public CodeWriter field(int opcode, String type, String name, String desc)
	{
		return this.element(new FieldInstruction(opcode, type, name, desc));
	}

	public CodeWriter jump(int opcode, Marker marker)
	{
		return this.element(new JumpInstruction(opcode, marker));
	}

	public CodeWriter localVariable(int opcode, int index)
	{
		return this.element(new LocalVariableInstruction(opcode, index));
	}

	public CodeWriter method(int opcode, String type, String name, String desc, boolean isAbstract)
	{
		if (isAbstract) return this.element(new InterfaceMethodInstruction(opcode, type, name, desc, Type.getArgumentsAndReturnSizes(desc) >> 2));
		else return this.element(new MethodInstruction(opcode, type, name, desc, isAbstract));
	}

	public CodeWriter number(int opcode, int num)
	{
		return this.element(new NumberInstruction(opcode, num));
	}

	public CodeWriter instruction(int opcode)
	{
		return this.element(new SimpleInstruction(opcode));
	}

	public CodeWriter type(int opcode, String type)
	{
		return this.element(new TypeInstruction(opcode, type));
	}

	public CodeWriter iinc(int indexbyte, int constbyte)
	{
		return this.element(new IincInstruction(indexbyte, constbyte));
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
		code.setMaxStack((short) this.stack);
		code.setMaxLocals((short) this.local);

		RandomAccessByteArray array = new RandomAccessByteArray();
		boolean[] wide = {false};
		Map<Integer, Marker> markerMap = new HashMap<>();

		for (Element element : elements)
		{
			element.consume(pool, array, wide, markerMap);
		}

		for (Map.Entry<Integer, Marker> entry : markerMap.entrySet())
		{
			int position = entry.getKey();
			Marker marker = entry.getValue();
			array.seek(position);
			int opcode = array.read();
			if (opcode == Opcodes.GOTO_W)
			{
				array.writeInt(marker.address - position);
			}
			else
			{
				array.writeShort(marker.address - position);;
			}
		}

		code.setCode(array.toByteArray());

		for (StructExceptionTable table : this.exceptionTables)
		{
			code.addExceptionTable(table);
		}

		for (AttributeWriter writer : this.attributes)
		{
			code.addAttribute(writer.getAttribute(pool));
		}

		return code;
	}
}
