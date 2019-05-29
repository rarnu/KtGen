package com.rarnu.ktgen

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import javax.swing.*

const val IDX_JS_LIBRARY = 0
const val IDX_JVM_LIBRARY = 1
const val IDX_ANDROID_ARM64_SHARED = 2
const val IDX_ANDROID_ARM64_STATIC = 3
const val IDX_ANDROID_ARM64_EXECUTABLE = 4
const val IDX_ANDROID_ARM32_SHARED = 5
const val IDX_ANDROID_ARM32_STATIC = 6
const val IDX_ANDROID_ARM32_EXECUTABLE = 7
const val IDX_IOS_ARM64_FRAMEWORK = 8
const val IDX_IOS_ARM64_STATIC = 9
const val IDX_IOS_ARM32_FRAMEWORK = 10
const val IDX_IOS_ARM32_STATIC = 11
const val IDX_IOS_EMU_FRAMEWORK = 12
const val IDX_IOS_EMU_STATIC = 13
const val IDX_MACOS_EXECUTABLE = 14
const val IDX_MACOS_SHARED = 15
const val IDX_MACOS_STATIC = 16
const val IDX_MACOS_FRAMEWORK = 17
const val IDX_LINUX_EXECUTABLE = 18
const val IDX_LINUX_SHARED = 19
const val IDX_LINUX_STATIC = 20
const val IDX_LINUX_MIPS_EXECUTABLE = 21
const val IDX_LINUX_MIPS_SHARED = 22
const val IDX_LINUX_MIPS_STATIC = 23
const val IDX_LINUX_MIPSEL_EXECUTABLE = 24
const val IDX_LINUX_MIPSEL_SHARED = 25
const val IDX_LINUX_MIPSEL_STATIC = 26
const val IDX_WINDOWS_EXECUTABLE = 27
const val IDX_WINDOWS_SHARED = 28
const val IDX_WINDOWS_STATIC = 29
const val IDX_RASPBERRYPI_EXECUTABLE = 30
const val IDX_RASPBERRYPI_SHARED = 31
const val IDX_RASPBERRYPI_STATIC = 32
const val IDX_WEBASSEMBLY_EXECUTABLE = 33
const val IDX_LIBRARY_COROUINES = 34
const val IDX_LIBRARY_SERIALIZATION = 35
const val IDX_LIBRARY_KTORCLIENT = 36
const val IDX_PLUGIN_MAVEN_PUBLISH = 37
const val IDX_PLUGIN_SIGNING = 38

fun selectTarget(owner: JFrame, callback: (List<Int>) -> Unit) =
    callback(SelectTargetDialog(owner).selected)

class SelectTargetDialog(owner: JFrame) : JDialog(owner, true) {

    private val listData = arrayOf(
        /* 0  */ "target: JS Library",
        /* 1  */ "target: JVM Library",
        /* 2  */ "target: Android Arm64 Shared Library",
        /* 3  */ "target: Android Arm64 Static Library",
        /* 4  */ "target: Android Arm64 Executable",
        /* 5  */ "target: Android Arm32 Shared Library",
        /* 6  */ "target: Android Arm32 Static Library",
        /* 7  */ "target: Android Arm32 Executable",
        /* 8  */ "target: iOS Arm64 Framework",
        /* 9  */ "target: iOS Arm64 Static Library",
        /* 10 */ "target: iOS Arm32 Framework",
        /* 11 */ "target: iOS Arm32 Static Library",
        /* 12 */ "target: iOS Emu Framework",
        /* 13 */ "target: iOS Emu Static Library",
        /* 14 */ "target: Mac X64 Executable",
        /* 15 */ "target: Mac X64 Shared Library",
        /* 16 */ "target: Mac X64 Static Library",
        /* 17 */ "target: Mac X64 Framework",
        /* 18 */ "target: Linux X64 Executable",
        /* 19 */ "target: Linux X64 Shared Library",
        /* 20 */ "target: Linux X64 Static Library",
        /* 21 */ "target: Linux Mips32 Executable",
        /* 22 */ "target: Linux Mips32 Shared Library",
        /* 23 */ "target: Linux Mips32 Static Library",
        /* 24 */ "target: Linux Mipsel32 Executable",
        /* 25 */ "target: Linux Mipsel32 Shared Library",
        /* 26 */ "target: Linux Mipsel32 Static Library",
        /* 27 */ "target: Windows X64 Executable",
        /* 28 */ "target: Windows X64 Shared Library",
        /* 29 */ "target: Windows X64 Static Library",
        /* 30 */ "target: RaspberryPi Executable",
        /* 31 */ "target: RaspberryPi Shared Library",
        /* 32 */ "target: RaspberryPi Static Library",
        /* 33 */ "target: WebAssembly Executable",
        /* 34 */ "library: Coroutines",
        /* 35 */ "library: Serialization",
        /* 36 */ "library: Ktor-Client",
        /* 37 */ "plugin: Maven-Publish",
        /* 38 */ "plugin: Signing"
    )
    val selected = mutableListOf<Int>()

    init {
        layout = BorderLayout()
        contentPane.background = Color.white
        setSize(400, 300)
        isResizable = false
        defaultCloseOperation = DISPOSE_ON_CLOSE
        setLocationRelativeTo(null)

        val root = JPanel(BorderLayout())
        val list = JList<String>()
        val scroll = JScrollPane(list)
        root.add(scroll, BorderLayout.CENTER)
        val btn = JButton("Generate")
        root.add(btn, BorderLayout.SOUTH)
        contentPane = root

        list.setListData(listData)
        list.cellRenderer = TargetCell()
        list.selectionModel = object : DefaultListSelectionModel() {
            override fun setSelectionInterval(index0: Int, index1: Int) {
                if (super.isSelectedIndex(index0)) {
                    selected.remove(index0)
                    super.removeSelectionInterval(index0, index1)
                } else {
                    selected.add(index0)
                    super.addSelectionInterval(index0, index1)
                }
            }
        }

        btn.addActionListener { dispose() }
        isVisible = true
    }
}

class TargetCell : JCheckBox(), ListCellRenderer<String> {
    override fun getListCellRendererComponent(list: JList<out String>?, value: String?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        background = Color.WHITE
        text = value
        setSelected(isSelected)
        return this
    }
}