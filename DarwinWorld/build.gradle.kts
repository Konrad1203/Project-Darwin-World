plugins {
    id("java")
	id ("application")
    id ("org.openjfx.javafxplugin") version ("0.0.13")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
	javafx {
        version = "20"
        modules = listOf("javafx.controls","javafx.fxml")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.openjfx:javafx-controls:21")
}

tasks.test {
    useJUnitPlatform()
}