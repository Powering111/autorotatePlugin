plugins {
    kotlin("jvm") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
}
tasks{
    test{
        useJUnitPlatform()
    }

    build{
        dependsOn(shadowJar)
    }

    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
    }
    shadowJar{
        archiveBaseName.set(project.property("pluginName").toString())
        archiveVersion.set("")
        archiveClassifier.set("")
    }
    create<Jar>("sourcesJar") {
        from(sourceSets["main"].allSource)
        archiveClassifier.set("sources")
    }
    create<Copy>("copyToServer"){

        from(shadowJar)
        val serverDir = project.property("serverDir")
        val dest = File("$serverDir/plugins")
        into(dest)
    }

}

kotlin {
    jvmToolchain(17)
}
