package org.mve.asm.file.attribute.annotation.type;

import org.mve.asm.file.attribute.annotation.type.location.Location;

import java.util.Arrays;
import java.util.Objects;

public class TypeAnnotationLocation
{
	public Location[] location = new Location[0];

	public void location(Location location)
	{
		this.location = Arrays.copyOf(this.location, this.location.length+1);
		this.location[this.location.length-1] = Objects.requireNonNull(location);
	}

	public int length()
	{
		return 1 + (2 * this.location.length);
	}

	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		b[0] = (byte) this.location.length;
		int index = 1;
		for (Location s : this.location)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 2);
			index+=2;
		}
		return b;
	}
}
