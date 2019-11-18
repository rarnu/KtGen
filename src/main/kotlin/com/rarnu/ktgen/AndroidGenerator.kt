@file:Suppress("DuplicatedCode")

package com.rarnu.ktgen

import com.rarnu.common.Resource
import com.rarnu.common.replaceTag
import java.io.File

fun generateAndroidProject(path: String, pkgName: String, projName: String, callback: (Boolean) -> Unit) {
    if (path == "" || pkgName == "" || projName == "") {
        callback(false)
        return
    }
    val basePath = "$path/$projName"
    if (File(basePath).exists()) {
        callback(false)
        return
    }

    val appPath = "$basePath/app"
    val srcPath = "$basePath/app/src/main"
    File(appPath).mkdirs()
    File(srcPath).mkdirs()
    File(srcPath, "res/drawable").mkdirs()
    File(srcPath, "res/layout").mkdirs()
    File(srcPath, "res/values").mkdirs()
    File(srcPath, "kotlin/${pkgName.replace(".", "/")}").mkdirs()

    File("$basePath/build.gradle").writeText(Resource.read("android/build.gradle.tmp"))
    File("$basePath/gradle.properties").writeText(Resource.read("android/gradle.properties.tmp"))
    File("$basePath/settings.gradle").writeText(Resource.read("android/gradle.properties.tmp"))
    File("$appPath/build.gradle").writeText(Resource.read("android/app.build.gradle.tmp"))
    File("$srcPath/AndroidManifest.xml").writeText(Resource.read("android/AndroidManifest.xml.tmp")
        .replaceTag("{{packageName}}") { pkgName })

    File("$srcPath/res/drawable/ic_launcher.png").writeText(Resource.read("android/ic_launcher.png.tmp"))
    File("$srcPath/res/layout/activity_main.xml").writeText(Resource.read("android/activity_main.xml.tmp"))
    File("$srcPath/res/values/strings.xml").writeText(Resource.read("android/strings.xml.tmp")
        .replaceTag("{{projectName}}") { projName })

    File("$srcPath/kotlin/${pkgName.replace(".", "/")}/MainActivity.kt").writeText(Resource.read("android/MainActivity.kt.tmp")
        .replaceTag("{{packageName}}") { pkgName })

    callback(true)
}