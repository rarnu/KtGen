import konclik.Command
import konclik.Parameter
import konclik.konclikApp
import platform.posix.system
import util.fullBinaryPath

private const val OPT_PACKAGE = "--package"
private const val OPT_NAME = "--name"
private const val OPT_OUTPUT = "--output"
private val PATH_JAR = fullBinaryPath.substringBeforeLast("/")

fun main(args: Array<String>) = konclikApp(args) {
    metadata {
        name = "KtGen CLI"
        description = "generate kotlin projects"
        version = "0.1.0"
    }
    command {
        metadata {
            name = "ktor"
            description = "Generate Ktor project"
        }
        parameters {
            options = listOf(
                Parameter.Option.Value(OPT_PACKAGE),
                Parameter.Option.Value(OPT_NAME),
                Parameter.Option.Value(OPT_OUTPUT)
            )
        }
        action { command, parameters ->
            val pkgOpt = parameters.options[OPT_PACKAGE]?.get(0) ?: ""
            val nameOpt = parameters.options[OPT_NAME]?.get(0) ?: ""
            val outOpt = parameters.options[OPT_OUTPUT]?.get(0) ?: ""
            if (pkgOpt == "" || nameOpt == "" || outOpt == "") {
                printError(command)
                return@action
            }
            val cmd = "java -jar $PATH_JAR/ktgen.jar 1 $pkgOpt $nameOpt \"$outOpt\""
            system(cmd)
        }
    }

    command {
        metadata {
            name = "ktnode"
            description = "Generate Kotlin/Nodejs project"
        }
        parameters {
            options = listOf(
                Parameter.Option.Value(OPT_PACKAGE),
                Parameter.Option.Value(OPT_NAME),
                Parameter.Option.Value(OPT_OUTPUT)
            )
        }
        action { command, parameters ->
            val pkgOpt = parameters.options[OPT_PACKAGE]?.get(0) ?: ""
            val nameOpt = parameters.options[OPT_NAME]?.get(0) ?: ""
            val outOpt = parameters.options[OPT_OUTPUT]?.get(0) ?: ""
            if (pkgOpt == "" || nameOpt == "" || outOpt == "") {
                printError(command)
                return@action
            }
            val cmd = "java -jar $PATH_JAR/ktgen.jar 2 $pkgOpt $nameOpt \"$outOpt\""
            system(cmd)
        }
    }

    command {
        metadata {
            name = "ktjs"
            description = "Generate Kotlin/Js project"
        }
        parameters {
            options = listOf(
                Parameter.Option.Value(OPT_NAME),
                Parameter.Option.Value(OPT_OUTPUT)
            )
        }
        action { command, parameters ->
            val nameOpt = parameters.options[OPT_NAME]?.get(0) ?: ""
            val outOpt = parameters.options[OPT_OUTPUT]?.get(0) ?: ""
            if (nameOpt == "" || outOpt == "") {
                printError(command)
                return@action
            }
            val cmd = "java -jar $PATH_JAR/ktgen.jar 3 $nameOpt \"$outOpt\""
            system(cmd)
        }
    }

    command {
        metadata {
            name = "react"
            description = "Generate Ktor/React project"
        }
        parameters {
            options = listOf(
                Parameter.Option.Value(OPT_PACKAGE),
                Parameter.Option.Value(OPT_NAME),
                Parameter.Option.Value(OPT_OUTPUT)
            )
        }
        action { command, parameters ->
            val pkgOpt = parameters.options[OPT_PACKAGE]?.get(0) ?: ""
            val nameOpt = parameters.options[OPT_NAME]?.get(0) ?: ""
            val outOpt = parameters.options[OPT_OUTPUT]?.get(0) ?: ""
            if (pkgOpt == "" || nameOpt == "" || outOpt == "") {
                printError(command)
                return@action
            }
            val cmd = "java -jar $PATH_JAR/ktgen.jar 4 $pkgOpt $nameOpt \"$outOpt\""
            system(cmd)
        }
    }
}

private fun printError(cmd: Command) = println("${cmd.name}: param error")