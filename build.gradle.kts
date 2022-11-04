// D:\tools\jdk-19\bin\java -cp "bin;fat" io.vertx.core.Launcher run com.hk.activity.MainVerticle

import com.google.protobuf.gradle.*
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  extra.apply {
    set("grpcVersion", "+")
    set("protobufVersion", "+")
    set("kotlinVersion", "1.7.20")
    set("protocVersion", "+")
    set("protobufPluginVersion", "0.9.1")
  }

  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven ("https://kotlin.bintray.com/kotlinx")
  }

  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${property("kotlinVersion")}")
    classpath("com.google.protobuf:protobuf-gradle-plugin:${property("protobufPluginVersion")}")
  }
}

plugins {
  id("java")
  id("com.google.protobuf") version "${property("protobufPluginVersion")}"
  kotlin("jvm") version("${property("kotlinVersion")}")
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"
  id("io.spring.dependency-management") version("1.1.0")
}

group = "com.hk"
version = "1.0.0-SNAPSHOT"

repositories {
  maven {
    url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
    mavenContent {
      snapshotsOnly()
    }
  }
  mavenCentral()
}

val vertxVersion = "4.3.5-SNAPSHOT"
val junitJupiterVersion = "5.9.1"

val mainVerticleName = "com.hk.activity.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  //implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${property("kotlinVersion")}")
  implementation("javax.annotation:javax.annotation-api:1.3.2")
  implementation("com.google.protobuf:protobuf-java:3.21.0-rc-1")
  //implementation("io.grpc:grpc-netty:${property("grpcVersion")}")
  //implementation("io.grpc:grpc-protobuf:${property("grpcVersion")}")
  //implementation("io.grpc:grpc-stub:${property("grpcVersion")}")

  implementation(project(":message"))

  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-sql-client-templates")
  //implementation("io.vertx:vertx-mysql-client")
  implementation("io.vertx:vertx-pg-client")
  implementation("io.vertx:vertx-lang-kotlin")
  implementation("io.vertx:vertx-redis-client")
  implementation(kotlin("stdlib-jdk8"))
  testImplementation(project(":message"))
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:${property("protocVersion")}" }

    plugins {
        id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:${property("grpcVersion")}" }
    }

    generatedFilesBaseDir = "${property("projectDir")}/src"
    generateProtoTasks {
        all().forEach { task ->
          task.plugins { id("grpc") { outputSubDir = "java" } }
        }
    }
}

tasks.processResources  {
  mustRunAfter("generateProto")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "18"

//tasks.withType<JavaCompile> {
//  options.encoding = "UTF-8"
//}

tasks.jar {
  manifest {
    attributes(mapOf("Main-Class" to launcherClassName))
  }
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}

tasks.wrapper {
  gradleVersion = "8.0-milestone-3"
  distributionType = Wrapper.DistributionType.ALL
}
