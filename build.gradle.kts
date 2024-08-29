plugins {
	id("org.springframework.boot") version "3.3.3"
	id("maven-publish")
	id("io.spring.dependency-management") version "1.1.6"
	id("java")
	id("application")
	id("org.sonarqube") version "5.1.0.4882"
	id("jacoco")
}

val targetJavaVersion = (project.property("jdk_version") as String).toInt()
val javaVersion = JavaVersion.toVersion(targetJavaVersion)

sonarqube {
	properties {
		property("sonar.projectKey", "sibmaks_scrum-poker")
		property("sonar.projectVersion", project.version)
		property("sonar.organization", "sibmaks")
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.java.source", "${targetJavaVersion}")
		property("sonar.java.target", "${targetJavaVersion}")
	}
}

java {
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.retry:spring-retry")

	implementation("jakarta.annotation:jakarta.annotation-api")
	implementation("jakarta.persistence:jakarta.persistence-api")
	implementation("jakarta.validation:jakarta.validation-api")

	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	runtimeOnly("org.postgresql:postgresql")

	compileOnly("jakarta.servlet:jakarta.servlet-api")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")

	testImplementation("com.opentable.components:otj-pg-embedded:${project.property("lib_otg_pg_embedded_version")}")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	testImplementation("org.mockito:mockito-core")
	testImplementation("org.mockito:mockito-junit-jupiter")
}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
}