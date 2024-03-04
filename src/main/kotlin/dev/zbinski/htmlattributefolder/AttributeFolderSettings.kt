package dev.zbinski.htmlattributefolder

import javax.swing.JComponent
import com.intellij.openapi.options.Configurable

class AttributeFolderSettings(): Configurable {
    private var settingsComponent: AttributeFolderComponent? = null

    override fun getDisplayName(): String {
        return "Attribute Folder Settings"
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return settingsComponent?.preferredFocusedComponent
    }

    override fun createComponent(): JComponent? {
        settingsComponent = AttributeFolderComponent()
        return settingsComponent?.panel
    }

    override fun isModified(): Boolean {
        val settings = AttributeFolderState.instance
        val attributes = getAttributes(settingsComponent?.attributes)

        return !compareLists(attributes, getAttributes(settings.attributes))
                || settingsComponent?.placeholder != settings.placeholder
                || settingsComponent?.foldingMethod != settings.foldingMethod
                || settingsComponent?.collapseByDefault != settings.collapseByDefault
    }

    override fun apply() {
        val settings: AttributeFolderState = AttributeFolderState.instance
        settings.placeholder = settingsComponent?.placeholder ?: ""
        settings.foldingMethod = settingsComponent?.foldingMethod ?: 0
        settings.collapseByDefault = settingsComponent?.collapseByDefault ?: true
        settings.attributes = ArrayList(getAttributes(settingsComponent?.attributes))
    }

    override fun reset() {
        val settings: AttributeFolderState = AttributeFolderState.instance
        settingsComponent?.placeholder = settings.placeholder
        settingsComponent?.foldingMethod = settings.foldingMethod
        settingsComponent?.collapseByDefault = settings.collapseByDefault
        settingsComponent?.attributes = settings.attributes.joinToString(",")
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    private fun compareLists(list1: List<String>, list2: List<String>): Boolean {
        return list1.size == list2.size && list1.toSet() == list2.toSet()
    }

    private fun getAttributes(rawAttributes: String? = null): List<String> {
        return rawAttributes?.split(',')?.map { it.trim() } ?: ArrayList()
    }

    private fun getAttributes(rawAttributes: List<String>? = null): List<String> {
        return rawAttributes?.flatMap { it.split(',').map { s -> s.trim() } } ?: ArrayList()
    }
}
