import konclik.*

expect val name: String
expect val exePath: String
expect fun system(args: String)

private const val OPT_PACKAGE = "--package"
private const val OPT_NAME = "--name"
private const val OPT_OUTPUT = "--output"
private val paramPackage = Parameter.Option.Value(OPT_PACKAGE)
private val paramName = Parameter.Option.Value(OPT_NAME)
private val paramOutput = Parameter.Option.Value(OPT_OUTPUT)
private val listOpt = listOf(paramPackage, paramName, paramOutput)
private val listJsOpt = listOf(paramName, paramOutput)

private var PATH_JAR = ""

fun main(args: Array<String>) = konclikApp(args) {
    PATH_JAR = exePath.substringBeforeLast("/") + "/ktgen.jar"
    metadata {
        name = "KtGen CLI"
        description = "generate kotlin projects"
        version = "0.1.1"
    }
    newCommand("ktor", "Generate Ktor project", listOpt, 1)
    newCommand("ktnode", "Generate Kotlin/Nodejs project", listOpt, 2)
    newCommand("ktjs", "Generate Kotlin/Js project", listJsOpt, 3)
    newCommand("react", "Generate Ktor/React project", listOpt, 4)
    newCommand("native", "Generate Kotlin/Native project", listOpt, 5)
    newCommand("jni", "Generate Kotlin/JNI project", listOpt, 6)
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
    when (mode) {
        3 -> {
            if (nameOpt == "" || outOpt == "") {
                printError(cmd)
                return
            }
            callback(arrayOf(nameOpt, outOpt))
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
    system(cmd)
}

private fun printError(cmd: Command) = println("${cmd.name}: param error")