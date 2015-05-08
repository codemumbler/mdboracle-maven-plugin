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

	public static final String GOAL = "write-script";
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
	public void fullScript() throws Exception {
		new File("target/test-classes/full-conversion/target/test-classes").mkdirs();
		executeMojo("target/test-classes/full-conversion");
		Assert.assertEquals(IOUtil.toString(new FileInputStream("target/test-classes/full-conversion/expected.sql")),
				IOUtil.toString(new FileInputStream("target/test-classes/full-conversion/target/test-classes/sql.sql")));
	}

	@Test
	public void ddlScript() throws Exception {
		new File("target/test-classes/ddl-conversion/target/test-classes").mkdirs();
		executeMojo("target/test-classes/ddl-conversion");
		Assert.assertEquals(IOUtil.toString(new FileInputStream("target/test-classes/ddl-conversion/expected.sql")).trim(),
				IOUtil.toString(new FileInputStream("target/test-classes/ddl-conversion/target/test-classes/sql.sql")).trim());
	}

	@Test
	public void onlyInsertions() throws Exception {
		new File("target/test-classes/data-conversion/target/test-classes").mkdirs();
		executeMojo("target/test-classes/data-conversion");
		Assert.assertEquals(IOUtil.toString(new FileInputStream("target/test-classes/data-conversion/expected.sql")).trim(),
				IOUtil.toString(new FileInputStream("target/test-classes/data-conversion/target/test-classes/sql.sql")).trim());
	}

	@Test( expected = MojoExecutionException.class )
	public void noAccessFile() throws Exception {
		executeMojo("target/test-classes/bad-mdb-file");
	}

	@Test( expected = MojoExecutionException.class )
	public void noOutputFile() throws Exception {
		executeMojo("target/test-classes/bad-output-file");
	}

	private void executeMojo(String pomFile) throws Exception {
		File pom = new File(pomFile);
		mojo = (MdbConverterMojo) rule.lookupConfiguredMojo(pom, GOAL);
		mojo.execute();
	}
}