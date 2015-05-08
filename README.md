# mdboracle-maven-plugin
Maven plugin for converting MS Access to Oracle SQL scripts

Sample usage:
```
<build>
		<plugins>
			<plugin>
				<groupId>io.github.codemumbler.maven.plugin</groupId>
				<artifactId>mdbconverter</artifactId>
				<executions>
					<execution>
						<goals>
							<goal>write-script</goal>
						</goals>
						<phase>generate-resources</phase>
					</execution>
				</executions>
				<configuration>
					<accessFile>simple.accdb</accessFile>
					<outputFile>output-script.sql</outputFile>
					<writeDDL>true</writeDDL>
					<writeDML>true</writeDML>
				</configuration>
			</plugin>
		</plugins>
	</build>
```

Writes out a full Oracle SQL conversion to output-script.sql
