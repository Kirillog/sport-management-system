import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val kotlinVersion = "1.5.31"

plugins {
    kotlin("jvm") version "1.5.31"
    id("application")
    id("org.panteleyev.jpackageplugin") version "1.3.1"
    id("com.github.johnrengelman.shadow") version "5.1.0"
    id("org.jetbrains.compose") version "1.0.0"
}


repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

val module = "ru.emkn.kotlin.sms"
version = "1.0.0"


dependencies {
    implementation("com.xenomachina:kotlin-argparser:2.0.7")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    runtimeOnly("org.slf4j:slf4j-simple:1.7.29")
    implementation("com.sksamuel.hoplite:hoplite-core:1.4.15")
    implementation("com.sksamuel.hoplite:hoplite-json:1.4.15")

//    Libs for working with database
    implementation("com.h2database:h2:1.4.199")
    implementation("org.jetbrains.exposed:exposed-core:0.36.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.36.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.36.2")
    implementation("org.jetbrains.exposed:exposed-java-time:0.36.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")

    implementation(compose.desktop.currentOs)
}

tasks.withType<ShadowJar> {
    manifest {
        attributes["Main-Class"] = "$module.MainKt"
    }
}

tasks {

    test {
        useJUnitPlatform()
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    application {
        mainClassName = "$module.MainKt"
    }

    jar {
        manifest {
            attributes("Main-Class" to "$module.MainKt")
        }
    }

    artifacts {
        add("archives", jar)
    }

    task("copyDependencies", Copy::class) {
        from(configurations.runtimeClasspath).into("$buildDir/jars")
    }

    task("copyJar", Copy::class) {
        from(jar).into("$buildDir/jars")
    }

    jpackage {
        dependsOn("build", "jar", "copyDependencies", "copyJar")

        input = "$buildDir/jars"
        destination = "$buildDir/dist"

        appName = project.name
        vendor = "SeKirA"
        
        mainJar = jar.get().archiveFileName.get()
        mainClass = "ru.emkn.kotlin.sms.MainKt"

        javaOptions = listOf("-Dfile.encoding=UTF-8")

        icon = "src/main/resources/sekira.ico"

        windows {
            winDirChooser = true
            winConsole = false
            winMenu = true
            winPerUserInstall = true
            winShortcut = true
        }
    }
}
