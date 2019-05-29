package com.rarnu.ktgen

import com.rarnu.kt.common.swingMainThread
import java.io.File
import kotlin.concurrent.thread

fun generateKtorReactProject(path: String, pkgName: String, projName: String, callback:(Boolean) -> Unit) {
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
    File("$backPath/build.gradle").writeText(Resource.read("ktorreact/backend/build.gradle.tmp").replace("{{projectName}}", projName))
    File("$backPath/resources/application.conf").writeText(Resource.read("ktorreact/backend/application.conf.tmp").replace("{{packageName}}", pkgName))
    File("$backPath/resources/logback.xml").writeText(Resource.read("ktorreact/backend/logback.xml.tmp"))

    File("$backPath/src/Application.kt").writeText(Resource.read("ktorreact/backend/Application.kt.tmp").superReplace(
        arrayOf("{{packageName}}", "{{projectName}}", "{{ProjectName}}"), arrayOf(pkgName, projName, projName.proj())))
    File("$backPath/src/Routing.kt").writeText(Resource.read("ktorreact/backend/Routing.kt.tmp").superReplace(
        arrayOf("{{packageName}}", "{{ProjectName}}", "{{projectName}}"), arrayOf(pkgName, projName.proj(), projName)
    ))
    File("$backPath/src/Session.kt").writeText(Resource.read("ktorreact/backend/Session.kt.tmp").superReplace(
        arrayOf("{{packageName}}", "{{ProjectName}}"), arrayOf(pkgName, projName.proj())
    ))

    // frontend
    File("$frontPath/public").mkdirs()
    File("$frontPath/src/app").mkdirs()
    File("$frontPath/src/index").mkdirs()
    File("$frontPath/src/logo").mkdirs()
    File("$frontPath/src/ticker").mkdirs()
    File("$frontPath/build.gradle").writeText(Resource.read("ktorreact/frontend/build.gradle.tmp"))
    File("$frontPath/package.json").writeText(Resource.read("ktorreact/frontend/package.json.tmp").replace("{{projectName}}", projName))
    File("$frontPath/yarn.lock").writeText(Resource.read("ktorreact/frontend/yarn.lock.tmp"))
    File("$frontPath/public/index.html").writeText(Resource.read("ktorreact/frontend/index.html.tmp").replace("{{projectName}}", projName))
    File("$frontPath/public/jquery-2.0.0.min.js").writeText(Resource.read("ktorreact/frontend/jquery-2.0.0.min.js.tmp"))
    File("$frontPath/public/favicon.ico").writeText(Resource.read("ktorreact/frontend/favicon.ico.tmp"))
    File("$frontPath/public/manifest.json").writeText(Resource.read("ktorreact/frontend/manifest.json.tmp").replace("{{projectName}}", projName))
    File("$frontPath/src/app/App.css").writeText(Resource.read("ktorreact/frontend/App.css.tmp"))
    File("$frontPath/src/app/App.kt").writeText(Resource.read("ktorreact/frontend/App.kt.tmp").replace("{{projectName}}", projName))
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
    File("$basePath/settings.gradle").writeText(Resource.read("ktorreact/settings.gradle.tmp").replace("{{projectName}}", projName))

    // extract zip
    thread {
        Resource.extract("ktorreact/node_modules.zip", frontPath)
        swingMainThread {
            callback(true)
        }
    }
}