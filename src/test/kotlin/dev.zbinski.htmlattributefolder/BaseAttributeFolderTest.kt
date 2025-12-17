package dev.zbinski.htmlattributefolder

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldRegion
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Assume

abstract class BaseAttributeFolderTest : BasePlatformTestCase() {
    protected lateinit var file: PsiFile
    protected lateinit var document: Document

    protected fun setupDocument(fileName: String, text: String) {
        file = myFixture.configureByText(fileName, text)
        document = myFixture.getDocument(file)
    }

    protected fun configureAttributeFolder(collapseByDefault: Boolean, listOfAttributes: ArrayList<String> = arrayListOf("class", "className")) {
        val state = AttributeFolderState.instance
        state.attributes = listOfAttributes
        state.foldingMethod = 0
        state.collapseByDefault = collapseByDefault
        state.placeholder = "__PLACEHOLDER__"
    }

    protected fun applyPluginFoldingAndRender(): String {
        val builder = AttributeFolder()
        val descriptors = builder.buildFoldRegions(file, document, false)
        val editor = myFixture.editor

        editor.foldingModel.runBatchFoldingOperation {
            for (d in descriptors) {
                val region = editor.foldingModel.addFoldRegion(
                    d.range.startOffset,
                    d.range.endOffset,
                    builder.getPlaceholderText(d.element)
                )
                if (region != null) {
                    region.isExpanded = !builder.isCollapsedByDefault(d.element)
                }
            }
        }

        return renderVisualText(document.text, editor.foldingModel.allFoldRegions.toList())
    }

    protected fun renderVisualText(documentText: String, regions: List<FoldRegion>): String {
        val collapsed = regions.filter { !it.isExpanded }
            .sortedBy { it.startOffset }

        val sb = StringBuilder()
        var i = 0

        for (r in collapsed) {
            if (r.startOffset < i) continue
            sb.append(documentText.substring(i, r.startOffset))
            sb.append(r.placeholderText ?: "")
            i = r.endOffset
        }

        sb.append(documentText.substring(i))
        return sb.toString()
    }

    protected fun assertContains(actual: String, expectedSubstring: String, context: String = "") {
        kotlin.test.assertTrue(
            actual.contains(expectedSubstring),
            message = buildString {
                appendLine("Expected substring not found:")
                appendLine("  expected: $expectedSubstring")
                appendLine()
                appendLine("  actual:")
                appendLine(actual)
                if (context.isNotBlank()) {
                    appendLine()
                    appendLine("  context:")
                    appendLine(context)
                }
            }
        )
    }

    protected fun skipTestIfJSXIsNotSupported() {
        val languageId = file.language.id
        Assume.assumeTrue(
            "Skipping test: JSX/TSX not supported in this test runtime (languageId=$languageId)",
            languageId.contains("TypeScript", ignoreCase = true) && languageId.contains("JSX", ignoreCase = true)
        )
    }
}