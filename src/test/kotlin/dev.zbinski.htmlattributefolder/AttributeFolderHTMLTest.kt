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

/**
 * Tests for folding method 1 (name-only placeholder).
 */
class AttributeFolderHTMLMethod1Test : BaseAttributeFolderTest() {
    fun testFoldingMethod1ReplacesEntireAttribute() {
        setupDocument("method1.html", """<div class="a b" id="main" />""")
        val state = AttributeFolderState.instance
        state.attributes = arrayListOf("class")
        state.foldingMethod = 1
        state.collapseByDefault = true
        state.placeholder = "__PLACEHOLDER__"

        val visualText = applyPluginFoldingAndRender()
        // Method 1 folds the entire attribute and uses the attribute name as placeholder
        assertContains(visualText, "class")
        // The value "a b" should be hidden
        kotlin.test.assertFalse(
            visualText.contains("""class="a b""""),
            "Expected the full attribute to be folded, but found it in: $visualText"
        )
        // id should remain untouched
        assertContains(visualText, """id="main"""")
    }

    fun testFoldingMethod1UncollapsedShowsFullAttribute() {
        setupDocument("method1u.html", """<div class="a b" />""")
        val state = AttributeFolderState.instance
        state.attributes = arrayListOf("class")
        state.foldingMethod = 1
        state.collapseByDefault = false
        state.placeholder = "__PLACEHOLDER__"

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """class="a b"""")
    }
}

/**
 * Tests for single-quoted attribute values.
 */
class AttributeFolderHTMLSingleQuoteTest : BaseAttributeFolderTest() {
    fun testFoldingSingleQuotedAttributes() {
        setupDocument("sq.html", """<div class='foo bar' />""")
        configureAttributeFolder(collapseByDefault = true)

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """class='__PLACEHOLDER__'""")
    }

    fun testFoldingSingleQuotedUncollapsed() {
        setupDocument("squ.html", """<div class='foo bar' />""")
        configureAttributeFolder(collapseByDefault = false)

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """class='foo bar'""")
    }
}

/**
 * Tests for custom attribute lists (e.g. data-foo).
 */
class AttributeFolderHTMLCustomAttributeTest : BaseAttributeFolderTest() {
    fun testFoldingCustomAttributeInList() {
        setupDocument("custom.html", """<div data-foo="bar baz" class="x" />""")
        configureAttributeFolder(collapseByDefault = true, arrayListOf("data-foo"))

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """data-foo="__PLACEHOLDER__"""")
        // class is NOT in the fold list, so it should remain
        assertContains(visualText, """class="x"""")
    }
}

/**
 * Tests for attributes without values (e.g. disabled).
 */
class AttributeFolderHTMLNoValueAttributeTest : BaseAttributeFolderTest() {
    fun testAttributeWithoutValueIsSkipped() {
        setupDocument("noval.html", """<input disabled class="btn" />""")
        configureAttributeFolder(collapseByDefault = true)

        val visualText = applyPluginFoldingAndRender()
        // disabled has no =, so it should remain as-is
        assertContains(visualText, "disabled")
        // class should be folded
        assertContains(visualText, """class="__PLACEHOLDER__"""")
    }
}

/**
 * Tests for multiple sibling elements.
 */
class AttributeFolderHTMLMultipleElementsTest : BaseAttributeFolderTest() {
    fun testFoldingMultipleSiblingElements() {
        setupDocument("multi.html", """<div class="a"><span class="b">text</span></div><div class="c" />""")
        configureAttributeFolder(collapseByDefault = true)

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """class="__PLACEHOLDER__""", "first div class")
        // Check that all three class attributes are folded
        val count = Regex("""class="__PLACEHOLDER__"""").findAll(visualText).count()
        kotlin.test.assertEquals(3, count, "Expected 3 folded class attributes, got $count in: $visualText")
    }
}

/**
 * Tests for nested elements.
 */
class AttributeFolderHTMLNestedElementsTest : BaseAttributeFolderTest() {
    fun testFoldingNestedElements() {
        setupDocument("nested.html", """<div class="outer"><span class="inner">hello</span></div>""")
        configureAttributeFolder(collapseByDefault = true)

        val visualText = applyPluginFoldingAndRender()
        assertContains(visualText, """class="__PLACEHOLDER__""")
        // Both outer and inner should be folded
        val count = Regex("""class="__PLACEHOLDER__"""").findAll(visualText).count()
        kotlin.test.assertEquals(2, count, "Expected 2 folded class attributes in nested elements, got $count in: $visualText")
    }
}

/**
 * Tests for empty attribute values.
 */
class AttributeFolderHTMLEmptyValueTest : BaseAttributeFolderTest() {
    fun testFoldingEmptyAttributeValue() {
        setupDocument("empty.html", """<div class="" />""")
        configureAttributeFolder(collapseByDefault = true)

        val visualText = applyPluginFoldingAndRender()
        // Empty value: start == end, so no fold region should be created.
        // The attribute should remain as-is.
        assertContains(visualText, """class=""""")
    }
}

/**
 * Tests for whitespace around = in attributes.
 */
class AttributeFolderHTMLWhitespaceAroundEqualsTest : BaseAttributeFolderTest() {
    fun testAttributeWithWhitespaceAroundEquals() {
        // Note: HTML parsers typically normalize this, so the PSI tree
        // may or may not preserve whitespace. We test that no crash occurs
        // and the output is reasonable.
        setupDocument("ws.html", """<div class = "foo bar" />""")
        configureAttributeFolder(collapseByDefault = true)

        // Should not crash; behavior depends on parser normalization
        val visualText = applyPluginFoldingAndRender()
        // The text should at least contain div
        assertContains(visualText, "div")
    }
}
