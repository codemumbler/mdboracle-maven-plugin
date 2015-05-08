package io.github.codemumbler.maven.plugin;

import org.apache.maven.plugin.testing.MojoRule;
import org.codehaus.plexus.util.IOUtil;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

public class DataConverterMojoTest {

	public static final String DATA_CONVERSION = "data-conversion";
	private DataConverterMojo mojo;

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

	private void executeMojo(String pomFile) throws Exception {
		File pom = new File(pomFile);
		mojo = (DataConverterMojo) rule.lookupConfiguredMojo(pom, DATA_CONVERSION);
		mojo.execute();
	}
}
