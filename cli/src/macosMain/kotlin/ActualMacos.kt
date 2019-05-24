import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.osx.PROC_PIDPATHINFO_MAXSIZE
import platform.osx.proc_pidpath
import platform.posix.getpid

actual val name: String get() = "Mac"

actual val exePath: String get() {
    memScoped {
        val length = PROC_PIDPATHINFO_MAXSIZE
        val pathBuf = allocArray<ByteVar>(length)
        val myPid = getpid()
        val res = proc_pidpath(myPid, pathBuf, length.toUInt())
        if (res < 1) throw RuntimeException("proc_pidpath failed: $res")
        return pathBuf.toKString()
    }
}

actual fun system(args: String) {
    platform.posix.system(args)
}