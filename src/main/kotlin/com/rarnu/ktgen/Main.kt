package com.rarnu.ktgen

import com.rarnu.ktgen.MainForm
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.UIManager
import javax.swing.plaf.metal.MetalLookAndFeel

fun main() {
    UIManager.setLookAndFeel(MetalLookAndFeel())
    JFrame.setDefaultLookAndFeelDecorated(true)
    JDialog.setDefaultLookAndFeelDecorated(true)
    MainForm()
}