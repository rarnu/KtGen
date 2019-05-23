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
        val mode = args[0]
        when(mode) {
            // ktor
            "1" -> generateKtorProject(args[3], args[1], args[2]) { println(if (it) "done!" else "error!") }
            // knode
            "2" -> generateKtNodeProject(args[3], args[1], args[2]) { println(if (it) "done!" else "error!") }
            // ktjs
            "3" -> generateKtJsProject(args[2], args[1]) { println(if (it) "done!" else "error!") }
            // ktor + react
            "4" -> generateKtorReactProject(args[3], args[1], args[2]) { println(if (it) "done!" else "error!") }
        }
    }
}