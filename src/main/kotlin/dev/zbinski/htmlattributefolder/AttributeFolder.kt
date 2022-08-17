package dev.zbinski.htmlattributefolder

import com.intellij.psi.PsiElement
import kotlinx.coroutines.runBlocking

import com.intellij.openapi.util.TextRange
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.editor.FoldingGroup

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor

class AttributeFolder: FoldingBuilderEx(), DumbAware {
    companion object {
        private const val ATTRIBUTE_SEPARATOR = "="
        const val ATTRIBUTE_NAME = "data-cy"
        const val ATTRIBUTE_WRAPPER = "\""
        const val ATTRIBUTE_BEGINNING = ATTRIBUTE_NAME + ATTRIBUTE_SEPARATOR + ATTRIBUTE_WRAPPER
    }

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> = runBlocking {
        val descriptors = ArrayList<FoldingDescriptor>()
        for (attribute in getAttributes(Array(1) { root })) {
            val start = attribute.textRange.startOffset + ATTRIBUTE_BEGINNING.length
            val end = attribute.textRange.endOffset - ATTRIBUTE_WRAPPER.length
            if (end > start) {
                descriptors.add(FoldingDescriptor(attribute.node, TextRange(start, end), FoldingGroup.newGroup(ATTRIBUTE_NAME)))
            }
        }

        return@runBlocking descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String {
        return "..."
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return true
    }

    private infix fun getAttributes(elements: Array<PsiElement>): Sequence<PsiElement> = sequence {
        for (child in elements) {
            if (child.text.startsWith(ATTRIBUTE_BEGINNING)) {
                yield(child)
            }

            val items = getAttributes(child.children).iterator()
            while (items.hasNext()) {
                yield(items.next())
            }
        }
    }
}