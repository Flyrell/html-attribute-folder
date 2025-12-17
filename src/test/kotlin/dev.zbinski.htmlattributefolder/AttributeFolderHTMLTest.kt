package dev.zbinski.htmlattributefolder

private const val HTML_SNIPPET =
    """<div class="a b" className="c d" style="background-color: red;" data-foo="foo-data" />"""

class AttributeFolderHTMLTest : BaseAttributeFolderTest() {
    fun testFoldingHtmlAttributesCollapsed() {
        assertContains(document.text, """class="a b"""")
        assertContains(document.text, """className="c d"""")
        assertContains(document.text, """style="background-color: red;"""")
        assertContains(document.text, """data-foo="foo-data"""")

        configureAttributeFolder(collapseByDefault = true)

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """class="__PLACEHOLDER__"""")
        assertContains(visualText, """className="__PLACEHOLDER__"""")
        assertContains(visualText, """style="background-color: red;"""")
        assertContains(visualText, """data-foo="foo-data"""")
    }

    fun testFoldingHtmlAttributesUncollapsed() {
        assertContains(document.text, """class="a b"""")
        assertContains(document.text, """className="c d"""")
        assertContains(document.text, """style="background-color: red;"""")
        assertContains(document.text, """data-foo="foo-data"""")

        configureAttributeFolder(collapseByDefault = false)

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """class="a b"""")
        assertContains(visualText, """className="c d"""")
        assertContains(visualText, """style="background-color: red;"""")
        assertContains(visualText, """data-foo="foo-data"""")
    }

    override fun setUp() {
        super.setUp()
        setupDocument("test.html", HTML_SNIPPET)
    }
}
