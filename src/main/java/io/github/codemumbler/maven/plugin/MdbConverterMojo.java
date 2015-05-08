package io.github.codemumbler.maven.plugin;

import io.github.codemumbler.Database;
import io.github.codemumbler.MDBReader;
import io.github.codemumbler.OracleScriptWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.PrintWriter;

@Mojo(name = "write-script", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class MdbConverterMojo
		extends AbstractMojo {

	@Parameter(defaultValue = "${project.basedir}/src/main/resources/db/migration/V1_1__transfer_from_mdb.sql")
	private File outputFile;

	@Parameter(required = true)
	private File accessFile;

	@Parameter(defaultValue = "true")
	private boolean writeDDL;

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
			OracleScriptWriter writer = new OracleScriptWriter(database);
			PrintWriter printWriter = new PrintWriter(outputFile);
			if ( writeDDL && writeDML )
				printWriter.println(writer.writeScript());
			else if ( writeDML )
				printWriter.println(writer.writeDatabaseInsertions());
			else if ( writeDDL )
				printWriter.println(writer.writeDDLScript());
			printWriter.flush();
			printWriter.close();
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to write output to file.", e);
		}
	}
}
