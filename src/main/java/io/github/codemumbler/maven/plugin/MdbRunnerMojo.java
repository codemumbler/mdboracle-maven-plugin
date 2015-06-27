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
import java.io.PrintWriter;

@Mojo(name = "build-database", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class MdbRunnerMojo
		extends AbstractMojo {

	@Parameter(required = true)
	private String jdbcURL;

	@Parameter(required = true)
	private String username;

	@Parameter(required = true)
	private String password;

	@Parameter(required = true)
	private File accessFile;

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
			runner.executeCreation(database);
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to build database.", e);
		}
	}
}
