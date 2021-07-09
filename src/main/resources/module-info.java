open module org.mve.rfx {
	requires java.management;
	requires jdk.unsupported;

	exports org.mve.asm;
	exports org.mve.asm.attribute;
	exports org.mve.asm.attribute.bootstrap;
	exports org.mve.asm.attribute.code;
	exports org.mve.asm.attribute.code.exception;
	exports org.mve.asm.attribute.code.local;
	exports org.mve.asm.attribute.code.stack;
	exports org.mve.asm.attribute.code.stack.verification;
	exports org.mve.asm.attribute.module;
	exports org.mve.asm.constant;
	exports org.mve.asm.file;
	exports org.mve.asm.file.attribute;
	exports org.mve.asm.file.attribute.annotation;
	exports org.mve.asm.file.attribute.annotation.type;
	exports org.mve.asm.file.attribute.annotation.type.local;
	exports org.mve.asm.file.attribute.annotation.type.path;
	exports org.mve.asm.file.attribute.bootstrap;
	exports org.mve.asm.file.attribute.code.exception;
	exports org.mve.asm.file.attribute.element;
	exports org.mve.asm.file.attribute.inner;
	exports org.mve.asm.file.attribute.line;
	exports org.mve.asm.file.attribute.local;
	exports org.mve.asm.file.attribute.method;
	exports org.mve.asm.file.attribute.module;
	exports org.mve.asm.file.attribute.stack;
	exports org.mve.asm.file.attribute.stack.verification;
	exports org.mve.asm.file.constant;
	exports org.mve.invoke;
	exports org.mve.invoke.common;
	exports org.mve.io;
}
