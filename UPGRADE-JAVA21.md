Java 21 upgrade notes

This repository has been updated to target Java 21 (JDK 21) and Spring Boot 3.4.11 on branch `uc-1`.

To build and run the project locally you must have a Java 21 JDK installed and available to Maven. If you see a build error like "release version 21 not supported", it means your local Java is older.

Quick setup (Windows PowerShell):

```powershell
# Download and install a JDK 21 distribution (Eclipse Temurin example):
# Visit https://adoptium.net or install manually and note the installation path.

# After installing, set JAVA_HOME to the JDK 21 installation (example path):
$env:JAVA_HOME = 'C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.0.0-hotspot'
[Environment]::SetEnvironmentVariable('JAVA_HOME', $env:JAVA_HOME, 'User')
# Add Java bin to PATH for the current shell (or update system PATH):
$env:Path = "$env:JAVA_HOME\\bin;" + $env:Path

# Verify
mvn -version
java -version
```

If you prefer to keep multiple JDKs, consider configuring the Maven Toolchains plugin or using a CI workflow that runs tests on JDK 21.

If you want, I added a sample Maven Toolchains plugin configuration to the `pom.xml` and a GitHub Actions workflow that builds on JDK 21 (`.github/workflows/ci-java21.yml`).

Sample `~/.m2/toolchains.xml` to select a local JDK 21 installation (replace the path with your JDK 21 install):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains xmlns="http://maven.apache.org/TOOLCHAINS/1.1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
						xsi:schemaLocation="http://maven.apache.org/TOOLCHAINS/1.1.0 https://maven.apache.org/xsd/toolchains-1.1.0.xsd">
	<toolchain>
		<type>jdk</type>
		<provides>
			<version>21</version>
			<vendor>eclipse</vendor>
		</provides>
		<configuration>
			<jdkHome>C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.0.0-hotspot</jdkHome>
		</configuration>
	</toolchain>
</toolchains>
```

Place that file at `%USERPROFILE%\\.m2\\toolchains.xml` on Windows. With the toolchains config and the plugin in `pom.xml`, Maven will prefer that JDK for builds that require Java 21.

The CI workflow will run on GitHub using Temurin JDK 21 so your PRs will be validated automatically.
