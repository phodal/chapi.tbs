plugins {
    application
    java
    kotlin("jvm") version "1.3.70"
    kotlin("plugin.serialization") version "1.3.70"
}

application {
    mainClassName = "com.phodal.chapi.tbs.Main"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClassName
    }

    from(configurations.runtime.get().map {if (it.isDirectory) it else zipTree(it)})
}

allprojects {
    group = "com.phodal.chapi.tbs"
    version = "0.0.5"

    repositories {
        mavenCentral()
        mavenLocal()
        jcenter()
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
    implementation("com.phodal.chapi:chapi-domain:0.0.5")
    implementation("com.phodal.chapi:chapi-application:0.0.5")
    implementation("com.phodal.chapi:chapi-ast-java:0.0.5")

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    // Kotlin reflection.
    implementation(kotlin("test"))
    implementation(kotlin("test-junit"))

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    testRuntimeOnly("org.junit.platform:junit-platform-console:1.6.0")
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

