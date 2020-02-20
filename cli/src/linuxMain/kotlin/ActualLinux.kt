import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.*

@ExperimentalUnsignedTypes
actual val exePath: String
    get() = memScoped {
        val length = PATH_MAX.toULong()
        val pathBuf = allocArray<ByteVar>(length.toInt())
        val myPid = getpid()
        val res = readlink("/proc/$myPid/exe", pathBuf, length)
        if (res < 1) throw RuntimeException("/proc/$myPid/exe failed: $res")
        pathBuf.toKString()
    }


actual fun runCommand(cmd: String) = memScoped {
    var ret = ""
    var err = ""
    val size = 1024
    val buf = allocArray<ByteVar>(size)
    val fst = popen(cmd, "r")
    if (fst != null) {
        while (fgets(buf, size, fst) != null) {
            ret += buf.toKString()
        }
    } else {
        err = strerror(errno)?.toKString() ?: ""
    }
    pclose(fst)
    CommandResult(ret, err)
}
