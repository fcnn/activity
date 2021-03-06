import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    extra.apply {
        //set("grpcVersion","+")
        //set("kotlinVersion", "1.3.70")
    }

    repositories {
	    mavenCentral()
    }

    dependencies {
        classpath( "org.jetbrains.kotlin:kotlin-gradle-plugin:${property("kotlinVersion")}")
    }
}

plugins{
	id("java")
	id("application")
	kotlin("jvm")
}

application {
    //mainClass.set("HelloworldclientKt")
}

java.sourceCompatibility = JavaVersion.VERSION_18

    repositories {
	    mavenCentral()
    }

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${property("kotlinVersion")}")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "18"
	}
}

