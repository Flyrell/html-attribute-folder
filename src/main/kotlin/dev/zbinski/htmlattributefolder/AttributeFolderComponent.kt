package dev.zbinski.htmlattributefolder

import javax.swing.JPanel
import javax.swing.JComponent
import com.intellij.util.ui.FormBuilder
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField

class AttributeFolderComponent {
    val panel: JPanel
    private val attributesInput = JBTextField()
    private val placeholderInput = JBTextField()
    private val collapseByDefaultInput = JBCheckBox()
    private val foldingMethodMenu = ComboBox(arrayOf(
        "Attribute Name + Placeholder",
        "Attribute Name"
    ))

    init {
        foldingMethodMenu.setMinimumAndPreferredWidth(250)
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(
                JBLabel("List of attributes to fold (separated by comma)"),
                attributesInput,
                1,
                false
            )
            .addLabeledComponent(
                JBLabel("Placeholder text"),
                placeholderInput,
                2,
                false
            )
            .addVerticalGap(10)
            .addLabeledComponent(
                JBLabel("Folding method"),
                foldingMethodMenu,
                3,
                false
            )
            .addLabeledComponent(
                JBLabel("Collapse by default"),
                collapseByDefaultInput,
                3,
                false
            )
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    val preferredFocusedComponent: JComponent
        get() = attributesInput

    var attributes: String
        get() = attributesInput.text
        set(newText) {
            attributesInput.text = newText
        }

    var placeholder: String
        get() = placeholderInput.text
        set(newText) {
            placeholderInput.text = newText
        }

    var collapseByDefault: Boolean
        get() = collapseByDefaultInput.isSelected
        set(newState) {
            collapseByDefaultInput.isSelected = newState
        }

    var foldingMethod: Int
        get() = foldingMethodMenu.selectedIndex
        set(newSelectedIndex) {
            foldingMethodMenu.selectedIndex = newSelectedIndex
        }
}