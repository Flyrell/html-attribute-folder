fun properties(key: String) = providers.gradleProperty(key)

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.intellij") version "1.17.2"
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = properties("javaVersion").get()
        targetCompatibility = properties("javaVersion").get()
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = properties("javaVersion").get()
    }

    patchPluginXml {
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
//        channels.set(listOf("alpha"))
    }
}
