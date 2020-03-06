@file:Suppress("DuplicatedCode")

package com.rarnu.ktgen

import com.rarnu.common.Resource
import com.rarnu.common.replaceTag
import com.rarnu.common.toTitleUpperCase
import java.io.File

fun generateKtorReactProject(path: String, pkgName: String, projName: String, callback: (Boolean) -> Unit) {
    if (path == "" || pkgName == "" || projName == "") {
        callback(false)
        return
    }
    val basePath = "$path/$projName"
    if (File(basePath).exists()) {
        callback(false)
        return
    }
    val backPath = "$basePath/backend"
    val frontPath = "$basePath/frontend"
    File(backPath).mkdirs()
    File(frontPath).mkdirs()

    // backend
    File("$backPath/resources/web").mkdirs()
    File("$backPath/src").mkdirs()
    File("$backPath/build.gradle").writeText(Resource.read("ktorreact/backend/build.gradle.tmp")
        .replaceTag("{{projectName}}") {
            projName
        }
    )
    File("$backPath/resources/application.conf").writeText(Resource.read("ktorreact/backend/application.conf.tmp")
        .replaceTag("{{packageName}}") {
            pkgName
        }.replaceTag("{{ProjectName}}") {
            projName.toTitleUpperCase()
        }
    )
    File("$backPath/resources/logback.xml").writeText(Resource.read("ktorreact/backend/logback.xml.tmp"))

    File("$backPath/src/Application.kt").writeText(Resource.read("ktorreact/backend/Application.kt.tmp")
        .replaceTag("{{packageName}}") {
            pkgName
        }.replaceTag("{{projectName}}") {
            projName
        }.replaceTag("{{ProjectName}}") {
            projName.toTitleUpperCase()
        }
    )
    File("$backPath/src/Routing.kt").writeText(Resource.read("ktorreact/backend/Routing.kt.tmp")
        .replaceTag("{{packageName}}") {
            pkgName
        }.replaceTag("{{ProjectName}}") {
            projName.toTitleUpperCase()
        }.replaceTag("{{projectName}}") {
            projName
        }
    )
    File("$backPath/src/Session.kt").writeText(Resource.read("ktorreact/backend/Session.kt.tmp")
        .replaceTag("{{packageName}}") {
            pkgName
        }.replaceTag("{{ProjectName}}") {
            projName.toTitleUpperCase()
        }
    )

    // frontend
    File("$frontPath/public").mkdirs()
    File("$frontPath/src/app").mkdirs()
    File("$frontPath/src/index").mkdirs()
    File("$frontPath/src/logo").mkdirs()
    File("$frontPath/src/ticker").mkdirs()
    File("$frontPath/build.gradle").writeText(Resource.read("ktorreact/frontend/build.gradle.tmp"))
    File("$frontPath/package.json").writeText(Resource.read("ktorreact/frontend/package.json.tmp")
        .replaceTag("{{projectName}}") {
            projName
        }
    )
    File("$frontPath/yarn.lock").writeText(Resource.read("ktorreact/frontend/yarn.lock.tmp"))
    File("$frontPath/public/index.html").writeText(Resource.read("ktorreact/frontend/index.html.tmp")
        .replaceTag("{{projectName}}") {
            projName
        }
    )
    File("$frontPath/public/jquery-2.0.0.min.js").writeText(Resource.read("ktorreact/frontend/jquery-2.0.0.min.js.tmp"))
    File("$frontPath/public/favicon.ico").writeBytes(Resource.readBytes("ktorreact/frontend/favicon.ico.tmp"))
    File("$frontPath/public/manifest.json").writeText(Resource.read("ktorreact/frontend/manifest.json.tmp")
        .replaceTag("{{projectName}}") {
            projName
        }
    )
    File("$frontPath/src/app/App.css").writeText(Resource.read("ktorreact/frontend/App.css.tmp"))
    File("$frontPath/src/app/App.kt").writeText(Resource.read("ktorreact/frontend/App.kt.tmp")
        .replaceTag("{{projectName}}") {
            projName
        }
    )
    File("$frontPath/src/index/index.css").writeText(Resource.read("ktorreact/frontend/index.css.tmp"))
    File("$frontPath/src/index/index.kt").writeText(Resource.read("ktorreact/frontend/index.kt.tmp"))
    File("$frontPath/src/ticker/Ticker.kt").writeText(Resource.read("ktorreact/frontend/Ticker.kt.tmp"))
    File("$frontPath/src/logo/kotlin.svg").writeText(Resource.read("ktorreact/frontend/kotlin.svg.tmp"))
    File("$frontPath/src/logo/react.svg").writeText(Resource.read("ktorreact/frontend/react.svg.tmp"))
    File("$frontPath/src/logo/Logo.css").writeText(Resource.read("ktorreact/frontend/Logo.css.tmp"))
    File("$frontPath/src/logo/Logo.kt").writeText(Resource.read("ktorreact/frontend/Logo.kt.tmp"))

    // project
    File("$basePath/build.gradle").writeText(Resource.read("ktorreact/build.gradle.tmp"))
    File("$basePath/gradle.properties").writeText(Resource.read("ktorreact/gradle.properties.tmp"))
    File("$basePath/release.sh").writeText(Resource.read("ktorreact/release.sh.tmp"))
    File("$basePath/settings.gradle").writeText(Resource.read("ktorreact/settings.gradle.tmp")
        .replaceTag("{{projectName}}") {
            projName
        }
    )

    callback(true)
}