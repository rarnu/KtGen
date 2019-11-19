@file:Suppress("DuplicatedCode")

package com.rarnu.ktgen

import com.rarnu.common.Resource
import com.rarnu.common.replaceTag
import java.io.File

fun generateSwingProject(path: String, pkgName: String, projName: String, callback: (Boolean) -> Unit) {
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

    File("$basePath/build.gradle").writeText(Resource.read("swing/build.gradle.tmp")
        .replaceTag("{{packageName}}") { pkgName })
    File("$basePath/gradle.properties").writeText(Resource.read("swing/gradle.properties.tmp"))
    File("$basePath/settings.gradle").writeText(Resource.read("swing/settings.gradle.tmp")
        .replaceTag("{{projectName}}") { projName })

    File("$srcPath/kotlin/${pkgName.replace(".", "/")}/Main.kt").writeText(Resource.read("swing/Main.kt.tmp")
        .replaceTag("{{packageName}}") { pkgName})
    File("$srcPath/kotlin/${pkgName.replace(".", "/")}/MainForm.kt.tmp").writeText(Resource.read("swing/MainForm.kt.tmp")
        .replaceTag("{{packageName}}") { pkgName} .replaceTag("{{projectName}}") { projName })

    callback(true)
}