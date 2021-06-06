package org.mve.asm.file;

public enum AttributeType
{
	ANNOTATION_DEFAULT("AnnotationDefault"),
	BOOTSTRAP_METHODS("BootstrapMethods"),
	CODE("Code"),
	CONSTANT_VALUE("ConstantValue"),
	DEPRECATED("Deprecated"),
	ENCLOSING_METHOD("EnclosingMethods"),
	EXCEPTIONS("Exceptions"),
	INNER_CLASSES("InnerClasses"),
	LINE_NUMBER_TABLE("LineNumberTable"),
	LOCAL_VARIABLE_TABLE("LocalVariableTable"),
	LOCAL_VARIABLE_TYPE_TABLE("LocalVariableTypeTable"),
	METHOD_PARAMETERS("MethodParameters"),
	MODULE("Module"),
	MODULE_MAIN_CLASS("ModuleMainClass"),
	MODULE_PACKAGES("ModulePackages"),
	NEST_HOST("NestHost"),
	NEST_MEMBERS("NestMembers"),
	PERMITTED_SUBCLASSES("PermittedSubclasses"),
	RUNTIME_INVISIBLE_ANNOTATIONS("RuntimeInvisibleAnnotations"),
	RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS("RuntimeInvisibleParameterAnnotations"),
	RUNTIME_INVISIBLE_TYPE_ANNOTATIONS("RuntimeInvisibleTypeAnnotations"),
	RUNTIME_VISIBLE_ANNOTATIONS("RuntimeVisibleAnnotations"),
	RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS("RuntimeVisibleParameterAnnotations"),
	RUNTIME_VISIBLE_TYPE_ANNOTATIONS("RuntimeVisibleTypeAnnotations"),
	SIGNATURE("Signature"),
	SOURCE_DEBUG_EXTENSION("SourceDebugExtension"),
	SOURCE_FILE("SourceFile"),
	STACK_MAP_TABLE("StackMapTable"),
	SYNTHETIC("Synthetic"),
	UNKNOWN("Unknown");

	private final String name;

	private AttributeType(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public static AttributeType getType(String name)
	{
		if (name == null) return null;
		else if (name.equals("AnnotationDefault")) return ANNOTATION_DEFAULT;
		else if (name.equals("BootstrapMethods")) return BOOTSTRAP_METHODS;
		else if (name.equals("Code")) return CODE;
		else if (name.equals("ConstantValue")) return CONSTANT_VALUE;
		else if (name.equals("Deprecated")) return DEPRECATED;
		else if (name.equals("EnclosingMethod")) return ENCLOSING_METHOD;
		else if (name.equals("Exceptions")) return EXCEPTIONS;
		else if (name.equals("InnerClasses")) return INNER_CLASSES;
		else if (name.equals("LineNumberTable")) return LINE_NUMBER_TABLE;
		else if (name.equals("LocalVariableTable")) return LOCAL_VARIABLE_TABLE;
		else if (name.equals("LocalVariableTypeTable")) return LOCAL_VARIABLE_TYPE_TABLE;
		else if (name.equals("MethodParameters")) return METHOD_PARAMETERS;
		else if (name.equals("Module")) return MODULE;
		else if (name.equals("ModuleMainClass")) return MODULE_MAIN_CLASS;
		else if (name.equals("ModulePackages")) return MODULE_PACKAGES;
		else if (name.equals("NestHost")) return NEST_HOST;
		else if (name.equals("NestMembers")) return NEST_MEMBERS;
		else if (name.equals("PermittedSubclasses")) return PERMITTED_SUBCLASSES;
		else if (name.equals("RuntimeInvisibleAnnotations")) return RUNTIME_INVISIBLE_ANNOTATIONS;
		else if (name.equals("RuntimeInvisibleParameterAnnotations")) return RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS;
		else if (name.equals("RuntimeInvisibleTypeAnnotations")) return RUNTIME_INVISIBLE_TYPE_ANNOTATIONS;
		else if (name.equals("RuntimeVisibleAnnotations")) return RUNTIME_VISIBLE_ANNOTATIONS;
		else if (name.equals("RuntimeVisibleParameterAnnotations")) return RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS;
		else if (name.equals("RuntimeVisibleTypeAnnotations")) return RUNTIME_VISIBLE_TYPE_ANNOTATIONS;
		else if (name.equals("Signature")) return SIGNATURE;
		else if (name.equals("SourceDebugExtension")) return SOURCE_DEBUG_EXTENSION;
		else if (name.equals("SourceFile")) return SOURCE_FILE;
		else if (name.equals("StackMapTable")) return STACK_MAP_TABLE;
		else if (name.equals("Synthetic")) return SYNTHETIC;
		else return UNKNOWN;
	}
}
