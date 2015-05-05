package io.github.codemumbler.maven.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.github.codemumbler.Database;
import io.github.codemumbler.MDBReader;
import io.github.codemumbler.OracleScriptWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

@Mojo(name = "mdbconverter", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class MdbConverterMojo
		extends AbstractMojo {

	@Parameter(defaultValue = "${project.basedir}/src/main/resources/db/migration/V1_1__transfer_from_mdb.sql")
	private File outputFile;

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
			OracleScriptWriter writer = new OracleScriptWriter(database);
			PrintWriter printWriter = new PrintWriter(outputFile);
			printWriter.println(writer.writeScript());
			printWriter.flush();
			printWriter.close();
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to write output to file.", e);
		}
	}
}
