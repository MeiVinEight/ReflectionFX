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
			short count = datain.readShort();
			for (int i = 0; i < count; i++)
			{
				attr.addAnnotation(AnnotationReader.read(file, datain));
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.RUNTIME_INVISIBLE_ANNOTATIONS)
		{
			AttributeRuntimeInvisibleAnnotations attr = new AttributeRuntimeInvisibleAnnotations(nameIndex);
			short count = datain.readShort();
			for (int i = 0; i < count; i++)
			{
				attr.addAnnotation(AnnotationReader.read(file, datain));
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS)
		{
			AttributeRuntimeVisibleParameterAnnotations attr = new AttributeRuntimeVisibleParameterAnnotations(nameIndex);
			int count = datain.readByte() & 0XFF;
			for (int i = 0; i < count; i++)
			{
				StructParameterAnnotation struct = new StructParameterAnnotation();
				int c1 = datain.readShort() & 0XFFFF;
				for (int j = 0; j < c1; j++)
				{
					struct.addAnnotation(AnnotationReader.read(file, datain));
				}
				attr.addParameterAnnotation(struct);
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS)
		{
			AttributeRuntimeInvisibleParameterAnnotations attr = new AttributeRuntimeInvisibleParameterAnnotations(nameIndex);
			int count = datain.readByte() & 0XFF;
			for (int i = 0; i < count; i++)
			{
				StructParameterAnnotation struct = new StructParameterAnnotation();
				int c1 = datain.readShort() & 0XFFFF;
				for (int j = 0; j < c1; j++)
				{
					struct.addAnnotation(AnnotationReader.read(file, datain));
				}
				attr.addParameterAnnotation(struct);
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.RUNTIME_VISIBLE_TYPE_ANNOTATIONS)
		{
			AttributeRuntimeVisibleTypeAnnotations attr = new AttributeRuntimeVisibleTypeAnnotations(nameIndex);
			int count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				attr.addTypeAnnotation(TypeAnnotationReader.read(file, datain));
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.RUNTIME_INVISIBLE_TYPE_ANNOTATIONS)
		{
			AttributeRuntimeInvisibleTypeAnnotations attr = new AttributeRuntimeInvisibleTypeAnnotations(nameIndex);
			int count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				attr.addTypeAnnotation(TypeAnnotationReader.read(file, datain));
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.ANNOTATION_DEFAULT)
		{
			AttributeAnnotationDefault attr = new AttributeAnnotationDefault(nameIndex);
			attr.setDefaultValue(ElementValueReader.read(file, datain));
			datain.close();
			return attr;
		}
		else if (type == AttributeType.BOOTSTRAP_METHODS)
		{
			AttributeBootstrapMethods attr = new AttributeBootstrapMethods(nameIndex);
			int count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				StructBootstrapMethod method = new StructBootstrapMethod();
				method.setBootstrapMethodReference(datain.readShort());
				int c = datain.readShort() & 0XFFFF;
				for (int j = 0; j < c; j++)
				{
					method.addBootstrapMethodArgument(datain.readShort());
				}
				attr.addBootstrapMethod(method);
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.METHOD_PARAMETERS)
		{
			AttributeMethodParameters attr = new AttributeMethodParameters(nameIndex);
			int count = datain.readByte() & 0XFF;
			for (int i = 0; i < count; i++)
			{
				StructMethodParameter parameter = new StructMethodParameter();
				parameter.setNameIndex(datain.readShort());
				parameter.setAccessFlag(datain.readShort());
				attr.addMethodParameter(parameter);
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.MODULE)
		{
			AttributeModule attr = new AttributeModule(nameIndex);
			attr.setModuleNameIndex(datain.readShort());
			attr.setModuleFlags(datain.readShort());
			attr.setModuleVersionIndex(datain.readShort());
			int count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				StructModuleRequire require = new StructModuleRequire();
				require.setRequiresIndex(datain.readShort());
				require.setRequiresFlags(datain.readShort());
				require.setRequiresVersionIndex(datain.readShort());
				attr.addModuleRequire(require);
			}
			count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				StructModuleExport export = new StructModuleExport();
				export.setExportIndex(datain.readShort());
				export.setExportFlags(datain.readShort());
				int c = datain.readShort() & 0XFFFF;
				for (int j = 0; j < c; j++)
				{
					export.addExportTo(datain.readShort());
				}
				attr.addModuleExport(export);
			}
			count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				StructModuleOpen open = new StructModuleOpen();
				open.setOpenIndex(datain.readShort());
				open.setOpenFlags(datain.readShort());
				int c = datain.readShort() & 0XFFFF;
				for (int j = 0; j < c; j++)
				{
					open.addOpenTo(datain.readShort());
				}
				attr.addModuleOpen(open);
			}
			count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				attr.addModuleUse(datain.readShort());
			}
			count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				StructModuleProvide provide = new StructModuleProvide();
				provide.setProvideIndex(datain.readShort());
				int c = datain.readShort() & 0XFFFF;
				for (int j = 0; j < c; j++)
				{
					provide.addProvideWith(datain.readShort());
				}
				attr.addModuleProvide(provide);
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.MODULE_PACKAGES)
		{
			AttributeModulePackages attr = new AttributeModulePackages(nameIndex);
			int count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				attr.addModulePackage(datain.readShort());
			}
			datain.close();
			return attr;
		}
		else if (type == AttributeType.MODULE_MAIN_CLASS)
		{
			AttributeModuleMainClass attr = new AttributeModuleMainClass(nameIndex);
			attr.setMainClassIndex(datain.readShort());
			datain.close();
			return attr;
		}
		else if (type == AttributeType.NEST_HOST)
		{
			AttributeNestHost attr = new AttributeNestHost(nameIndex);
			attr.setHostClassIndex(datain.readShort());
			datain.close();
			return attr;
		}
		else if (type == AttributeType.NEST_MEMBERS)
		{
			AttributeNestMembers attr = new AttributeNestMembers(nameIndex);
			int count = datain.readShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				attr.addNestMember(datain.readShort());
			}
			datain.close();
			return attr;
		}
		else throw new ClassFormatError();
	}
}
