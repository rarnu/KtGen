package com.rarnu.ktgen

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
    File("$resourcesPath/static/js").mkdirs()

    File("$basePath/build.gradle").writeText(Resource.read("ktnode/build.gradle.tmp").replace("{{projectName}}", projName))
    File("$basePath/settings.gradle").writeText(Resource.read("ktnode/settings.gradle.tmp").replace("{{projectName}}", projName))
    File("$basePath/gradle.properties").writeText(Resource.read("ktnode/gradle.properties.tmp"))
    File("$basePath/package.json").writeText(Resource.read("ktnode/package.json.tmp").replace("{{projectName}}", projName))

    File("$resourcesPath/index.html").writeText(Resource.read("ktnode/index.html.tmp"))
    File("$resourcesPath/static/js/ktor.js").writeText(Resource.read("ktnode/ktor.js.tmp"))
    File("$resourcesPath/static/js/jquery-2.0.0.min.js").writeText(Resource.read("ktnode/jquery-2.0.0.min.js.tmp"))

    File("$srcPath/NodeKt.kt").writeText(Resource.read("ktnode/NodeKt.kt.tmp").replace("{{packageName}}", pkgName))
    File("$srcPath/Main.kt").writeText(Resource.read("ktnode/Main.kt.tmp").replace("{{packageName}}", pkgName))

    callback(true)
}