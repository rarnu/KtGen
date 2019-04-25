package com.rarnu.ktgen

import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.filechooser.FileSystemView


class MainForm : JFrame("KtGen"), ActionListener {


    private val pnlKtor: JPanel
    private val pnlKtNode: JPanel

    // ktor
    private val txtKtorPackage: JTextField
    private val txtKtorName: JTextField
    private lateinit var txtKtorDest: JTextField
    private lateinit var btnKtorDest: JButton
    private val btnKtorGen: JButton

    // ktnode
    private val txtKtNodePackage: JTextField
    private val txtKtNodeName: JTextField
    private lateinit var txtKtNodeDest: JTextField
    private lateinit var btnKtNodeDest: JButton
    private val btnKtNodeGen: JButton

    init {

        fun buildDestItem(label: String, parent: JPanel, callback: (JTextField, JButton) -> Unit) {
            val pnl = JPanel(BorderLayout())
            pnl.preferredSize = Dimension(450, 28)
            val lbl = JLabel(label)
            lbl.preferredSize = Dimension(100, 28)
            val txt = JTextField()
            txt.isEditable = false
            txt.background = Color.WHITE
            val btn = JButton("...")
            btn.addActionListener(this)
            btn.preferredSize = Dimension(32, 28)
            pnl.add(lbl, BorderLayout.WEST)
            pnl.add(btn, BorderLayout.EAST)
            pnl.add(txt, BorderLayout.CENTER)
            parent.add(pnl)
            callback(txt, btn)
        }

        fun buildItem(label: String, parent: JPanel): JTextField {
            val pnl = JPanel(BorderLayout())
            pnl.preferredSize = Dimension(450, 28)
            val lbl = JLabel(label)
            lbl.preferredSize = Dimension(100, 28)
            val txt = JTextField()
            pnl.add(lbl, BorderLayout.WEST)
            pnl.add(txt, BorderLayout.CENTER)
            parent.add(pnl)
            return txt
        }
        fun buildButton(txt: String, parent: JPanel): JButton {
            val btn = JButton(txt)
            btn.preferredSize = Dimension(100, 28)
            btn.addActionListener(this)
            parent.add(btn)
            return btn
        }

        contentPane.background = Color.white
        setSize(500, 210)
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)

        pnlKtor = JPanel(FlowLayout())
        pnlKtor.border = EmptyBorder(8, 8, 8, 8)

        txtKtorPackage = buildItem("Package Name", pnlKtor)
        txtKtorName = buildItem("Project Name", pnlKtor)
        buildDestItem("Destination", pnlKtor) { t, b ->
            txtKtorDest = t
            btnKtorDest = b
        }
        btnKtorGen = buildButton("Generate", pnlKtor)

        pnlKtNode = JPanel(FlowLayout())
        pnlKtNode.border = EmptyBorder(8, 8, 8, 8)

        txtKtNodePackage = buildItem("Package Name", pnlKtNode)
        txtKtNodeName = buildItem("Project Name", pnlKtNode)
        buildDestItem("Destination", pnlKtNode) { t, b ->
            txtKtNodeDest = t
            btnKtNodeDest = b
        }
        btnKtNodeGen = buildButton("Generate", pnlKtNode)

        val tab = JTabbedPane()
        tab.addTab("Ktor", pnlKtor)
        tab.addTab("KtNode", pnlKtNode)
        tab.selectedIndex = 0

        contentPane = tab
        isVisible = true
    }

    override fun actionPerformed(e: ActionEvent) {
        when(e.source) {
            btnKtorDest -> chooseDir { txtKtorDest.text = it.absolutePath }
            btnKtNodeDest -> chooseDir { txtKtNodeDest.text = it.absolutePath }
            btnKtorGen -> generateKtorProject(txtKtorDest.text, txtKtorPackage.text, txtKtorName.text) { JOptionPane.showMessageDialog(this, if (it) "Project generated." else "Generate failed.") }
            btnKtNodeGen -> generateKtNodeProject(txtKtNodeDest.text, txtKtNodePackage.text, txtKtNodeName.text) { JOptionPane.showMessageDialog(this, if (it) "Project generated." else "Generate failed.") }
        }
    }

    private fun chooseDir(callback: (File) -> Unit) {
        val chooser = JFileChooser()
        chooser.currentDirectory = FileSystemView.getFileSystemView().homeDirectory
        chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        chooser.showOpenDialog(this)
        val f = chooser.selectedFile
        callback(f)
    }

}