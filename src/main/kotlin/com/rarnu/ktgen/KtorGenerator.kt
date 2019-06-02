package com.rarnu.ktgen

import com.rarnu.common.Resource
import com.rarnu.common.replaceTag
import com.rarnu.common.toTitleUpperCase
import java.io.File

fun generateKtorProject(path: String, pkgName: String, projName: String, callback: (Boolean) -> Unit) {
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
    val resourcesPath = "$basePath/resources"
    File(srcPath).mkdirs()
    File(resourcesPath).mkdirs()
    File("$resourcesPath/static/js").mkdirs()
    File("$resourcesPath/web").mkdirs()

    // gradle
    File("$basePath/build.gradle").writeText(Resource.read("ktor/build.gradle.tmp")
        .replaceTag("{{projectName}}") {
            projName
        }
    )
    File("$basePath/gradle.properties").writeText(Resource.read("ktor/gradle.properties.tmp"))
    File("$basePath/settings.gradle").writeText(Resource.read("ktor/settings.gradle.tmp")
        .replaceTag("{{projectName}}") {
            projName
        }
    )

    // src
    File("$srcPath/Application.kt").writeText(Resource.read("ktor/Application.kt.tmp")
        .replaceTag("{{packageName}}") {
            pkgName
        }.replaceTag("{{projectName}}") {
            projName
        }.replaceTag("{{ProjectName}}") {
            projName.toTitleUpperCase()
        }
    )
    File("$srcPath/Session.kt").writeText(Resource.read("ktor/Session.kt.tmp")
        .replaceTag("{{packageName}}") {
            pkgName
        }.replaceTag("{{ProjectName}}") {
            projName.toTitleUpperCase()
        }
    )
    File("$srcPath/Routing.kt").writeText(Resource.read("ktor/Routing.kt.tmp")
        .replaceTag("{{packageName}}") {
            pkgName
        }.replaceTag("{{ProjectName}}") {
            projName.toTitleUpperCase()
        }.replaceTag("{{projectName}}") {
            projName
        }
    )

    // resource
    File("$resourcesPath/application.conf").writeText(Resource.read("ktor/application.conf.tmp")
        .replaceTag("{{packageName}}") {
            pkgName
        }
    )
    File("$resourcesPath/logback.xml").writeText(Resource.read("ktor/logback.xml.tmp"))
    File("$resourcesPath/web/index.html").writeText(Resource.read("ktor/index.html.tmp"))
    File("$resourcesPath/static/js/ktor.js").writeText(Resource.read("ktor/ktor.js.tmp"))

    callback(true)
}