import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.PATH_MAX
import platform.posix.getpid
import platform.posix.readlink

actual val name: String get() = "Linux"

actual val exePath: String get() {
    memScoped {
        val length = PATH_MAX.toULong()
        val pathBuf = allocArray<ByteVar>(length.toInt())
        val myPid = getpid()
        val res = readlink("/proc/$myPid/exe", pathBuf, length)
        if (res < 1) throw RuntimeException("/proc/$myPid/exe failed: $res")
        return pathBuf.toKString()
    }
}

actual fun system(args: String) {
    platform.posix.system(args)
}