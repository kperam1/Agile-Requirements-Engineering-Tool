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

If you want, I can add a sample Maven Toolchains configuration and a CI workflow to run the build on JDK 21.
