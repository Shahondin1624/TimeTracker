
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "1.9.22"
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlin.logging)
            implementation(libs.logback.classic)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
            implementation("org.jetbrains.compose.material3:material3-desktop:1.7.3")
            // https://mvnrepository.com/artifact/org.jetbrains.compose.material/material-icons-extended
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs) {
                exclude("org.jetbrains.compose.material", "material-icons-extended-desktop")
            }
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

version = "1.1.0"

compose.desktop {
    application {
        mainClass = "com.github.shahondin1624.MainKt"

        buildTypes.release.proguard {
            version.set("7.4.2")
            obfuscate.set(false)
            optimize.set(false)
            configurationFiles.from(file("configs/proguard-rules.pro"))
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb,
                TargetFormat.Exe, TargetFormat.AppImage)
            packageVersion = version.toString()
            packageName = "TimeTracker"

            windows {
                iconFile.set(project.file("src/commonMain/composeResources/drawable/Hourglass_Win.ico"))
            }

            linux {
                iconFile.set(project.file("src/commonMain/composeResources/drawable/Hourglass.svg"))
            }

        }
    }
}

tasks {
    register("printVersion") {
        doLast {
            println(version)
        }
    }
}
