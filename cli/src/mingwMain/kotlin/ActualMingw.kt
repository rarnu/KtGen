import kotlinx.cinterop.*
import platform.posix.*
import platform.windows.*

actual val exePath: String get() {
    val hmodule = GetModuleHandleW(null)
    val wstr = nativeHeap.allocArray<UShortVar>(MAX_PATH)
    GetModuleFileNameW(hmodule, wstr, MAX_PATH)
    PathRemoveFileSpecW(wstr)
    return wstr.toKString()
}

private fun CArrayPointer<UShortVar>.toKString(): String = memScoped {
    val sz = WideCharToMultiByte(CP_UTF8, WC_ERR_INVALID_CHARS, this@toKString, -1, null, 0, null, null)
    val utf8 = allocArray<ByteVar>(sz)
    val r = WideCharToMultiByte(CP_UTF8, WC_ERR_INVALID_CHARS, this@toKString, -1, utf8, sz, null, null)
    if (r == 0) throw RuntimeException("Could not convert to UTF-8")
    utf8.toKString()
}

actual fun runCommand(cmd: String) = memScoped {
    var ret = ""
    var err = ""
    val size = 1024
    val buf = allocArray<ByteVar>(size)
    val fst = _popen(cmd, "r")
    if (fst != null) {
        while (fgets(buf, size, fst) != null) {
            ret += buf.toKString()
        }
    } else {
        err = strerror(errno)?.toKString() ?: ""
    }
    _pclose(fst)
    CommandResult(ret, err)
}