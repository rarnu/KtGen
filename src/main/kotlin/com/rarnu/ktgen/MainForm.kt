package com.rarnu.ktgen

import com.rarnu.swingfx.showDirectoryDialog
import com.rarnu.swingfx.showFileDialog
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
    private val pnlKtorReact: JPanel
    private val pnlKni: JPanel

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

    // ktor-react
    private val txtKtorReactPackage: JTextField
    private val txtKtorReactName: JTextField
    private lateinit var txtKtorReactDest: JTextField
    private lateinit var btnKtorReactDest: JButton
    private val btnKtorReactGen: JButton

    // kni
    private val txtKniPackage: JTextField
    private val txtKniName: JTextField
    private lateinit var txtKniDest: JTextField
    private lateinit var btnKniDest: JButton
    private val btnKniGen: JButton

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
            btn.preferredSize = Dimension(200, 28)
            btn.addActionListener(this)
            parent.add(btn)
            return btn
        }

        contentPane.background = Color.white
        setSize(500, 210)
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)

        // ktor
        pnlKtor = JPanel(FlowLayout())
        pnlKtor.border = EmptyBorder(8, 8, 8, 8)

        txtKtorPackage = buildItem("Package Name", pnlKtor)
        txtKtorName = buildItem("Project Name", pnlKtor)
        buildDestItem("Destination", pnlKtor) { t, b ->
            txtKtorDest = t
            btnKtorDest = b
        }
        btnKtorGen = buildButton("Generate", pnlKtor)

        // ktnode
        pnlKtNode = JPanel(FlowLayout())
        pnlKtNode.border = EmptyBorder(8, 8, 8, 8)

        txtKtNodePackage = buildItem("Package Name", pnlKtNode)
        txtKtNodeName = buildItem("Project Name", pnlKtNode)
        buildDestItem("Destination", pnlKtNode) { t, b ->
            txtKtNodeDest = t
            btnKtNodeDest = b
        }
        btnKtNodeGen = buildButton("Generate", pnlKtNode)

        // ktor-react
        pnlKtorReact = JPanel(FlowLayout())
        pnlKtorReact.border = EmptyBorder(8, 8, 8, 8)

        txtKtorReactPackage = buildItem("Package Name", pnlKtorReact)
        txtKtorReactName = buildItem("Project Name", pnlKtorReact)
        buildDestItem("Destination", pnlKtorReact) { t, b ->
            txtKtorReactDest = t
            btnKtorReactDest = b
        }
        btnKtorReactGen = buildButton("Generate", pnlKtorReact)

        // kotlin/native
        pnlKni = JPanel(FlowLayout())
        pnlKni.border = EmptyBorder(8, 8, 8, 8)
        txtKniPackage = buildItem("Package Name", pnlKni)
        txtKniName = buildItem("Project Name", pnlKni)
        buildDestItem("Destination", pnlKni) { t, b ->
            txtKniDest = t
            btnKniDest = b
        }
        btnKniGen = buildButton("Select Target & Generate", pnlKni)

        val tab = JTabbedPane()
        tab.addTab("Ktor", pnlKtor)
        tab.addTab("Ktjs", pnlKtNode)
        tab.addTab("KtorReact", pnlKtorReact)
        tab.addTab("Native", pnlKni)
        tab.selectedIndex = 0

        contentPane = tab
        isVisible = true
    }

    override fun actionPerformed(e: ActionEvent) {
        when(e.source) {
            btnKtorDest -> showDirectoryDialog { txtKtorDest.text = it.absolutePath }
            btnKtNodeDest -> showDirectoryDialog { txtKtNodeDest.text = it.absolutePath }
            btnKtorReactDest -> showDirectoryDialog { txtKtorReactDest.text = it.absolutePath }
            btnKniDest -> showDirectoryDialog { txtKniDest.text = it.absolutePath }
            btnKtorGen -> generateKtorProject(txtKtorDest.text, txtKtorPackage.text, txtKtorName.text) { showGenerateResult(it) }
            btnKtNodeGen -> {
                btnKtNodeGen.isEnabled = false
                generateKtNodeProject(txtKtNodeDest.text, txtKtNodePackage.text, txtKtNodeName.text) {
                    showGenerateResult(it)
                    btnKtNodeGen.isEnabled = true
                }
            }
            btnKtorReactGen -> {
                btnKtorReactGen.isEnabled = false
                generateKtorReactProject(txtKtorReactDest.text, txtKtorReactPackage.text, txtKtorReactName.text) {
                    showGenerateResult(it)
                    btnKtorReactGen.isEnabled = true
                }
            }
            btnKniGen -> selectTarget(this) { t -> generateKniProject(txtKniDest.text, txtKniPackage.text, txtKniName.text, t) { showGenerateResult(it) } }
        }
    }

    private fun showGenerateResult(succ: Boolean) = JOptionPane.showMessageDialog(this, if (succ) "Project generated." else "Generate failed.")

}