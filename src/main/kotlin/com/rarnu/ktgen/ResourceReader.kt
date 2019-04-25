package com.rarnu.ktgen

object Resource {
    fun read(file: String): String {
        val ins = javaClass.getResourceAsStream("/$file")
        val size = ins.available()
        val buf = ByteArray(size)
        ins.read(buf)
        ins.close()
        return String(buf)
    }
}