package org.mve.asm.file.attribute;

import org.mve.asm.file.Class;
import org.mve.asm.file.attribute.stack.StackMapFrame;
import org.mve.asm.file.attribute.annotation.Annotation;
import org.mve.asm.file.attribute.method.Argument;
import org.mve.asm.file.attribute.module.ModuleExport;
import org.mve.asm.file.attribute.module.ModuleOpen;
import org.mve.asm.file.attribute.module.ModuleProvide;
import org.mve.asm.file.attribute.module.ModuleRequire;
import org.mve.asm.file.attribute.annotation.ParameterAnnotation;
import org.mve.asm.file.attribute.annotation.TypeAnnotation;
import org.mve.asm.file.attribute.bootstrap.BootstrapMethod;
import org.mve.asm.file.attribute.code.exception.Exception;
import org.mve.asm.file.attribute.element.ElementValue;
import org.mve.asm.file.attribute.inner.InnerClass;
import org.mve.asm.file.attribute.line.LineNumber;
import org.mve.asm.file.attribute.local.LocalVariable;
import org.mve.asm.file.constant.Constant;
import org.mve.asm.file.constant.ConstantType;
import org.mve.asm.file.constant.ConstantUTF8;
import org.mve.io.RandomAccessByteArray;

import java.nio.charset.StandardCharsets;

public abstract class Attribute
{
	public int name;

	public static Attribute read(Class file, RandomAccessByteArray in)
	{
		int nameIndex = in.readUnsignedShort();
		int len = in.readInt();
		byte[] data = new byte[len];
		if (in.read(data) != len) throw new ClassFormatError();
		RandomAccessByteArray datain = new RandomAccessByteArray(data);
		Constant element = file.constant.element[nameIndex];
		if (element.type() != ConstantType.CONSTANT_UTF8) throw new ClassFormatError("Invalid constant pool index "+nameIndex);
		ConstantUTF8 utf8 = (ConstantUTF8) element;
		String name = new String(utf8.value, StandardCharsets.UTF_8);
		AttributeType type = AttributeType.getType(name);
		if (type == AttributeType.CONSTANT_VALUE)
		{
			AttributeConstantValue attr = new AttributeConstantValue();
			attr.name = nameIndex;
			attr.value = datain.readUnsignedShort();
			return attr;
		}
		else if (type == AttributeType.CODE)
		{
			AttributeCode attr = new AttributeCode();
			attr.name = nameIndex;
			attr.stack = datain.readUnsignedShort();
			attr.local = datain.readUnsignedShort();
			int code_length = datain.readInt();
			byte[] code = new byte[code_length];
			if (datain.read(code) != code_length) throw new ClassFormatError();
			attr.code = code;
			int exception_table_length = datain.readUnsignedShort();
			for (int i = 0; i < exception_table_length; i++)
			{
				Exception exceptionTable = new Exception();
				exceptionTable.start = datain.readUnsignedShort();
				exceptionTable.end = datain.readUnsignedShort();
				exceptionTable.caught = datain.readUnsignedShort();
				exceptionTable.type = datain.readUnsignedShort();
				attr.exception(exceptionTable);
			}
			int attributes_count = datain.readUnsignedShort();
			for (int i = 0; i < attributes_count; i++)
			{
				attr.attribute(read(file, datain));
			}
			return attr;
		}
		else if (type == AttributeType.STACK_MAP_TABLE)
		{
			AttributeStackMapTable attr = new AttributeStackMapTable();
			attr.name = nameIndex;
			int count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				attr.frame(StackMapFrame.read(file, datain));
			}
			return attr;
		}
		else if (type == AttributeType.EXCEPTIONS)
		{
			AttributeExceptions attr = new AttributeExceptions();
			attr.name = nameIndex;
			int count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				attr.exception(datain.readUnsignedShort());
			}
			return attr;
		}
		else if (type == AttributeType.INNER_CLASSES)
		{
			AttributeInnerClasses attr = new AttributeInnerClasses();
			attr.name = nameIndex;
			int count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				InnerClass struct = new InnerClass();
				struct.inner = datain.readUnsignedShort();
				struct.outer = datain.readUnsignedShort();
				struct.name = datain.readUnsignedShort();
				struct.access = datain.readUnsignedShort();
				attr.inner(struct);
			}
			return attr;
		}
		else if (type == AttributeType.ENCLOSING_METHOD)
		{
			AttributeEnclosingMethod attr = new AttributeEnclosingMethod();
			attr.name = nameIndex;
			attr.clazz = datain.readUnsignedShort();
			attr.method = datain.readUnsignedShort();
			return attr;
		}
		else if (type == AttributeType.SYNTHETIC)
		{
			AttributeSynthetic attribute = new AttributeSynthetic();
			attribute.name = nameIndex;
			return attribute;
		}
		else if (type == AttributeType.SIGNATURE)
		{
			AttributeSignature attr = new AttributeSignature();
			attr.name = nameIndex;
			attr.signature = datain.readUnsignedShort();
			return attr;
		}
		else if (type == AttributeType.SOURCE_FILE)
		{
			AttributeSourceFile attr = new AttributeSourceFile();
			attr.name = nameIndex;
			attr.source = datain.readUnsignedShort();
			return attr;
		}
		else if (type == AttributeType.SOURCE_DEBUG_EXTENSION)
		{
			AttributeSourceDebugExtension attr = new AttributeSourceDebugExtension();
			attr.name = nameIndex;
			byte[] b = new byte[len];
			if (datain.read(b) != len) throw new ClassFormatError();
			attr.extension = b;
			return attr;
		}
		else if (type == AttributeType.LINE_NUMBER_TABLE)
		{
			AttributeLineNumberTable attr = new AttributeLineNumberTable();
			attr.name = nameIndex;
			int count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				LineNumber struct = new LineNumber();
				struct.start = datain.readUnsignedShort();
				struct.line = datain.readUnsignedShort();
				attr.line(struct);
			}
			return attr;
		}
		else if (type == AttributeType.LOCAL_VARIABLE_TABLE)
		{
			AttributeLocalVariableTable attr = new AttributeLocalVariableTable();
			attr.name = nameIndex;
			int count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				LocalVariable struct = new LocalVariable();
				struct.start = datain.readUnsignedShort();
				struct.length = datain.readUnsignedShort();
				struct.name = datain.readUnsignedShort();
				struct.type = datain.readUnsignedShort();
				struct.slot = datain.readUnsignedShort();
				attr.local(struct);
			}
			return attr;
		}
		else if (type == AttributeType.LOCAL_VARIABLE_TYPE_TABLE)
		{
			AttributeLocalVariableTypeTable attr = new AttributeLocalVariableTypeTable();
			attr.name = nameIndex;
			int count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				LocalVariable struct = new LocalVariable();
				struct.start = datain.readUnsignedShort();
				struct.length = datain.readUnsignedShort();
				struct.name = datain.readUnsignedShort();
				struct.type = datain.readUnsignedShort();
				struct.slot = datain.readUnsignedShort();
				attr.local(struct);
			}
			return attr;
		}
		else if (type == AttributeType.DEPRECATED)
		{
			AttributeDeprecated attribute = new AttributeDeprecated();
			attribute.name = nameIndex;
			return attribute;
		}
		else if (type == AttributeType.RUNTIME_VISIBLE_ANNOTATIONS)
		{
			AttributeRuntimeVisibleAnnotations attr = new AttributeRuntimeVisibleAnnotations();
			attr.name = nameIndex;
			int count = datain.readUnsignedShort();
			for (int i = 0; i < count; i++)
			{
				attr.annotation(Annotation.read(file, datain));
			}
			return attr;
		}
		else if (type == AttributeType.RUNTIME_INVISIBLE_ANNOTATIONS)
		{
			AttributeRuntimeInvisibleAnnotations attr = new AttributeRuntimeInvisibleAnnotations();
			attr.name = nameIndex;
			int count = datain.readUnsignedShort();
			for (int i = 0; i < count; i++)
			{
				attr.annotation(Annotation.read(file, datain));
			}
			return attr;
		}
		else if (type == AttributeType.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS)
		{
			AttributeRuntimeVisibleParameterAnnotations attr = new AttributeRuntimeVisibleParameterAnnotations();
			attr.name = nameIndex;
			int count = datain.readUnsignedByte() & 0XFF;
			for (int i = 0; i < count; i++)
			{
				ParameterAnnotation struct = new ParameterAnnotation();
				int c1 = datain.readUnsignedShort() & 0XFFFF;
				for (int j = 0; j < c1; j++)
				{
					struct.annotation(Annotation.read(file, datain));
				}
				attr.annotation(struct);
			}
			return attr;
		}
		else if (type == AttributeType.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS)
		{
			AttributeRuntimeInvisibleParameterAnnotations attr = new AttributeRuntimeInvisibleParameterAnnotations();
			attr.name = nameIndex;
			int count = datain.readUnsignedByte() & 0XFF;
			for (int i = 0; i < count; i++)
			{
				ParameterAnnotation struct = new ParameterAnnotation();
				int c1 = datain.readUnsignedShort() & 0XFFFF;
				for (int j = 0; j < c1; j++)
				{
					struct.annotation(Annotation.read(file, datain));
				}
				attr.annotation(struct);
			}
			return attr;
		}
		else if (type == AttributeType.RUNTIME_VISIBLE_TYPE_ANNOTATIONS)
		{
			AttributeRuntimeVisibleTypeAnnotations attr = new AttributeRuntimeVisibleTypeAnnotations();
			attr.name = nameIndex;
			int count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				attr.annotation(TypeAnnotation.read(file, datain));
			}
			return attr;
		}
		else if (type == AttributeType.RUNTIME_INVISIBLE_TYPE_ANNOTATIONS)
		{
			AttributeRuntimeInvisibleTypeAnnotations attr = new AttributeRuntimeInvisibleTypeAnnotations();
			attr.name = nameIndex;
			int count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				attr.annotation(TypeAnnotation.read(file, datain));
			}
			return attr;
		}
		else if (type == AttributeType.ANNOTATION_DEFAULT)
		{
			AttributeAnnotationDefault attr = new AttributeAnnotationDefault();
			attr.name = nameIndex;
			attr.value = ElementValue.read(file, datain);
			return attr;
		}
		else if (type == AttributeType.BOOTSTRAP_METHODS)
		{
			AttributeBootstrapMethods attr = new AttributeBootstrapMethods();
			attr.name = nameIndex;
			int count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				BootstrapMethod method = new BootstrapMethod();
				method.reference = datain.readUnsignedShort();
				int c = datain.readUnsignedShort() & 0XFFFF;
				for (int j = 0; j < c; j++)
				{
					method.argument(datain.readUnsignedShort());
				}
				attr.bootstrap(method);
			}
			return attr;
		}
		else if (type == AttributeType.METHOD_PARAMETERS)
		{
			AttributeMethodArguments attr = new AttributeMethodArguments();
			attr.name = nameIndex;
			int count = datain.readUnsignedByte() & 0XFF;
			for (int i = 0; i < count; i++)
			{
				Argument parameter = new Argument();
				parameter.name = datain.readUnsignedShort();
				parameter.access = datain.readUnsignedShort();
				attr.argument(parameter);
			}
			return attr;
		}
		else if (type == AttributeType.MODULE)
		{
			AttributeModule attr = new AttributeModule();
			attr.name = nameIndex;
			attr.module = datain.readUnsignedShort();
			attr.flag = datain.readUnsignedShort();
			attr.version = datain.readUnsignedShort();
			int count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				ModuleRequire require = new ModuleRequire();
				require.require = datain.readUnsignedShort();
				require.flag = datain.readUnsignedShort();
				require.version = datain.readUnsignedShort();
				attr.require(require);
			}
			count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				ModuleExport export = new ModuleExport();
				export.export = datain.readUnsignedShort();
				export.flag = datain.readUnsignedShort();
				int c = datain.readUnsignedShort() & 0XFFFF;
				for (int j = 0; j < c; j++)
				{
					export.to(datain.readUnsignedShort());
				}
				attr.export(export);
			}
			count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				ModuleOpen open = new ModuleOpen();
				open.open = datain.readUnsignedShort();
				open.flag = datain.readUnsignedShort();
				int c = datain.readUnsignedShort() & 0XFFFF;
				for (int j = 0; j < c; j++)
				{
					open.to(datain.readUnsignedShort());
				}
				attr.open(open);
			}
			count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				attr.use(datain.readUnsignedShort());
			}
			count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				ModuleProvide provide = new ModuleProvide();
				provide.provide = datain.readUnsignedShort();
				int c = datain.readUnsignedShort() & 0XFFFF;
				for (int j = 0; j < c; j++)
				{
					provide.with(datain.readUnsignedShort());
				}
				attr.provide(provide);
			}
			return attr;
		}
		else if (type == AttributeType.MODULE_PACKAGES)
		{
			AttributeModulePackages attr = new AttributeModulePackages();
			attr.name = nameIndex;
			int count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				attr.packages(datain.readUnsignedShort());
			}
			return attr;
		}
		else if (type == AttributeType.MODULE_MAIN_CLASS)
		{
			AttributeModuleMainClass attr = new AttributeModuleMainClass();
			attr.name = nameIndex;
			attr.main = datain.readUnsignedShort();
			return attr;
		}
		else if (type == AttributeType.NEST_HOST)
		{
			AttributeNestHost attr = new AttributeNestHost();
			attr.name = nameIndex;
			attr.host = datain.readUnsignedShort();
			return attr;
		}
		else if (type == AttributeType.NEST_MEMBERS)
		{
			AttributeNestMembers attr = new AttributeNestMembers();
			attr.name = nameIndex;
			int count = datain.readUnsignedShort() & 0XFFFF;
			for (int i = 0; i < count; i++)
			{
				attr.classes(datain.readUnsignedShort());
			}
			return attr;
		}
		else if (type == AttributeType.PERMITTED_SUBCLASSES)
		{
			AttributePermittedSubclasses attr = new AttributePermittedSubclasses();
			attr.name = nameIndex;
			int a = datain.readUnsignedShort();
			for (int i=0; i<a; i++)
			{
				attr.classes(datain.readUnsignedShort());
			}
			return attr;
		}
		else if (type == AttributeType.UNKNOWN)
		{
			AttributeUnknown attr = new AttributeUnknown();
			attr.name = nameIndex;
			attr.code = data.clone();
			return attr;
		}
		else throw new ClassFormatError();
	}

	public abstract AttributeType type();

	public abstract int length();

	public abstract byte[] toByteArray();
}
