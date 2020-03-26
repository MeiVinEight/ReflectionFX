package org.mve.util.asm;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TypeAnnotationReader
{
	public static StructTypeAnnotation read(ClassFile file, InputStream input) throws IOException
	{
		DataInputStream in = new DataInputStream(input);
		StructTypeAnnotation annotation = new StructTypeAnnotation();
		byte targetType = in.readByte();
		switch (targetType)
		{
			case 0X00:
			case 0X01:
			{
				TypeAnnotationTargetTypeParameter target = new TypeAnnotationTargetTypeParameter(targetType);
				target.setTypeParameterIndex(in.readByte());
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X10:
			{
				TypeAnnotationTargetSupertype target = new TypeAnnotationTargetSupertype(targetType);
				target.setSupertypeIndex(in.readShort());
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X11:
			case 0X12:
			{
				TypeAnnotationTargetTypeParameterBound target = new TypeAnnotationTargetTypeParameterBound(targetType);
				target.setTypeParameterIndex(in.readByte());
				target.setBoundIndex(in.readByte());
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
				target.setFormalParameterIndex(in.readByte());
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X17:
			{
				TypeAnnotationTargetThrows target = new TypeAnnotationTargetThrows(targetType);
				target.setThrowsTypeIndex(in.readShort());
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X40:
			case 0X41:
			{
				TypeAnnotationTargetLocalVariable target = new TypeAnnotationTargetLocalVariable(targetType);
				int count = in.readShort() & 0XFFFF;
				for (int i = 0; i < count; i++)
				{
					StructLocalVariableTargetTable table = new StructLocalVariableTargetTable();
					table.setStartPc(in.readShort());
					table.setLength(in.readShort());
					table.setIndex(in.readShort());
					target.addLocalVariableTargetTable(table);
				}
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X42:
			{
				TypeAnnotationTargetCatch target = new TypeAnnotationTargetCatch(targetType);
				target.setExceptionTableIndex(in.readShort());
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
			case 0X43:
			case 0X44:
			case 0X45:
			case 0X46:
			{
				TypeAnnotationTargetOffset target = new TypeAnnotationTargetOffset(targetType);
				target.setOffset(in.readShort());
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
				target.setOffset(in.readShort());
				target.setTypeArgumentIndex(in.readByte());
				annotation.setTypeAnnotationTarget(targetType, target);
				break;
			}
		}
		StructTypeAnnotationPath path = new StructTypeAnnotationPath();
		int count = in.readByte() & 0XFF;
		for (int i = 0; i < count; i++)
		{
			StructPath p = new StructPath();
			p.setTypePathKind(in.readByte());
			p.setTypeArgumentIndex(in.readByte());
			path.addPath(p);
		}
		annotation.setTargetPath(path);
		annotation.setTypeIndex(in.readShort());
		count = in.readShort() & 0XFFFF;
		for (int i = 0; i < count; i++)
		{
			StructElementValuePairs pairs = new StructElementValuePairs();
			pairs.setElementNameIndex(in.readShort());
			pairs.setElementValue(ElementValueReader.read(file, in));
			annotation.addElementValuePairs(pairs);
		}
		return annotation;
	}
}
