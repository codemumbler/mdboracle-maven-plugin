package io.github.codemumbler.maven.plugin;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class MdbConverterMojoTest {

	@Rule
	public MojoRule rule = new MojoRule()
	{
		@Override
		protected void before() throws Throwable
		{
		}

		@Override
		protected void after()
		{
		}
	};

	@Test
	public void outputFile() throws Exception {
		File pom = new File("src/test/resources/pom.xml");
		MdbConverterMojo mojo = (MdbConverterMojo) rule.lookupMojo("mdboracle", pom);
		mojo.execute();
	}
}