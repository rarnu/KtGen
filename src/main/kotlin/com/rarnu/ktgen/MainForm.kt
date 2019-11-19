package com.rarnu.ktgen

import com.rarnu.swingfx.showDirectoryDialog
import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JTabbedPane
import javax.swing.JTextField
import javax.swing.border.EmptyBorder

class MainForm : JFrame("KtGen") {

    init {
        contentPane.background = Color.white
        setSize(550, 210)
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)

        contentPane = JTabbedPane()
            .addPanel("Ktor", buildTab(0))
            .addPanel("Ktjs", buildTab(1))
            .addPanel("KtorReact", buildTab(2))
            .addPanel("Native", buildTab(3))
            .addPanel("Android", buildTab(4))
            .addPanel("Swing", buildTab(5))
            .addPanel("iOS", buildTab(6))
            .addPanel("Common", buildTab(7))
            .setIndex(0)

        isVisible = true
    }

    private fun buildTab(type: Int) = panel(FlowLayout()) {
        var txtPackageName: JTextField? = null
        var txtProjectName: JTextField? = null
        var txtDestination: JTextField? = null
        border = EmptyBorder(8, 8, 8, 8)
        add(panel(BorderLayout()) {
            addWest(label("Package Name") { })
            txtPackageName = addCenterAlso(textfield {  })
        })
        add(panel(BorderLayout()) {
            addWest(label("Project Name") { })
            txtProjectName = addCenterAlso(textfield {  })
        })
        add(panel(BorderLayout()) {
            addWest(label("Destination") {  })
            addEast(button("...", 32, 28) { addActionListener { showDirectoryDialog { txtDestination?.text = it.absolutePath } } })
            txtDestination = addCenterAlso(textfield { isEditable = false; background = Color.WHITE })
        })
        add(button("Generate") {
            addActionListener {
                this.isEnabled = false
                doGenerate(type, txtPackageName!!.text, txtProjectName!!.text, txtDestination!!.text) {
                    showGenerateResult(it)
                    this.isEnabled = true
                }
            }
        })
    }

    private fun doGenerate(type: Int, pkg: String, proj: String, dest: String, callback:(succ: Boolean) -> Unit) {
        when(type) {
            0 -> generateKtorProject(dest, pkg, proj) { callback(it) }
            1 -> generateKtNodeProject(dest, pkg, proj) { callback(it) }
            2 -> generateKtorReactProject(dest, pkg, proj) { callback(it) }
            3 -> selectTarget(this) { t -> generateKniProject(dest, pkg, proj, t) { callback(it) } }
            4 -> generateAndroidProject(dest, pkg, proj) { callback(it) }
            5 -> generateSwingProject(dest, pkg, proj) { callback(it) }
            6 -> {
                // ios
            }
            7 -> {
                // common
            }
        }
    }

    private fun showGenerateResult(succ: Boolean) = JOptionPane.showMessageDialog(this, if (succ) "Project generated." else "Generate failed.")

}