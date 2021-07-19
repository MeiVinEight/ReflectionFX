package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.annotation.NameAndValue;
import org.mve.asm.attribute.annotation.TypeAnnotation;
import org.mve.asm.attribute.annotation.type.location.Location;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationLocation;
import org.mve.asm.file.attribute.element.ElementNameAndValue;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public class TypeAnnotationArray<T extends TypeAnnotationArray<T>>
{
	public TypeAnnotation[] annotation = new TypeAnnotation[0];

	@SuppressWarnings("unchecked")
	public T annotation(TypeAnnotation annotation)
	{
		this.annotation = Arrays.copyOf(this.annotation, this.annotation.length+1);
		this.annotation[this.annotation.length-1] = annotation;
		return (T) this;
	}

	public org.mve.asm.file.attribute.annotation.TypeAnnotation[] array(ConstantArray array)
	{
		org.mve.asm.file.attribute.annotation.TypeAnnotation[] value = new org.mve.asm.file.attribute.annotation.TypeAnnotation[this.annotation.length];
		for (int i = 0; i < this.annotation.length; i++)
		{
			org.mve.asm.file.attribute.annotation.TypeAnnotation annotation = new org.mve.asm.file.attribute.annotation.TypeAnnotation();
			annotation.kind = this.annotation[i].value.type;
			annotation.value = this.annotation[i].value.value(array);
			annotation.location = new TypeAnnotationLocation();
			for (Location location : this.annotation[i].location)
			{
				annotation.location.location(new org.mve.asm.file.attribute.annotation.type.location.Location(location.kind, location.argument));
			}
			annotation.type = ConstantPoolFinder.findClass(array, this.annotation[i].type);
			for (NameAndValue v : this.annotation[i].element)
			{
				ElementNameAndValue element = new ElementNameAndValue();
				element.name = ConstantPoolFinder.findUTF8(array, v.name);
				element.value = NameAndValue.value(array, v.value);
				annotation.element(element);
			}
			value[i] = annotation;
		}
		return value;
	}
}
