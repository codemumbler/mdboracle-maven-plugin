package io.github.codemumbler.maven.plugin;

import io.github.codemumbler.Database;
import io.github.codemumbler.MDBReader;
import io.github.codemumbler.OracleScriptWriter;
import io.github.codemumbler.ScriptRunner;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

@Mojo(name = "build-database",
    defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class MdbRunnerMojo extends AbstractMojo {

  @SuppressWarnings("unused")
  @Parameter(required = true)
  private String jdbcURL;

  @SuppressWarnings("unused")
  @Parameter(required = true)
  private String username;

  @SuppressWarnings("unused")
  @Parameter(required = true)
  private String password;

  @SuppressWarnings("unused")
  @Parameter(required = true)
  private File accessFile;

  @SuppressWarnings("unused")
  @Parameter(defaultValue = "true")
  private boolean writeDDL;

  @SuppressWarnings("unused")
  @Parameter(defaultValue = "true")
  private boolean writeDML;

  public void execute() throws MojoExecutionException {
    Database database;
    try {
      MDBReader reader = new MDBReader(accessFile);
      database = reader.loadDatabase();
    } catch (Exception e) {
      throw new MojoExecutionException("Failed to locate MDB file.", e);
    }
    try {
      ScriptRunner runner = new ScriptRunner(jdbcURL, username, password);
      if (writeDDL && writeDML)
        runner.executeCreation(database);
      else if (writeDML) {
        OracleScriptWriter writer = new OracleScriptWriter(database);
        runner.executeScript(writer.writeDatabaseInsertions());
      } else if (writeDDL) {
        OracleScriptWriter writer = new OracleScriptWriter(database);
        runner.executeScript(writer.writeDDLScript());
      }
    } catch (Exception e) {
      throw new MojoExecutionException("Failed to build database.", e);
    }
  }
}
