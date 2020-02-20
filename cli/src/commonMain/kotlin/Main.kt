import konclik.*

data class CommandResult(val output: String, val error: String)
expect fun runCommand(cmd: String): CommandResult
expect val exePath: String

private const val OPT_PACKAGE = "--package"
private const val OPT_NAME = "--name"
private const val OPT_OUTPUT = "--output"
private const val OPT_FEATURE = "--feature"
private val paramPackage = Parameter.Option.Value(OPT_PACKAGE)
private val paramName = Parameter.Option.Value(OPT_NAME)
private val paramOutput = Parameter.Option.Value(OPT_OUTPUT)
private val paramFeature = Parameter.Option.Value(OPT_FEATURE)
private val listOpt = listOf(paramPackage, paramName, paramOutput)
private val listNativeOpt = listOf(paramPackage, paramName, paramOutput, paramFeature)

private var PATH_JAR = ""

fun main(args: Array<String>) = konclikApp(args) {

    PATH_JAR = exePath.substringBeforeLast("/") + "/ktgen.jar"
    metadata {
        name = "KtGen CLI"
        description = "generate kotlin projects"
        version = "1.0.0"
    }
    newCommand("ktor", "Generate Ktor project", listOpt, 1)
    newCommand("ktnode", "Generate Kotlin/Nodejs project", listOpt, 2)
    newCommand("react", "Generate Ktor/React project", listOpt, 3)
    newCommand("native", "Generate Kotlin/Native project", listNativeOpt, 4)
    newCommand("android", "Generate Android project", listOpt, 5)
    newCommand("swing", "Generate Swing project", listOpt, 6)
    newCommand("common", "Generate Common project", listOpt, 7)
    newCommand("ktorplugin", "Generate Ktor-Plugin project", listOpt, 8)
}

private fun KonclikAppBuilder.newCommand(cmdName: String, cmdDesc: String, cmdOptions: List<Parameter.Option>, mode: Int) = command {
    metadata {
        name = cmdName
        description = cmdDesc
    }
    parameters { options = cmdOptions }
    action { command, parameters -> checkParam(command, mode, parameters) { executeGenerator(mode, it) } }
}


private fun checkParam(cmd: Command, mode: Int, param: ParseResult.Parameters, callback: (args: Array<String>) -> Unit) {
    val pkgOpt = param.options[OPT_PACKAGE]?.get(0) ?: ""
    val nameOpt = param.options[OPT_NAME]?.get(0) ?: ""
    val outOpt = param.options[OPT_OUTPUT]?.get(0) ?: ""
    val featureOpt = param.options[OPT_FEATURE]?.get(0) ?: ""
    when (mode) {
        4 -> {
            if (nameOpt == "" || outOpt == "" || pkgOpt == "" || featureOpt == "") {
                printError(cmd)
                return
            }
            callback(arrayOf(pkgOpt, nameOpt, outOpt, featureOpt))
        }
        else -> {
            if (pkgOpt == "" || nameOpt == "" || outOpt == "") {
                printError(cmd)
                return
            }
            callback(arrayOf(pkgOpt, nameOpt, outOpt))
        }
    }
}

private fun executeGenerator(mode: Int, args: Array<String>) {
    var cmd = "java -jar $PATH_JAR $mode "
    args.forEach { cmd += "\"$it\" " }
    val ret = runCommand(cmd)
    if (ret.error == "") {
        println(ret.output)
    } else {
        println("ERROR: ${ret.error}")
    }
}

private fun printError(cmd: Command) = println("${cmd.name}: param error")
