package com.rarnu.ktgen

import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.UIManager
import javax.swing.plaf.metal.MetalLookAndFeel

fun main(args: Array<String>) {

    if (args.isEmpty()) {
        UIManager.setLookAndFeel(MetalLookAndFeel())
        JFrame.setDefaultLookAndFeelDecorated(true)
        JDialog.setDefaultLookAndFeelDecorated(true)
        MainForm()
    } else {
        when(args[0]) {
            // ktor
            "1" -> generateKtorProject(args[3], args[1], args[2]) { printResult(it) }
            // knode
            "2" -> generateKtNodeProject(args[3], args[1], args[2]) { printResult(it) }
            // ktor + react
            "3" -> generateKtorReactProject(args[3], args[1], args[2]) { printResult(it) }
            // kotlin/native
            "4" -> generateKniProject(args[3], args[1], args[2], args[4].split(",").map { it.toInt() }) { printResult(it) }
            // android
            "5" -> generateAndroidProject(args[3], args[1], args[2]) { printResult(it) }
            // swing
            "6" -> generateSwingProject(args[3], args[1], args[2]) { printResult(it) }
            // common
            "7" -> generateCommonProject(args[3], args[1], args[2]) { printResult(it) }
        }
    }
}

private fun printResult(succ: Boolean) = println(if (succ) "done!" else "error!")