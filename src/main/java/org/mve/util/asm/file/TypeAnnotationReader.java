package org.mve.util.asm.file;

import org.mve.io.RandomAccessByteArray;

public class TypeAnnotationReader
{
	public static StructTypeAnnotation read(ClassFile file, RandomAccessByteArray input)
	{
		StructTypeAnnotation annotation = new StructTypeAnnotation();
		byte targetType = input.readByte();
		switch (targetType)
		{
			case 0X00:
			case 0X01:
			{
				TypeAnnotationTargetTypeParameter target = new TypeAnnotationTargetTypeParameter(targetType);
				target.setTypeParameterIndex(input.readByte());
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X10:
			{
				TypeAnnotationTargetSupertype target = new TypeAnnotationTargetSupertype(targetType);
				target.setSupertypeIndex(input.readShort());
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X11:
			case 0X12:
			{
				TypeAnnotationTargetTypeParameterBound target = new TypeAnnotationTargetTypeParameterBound(targetType);
				target.setTypeParameterIndex(input.readByte());
				target.setBoundIndex(input.readByte());
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X13:
			case 0X14:
			case 0X15:
			{
				TypeAnnotationTargetEmpty target = new TypeAnnotationTargetEmpty(targetType);
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X16:
			{
				TypeAnnotationTargetFormalParameter target = new TypeAnnotationTargetFormalParameter(targetType);
				target.setFormalParameterIndex(input.readByte());
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X17:
			{
				TypeAnnotationTargetThrows target = new TypeAnnotationTargetThrows(targetType);
				target.setThrowsTypeIndex(input.readShort());
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X40:
			case 0X41:
			{
				TypeAnnotationTargetLocalVariable target = new TypeAnnotationTargetLocalVariable(targetType);
				int count = input.readShort() & 0XFFFF;
				for (int i = 0; i < count; i++)
				{
					StructLocalVariableTargetTable table = new StructLocalVariableTargetTable();
					table.setStartPc(input.readShort());
					table.setLength(input.readShort());
					table.setIndex(input.readShort());
					target.addLocalVariableTargetTable(table);
				}
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X42:
			{
				TypeAnnotationTargetCatch target = new TypeAnnotationTargetCatch(targetType);
				target.setExceptionTableIndex(input.readShort());
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X43:
			case 0X44:
			case 0X45:
			case 0X46:
			{
				TypeAnnotationTargetOffset target = new TypeAnnotationTargetOffset(targetType);
				target.setOffset(input.readShort());
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X47:
			case 0X48:
			case 0X49:
			case 0X4A:
			case 0X4B:
			{
				TypeAnnotationTargetTypeArgument target = new TypeAnnotationTargetTypeArgument(targetType);
				target.setOffset(input.readShort());
				target.setTypeArgumentIndex(input.readByte());
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
		}
		StructTypeAnnotationPath path = new StructTypeAnnotationPath();
		int count = input.readByte() & 0XFF;
		for (int i = 0; i < count; i++)
		{
			StructPath p = new StructPath();
			p.setTypePathKind(input.readByte());
			p.setTypeArgumentIndex(input.readByte());
			path.addPath(p);
		}
		annotation.setTargetPath(path);
		annotation.setTypeIndex(input.readShort());
		count = input.readShort() & 0XFFFF;
		for (int i = 0; i < count; i++)
		{
			StructElementValuePairs pairs = new StructElementValuePairs();
			pairs.setElementNameIndex(input.readShort());
			pairs.setElementValue(ElementValueReader.read(file, input));
			annotation.addElementValuePairs(pairs);
		}
		return annotation;
	}
}
