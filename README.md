Project Setup
Requirements:
Java 21 or higher
MySQL 8.0 or higher
Maven 3.8+
JavaFX SDK 21
Spring Boot (configured in project)
Steps to run:

Clone the project
git clone https://github.com/kperam1/Agile-Requirements-Engineering-Tool.git
cd Agile-Requirements-Engineering-Tool

Build the project

mvn clean install

Start the Spring Boot backend
cd backend
mvn spring-boot:run

In a new terminal, start the JavaFX frontend
mvn javafx:run
