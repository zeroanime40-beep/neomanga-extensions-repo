import groovy.json.JsonSlurper
import groovy.json.JsonOutput

buildscript {
    dependencies {
        classpath(libs.kotlin.gradle)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}

tasks.register("createRepo") {
    group = "publishing"
    description = "Packages all extension APKs and generates repo metadata files in the 'repo/' directory"

    doLast {
        val repoDir = file("repo")
        val apkDir = repoDir.resolve("apk")

        repoDir.deleteRecursively()
        apkDir.mkdirs()

        val indexList = mutableListOf<Map<String, Any>>()

        subprojects.forEach { subproject ->
            val sourceInfoFile = subproject.layout.buildDirectory.file("keiyoushi-source-info.json").get().asFile
            if (sourceInfoFile.exists()) {
                val parser = JsonSlurper()
                val info = parser.parse(sourceInfoFile) as Map<String, Any>

                val pkg = info["packageName"] as String
                val name = info["name"] as String
                val versionCode = info["versionCode"] as Int
                val versionName = info["versionName"] as String
                val contentWarning = info["contentWarning"] as Int
                val sourcesList = info["sources"] as List<Map<String, Any>>

                val lang = pkg.split(".")[4]
                val applicationIdSuffix = pkg.substringAfter("eu.kanade.tachiyomi.extension.")
                val apkName = "tachiyomi-$applicationIdSuffix-v$versionName.apk"

                // Copy and rename APK
                val apkSourceDir = subproject.layout.buildDirectory.dir("outputs/apk/release").get().asFile
                val apkFiles = apkSourceDir.listFiles { f -> f.extension == "apk" }
                if (apkFiles != null && apkFiles.isNotEmpty()) {
                    val apkFile = apkFiles.first()
                    apkFile.copyTo(apkDir.resolve(apkName), overwrite = true)
                } else {
                    logger.warn("No release APK found for module ${subproject.name}")
                }

                val nsfw = if (contentWarning == 3) 1 else 0

                val mappedSources = sourcesList.map { source ->
                    mapOf(
                        "name" to source["name"],
                        "lang" to source["lang"],
                        "id" to source["id"].toString(),
                        "baseUrl" to source["baseUrl"]
                    )
                }

                val indexEntry = mapOf(
                    "name" to "Tachiyomi: $name",
                    "pkg" to pkg,
                    "apk" to apkName,
                    "lang" to lang,
                    "code" to versionCode,
                    "version" to versionName,
                    "nsfw" to nsfw,
                    "sources" to mappedSources
                )

                indexList.add(indexEntry)
            }
        }

        // Write index.min.json (minified JSON)
        val indexJson = JsonOutput.toJson(indexList)
        repoDir.resolve("index.min.json").writeText(indexJson)

        // Write repo.json (pretty printed metadata)
        val repoMeta = mapOf(
            "meta" to mapOf(
                "name" to "NEO MANGA Extensions",
                "website" to "https://github.com/zeroanime40-beep/neomanga-extensions-repo"
            )
        )
        repoDir.resolve("repo.json").writeText(JsonOutput.prettyPrint(JsonOutput.toJson(repoMeta)))
        logger.lifecycle("Successfully generated repository files in: ${repoDir.absolutePath}")
    }
}

// Register dependencies on subproject assembleRelease tasks after evaluation is completed
gradle.projectsEvaluated {
    tasks.named("createRepo") {
        dependsOn(
            subprojects
                .filter { it.plugins.hasPlugin("kei.plugins.extension") }
                .map { "${it.path}:assembleRelease" }
        )
    }
}

