import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    idea
    maven
    kotlin("jvm") version "1.3.71"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "com.auqkwatech"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    //maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://papermc.io/repo/repository/maven-public/")
    jcenter()
}

dependencies {
    /* Kotlin */
    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("compiler-embeddable"))
    compile(kotlin("script-util"))
    compile(kotlin("scripting-compiler-embeddable"))
    compile(kotlin("reflect"))

    /* Kyori Text API */
    compile("net.kyori:text-adapter-bukkit:3.0.0")
    compile("net.kyori:text-api:3.0.0")
    compile("net.kyori:text-serializer-legacy:3.0.0")

    /* Spigot */
    implementation("com.destroystokyo.paper", "paper-api", "1.15.2-R0.1-SNAPSHOT")

    /* FastUtil */
    implementation("it.unimi.dsi", "fastutil", "8.3.1")
    implementation("commons-io", "commons-io", "2.6")

    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    build {
        dependsOn(shadowJar)
    }
}

val shadowJar = tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("shadow")
    mergeServiceFiles()
    configurations = arrayListOf(project.configurations.compile.get())
    manifest {
        attributes(mapOf("Main-Class" to "com.auqkwatech.auqkwacore.AuqkwaCore"))
    }
    dependencies {
        exclude(dependency("com.google.code.gson:"))
        exclude(dependency("com.google.guava:"))
        exclude(dependency("commons-lang:"))
        exclude(dependency("net.md-5:"))
        exclude(dependency("org.spigotmc:"))
        exclude(dependency("org.yaml:"))
    }
    minimize()
}

artifacts {
    archives(shadowJar)
}
