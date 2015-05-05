package io.github.codemumbler.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.codehaus.plexus.util.IOUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

public class MdbConverterMojoTest {

	private MdbConverterMojo mojo;

	@Rule
	public MojoRule rule = new MojoRule() {
		@Override
		protected void before() throws Throwable {
		}

		@Override
		protected void after() {
		}
	};

	@Test
	public void outputFile() throws Exception {
		executeMojo("src/test/resources/working-pom.xml");
		Assert.assertEquals(IOUtil.toString(new FileInputStream("src/test/resources/expected.sql")),
				IOUtil.toString(new FileInputStream("target/test-classes/sql.sql")));
	}

	@Test( expected = MojoExecutionException.class )
	public void noAccessFile() throws Exception {
		executeMojo("src/test/resources/bad-mdb-file-pom.xml");
	}

	@Test( expected = MojoExecutionException.class )
	public void noOutputFile() throws Exception {
		executeMojo("src/test/resources/bad-output-file-pom.xml");
	}

	private void executeMojo(String pomFile) throws Exception {
		File pom = new File(pomFile);
		mojo = (MdbConverterMojo) rule.lookupMojo("mdboracle", pom);
		mojo.execute();
	}
}