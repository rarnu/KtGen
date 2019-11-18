@file:Suppress("HasPlatformType", "unused")

package com.rarnu.ktgen

import java.awt.*
import javax.swing.*

inline fun panel(layout: LayoutManager, width: Int = 450, height: Int = 28, block: JPanel.() -> Unit) = JPanel(layout).apply { preferredSize = Dimension(width, height) }.apply(block)
inline fun label(text: String, width: Int = 100, height: Int = 28, block: JLabel.() -> Unit) = JLabel(text).apply { preferredSize = Dimension(width, height) }.apply(block)
inline fun textfield(block: JTextField.() -> Unit) = JTextField().apply(block)
inline fun button(text: String, width: Int = 200, height: Int = 28, block: JButton.() -> Unit) = JButton(text).apply { preferredSize = Dimension(width, height) }.apply(block)

fun <T: JComponent> Container.addAlso(comp: T, constraints: Any): T {
    add(comp, constraints)
    return comp
}

fun <T: JComponent> Container.addCenterAlso(comp: T): T {
    add(comp, BorderLayout.CENTER)
    return comp
}

fun Container.addWest(comp: Component) = add(comp, BorderLayout.WEST)
fun Container.addEast(comp: Component) = add(comp, BorderLayout.EAST)
fun Container.addCenter(comp: Component) = add(comp, BorderLayout.CENTER)
fun JTabbedPane.addPanel(title: String, panel: JPanel): JTabbedPane {
    addTab(title, panel)
    return this
}
fun JTabbedPane.setIndex(idx: Int): JTabbedPane {
    selectedIndex = idx
    return this
}