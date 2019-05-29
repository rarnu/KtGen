package com.rarnu.ktgen

fun String.proj() = substring(0, 1).toUpperCase() + substring(1)

fun String.superReplace(keys: Array<String>, conditions: Array<Boolean>, trueReplacement: Array<String>, falseReplacement: Array<String>): String {
    var ret = this
    for (i in keys.indices) {
        ret = ret.replace(keys[i], if (conditions[i]) trueReplacement[i] else falseReplacement[i])
    }
    return ret
}

fun String.superReplace(keys: Array<String>, replacement: Array<String>): String {
    var ret = this
    for (i in keys.indices) {
        ret = ret.replace(keys[i], replacement[i])
    }
    return ret
}

fun String.replaceTag(tag: String, block:() -> String) = replace(tag, block())

fun String.block(block:() -> Unit) = this