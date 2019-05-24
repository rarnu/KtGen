import kotlinx.cinterop.*
import platform.windows.*
import platform.posix.*
import kotlin.math.max

actual val name: String get() = "Windows"

actual val exePath: String get() {
    val hmodule = GetModuleHandleW(null)
    val wstr: WSTR = nativeHeap.allocArray<UShortVar>(MAX_PATH)
    GetModuleFileNameW(hmodule, wstr, MAX_PATH)
    PathRemoveFileSpecW(wstr)
    return wstr.toKString()
}

private fun WSTR.toKString(): String = memScoped {
    val sz = WideCharToMultiByte(CP_UTF8, WC_ERR_INVALID_CHARS, this@toKString, -1, null, 0, null, null)
    val utf8 = allocArray<ByteVar>(sz)
    val r = WideCharToMultiByte(CP_UTF8, WC_ERR_INVALID_CHARS, this@toKString, -1, utf8, sz, null, null)
    if (r == 0) throw RuntimeException("Could not convert to UTF-8")
    utf8.toKString()
}

actual fun system(args: String) {
    platform.posix.system(args)
}