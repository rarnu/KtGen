package com.rarnu.ktgen

import com.rarnu.common.Resource
import com.rarnu.common.replaceTag
import java.io.File

fun generateKtJsProject(path: String, projName: String, callback:(Boolean) -> Unit) {
    if (path == "" || projName == "") {
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
    File("$resourcesPath/lib").mkdirs()

    File("$basePath/build.gradle").writeText(Resource.read("ktjs/build.gradle.tmp").replaceTag("\"{{projectName}}\"") { projName })
    File("$basePath/settings.gradle").writeText(Resource.read("ktjs/settings.gradle.tmp").replaceTag("{{projectName}}") { projName })
    File("$basePath/gradle.properties").writeText(Resource.read("ktjs/gradle.properties.tmp"))

    File("$resourcesPath/lib/require.js").writeText(Resource.read("ktjs/require.js.tmp"))
    File("$resourcesPath/index.html").writeText(Resource.read("ktjs/index.html.tmp").replaceTag("{{projectName}}") { projName })
    File("$srcPath/Main.kt").writeText(Resource.read("ktjs/Main.kt.tmp"))

    File("$resourcesPath/lib/jquery-2.0.0.min.js").writeText(Resource.read("ktjs/jquery-2.0.0.min.js.tmp"))
    File("$srcPath/jquery.kt").writeText(Resource.read("ktjs/jquery.kt.tmp"))

    callback(true)
}