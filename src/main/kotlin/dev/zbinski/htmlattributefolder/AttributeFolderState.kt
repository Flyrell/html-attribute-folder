package dev.zbinski.htmlattributefolder

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil


/**
 * Supports storing the application settings in a persistent way.
 * The [State] and [Storage] annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
        name = "dev.zbinski.htmlattributefolder.AttributeFolderState",
        storages = [Storage("html-attribute-folder.settings.xml")]
)
class AttributeFolderState : PersistentStateComponent<AttributeFolderState> {
    companion object {
        val instance: AttributeFolderState
            get() = ApplicationManager.getApplication().getService(AttributeFolderState::class.java)
    }

    val attributeWrapper = "\""
    val attributeSeparator = "="

    var foldingMethod = 0
    var placeholder = "..."
    var collapseByDefault = true
    var attributes = ArrayList<String>()
    var collapse = true

    override fun getState(): AttributeFolderState {
        return this
    }

    override fun loadState(state: AttributeFolderState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
