package com.rarnu.ktgen

import com.rarnu.common.*
import java.io.File


const val sourceTemplate = "{{target}}Main {\n" +
        "            dependencies {\n" +
        "                {{impl_default}}\n" +
        "                {{impl_serialization}}\n" +
        "                {{impl_ktorclient}}\n" +
        "                {{impl_coroutines}}\n" +
        "            }\n" +
        "        }"

const val targetTemplate = "{{targetName}}(\"{{target}}\") {\n" +
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

    // android
    val hasAndroidArm64Shared = targets.contains(IDX_ANDROID_ARM64_SHARED)
    val hasAndroidArm64Static = targets.contains(IDX_ANDROID_ARM64_STATIC)
    val hasAndroidArm64Executable = targets.contains(IDX_ANDROID_ARM64_EXECUTABLE)
    val hasAndroidArm32Shared = targets.contains(IDX_ANDROID_ARM32_SHARED)
    val hasAndroidArm32Static = targets.contains(IDX_ANDROID_ARM32_STATIC)
    val hasAndroidArm32Executable = targets.contains(IDX_ANDROID_ARM32_EXECUTABLE)

    // ios
    val hasIosArm64Framework = targets.contains(IDX_IOS_ARM64_FRAMEWORK)
    val hasIosArm64Static = targets.contains(IDX_IOS_ARM64_STATIC)
    val hasIosArm32Framework = targets.contains(IDX_IOS_ARM32_FRAMEWORK)
    val hasIosArm32Static = targets.contains(IDX_IOS_ARM32_STATIC)
    val hasIosEmuFramework = targets.contains(IDX_IOS_EMU_FRAMEWORK)
    val hasIosEmuStatic = targets.contains(IDX_IOS_EMU_STATIC)

    // macos
    val hasMacExecutable = targets.contains(IDX_MACOS_EXECUTABLE)
    val hasMacShared = targets.contains(IDX_MACOS_SHARED)
    val hasMacStatic = targets.contains(IDX_MACOS_STATIC)
    val hasMacFramework = targets.contains(IDX_MACOS_FRAMEWORK)

    // linux
    val hasLinuxExecutable = targets.contains(IDX_LINUX_EXECUTABLE)
    val hasLinuxShared = targets.contains(IDX_LINUX_SHARED)
    val hasLinuxStatic = targets.contains(IDX_LINUX_STATIC)
    val hasLinuxMipsExecutable = targets.contains(IDX_LINUX_MIPS_EXECUTABLE)
    val hasLinuxMipsShared = targets.contains(IDX_LINUX_MIPS_SHARED)
    val hasLinuxMipsStatic = targets.contains(IDX_LINUX_MIPS_STATIC)
    val hasLinuxMipselExecutable = targets.contains(IDX_LINUX_MIPSEL_EXECUTABLE)
    val hasLinuxMipselShared = targets.contains(IDX_LINUX_MIPSEL_SHARED)
    val hasLinuxMipselStatic = targets.contains(IDX_LINUX_MIPSEL_STATIC)
    val hasRaspBerryExecutable = targets.contains(IDX_RASPBERRYPI_EXECUTABLE)
    val hasRaspBerryShared = targets.contains(IDX_RASPBERRYPI_SHARED)
    val hasRaspBerryStatic = targets.contains(IDX_RASPBERRYPI_STATIC)

    // windows
    val hasWindowsExecutable = targets.contains(IDX_WINDOWS_EXECUTABLE)
    val hasWindowsShared = targets.contains(IDX_WINDOWS_SHARED)
    val hasWindowsStatic = targets.contains(IDX_WINDOWS_STATIC)

    // web assembly
    val hasWebAssemblyExecutable = targets.contains(IDX_WEBASSEMBLY_EXECUTABLE)


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

    val buildStr = Resource.read("kni/build.gradle.tmp")
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
            // work
            if (hasSerialization) "implementation 'org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0'" else ""
        }.replaceTag("{{impl_ktorclient}}") {
            // work
            if (hasKtorclient) "implementation 'io.ktor:ktor-client-core:1.2.0'" else ""
        }.replaceTag("{{impl_coroutines}}") {
            // work
            if (hasCoroutines) "implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.2.1'" else ""
        }
        // js
        .ifBlock(hasJs) {
            replaceTag("{{target_js}}") {
                "js() {\n" +
                        "        compilations.main.kotlinOptions {\n" +
                        "            moduleKind = \"umd\"\n" +
                        "            sourceMap = true\n" +
                        "        }\n" +
                        "    }"
            }.replaceTag("{{source_js}}") {
                sourceTemplate
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
            }.block {
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
        }.elseBlock {
            replaceTag("{{target_js}}") {
                ""
            }.replaceTag("{{source_js}}") {
                ""
            }
        }
        // jvm
        .ifBlock(hasJvm) {
            replaceTag("{{target_jvm}}") {
                "jvm() {\n" +
                        "        compilations.main.kotlinOptions {\n" +
                        "            jvmTarget = \"1.8\"\n" +
                        "        }\n" +
                        "    }"
            }.replaceTag("{{source_jvm}}") {
                sourceTemplate
                    .replaceTag("{{target}}") {
                        "jvm"
                    }.replaceTag("{{impl_default}}") {
                        "implementation kotlin('stdlib')"
                    }.replaceTag("{{impl_serialization}}") {
                        if (hasSerialization) "implementation 'org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0'" else ""
                    }.replaceTag("{{impl_ktorclient}}") {
                        if (hasKtorclient) "implementation 'io.ktor:ktor-client-apache:1.2.0'" else ""
                    }.replaceTag("{{impl_coroutines}}") {
                        if (hasCoroutines) "implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.1'" else ""
                    }
            }.block {
                File("$srcPath/jvmMain/kotlin").mkdirs()
                File("$srcPath/jvmMain/resources").mkdirs()
                File("$srcPath/jvmMain/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp")
                    .replaceTag("{{os_name}}") {
                        "JVM"
                    }.replaceTag("{{block_special}}") {
                        ""
                    }.replaceTag("{{import}}") {
                        ""
                    })
            }
        }.elseBlock {
            replaceTag("{{target_jvm}}") {
                ""
            }.replaceTag("{{source_jvm}}") {
                ""
            }
        }
        // android arm64
        .ifBlock(hasAndroidArm64Shared || hasAndroidArm64Static || hasAndroidArm64Executable) {
            replaceTag("{{target_android_arm64}}") {
                targetTemplate
                    .replaceTag("{{targetName}}") {
                        "androidNativeArm64"
                    }.replaceTag("{{target}}") {
                        "android64"
                    }.replaceTag("{{shared}}") {
                        if (hasAndroidArm64Shared) "sharedLib {}" else ""
                    }.replaceTag("{{static}}") {
                        if (hasAndroidArm64Static) "staticLib {}" else ""
                    }.replaceTag("{{executable}}") {
                        if (hasAndroidArm64Executable) "executable { entryPoint 'main' }" else ""
                    }.replaceTag("{{framework}}") {
                        ""
                    }
            }.replaceTag("{{source_android_arm64}}") {
                sourceTemplate
                    .replaceTag("{{target}}") {
                        "android64"
                    }.replaceTag("{{impl_default}}") {
                        ""
                    }.replaceTag("{{impl_serialization}}") {
                        ""
                    }.replaceTag("{{impl_ktorclient}}") {
                        ""
                    }.replaceTag("{{impl_coroutines}}") {
                        ""
                    }
            }.block {
                File("$srcPath/android64Main/kotlin").mkdirs()
                File("$srcPath/android64Main/resources").mkdirs()
                File("$srcPath/android64Main/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp")
                    .replaceTag("{{os_name}}") {
                        "Android Arm64"
                    }.replaceTag("{{block_special}}") {
                        "@CName(\"Java_${pkgName.replace(".", "_")}_HelloJni_hello\")\n" +
                                "fun jniHello(env: CPointer<JNIEnvVar>, thiz: jobject): jstring = memScoped {\n" +
                                "    return env.pointed.pointed!!.NewStringUTF!!.invoke(env, hello().cstr.ptr)!!\n" +
                                "}"
                    }.replaceTag("{{import}}") {
                        "import kotlinx.cinterop.*\n" +
                                "import platform.android.*"
                    }
                )
            }
        }.elseBlock {
            replaceTag("{{target_android_arm64}}") {
                ""
            }.replaceTag("{{source_android_arm64}}") {
                ""
            }
        }
        // android arm32
        .ifBlock(hasAndroidArm32Shared || hasAndroidArm32Static || hasAndroidArm32Executable) {
            replaceTag("{{target_android_arm32}}") {
                targetTemplate
                    .replaceTag("{{targetName}}") {
                        "androidNativeArm32"
                    }.replaceTag("{{target}}") {
                        "android32"
                    }.replaceTag("{{shared}}") {
                        if (hasAndroidArm32Shared) "sharedLib {}" else ""
                    }.replaceTag("{{static}}") {
                        if (hasAndroidArm32Static) "staticLib {}" else ""
                    }.replaceTag("{{executable}}") {
                        if (hasAndroidArm32Executable) "executable { entryPoint 'main' }" else ""
                    }.replaceTag("{{framework}}") {
                        ""
                    }
            }.replaceTag("{{source_android_arm32}}") {
                sourceTemplate
                    .replaceTag("{{target}}") {
                        "android32"
                    }.replaceTag("{{impl_default}}") {
                        ""
                    }.replaceTag("{{impl_serialization}}") {
                        ""
                    }.replaceTag("{{impl_ktorclient}}") {
                        ""
                    }.replaceTag("{{impl_coroutines}}") {
                        ""
                    }
            }.block {
                File("$srcPath/android32Main/kotlin").mkdirs()
                File("$srcPath/android32Main/resources").mkdirs()
                File("$srcPath/android32Main/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp")
                    .replaceTag("{{os_name}}") {
                        "Android Arm32"
                    }.replaceTag("{{block_special}}") {
                        "@CName(\"Java_${pkgName.replace(".", "_")}_HelloJni_hello\")\n" +
                                "fun jniHello(env: CPointer<JNIEnvVar>, thiz: jobject): jstring = memScoped {\n" +
                                "    return env.pointed.pointed!!.NewStringUTF!!.invoke(env, hello().cstr.ptr)!!\n" +
                                "}"
                    }.replaceTag("{{import}}") {
                        "import kotlinx.cinterop.*\n" +
                                "import platform.android.*"
                    }
                )
            }
        }.elseBlock {
            replaceTag("{{target_android_arm32}}") {
                ""
            }.replaceTag("{{source_android_arm32}}") {
                ""
            }
        }
        // ios arm64
        .ifBlock(hasIosArm64Framework || hasIosArm64Static) {
            replaceTag("{{target_ios_arm64}}") {
                targetTemplate
                    .replaceTag("{{targetName}}") {
                        "iosArm64"
                    }.replaceTag("{{target}}") {
                        "ios64"
                    }.replaceTag("{{shared}}") {
                        ""
                    }.replaceTag("{{static}}") {
                        if (hasIosArm64Static) "staticLib {}" else ""
                    }.replaceTag("{{executable}}") {
                        ""
                    }.replaceTag("{{framework}}") {
                        if (hasIosArm64Framework) "framework { embedBitcode 'bitcode' }" else ""
                    }
            }.replaceTag("{{source_ios_arm64}}") {
                sourceTemplate
                    .replaceTag("{{target}}") {
                        "ios64"
                    }.replaceTag("{{impl_default}}") {
                        ""
                    }.replaceTag("{{impl_serialization}}") {
                        if (hasSerialization) "implementation 'org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:0.11.0'" else ""
                    }.replaceTag("{{impl_ktorclient}}") {
                        if (hasKtorclient) "implementation 'io.ktor:ktor-client-ios:1.2.1'" else ""
                    }.replaceTag("{{impl_coroutines}}") {
                        if (hasCoroutines) "implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.2.1'" else ""
                    }
            }.block {
                File("$srcPath/ios64Main/kotlin").mkdirs()
                File("$srcPath/ios64Main/resources").mkdirs()
                File("$srcPath/ios64Main/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp")
                    .replaceTag("{{os_name}}") {
                        "iOS Arm64"
                    }.replaceTag("{{block_special}}") {
                        ""
                    }.replaceTag("{{import}}") {
                        ""
                    }
                )
            }
        }.elseBlock {
            replaceTag("{{target_ios_arm64}}") {
                ""
            }.replaceTag("{{source_ios_arm64}}") {
                ""
            }
        }
        // ios arm32
        .ifBlock(hasIosArm32Framework || hasIosArm32Static) {
            replaceTag("{{target_ios_arm32}}") {
                targetTemplate
                    .replaceTag("{{targetName}}") {
                        "iosArm32"
                    }.replaceTag("{{target}}") {
                        "ios32"
                    }.replaceTag("{{shared}}") {
                        ""
                    }.replaceTag("{{static}}") {
                        if (hasIosArm32Static) "staticLib {}" else ""
                    }.replaceTag("{{executable}}") {
                        ""
                    }.replaceTag("{{framework}}") {
                        if (hasIosArm32Framework) "framework { embedBitcode 'bitcode' }" else ""
                    }
            }.replaceTag("{{source_ios_arm32}}") {
                sourceTemplate
                    .replaceTag("{{target}}") {
                        "ios32"
                    }.replaceTag("{{impl_default}}") {
                        ""
                    }.replaceTag("{{impl_serialization}}") {
                        if (hasSerialization) "implementation 'org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:0.11.0'" else ""
                    }.replaceTag("{{impl_ktorclient}}") {
                        if (hasKtorclient) "implementation 'io.ktor:ktor-client-ios:1.2.1'" else ""
                    }.replaceTag("{{impl_coroutines}}") {
                        if (hasCoroutines) "implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.2.1'" else ""
                    }
            }.block {
                File("$srcPath/ios32Main/kotlin").mkdirs()
                File("$srcPath/ios32Main/resources").mkdirs()
                File("$srcPath/ios32Main/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp")
                    .replaceTag("{{os_name}}") {
                        "iOS Arm32"
                    }.replaceTag("{{block_special}}") {
                        ""
                    }.replaceTag("{{import}}") {
                        ""
                    }
                )
            }
        }.elseBlock {
            replaceTag("{{target_ios_arm32}}") {
                ""
            }.replaceTag("{{source_ios_arm32}}") {
                ""
            }
        }
        // ios emu
        .ifBlock(hasIosEmuFramework || hasIosEmuStatic) {
            replaceTag("{{target_ios_emu}}") {
                targetTemplate
                    .replaceTag("{{targetName}}") {
                        "iosX64"
                    }.replaceTag("{{target}}") {
                        "iosemu"
                    }.replaceTag("{{shared}}") {
                        ""
                    }.replaceTag("{{static}}") {
                        if (hasIosEmuStatic) "staticLib {}" else ""
                    }.replaceTag("{{executable}}") {
                        ""
                    }.replaceTag("{{framework}}") {
                        if (hasIosEmuFramework) "framework { embedBitcode 'bitcode' }" else ""
                    }
            }.replaceTag("{{source_ios_emu}}") {
                sourceTemplate
                    .replaceTag("{{target}}") {
                        "iosemu"
                    }.replaceTag("{{impl_default}}") {
                        ""
                    }.replaceTag("{{impl_serialization}}") {
                        if (hasSerialization) "implementation 'org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:0.11.0'" else ""
                    }.replaceTag("{{impl_ktorclient}}") {
                        if (hasKtorclient) "implementation 'io.ktor:ktor-client-ios:1.2.1'" else ""
                    }.replaceTag("{{impl_coroutines}}") {
                        if (hasCoroutines) "implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.2.1'" else ""
                    }
            }.block {
                File("$srcPath/iosemuMain/kotlin").mkdirs()
                File("$srcPath/iosemuMain/resources").mkdirs()
                File("$srcPath/iosemuMain/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp")
                    .replaceTag("{{os_name}}") {
                        "iOS Emulator"
                    }.replaceTag("{{block_special}}") {
                        ""
                    }.replaceTag("{{import}}") {
                        ""
                    }
                )
            }
        }.elseBlock {
            replaceTag("{{target_ios_emu}}") {
                ""
            }.replaceTag("{{source_ios_emu}}") {
                ""
            }
        }
        // macos
        .ifBlock(hasMacExecutable || hasMacShared || hasMacStatic || hasMacFramework) {
            replaceTag("{{target_macos}}") {
                targetTemplate
                    .replaceTag("{{targetName}}") {
                        "macosX64"
                    }.replaceTag("{{target}}") {
                        "macos"
                    }.replaceTag("{{shared}}") {
                        if (hasMacShared) "sharedLib { {{link}} }"
                            .replaceTag("{{link}}") {
                                if (hasKtorclient) "linkerOpts '-L/usr/lib -liconv'" else ""
                            } else ""
                    }.replaceTag("{{static}}") {
                        if (hasMacStatic) "staticLib { {{link}} }"
                            .replaceTag("{{link}}") {
                                if (hasKtorclient) "linkerOpts '-L/usr/lib -liconv'" else ""
                            } else ""
                    }.replaceTag("{{executable}}") {
                        if (hasMacExecutable) "executable {\n" +
                                "                entryPoint 'main'\n" +
                                "                {{link}}\n" +
                                "            }".replaceTag("{{link}}") {
                                    if (hasKtorclient) "linkerOpts '-L/usr/lib -liconv'" else ""
                                }
                        else ""
                    }.replaceTag("{{framework}}") {
                        if (hasMacFramework) "framework {\n" +
                                "                embedBitcode 'bitcode'\n" +
                                "                {{link}}\n" +
                                "            }".replaceTag("{{link}}") {
                                    if (hasKtorclient) "linkerOpts '-L/usr/lib -liconv'" else ""
                                }
                        else ""
                    }
            }.replaceTag("{{source_macos}}") {
                sourceTemplate
                    .replaceTag("{{target}}") {
                        "macos"
                    }.replaceTag("{{impl_default}}") {
                        ""
                    }.replaceTag("{{impl_serialization}}") {
                        if (hasSerialization) "implementation 'org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:0.11.0'" else ""
                    }.replaceTag("{{impl_ktorclient}}") {
                        if (hasKtorclient) "implementation 'io.ktor:ktor-client-curl:1.2.1'" else ""
                    }.replaceTag("{{impl_coroutines}}") {
                        if (hasCoroutines) "implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.2.1'" else ""
                    }
            }.block {
                File("$srcPath/macosMain/kotlin").mkdirs()
                File("$srcPath/macosMain/resources").mkdirs()
                File("$srcPath/macosMain/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp")
                    .replaceTag("{{os_name}}") {
                        "Mac X64"
                    }.replaceTag("{{block_special}}") {
                        ""
                    }.replaceTag("{{import}}") {
                        ""
                    }
                )
            }
        }.elseBlock {
            replaceTag("{{target_macos}}") {
                ""
            }.replaceTag("{{source_macos}}") {
                ""
            }
        }
        // linux
        .ifBlock(hasLinuxExecutable || hasLinuxShared || hasLinuxStatic) {
            replaceTag("{{target_linux}}") {
                targetTemplate
                    .replaceTag("{{targetName}}") {
                        "linuxX64"
                    }.replaceTag("{{target}}") {
                        "linux"
                    }.replaceTag("{{shared}}") {
                        if (hasLinuxShared) "sharedLib { }" else ""
                    }.replaceTag("{{static}}") {
                        if (hasLinuxStatic) "staticLib { }" else ""
                    }.replaceTag("{{executable}}") {
                        if (hasLinuxExecutable) "executable { entryPoint 'main' }" else ""
                    }.replaceTag("{{framework}}") {
                        ""
                    }
            }.replaceTag("{{source_linux}}") {
                sourceTemplate
                    .replaceTag("{{target}}") {
                        "linux"
                    }.replaceTag("{{impl_default}}") {
                        ""
                    }.replaceTag("{{impl_serialization}}") {
                        if (hasSerialization) "implementation 'org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:0.11.0'" else ""
                    }.replaceTag("{{impl_ktorclient}}") {
                        if (hasKtorclient) "implementation 'io.ktor:ktor-client-curl:1.2.1'" else ""
                    }.replaceTag("{{impl_coroutines}}") {
                        if (hasCoroutines) "implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.2.1'" else ""
                    }
            }.block {
                File("$srcPath/linuxMain/kotlin").mkdirs()
                File("$srcPath/linuxMain/resources").mkdirs()
                File("$srcPath/linuxMain/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp")
                    .replaceTag("{{os_name}}") {
                        "Linux X64"
                    }.replaceTag("{{block_special}}") {
                        ""
                    }.replaceTag("{{import}}") {
                        ""
                    }
                )
            }
        }.elseBlock {
            replaceTag("{{target_linux}}") {
                ""
            }.replaceTag("{{source_linux}}") {
                ""
            }
        }
        // linux mips
        .ifBlock(hasLinuxMipsExecutable || hasLinuxMipsShared || hasLinuxMipsStatic) {
            replaceTag("{{target_linux_mips}}") {
                targetTemplate
                    .replaceTag("{{targetName}}") {
                        "linuxMips32"
                    }.replaceTag("{{target}}") {
                        "mips"
                    }.replaceTag("{{shared}}") {
                        if (hasLinuxMipsShared) "sharedLib { }" else ""
                    }.replaceTag("{{static}}") {
                        if (hasLinuxMipsStatic) "staticLib { }" else ""
                    }.replaceTag("{{executable}}") {
                        if (hasLinuxMipsExecutable) "executable { entryPoint 'main' }" else ""
                    }.replaceTag("{{framework}}") {
                        ""
                    }
            }.replaceTag("{{source_linux_mips}}") {
                sourceTemplate
                    .replaceTag("{{target}}") {
                        "mips"
                    }.replaceTag("{{impl_default}}") {
                        ""
                    }.replaceTag("{{impl_serialization}}") {
                        ""
                    }.replaceTag("{{impl_ktorclient}}") {
                        ""
                    }.replaceTag("{{impl_coroutines}}") {
                        ""
                    }
            }.block {
                File("$srcPath/mipsMain/kotlin").mkdirs()
                File("$srcPath/mipsMain/resources").mkdirs()
                File("$srcPath/mipsMain/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp")
                    .replaceTag("{{os_name}}") {
                        "Linux Mips"
                    }.replaceTag("{{block_special}}") {
                        ""
                    }.replaceTag("{{import}}") {
                        ""
                    }
                )
            }
        }.elseBlock {
            replaceTag("{{target_linux_mips}}") {
                ""
            }.replaceTag("{{source_linux_mips}}") {
                ""
            }
        }
        // linux mipsel
        .ifBlock(hasLinuxMipselExecutable || hasLinuxMipselShared || hasLinuxMipselStatic) {
            replaceTag("{{target_linux_mipsel}}") {
                targetTemplate
                    .replaceTag("{{targetName}}") {
                        "linuxMipsel32"
                    }.replaceTag("{{target}}") {
                        "mipsel"
                    }.replaceTag("{{shared}}") {
                        if (hasLinuxMipselShared) "sharedLib { }" else ""
                    }.replaceTag("{{static}}") {
                        if (hasLinuxMipselStatic) "staticLib { }" else ""
                    }.replaceTag("{{executable}}") {
                        if (hasLinuxMipselExecutable) "executable { entryPoint 'main' }" else ""
                    }.replaceTag("{{framework}}") {
                        ""
                    }
            }.replaceTag("{{source_linux_mipsel}}") {
                sourceTemplate
                    .replaceTag("{{target}}") {
                        "mipsel"
                    }.replaceTag("{{impl_default}}") {
                        ""
                    }.replaceTag("{{impl_serialization}}") {
                        ""
                    }.replaceTag("{{impl_ktorclient}}") {
                        ""
                    }.replaceTag("{{impl_coroutines}}") {
                        ""
                    }
            }.block {
                File("$srcPath/mipselMain/kotlin").mkdirs()
                File("$srcPath/mipselMain/resources").mkdirs()
                File("$srcPath/mipselMain/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp")
                    .replaceTag("{{os_name}}") {
                        "Linux Mipsel"
                    }.replaceTag("{{block_special}}") {
                        ""
                    }.replaceTag("{{import}}") {
                        ""
                    }
                )
            }
        }.elseBlock {
            replaceTag("{{target_linux_mipsel}}") {
                ""
            }.replaceTag("{{source_linux_mipsel}}") {
                ""
            }
        }
        // raspberry pi
        .ifBlock(hasRaspBerryExecutable || hasRaspBerryShared || hasRaspBerryStatic) {
            replaceTag("{{target_raspberry}}") {
                targetTemplate
                    .replaceTag("{{targetName}}") {
                        "linuxArm32Hfp"
                    }.replaceTag("{{target}}") {
                        "raspberry"
                    }.replaceTag("{{shared}}") {
                        if (hasRaspBerryShared) "sharedLib { }" else ""
                    }.replaceTag("{{static}}") {
                        if (hasRaspBerryStatic) "staticLib { }" else ""
                    }.replaceTag("{{executable}}") {
                        if (hasRaspBerryExecutable) "executable { entryPoint 'main' }" else ""
                    }.replaceTag("{{framework}}") {
                        ""
                    }
            }.replaceTag("{{source_raspberry}}") {
                sourceTemplate
                    .replaceTag("{{target}}") {
                        "raspberry"
                    }.replaceTag("{{impl_default}}") {
                        ""
                    }.replaceTag("{{impl_serialization}}") {
                        ""
                    }.replaceTag("{{impl_ktorclient}}") {
                        ""
                    }.replaceTag("{{impl_coroutines}}") {
                        ""
                    }
            }.block {
                File("$srcPath/raspberryMain/kotlin").mkdirs()
                File("$srcPath/raspberryMain/resources").mkdirs()
                File("$srcPath/raspberryMain/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp")
                    .replaceTag("{{os_name}}") {
                        "Raspberry PI"
                    }.replaceTag("{{block_special}}") {
                        ""
                    }.replaceTag("{{import}}") {
                        ""
                    }
                )
            }
        }.elseBlock {
            replaceTag("{{target_raspberry}}") {
                ""
            }.replaceTag("{{source_raspberry}}") {
                ""
            }
        }
        // windows
        .ifBlock(hasWindowsExecutable || hasWindowsShared || hasWindowsStatic) {
            replaceTag("{{target_windows}}") {
                targetTemplate
                    .replaceTag("{{targetName}}") {
                        "mingwX64"
                    }.replaceTag("{{target}}") {
                        "mingw"
                    }.replaceTag("{{shared}}") {
                        if (hasWindowsShared) "sharedLib { }" else ""
                    }.replaceTag("{{static}}") {
                        if (hasWindowsStatic) "staticLib { }" else ""
                    }.replaceTag("{{executable}}") {
                        if (hasWindowsExecutable) "executable { entryPoint 'main' }" else ""
                    }.replaceTag("{{framework}}") {
                        ""
                    }
            }.replaceTag("{{source_windows}}") {
                sourceTemplate
                    .replaceTag("{{target}}") {
                        "mingw"
                    }.replaceTag("{{impl_default}}") {
                        ""
                    }.replaceTag("{{impl_serialization}}") {
                        if (hasSerialization) "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0" else ""
                    }.replaceTag("{{impl_ktorclient}}") {
                        if (hasKtorclient) "implementation 'io.ktor:ktor-client-curl:1.2.1'" else ""
                    }.replaceTag("{{impl_coroutines}}") {
                        if (hasCoroutines) "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.1" else ""
                    }
            }.block {
                File("$srcPath/mingwMain/kotlin").mkdirs()
                File("$srcPath/mingwMain/resources").mkdirs()
                File("$srcPath/mingwMain/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp")
                    .replaceTag("{{os_name}}") {
                        "Windows"
                    }.replaceTag("{{block_special}}") {
                        ""
                    }.replaceTag("{{import}}") {
                        ""
                    }
                )
            }
        }.elseBlock {
            replaceTag("{{target_windows}}") {
                ""
            }.replaceTag("{{source_windows}}") {
                ""
            }
        }
        // web assembly
        .ifBlock(hasWebAssemblyExecutable) {
            replaceTag("{{target_wasm32}}") {
                targetTemplate
                    .replaceTag("{{targetName}}") {
                        "wasm32"
                    }.replaceTag("{{target}}") {
                        "wasm32"
                    }.replaceTag("{{shared}}") {
                        ""
                    }.replaceTag("{{static}}") {
                        ""
                    }.replaceTag("{{executable}}") {
                        if (hasWebAssemblyExecutable) "executable { entryPoint 'main' }" else ""
                    }.replaceTag("{{framework}}") {
                        ""
                    }
            }.replaceTag("{{source_wasm32}}") {
                sourceTemplate
                    .replaceTag("{{target}}") {
                        "wasm32"
                    }.replaceTag("{{impl_default}}") {
                        ""
                    }.replaceTag("{{impl_serialization}}") {
                        ""
                    }.replaceTag("{{impl_ktorclient}}") {
                        ""
                    }.replaceTag("{{impl_coroutines}}") {
                        ""
                    }
            }.block {
                File("$srcPath/wasm32Main/kotlin").mkdirs()
                File("$srcPath/wasm32Main/resources").mkdirs()
                File("$srcPath/wasm32Main/kotlin/Actual.kt").writeText(Resource.read("kni/Actual.kt.tmp")
                    .replaceTag("{{os_name}}") {
                        "WASM32"
                    }.replaceTag("{{block_special}}") {
                        ""
                    }.replaceTag("{{import}}") {
                        ""
                    }
                )
            }
        }.elseBlock {
            replaceTag("{{target_wasm32}}") {
                ""
            }.replaceTag("{{source_wasm32}}") {
                ""
            }
        }.skipEmptyLine()

    File("$basePath/build.gradle").writeText(buildStr)
    callback(true)
}