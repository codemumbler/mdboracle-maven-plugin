package io.github.codemumbler.maven.plugin;

import io.github.codemumbler.ScriptRunner;
import oracle.jdbc.pool.OracleDataSource;
import org.apache.maven.plugin.testing.MojoRule;
import org.codehaus.plexus.util.IOUtil;
import org.junit.*;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MdbRunnerMojoTest {

  private static final String GOAL = "build-database";
  private MdbRunnerMojo mojo;
  private DataSource dataSource;
  private File cleanupScript;

  @Rule
  public MojoRule rule = new MojoRule() {
    @Override
    protected void before() throws Throwable {
    }

    @Override
    protected void after() {
    }
  };

  @Before
  public void setUp() throws Exception {
    dataSource = getDataSource();
    cleanupScript = null;
  }

  @After
  public void tearDown() throws Exception {
    if (cleanupScript != null) {
      runSqlFile(cleanupScript);
    }
  }

  private void runSqlFile(File sqlScript) throws Exception {
    ScriptRunner runner = new ScriptRunner(getDataSource());
    runner.executeScript(loadFileAsString(sqlScript.getAbsolutePath()));
  }

  @Test
  public void buildDatabase() throws Exception {
    cleanupScript = new File("target/test-classes/build-database/cleanup.sql");
    new File("target/test-classes/build-database/target/test-classes").mkdirs();
    executeMojo("target/test-classes/build-database");
    Assert.assertEquals("label1", getString());
  }

  @Test
  public void justDML() throws Exception {
    cleanupScript = new File("target/test-classes/build-database-dml/cleanup.sql");
    runSqlFile(new File("target/test-classes/build-database-dml/setup.sql"));
    new File("target/test-classes/build-database-dml/target/test-classes").mkdirs();
    executeMojo("target/test-classes/build-database-dml");
    Assert.assertEquals("label1", getString());
  }

  @Test
  public void justDDL() throws Exception {
    cleanupScript = new File("target/test-classes/build-database-ddl/cleanup.sql");
    new File("target/test-classes/build-database-ddl/target/test-classes").mkdirs();
    executeMojo("target/test-classes/build-database-ddl");
    Assert.assertEquals(null, getString());
  }

  private String getString() throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      try (Statement statement = connection.createStatement()) {
        try (ResultSet resultSet = statement.executeQuery("SELECT label FROM simple_table")) {
          if (resultSet.next()) {
            return resultSet.getString(1);
          }
        }
      }
    }
    return null;
  }

  private void executeMojo(String pomFile) throws Exception {
    File pom = new File(pomFile);
    mojo = (MdbRunnerMojo) rule.lookupConfiguredMojo(pom, GOAL);
    mojo.execute();
  }

  private DataSource getDataSource() throws SQLException {
    OracleDataSource ds = new OracleDataSource();
    ds.setURL("jdbc:oracle:thin:@localhost:1521/XE");
    ds.setUser("test");
    ds.setPassword("testpass");
    return ds;
  }

  private String loadFileAsString(String fileName) throws Exception {
    return IOUtil.toString(new FileInputStream(fileName)).replaceAll("\r", "").trim();
  }
}
