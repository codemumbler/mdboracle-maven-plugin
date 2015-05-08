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
		Assert.assertEquals("INSERT INTO SIMPLE_TABLE(ID, LABEL, DESCRIPTION) VALUES (1, 'label1', 'description1');\n" +
						"INSERT INTO SIMPLE_VALUE_TABLE(ID, DATE_TIME, CURRENCY, YES_NO, LABEL_LOOKUP, NUMBER, PRECISION_NUMBER, FOREIGN_KEY, TEXT_LOOKUP) VALUES (1, TO_TIMESTAMP('04/01/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 3.5000, 'Y', 1, 2, 1.1, 1, 'text1');\n" +
						"DECLARE\n" +
						"str varchar2(32767);\n" +
						"BEGIN\n" +
						"\tstr := 'test memo';\n" +
						"UPDATE SIMPLE_VALUE_TABLE SET MEMO = str WHERE ID = 1;\n" +
						"END;\n" +
						"/\n" +
						"INSERT INTO TEXT_KEY_TABLE(TEXT_KEY) VALUES ('text1');",
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