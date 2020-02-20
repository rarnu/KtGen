import kotlinx.cinterop.*
import platform.osx.PROC_PIDPATHINFO_MAXSIZE
import platform.osx.proc_pidpath
import platform.posix.*

@ExperimentalUnsignedTypes
actual val exePath: String
    get() = memScoped {
        val length = PROC_PIDPATHINFO_MAXSIZE
        val pathBuf = allocArray<ByteVar>(length)
        val myPid = getpid()
        val res = proc_pidpath(myPid, pathBuf, length.toUInt())
        if (res < 1) throw RuntimeException("proc_pidpath failed: $res")
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
