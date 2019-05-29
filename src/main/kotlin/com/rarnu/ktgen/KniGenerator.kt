package com.rarnu.ktgen

import java.io.File

val sourceTemplate = "{{target}}Main {\n" +
        "            dependencies {\n" +
        "                {{impl_default}}\n" +
        "                {{impl_serialization}}\n" +
        "                {{impl_ktorclient}}\n" +
        "                {{impl_coroutines}}\n" +
        "            }\n" +
        "        }"

val targetTemplate = "{{targetName}}(\"{{target}}\") {\n" +
        "        binaries {\n" +
        "            {{shared}}\n" +
        "            {{static}}\n" +
        "            {{framework}}\n" +
        "            {{executable}}\n" +
        "        }\n" +
        "    }"

fun generateKniProject(path: String, pkgName: String, projName: String, targets: List<Int>, callback: (Boolean) -> Unit) {

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

    val hasMaven = targets.contains(IDX_PLUGIN_MAVEN_PUBLISH)
    val hasSigning = targets.contains(IDX_PLUGIN_SIGNING)
    val hasSerialization = targets.contains(IDX_LIBRARY_SERIALIZATION)
    val hasKtorclient = targets.contains(IDX_LIBRARY_KTORCLIENT)
    val hasCoroutines = targets.contains(IDX_LIBRARY_COROUINES)
    val hasJs = targets.contains(IDX_JS_LIBRARY)
    val hasJvm = targets.contains(IDX_JVM_LIBRARY)
    val hasAndroidArm64Shared = targets.contains(IDX_ANDROID_ARM64_SHARED)
    val hasAndroidArm64Static = targets.contains(IDX_ANDROID_ARM64_STATIC)
    val hasAndroidArm64Executable = targets.contains(IDX_ANDROID_ARM64_EXECUTABLE)

    File("$basePath/settings.gradle").writeText(Resource.read("kni/settings.gradle.tmp").replaceTag("{{projectName}}") { projName })
    File("$basePath/gradle.properties").writeText(
        Resource.read("kni/gradle.properties.tmp")
            .replaceTag("{{serialization_version}}") {
                if (hasSerialization) "serialization_version=1.3.30" else ""
            }.replaceTag("{{block_maven_publish}}") {
                if (hasMaven) "nexusUploadUrl=\nnexusAccount=\nnexusPassword=" else ""
            }.replaceTag("{{block_signing}}") {
                if (hasSigning) "signing.keyId=\nsigning.password=\nsigning.secretKeyRingFile=" else ""
            }
    )
    File("$srcPath/commonMain/kotlin").mkdirs()
    File("$srcPath/commonMain/resources").mkdirs()
    File("$srcPath/commonMain/kotlin/Main.kt").writeText(Resource.read("kni/Main.kt.tmp"))

    var buildStr = Resource.read("kni/build.gradle.tmp")
        // build source
        .replaceTag("{{package}}") {
            pkgName
        }.replaceTag("{{classpath_serialization}}") {
            if (hasSerialization) "classpath \"org.jetbrains.kotlin:kotlin-serialization:\$serialization_version\"" else ""
        }.replaceTag("{{plugin_serialization}}") {
            if (hasSerialization) "apply plugin: 'kotlinx-serialization'" else ""
        }.replaceTag("{{maven_publish}}") {
            if (hasMaven) "apply plugin: 'maven-publish'" else ""
        }.replaceTag("{{block_publishing}}") {
            if (hasMaven) "publishing {\n" +
                    "    repositories {\n" +
                    "        maven {\n" +
                    "            url nexusUploadUrl\n" +
                    "            credentials {\n" +
                    "                username nexusAccount\n" +
                    "                password nexusPassword\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }\n" +
                    "}" else ""
        }.replaceTag("{{signing}}") {
            if (hasSigning) "apply plugin: 'signing'" else ""
        }.replaceTag("{{block_signing}}") {
            if (hasSigning) "signing {\n" +
                    "    sign configurations.archives\n" +
                    "}" else ""
        }
        // commonMain
        .replaceTag("{{impl_serialization}}") {
            if (hasSerialization) "implementation 'org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0'" else ""
        }.replaceTag("{{impl_ktorclient}}") {
            if (hasKtorclient) "implementation 'io.ktor:ktor-client-core:1.2.0'" else ""
        }.replaceTag("{{impl_coroutines}}") {
            if (hasCoroutines) "implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.2.1'" else ""
        }
        // js
        .replaceTag("{{target_js}}") {
            if (hasJs) "js() {\n" +
                    "        compilations.main.kotlinOptions {\n" +
                    "            moduleKind = \"umd\"\n" +
                    "            sourceMap = true\n" +
                    "        }\n" +
                    "    }" else ""
        }.replaceTag("{{source_js}}") {
            if (hasJs) sourceTemplate
                .replaceTag("{{target}}") {
                    "js"
                }.replaceTag("{{impl_default}}") {
                    "implementation kotlin('stdlib-js')"
                }.replaceTag("{{impl_serialization}}") {
                    if (hasSerialization) "implementation 'org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.11.0'" else ""
                }.replaceTag("{{impl_ktorclient}}") {
                    if (hasKtorclient) "implementation 'io.ktor:ktor-client-js:1.2.0'" else ""
                }.replaceTag("{{impl_coroutines}}") {
                    if (hasCoroutines) "implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.2.1'" else ""
                }
            else ""
        }.block {
            if (hasJs) {
                File("$srcPath/jsMain/kotlin").mkdirs()
                File("$srcPath/jsMain/resources").mkdirs()
                File("$srcPath/jsMain/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp")
                    .replaceTag("{{os_name}}") {
                        "JS"
                    }.replaceTag("{{block_special}}") {
                        ""
                    }.replaceTag("{{import}}") {
                        ""
                    })
            }
        }

    // TODO: convert
    // jvm
    buildStr = buildStr.replace(
        "{{target_jvm}}", if (hasJvm) "jvm() {\n" +
                "        compilations.main.kotlinOptions {\n" +
                "            jvmTarget = \"1.8\"\n" +
                "        }\n" +
                "    }" else ""
    )
    buildStr = buildStr.replace(
        "{{source_jvm}}", if (hasJvm)
            sourceTemplate.replace("{{target}}", "jvm")
                .replace("{{impl_default}}", "implementation kotlin('stdlib')")
                .replace("{{impl_serialization}}", if (hasSerialization) "implementation 'org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0'" else "")
                .replace("{{impl_ktorclient}}", if (hasKtorclient) "implementation 'io.ktor:ktor-client-apache:1.2.0'" else "")
                .replace("{{impl_coroutines}}", if (hasCoroutines) "implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.1'" else "") else ""
    )
    if (hasJvm) {
        File("$srcPath/jvmMain/kotlin").mkdirs()
        File("$srcPath/jvmMain/resources").mkdirs()
        File("$srcPath/jvmMain/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp").replace("{{os_name}}", "JVM").replace("{{block_special}}", "").replace("{{import}}", ""))
    }

    // android arm64

    if (hasAndroidArm64Shared || hasAndroidArm64Static || hasAndroidArm64Executable) {
        buildStr = buildStr.replace(
            "{{target_android_arm64}}", targetTemplate
                .replace("{{targetName}}", "androidNativeArm64")
                .replace("{{target}}", "android64")
                .replace("{{shared}}", if (hasAndroidArm64Shared) "sharedLib {}" else "")
                .replace("{{static}}", if (hasAndroidArm64Static) "staticLib {}" else "")
                .replace("{{executable}}", if (hasAndroidArm64Executable) "executable { entryPoint 'main' }" else "")
                .replace("{{framework}}", "")
        )

        buildStr = buildStr.replace(
            "{{source_android_arm64}}", if (hasJvm)
                sourceTemplate.replace("{{target}}", "android64")
                    .replace("{{impl_default}}", "")
                    .replace("{{impl_serialization}}", if (hasSerialization) "implementation 'org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0'" else "")
                    .replace("{{impl_ktorclient}}", if (hasKtorclient) "implementation 'io.ktor:ktor-client-android:1.2.0'" else "")
                    .replace("{{impl_coroutines}}", if (hasCoroutines) "implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.1'" else "") else ""
        )
        File("$srcPath/android64Main/kotlin").mkdirs()
        File("$srcPath/android64Main/resources").mkdirs()
        File("$srcPath/android64Main/kotlin/Actual.kt").writeText(
            Resource.read("kni/Actual.kt.tmp").replace("{{os_name}}", "JVM")
                .replace(
                    "{{block_special}}", "@CName(\"Java_${pkgName.replace(".", "_")}_HelloJni_hello\")\n" +
                            "fun jniHello(env: CPointer<JNIEnvVar>, thiz: jobject): jstring = memScoped {\n" +
                            "    return env.pointed.pointed!!.NewStringUTF!!.invoke(env, hello().cstr.ptr)!!\n" +
                            "}"
                )
                .replace(
                    "{{import}}", "import kotlinx.cinterop.*\n" +
                            "import platform.android.*"
                )
        )
    } else {
        buildStr = buildStr.replace("{{target_android_arm64}}", "")
        buildStr = buildStr.replace("{{source_android_arm64}}", "")
    }

    // android arm32
    val hasAndroidArm32Shared = targets.contains(IDX_ANDROID_ARM32_SHARED)
    val hasAndroidArm32Static = targets.contains(IDX_ANDROID_ARM32_STATIC)
    val hasAndroidArm32Executable = targets.contains(IDX_ANDROID_ARM32_EXECUTABLE)

    // ios arm64
    val hasIosArm64Framework = targets.contains(IDX_IOS_ARM64_FRAMEWORK)
    val hasIosArm64Static = targets.contains(IDX_IOS_ARM64_STATIC)

    // ios arm32
    val hasIosArm32Framework = targets.contains(IDX_IOS_ARM32_FRAMEWORK)
    val hasIosArm32Static = targets.contains(IDX_IOS_ARM32_STATIC)

    // ios emu
    val hasIosEmuFramework = targets.contains(IDX_IOS_EMU_FRAMEWORK)
    val hasIosEmuStatic = targets.contains(IDX_IOS_EMU_STATIC)

    // mac
    val hasMacExecutable = targets.contains(IDX_MACOS_EXECUTABLE)
    val hasMacShared = targets.contains(IDX_MACOS_SHARED)
    val hasMacStatic = targets.contains(IDX_MACOS_STATIC)
    val hasMacFramework = targets.contains(IDX_MACOS_FRAMEWORK)

    // linux

    // linux mips

    // linux mipsel

    // windows

    // raspberry pi

    // web assembly

    File("$basePath/build.gradle").writeText(buildStr)
    callback(true)
}