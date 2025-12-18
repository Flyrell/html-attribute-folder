fun properties(key: String) = providers.gradleProperty(key)

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.10.5"
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
        marketplace()
        jetbrainsRuntime()
    }
}

dependencies {
    intellijPlatform {
        val type = providers.gradleProperty("platformType").getOrElse("IU")
        val version = providers.gradleProperty("platformVersion").getOrElse("2024.3")
        create(type, version)

        pluginVerifier()

        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        bundledPlugin("JavaScript")
    }

    testImplementation(kotlin("test"))
}

intellijPlatform {
    pluginVerification {
        ides {
            create(org.jetbrains.intellij.platform.gradle.IntelliJPlatformType.IntellijIdeaCommunity, "2024.3")
        }
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = properties("javaVersion").get()
        targetCompatibility = properties("javaVersion").get()
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(properties("javaVersion").get()))
            // Deactivate K2 to avoid SpillingKt problems during the verification phase
            freeCompilerArgs.add("-Xuse-k2=false")
        }
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
        token = providers.gradleProperty("intellijPlatformPublishingToken")
    }
}
