package io.github.codemumbler.maven.plugin;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.util.IOUtil;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

public class MdbConverterMojoTest {

	public static final String FULL_CONVERSION = "full-conversion";
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
		Assume.assumeTrue(new File("target/test-classes/full-conversion/target/test-classes").mkdirs());
		executeMojo("target/test-classes/full-conversion");
		Assert.assertEquals(IOUtil.toString(new FileInputStream("target/test-classes/expected.sql")),
				IOUtil.toString(new FileInputStream("target/test-classes/full-conversion/target/test-classes/sql.sql")));
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
		mojo = (MdbConverterMojo) rule.lookupConfiguredMojo(pom, FULL_CONVERSION);
		mojo.execute();
	}
}