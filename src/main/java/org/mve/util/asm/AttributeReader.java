package org.mve.util.asm;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AttributeReader
{
	public static Attribute read(ClassFile file, InputStream in) throws IOException
	{
		DataInputStream datain = new DataInputStream(in);
		short nameIndex = datain.readShort();
		int len = datain.readInt();
		byte[] data = new byte[len];
		if (datain.read(data) != len) throw new ClassFormatError();
		datain = new DataInputStream(new ByteArrayInputStream(data));
		ConstantPoolElement element = file.getConstantPool().getConstantPoolElement(nameIndex);
		if (element.getType() != ConstantPoolElementType.CONSTANT_UTF8) throw new ClassFormatError("Invalid constant pool index "+nameIndex);
		ConstantUTF8 utf8 = (ConstantUTF8) element;
		String name = utf8.getUTF8();
		AttributeType type = AttributeType.getType(name);
		if (type == AttributeType.CONSTANT_VALUE)
		{
			AttributeConstantValue attr = new AttributeConstantValue(nameIndex);
			attr.setValueIndex(datain.readShort());
			datain.close();
			return attr;
		}
		else if (type == AttributeType.CODE)
		{
			AttributeCode attr = new AttributeCode(nameIndex);
			attr.setMaxStack(datain.readShort());
			attr.setMaxLocals(datain.readShort());
			int code_length = datain.readInt();
			byte[] code = new byte[code_length];
			if (datain.read(code) != code_length) throw new ClassFormatError();
			attr.setCode(code);
			short exception_table_length = datain.readShort();
			for (int i = 0; i < exception_table_length; i++)
			{
				StructExceptionTable exceptionTable = new StructExceptionTable();
				exceptionTable.setStartPc(datain.readShort());
				exceptionTable.setEndPc(datain.readShort());
				exceptionTable.setHandlerPc(datain.readShort());
				exceptionTable.setCatchPc(datain.readShort());
				attr.addExceptionTable(exceptionTable);
			}
			short attributes_count = datain.readShort();
			for (int i = 0; i < attributes_count; i++)
			{
				attr.addAttribute(AttributeReader.read(file, datain));
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.STACK_MAP_TABLE)
		{
			AttributeStackMapTable attr = new AttributeStackMapTable(nameIndex);
			int count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				attr.addStackMapFrame(StackMapFrameReader.read(file, datain));
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.EXCEPTIONS)
		{
			AttributeExceptions attr = new AttributeExceptions(nameIndex);
			int count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				attr.addException(datain.readShort());
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.INNER_CLASSES)
		{
			AttributeInnerClasses attr = new AttributeInnerClasses(nameIndex);
			int count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				StructInnerClass struct = new StructInnerClass();
				struct.setInnerClassInfoIndex(datain.readShort());
				struct.setOuterClassInfoIndex(datain.readShort());
				struct.setInnerNameIndex(datain.readShort());
				struct.setInnerClassAccessFlag(datain.readShort());
				attr.addInnerClass(struct);
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.ENCLOSING_METHOD)
		{
			AttributeEnclosingMethod attr = new AttributeEnclosingMethod(nameIndex);
			attr.setClassIndex(datain.readShort());
			attr.setMethodIndex(datain.readShort());
			datain.close();
			return attr;
		}
		else if (type == AttributeType.SYNTHETIC)
		{
			datain.close();
			return new AttributeSynthetic(nameIndex);
		}
		else if (type == AttributeType.SIGNATURE)
		{
			AttributeSignature attr = new AttributeSignature(nameIndex);
			attr.setSignatureIndex(datain.readShort());
			datain.close();
			return attr;
		}
		else if (type == AttributeType.SOURCE_FILE)
		{
			AttributeSourceFile attr = new AttributeSourceFile(nameIndex);
			attr.setSourceFileIndex(datain.readShort());
			datain.close();
			return attr;
		}
		else if (type == AttributeType.SOURCE_DEBUG_EXTENSION)
		{
			AttributeSourceDebugExtension attr = new AttributeSourceDebugExtension(nameIndex);
			byte[] b = new byte[len];
			if (datain.read(b) != len) throw new ClassFormatError();
			attr.setExtension(b);
			datain.close();
			return attr;
		}
		else if (type == AttributeType.LINE_NUMBER_TABLE)
		{
			AttributeLineNumberTable attr = new AttributeLineNumberTable(nameIndex);
			int count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				StructLineNumberTable struct = new StructLineNumberTable();
				struct.setStartPc(datain.readShort());
				struct.setLineNumber(datain.readShort());
				attr.addLineNumberTable(struct);
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.LOCAL_VARIABLE_TABLE)
		{
			AttributeLocalVariableTable attr = new AttributeLocalVariableTable(nameIndex);
			int count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				StructLocalVariableTable struct = new StructLocalVariableTable();
				struct.setStartPc(datain.readShort());
				struct.setLength(datain.readShort());
				struct.setNameIndex(datain.readShort());
				struct.setDescriptorIndex(datain.readShort());
				struct.setIndex(datain.readShort());
				attr.addLocalVariableTable(struct);
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.LOCAL_VARIABLE_TYPE_TABLE)
		{
			AttributeLocalVariableTypeTable attr = new AttributeLocalVariableTypeTable(nameIndex);
			int count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				StructLocalVariableTypeTable struct = new StructLocalVariableTypeTable();
				struct.setStartPc(datain.readShort());
				struct.setLength(datain.readShort());
				struct.setNameIndex(datain.readShort());
				struct.setSignatureIndex(datain.readShort());
				struct.setIndex(datain.readShort());
				attr.addLocalVariableTypeTable(struct);
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.DEPRECATED)
		{
			datain.close();
			return new AttributeDeprecated(nameIndex);
		}
		else if (type == AttributeType.RUNTIME_VISIBLE_ANNOTATIONS)
		{
			AttributeRuntimeVisibleAnnotations attr = new AttributeRuntimeVisibleAnnotations(nameIndex);
		}
	}
}
