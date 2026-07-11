pluginManagement {
    includeBuild("gradle/build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://www.jitpack.io")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("kei") {
            from(files("gradle/kei.versions.toml"))
        }
    }
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
        maven(url = "https://www.jitpack.io")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "NeoManga"

// ===== CORE MODULES =====
include(":core")
include(":compiler")

// ===== ACTIVE EXTENSION MODULES =====
// Only include modules that actually exist on disk
File(rootDir, "src").let { srcDir ->
    if (srcDir.exists()) {
        srcDir.eachDir { langDir ->
            langDir.eachDir { extDir ->
                val buildFile = File(extDir, "build.gradle.kts")
                if (buildFile.exists()) {
                    include("src:${langDir.name}:${extDir.name}")
                }
            }
        }
    }
}

// Sterile: explicitly do NOT include dead legacy modules like ":lib"
// Only lib-multisrc and lib dirs are included if they exist and have build files
File(rootDir, "lib-multisrc").let { dir ->
    if (dir.exists()) {
        dir.eachDir { include("lib-multisrc:${it.name}") }
    }
}

fun File.eachDir(block: (File) -> Unit) {
    val files = listFiles() ?: return
    for (file in files) {
        if (file.isDirectory && file.name != ".gradle" && file.name != "build") {
            block(file)
        }
    }
}
