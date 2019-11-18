@file:Suppress("DuplicatedCode")

package com.rarnu.ktgen

import com.rarnu.common.Resource
import com.rarnu.common.replaceTag
import java.io.File

fun generateKtNodeProject(path: String, pkgName: String, projName: String, callback:(Boolean) -> Unit) {
    if (path == "" || pkgName == "" || projName == "") {
        callback(false)
        return
    }
    val basePath = "$path/$projName"
    if (File(basePath).exists()) {
        callback(false)
        return
    }

    val srcPath = "$basePath/src/main/kotlin"
    val resourcesPath = "$basePath/src/main/resources"
    File(srcPath).mkdirs()
    File(resourcesPath).mkdirs()

    File("$basePath/build.gradle").writeText(Resource.read("ktnode/build.gradle.tmp").replaceTag("{{projectName}}") { projName })
    File("$basePath/settings.gradle").writeText(Resource.read("ktnode/settings.gradle.tmp").replaceTag("{{projectName}}") { projName })
    File("$basePath/gradle.properties").writeText(Resource.read("ktnode/gradle.properties.tmp"))
    File("$basePath/package.json").writeText(Resource.read("ktnode/package.json.tmp").replaceTag("{{projectName}}") { projName })
    File("$resourcesPath/index.html").writeText(Resource.read("ktnode/index.html.tmp"))
    File("$srcPath/Main.kt").writeText(Resource.read("ktnode/Main.kt.tmp").replaceTag("{{packageName}}") { pkgName })
    callback(true)
}