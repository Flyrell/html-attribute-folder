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