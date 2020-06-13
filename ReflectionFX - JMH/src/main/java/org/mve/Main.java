package org.mve;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Main
{
	public static void main(String[] args) throws Throwable
	{
		System.out.println(ReflectionFX.class.getSimpleName());
		Options opt = new OptionsBuilder()
			.include(ReflectionFX.class.getSimpleName())
			.warmupIterations(3)
			.warmupBatchSize(1_000_000)
			.measurementIterations(10)
			.measurementBatchSize(100_000_000)
			.mode(Mode.AverageTime)
			.forks(1)
			.build();
		new Runner(opt).run();
	}
}
