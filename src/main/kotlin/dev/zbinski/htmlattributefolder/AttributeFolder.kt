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
    private val settings = AttributeFolderState.instance

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> = runBlocking {
        val descriptors = ArrayList<FoldingDescriptor>()
        for (item in getAttributes(Array(1) { root })) {
            var end: Int
            var start: Int
            if (settings.foldingMethod == 1) {
                end = item.attribute.textRange.endOffset
                start = item.attribute.textRange.startOffset
            } else {
                val len = item.attributeName.length + settings.attributeSeparator.length + settings.attributeWrapper.length
                start = item.attribute.textRange.startOffset + len
                end = item.attribute.textRange.endOffset - settings.attributeWrapper.length
            }

            if (end > start) {
                descriptors.add(
                    FoldingDescriptor(
                        item.attribute.node,
                        TextRange(start, end),
                        FoldingGroup.newGroup(item.attributeName)
                    )
                )
            }
        }

        return@runBlocking descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String {
        if (settings.foldingMethod == 1) {
            for (attributeName in settings.attributes) {
                if (node.text.startsWith(attributeName))
                    return attributeName
            }
        }
        return settings.placeholder
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return settings.collapseByDefault
    }

    private infix fun getAttributes(elements: Array<PsiElement>): Sequence<Attribute> = sequence {
        for (child in elements) {
            for (attributeName in settings.attributes) {
                val attributeBeginning = attributeName + settings.attributeSeparator + settings.attributeWrapper
                if (child.text.startsWith(attributeBeginning)) {
                    yield(object : Attribute {
                        override val attribute = child
                        override val attributeName = attributeName
                    })
                }

                val items = getAttributes(child.children).iterator()
                while (items.hasNext()) {
                    yield(items.next())
                }
            }
        }
    }
}