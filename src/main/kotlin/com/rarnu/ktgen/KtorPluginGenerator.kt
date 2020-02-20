@file:Suppress("DuplicatedCode")

package com.rarnu.ktgen

import com.rarnu.common.Resource
import com.rarnu.common.replaceTag
import com.rarnu.common.toTitleUpperCase
import java.io.File

fun generateKtorPluginProject(path: String, pkgName: String, projName: String, callback: (Boolean) -> Unit) {
    if (path == "" || pkgName == "" || projName == "") {
        callback(false)
        return
    }
    val basePath = "$path/$projName"
    if (File(basePath).exists()) {
        callback(false)
        return
    }

    val srcPath = "$basePath/src"
    File(srcPath).mkdirs()

    // gradle
    File("$basePath/build.gradle").writeText(Resource.read("ktorplugin/build.gradle.tmp")
        .replaceTag("{{projectName}}") {
            projName
        }
    )
    File("$basePath/gradle.properties").writeText(Resource.read("ktorplugin/gradle.properties.tmp"))
    File("$basePath/settings.gradle").writeText(Resource.read("ktorplugin/settings.gradle.tmp")
        .replaceTag("{{projectName}}") {
            projName
        }
    )

    // src
    File("$srcPath/Routing.kt").writeText(Resource.read("ktorplugin/Routing.kt.tmp")
        .replaceTag("{{packageName}}") {
            pkgName
        }.replaceTag("{{ProjectName}}") {
            projName.toTitleUpperCase()
        }.replaceTag("{{projectName}}") {
            projName
        }
    )

    callback(true)
}