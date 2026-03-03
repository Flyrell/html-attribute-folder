package dev.zbinski.htmlattributefolder

private const val TSX_SNIPPET = """
    export const Component = () => (
      <div class="a b" className="c d" style={{ backgroundColor: "red", nested: { "foo": "bar" } }} data-foo="foo-data" />
    )
"""

class AttributeFolderTSXTest : BaseAttributeFolderTest() {
    fun testFoldingTSXAttributesCollapsed() {
        assertContains(document.text, """class="a b"""")
        assertContains(document.text, """className="c d"""")
        assertContains(document.text, """style={{ backgroundColor: "red", nested: { "foo": "bar" } }}""")
        assertContains(document.text, """data-foo="foo-data"""")

        configureAttributeFolder(collapseByDefault = true, arrayListOf("class", "className", "style"))

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """class="__PLACEHOLDER__"""")
        assertContains(visualText, """className="__PLACEHOLDER__"""")
        assertContains(visualText, """style={{__PLACEHOLDER__}}""")
        assertContains(visualText, """data-foo="foo-data"""")
    }

    fun testFoldingTSXAttributesUncollapsed() {
        assertContains(document.text, """class="a b"""")
        assertContains(document.text, """className="c d"""")
        assertContains(document.text, """style={{ backgroundColor: "red", nested: { "foo": "bar" } }}""")
        assertContains(document.text, """data-foo="foo-data"""")

        configureAttributeFolder(collapseByDefault = false, arrayListOf("class", "className", "style"))

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """class="a b"""")
        assertContains(visualText, """className="c d"""")
        assertContains(visualText, """style={{ backgroundColor: "red", nested: { "foo": "bar" } }}""")
        assertContains(visualText, """data-foo="foo-data"""")
    }

    override fun setUp() {
        super.setUp()
        setupDocument("test.tsx", TSX_SNIPPET)
        skipTestIfJSXIsNotSupported()
    }
}

/**
 * Tests for single-brace JSX attributes like onClick={handler}.
 */
class AttributeFolderTSXSingleBraceTest : BaseAttributeFolderTest() {
    fun testFoldingSingleBraceJSXAttribute() {
        setupDocument("singlebrace.tsx", """
            export const C = () => (
              <button onClick={handleClick} />
            )
        """)
        skipTestIfJSXIsNotSupported()

        configureAttributeFolder(collapseByDefault = true, arrayListOf("onClick"))

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """onClick={__PLACEHOLDER__}""")
    }

    fun testFoldingSingleBraceUncollapsed() {
        setupDocument("singlebrace_u.tsx", """
            export const C = () => (
              <button onClick={handleClick} />
            )
        """)
        skipTestIfJSXIsNotSupported()

        configureAttributeFolder(collapseByDefault = false, arrayListOf("onClick"))

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """onClick={handleClick}""")
    }
}

/**
 * Tests for deeply nested braces in JSX.
 */
class AttributeFolderTSXDeeplyNestedBracesTest : BaseAttributeFolderTest() {
    fun testFoldingDeeplyNestedBraces() {
        setupDocument("deep.tsx", """
            export const C = () => (
              <div style={{ a: { b: { c: 1 } } }} />
            )
        """)
        skipTestIfJSXIsNotSupported()

        configureAttributeFolder(collapseByDefault = true, arrayListOf("style"))

        val visualText = applyPluginFoldingAndRender()
        // Double braces: folds inside inner braces
        assertContains(visualText, """style={{__PLACEHOLDER__}}""")
    }
}

/**
 * Tests for mixed quote and brace attributes on the same element.
 */
class AttributeFolderTSXMixedAttributesTest : BaseAttributeFolderTest() {
    fun testFoldingMixedQuoteAndBraceAttributes() {
        setupDocument("mixed.tsx", """
            export const C = () => (
              <div className="my-class" style={{ color: "red" }} onClick={handler} />
            )
        """)
        skipTestIfJSXIsNotSupported()

        configureAttributeFolder(collapseByDefault = true, arrayListOf("className", "style", "onClick"))

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """className="__PLACEHOLDER__"""")
        assertContains(visualText, """style={{__PLACEHOLDER__}}""")
        assertContains(visualText, """onClick={__PLACEHOLDER__}""")
    }

    fun testMixedAttributesUncollapsed() {
        setupDocument("mixed_u.tsx", """
            export const C = () => (
              <div className="my-class" style={{ color: "red" }} onClick={handler} />
            )
        """)
        skipTestIfJSXIsNotSupported()

        configureAttributeFolder(collapseByDefault = false, arrayListOf("className", "style", "onClick"))

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """className="my-class"""")
        assertContains(visualText, """style={{ color: "red" }}""")
        assertContains(visualText, """onClick={handler}""")
    }
}

/**
 * Tests for deeply nested JSX element trees with foldable attributes at multiple levels.
 */
class AttributeFolderTSXDeeplyNestedElementsTest : BaseAttributeFolderTest() {
    fun testFoldingDeeplyNestedJSXElements() {
        setupDocument("deepnest.tsx", """
            export const C = () => (
              <div className="level-1">
                <section className="level-2" style={{ padding: "10px" }}>
                  <article className="level-3">
                    <span className="level-4" style={{ color: "blue" }}>text</span>
                  </article>
                </section>
              </div>
            )
        """)
        skipTestIfJSXIsNotSupported()

        configureAttributeFolder(collapseByDefault = true, arrayListOf("className", "style"))

        val visualText = applyPluginFoldingAndRender()
        // All className attributes at every nesting level should be folded
        val classCount = Regex("""className="__PLACEHOLDER__"""").findAll(visualText).count()
        kotlin.test.assertEquals(4, classCount, "Expected 4 folded className attributes across nesting levels, got $classCount in: $visualText")
        // Both style attributes should be folded
        val styleCount = Regex("""style=\{\{__PLACEHOLDER__\}\}""").findAll(visualText).count()
        kotlin.test.assertEquals(2, styleCount, "Expected 2 folded style attributes across nesting levels, got $styleCount in: $visualText")
    }

    fun testFoldingDeeplyNestedJSXElementsUncollapsed() {
        setupDocument("deepnest_u.tsx", """
            export const C = () => (
              <div className="level-1">
                <section className="level-2" style={{ padding: "10px" }}>
                  <article className="level-3">
                    <span className="level-4" style={{ color: "blue" }}>text</span>
                  </article>
                </section>
              </div>
            )
        """)
        skipTestIfJSXIsNotSupported()

        configureAttributeFolder(collapseByDefault = false, arrayListOf("className", "style"))

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """className="level-1"""")
        assertContains(visualText, """className="level-2"""")
        assertContains(visualText, """className="level-3"""")
        assertContains(visualText, """className="level-4"""")
        assertContains(visualText, """style={{ padding: "10px" }}""")
        assertContains(visualText, """style={{ color: "blue" }}""")
    }
}
