import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.panteleyev.jpackage.ImageType
import org.apache.tools.ant.taskdefs.condition.Os
import kotlin.io.path.ExperimentalPathApi
import java.io.*
import kotlin.io.path.createSymbolicLinkPointingTo

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
val mainClassName = "$module.MainKt"
version = "1.0"


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

val installerDirName = "$buildDir/installers"
val appDirName = "$buildDir/app-dir"
val jarsDir = "$buildDir/jars"

tasks {

    test {
        useJUnitPlatform()
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "16"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "16"
    }

    application {
        mainClassName = "$module.MainKt"
    }

    jar {
        manifest {
            attributes("Main-Class" to "$module.MainKt")
        }
    }

    withType<ShadowJar> {
        manifest {
            attributes("Main-Class" to "$module.MainKt")
        }
        val destinationDir = File("fat-jar")
        if (!destinationDir.exists())
            destinationDir.mkdir()
        destinationDirectory.set(destinationDir)
    }

    artifacts {
        add("archives", jar)
    }

    register("copyDependencies", Copy::class) {
        from(configurations.runtimeClasspath).into(jarsDir)
    }

    register("copyJar", Copy::class) {
        from(jar).into(jarsDir)
    }

    register<org.panteleyev.jpackage.JPackageTask>("winJPackage") {
        dependsOn(build, jar, "copyDependencies", "copyJar")

        input = jarsDir
        appName = project.name
        vendor = "SeKirA"

        mainJar = jar.get().archiveFileName.get()
        mainClass = mainClassName

        destination = installerDirName
        javaOptions = listOf("-Dfile.encoding=UTF-8")

        icon = "src/main/resources/sekira.ico"
        type = ImageType.EXE
        winDirChooser = true
        winConsole = false
        winMenu = true
        winPerUserInstall = true
        winShortcut = true
    }

    register<org.panteleyev.jpackage.JPackageTask>("linuxJPackage") {
        dependsOn(build, jar, "copyDependencies", "copyJar")

        input = jarsDir
        appName = project.name
        vendor = "SeKirA"

        mainJar = jar.get().archiveFileName.get()
        mainClass = mainClassName
        javaOptions = listOf("-Dfile.encoding=UTF-8")

        destination = appDirName
        if (File(destination).exists())
            File(destination).deleteRecursively()
        icon = "src/main/resources/sekira.png"
        type = ImageType.APP_IMAGE
    }

    register("appImage") {
        dependsOn("linuxJPackage")
        doFirst {
            if (!File(installerDirName).exists())
                File(installerDirName).mkdir()
        }
        doLast {
            val appName = project.name
            val appDir = File("$appDirName/$appName")

            val symLink = appDir.resolve("AppRun")
            @OptIn(ExperimentalPathApi::class)
            symLink.toPath().createSymbolicLinkPointingTo(appDir.resolve("bin/$appName").toPath())

            appDir.resolve("lib/$appName.png").let { sourceFile ->
                sourceFile.copyTo(appDir.resolve("$appName.png"))
                sourceFile.delete()
            }

            val desktopFile = appDir.resolve("$appName.desktop")
            OutputStreamWriter(FileOutputStream(desktopFile), "UTF-8").use {
                it.write(
                    """
                [Desktop Entry]
                Type=Application
                Version=$version
                Name=$appName
                Comment=System helping to provide competitions
                Path=/bin/$appName
                Exec=$appName
                Icon=$appName
                Terminal=false
                Categories=Education;Languages;Java;
            """.trimIndent()
                )
            }
            exec {
                commandLine("appimagetool.AppImage", "$buildDir/app-dir/$appName", "$installerDirName/$appName.AppImage")
            }
        }
    }

    register("installer") {
        if (Os.isFamily(Os.FAMILY_WINDOWS))
            dependsOn("winJPackage")
        else if (Os.isFamily(Os.FAMILY_UNIX))
            dependsOn("appImage")
        else
            println("Unsupported OS")
    }
}
