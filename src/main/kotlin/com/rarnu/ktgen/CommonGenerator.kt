@file:Suppress("DuplicatedCode")

package com.rarnu.ktgen

import com.rarnu.common.Resource
import com.rarnu.common.replaceTag
import java.io.File

fun generateCommonProject(path: String, pkgName: String, projName: String, callback: (Boolean) -> Unit) {
    if (path == "" || pkgName == "" || projName == "") {
        callback(false)
        return
    }
    val basePath = "$path/$projName"
    if (File(basePath).exists()) {
        callback(false)
        return
    }

    val srcPath = "$basePath/src/main"
    File(srcPath, "resources").mkdirs()
    File(srcPath, "kotlin/${pkgName.replace(".", "/")}").mkdirs()

    File("$basePath/build.gradle").writeText(Resource.read("common/build.gradle.tmp")
            .replaceTag("{{packageName}}") { pkgName })
    File("$basePath/gradle.properties").writeText(Resource.read("common/gradle.properties.tmp"))
    File("$basePath/settings.gradle").writeText(Resource.read("common/settings.gradle.tmp")
            .replaceTag("{{projectName}}") { projName })

    File("$srcPath/kotlin/${pkgName.replace(".", "/")}/Main.kt").writeText(Resource.read("common/Main.kt.tmp")
        .replaceTag("{{packageName}}") { pkgName })

    callback(true)
}