package org.mve.asm.file.attribute.annotation.type;

import org.mve.asm.file.attribute.annotation.type.location.Location;

import java.util.Arrays;
import java.util.Objects;

public class TypeAnnotationPath
{
	public Location[] path = new Location[0];

	public void path(Location path)
	{
		this.path = Arrays.copyOf(this.path, this.path.length+1);
		this.path[this.path.length-1] = Objects.requireNonNull(path);
	}

	public int length()
	{
		return 1 + (2 * this.path.length);
	}

	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		b[0] = (byte) this.path.length;
		int index = 1;
		for (Location s : this.path)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 2);
			index+=2;
		}
		return b;
	}
}
