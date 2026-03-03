package dev.zbinski.htmlattributefolder

import com.intellij.psi.PsiElement

import com.intellij.openapi.util.TextRange
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.editor.FoldingGroup

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor

private data class AttributePrefix(val name: String, val doubleQuote: String, val singleQuote: String, val brace: String)

class AttributeFolder: FoldingBuilderEx(), DumbAware {
    private val settings = AttributeFolderState.instance

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val attributes = settings.attributes
        val prefixes = attributes.map { name ->
            AttributePrefix(name, "$name=\"", "$name='", "$name={")
        }
        val minPrefixLen = prefixes.minOfOrNull { minOf(it.doubleQuote.length, it.singleQuote.length, it.brace.length) } ?: return emptyArray()

        val descriptors = ArrayList<FoldingDescriptor>()
        for (item in getAttributes(Array(1) { root }, prefixes, minPrefixLen)) {
            var end: Int
            var start: Int

            if (settings.foldingMethod == 1) {
                end = item.attribute.textRange.endOffset
                start = item.attribute.textRange.startOffset
            } else {
                val text = item.attribute.text
                val base = item.attribute.textRange.startOffset

                // Find '=' then the first non-space char after it
                val eq = text.indexOf(settings.attributeSeparator)
                if (eq < 0) continue
                var i = eq + 1
                while (i < text.length && text[i].isWhitespace()) i++
                if (i >= text.length) continue

                when (text[i]) {
                    // name="value" or name='value'
                    '"', '\'' -> {
                        val quote = text[i]
                        val open = i
                        val close = text.indexOf(quote, startIndex = open + 1)
                        if (close < 0) continue

                        start = base + open + 1
                        end = base + close
                    }
                    // name={...} or name={{...}}
                    '{' -> {
                        val outerOpen = i
                        val outerClose = findMatchingBrace(text, outerOpen) ?: continue

                        val isDouble = outerOpen + 1 < text.length && text[outerOpen + 1] == '{'
                        if (isDouble) {
                            val innerOpen = outerOpen + 1
                            val innerClose = findMatchingBrace(text, innerOpen) ?: continue

                            // Fold inside INNER braces INCLUDING whitespace so output becomes: style={{__PLACEHOLDER__}}
                            start = base + innerOpen + 1
                            end = base + innerClose
                        } else {
                            // Fold inside single braces INCLUDING whitespace: name={__PLACEHOLDER__}
                            start = base + outerOpen + 1
                            end = base + outerClose
                        }
                    }
                    else -> continue
                }
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

        return descriptors.toTypedArray()
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

    private fun findMatchingBrace(text: String, openIndex: Int): Int? {
        if (openIndex !in text.indices || text[openIndex] != '{') return null
        var depth = 0
        var i = openIndex
        while (i < text.length) {
            when (text[i]) {
                '{' -> depth++
                '}' -> {
                    depth--
                    if (depth == 0) return i
                }
            }
            i++
        }
        return null
    }

    private fun getAttributes(
        elements: Array<PsiElement>,
        prefixes: List<AttributePrefix>,
        minPrefixLen: Int
    ): Sequence<Attribute> = sequence {
        for (child in elements) {
            if (child.textLength >= minPrefixLen) {
                val t = child.text
                for (prefix in prefixes) {
                    val startsLikeAttribute =
                        t.startsWith(prefix.doubleQuote) ||
                                t.startsWith(prefix.singleQuote) ||
                                t.startsWith(prefix.brace)

                    if (startsLikeAttribute) {
                        yield(object : Attribute {
                            override val attribute = child
                            override val attributeName = prefix.name
                        })
                    }
                }
            }

            yieldAll(getAttributes(child.children, prefixes, minPrefixLen))
        }
    }
}
