package dev.zbinski.htmlattributefolder

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.FoldRegion

class AttributeFolderAction : AnAction() {
    private val settings = AttributeFolderState.instance

    private fun getFoldRegionsForRelevantAttributes(foldRegions: Array<FoldRegion>): List<FoldRegion> {
        val relevantAttributes = settings.attributes

        // Identifying a fold based on its "groupName"/"debugName" is possible because all folds relevant
        // to us have a self-defined `FoldingGroup` including the "groupname"/"debugName".
        // (see ->AttributeFolder.buildFoldRegions:37)
        return foldRegions.filter {
            val group = it.group
            // The `group.toString()` returns the "groupName"/"debugName"
            group != null && relevantAttributes.contains(group.toString())
        }

    }

    private fun performFoldOperation(fold: FoldRegion) {
        fold.isExpanded = !settings.collapse
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val project = e.getData(CommonDataKeys.PROJECT)
        if (editor == null || project == null) {
            return
        }
        settings.collapse = !settings.collapse

        val foldingModel = editor.foldingModel
        val allFoldRegions = foldingModel.allFoldRegions
        ApplicationManager.getApplication().runWriteAction {
            foldingModel.runBatchFoldingOperation {
                getFoldRegionsForRelevantAttributes(allFoldRegions).forEach(::performFoldOperation)
            }
        }
    }
}
