package com.rarnu.ktgen

import java.io.File

fun generateNativeProject(path: String, projName: String, callback:(Boolean) -> Unit) {
    if (path == "" || projName == "") {
        callback(false)
        return
    }
    val basePath = "$path/$projName"
    if (File(basePath).exists()) {
        callback(false)
        return
    }
    val srcPath = "$basePath/src"
    File("$srcPath/commonMain/kotlin").mkdirs()
    File("$srcPath/commonMain/resources").mkdirs()
    File("$srcPath/linuxMain/kotlin").mkdirs()
    File("$srcPath/linuxMain/resources").mkdirs()
    File("$srcPath/macosMain/kotlin").mkdirs()
    File("$srcPath/macosMain/resources").mkdirs()

    File("$basePath/build.gradle").writeText(Resource.read("native/build.gradle.tmp"))
    File("$basePath/settings.gradle").writeText(Resource.read("native/settings.gradle.tmp").replace("{{projectName}}", projName))
    File("$basePath/gradle.properties").writeText(Resource.read("native/gradle.properties.tmp"))

    File("$srcPath/commonMain/kotlin/Main.kt").writeText(Resource.read("native/Main.kt.tmp"))
    File("$srcPath/linuxMain/kotlin/ActualLinux.kt").writeText(Resource.read("native/ActualLinux.kt.tmp"))
    File("$srcPath/macosMain/kotlin/ActualMacos.kt").writeText(Resource.read("native/ActualMacos.kt.tmp"))

    callback(true)
}