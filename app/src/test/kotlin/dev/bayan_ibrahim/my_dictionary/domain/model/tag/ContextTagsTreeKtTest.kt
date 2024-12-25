package dev.bayan_ibrahim.my_dictionary.domain.model.tag

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ContextTagsTreeKtTest {
    private lateinit var tagsList: List<ContextTag>
    private lateinit var tagsTree: ContextTagsMutableTree

    @Before
    fun setup() {
        tagsList = listOf(
            ContextTag("object", "food", "fruit"),
            ContextTag("object", "food", "vegetables"),
            ContextTag("object", "food", "healthy"),
            ContextTag("object", "device"),
            ContextTag("language", "en"),
        )
        tagsTree = ContextTagsMutableTree().apply {
            nextLevel["object"] = ContextTagsMutableTree(ContextTag("object")).apply {
                nextLevel["food"] = ContextTagsMutableTree(ContextTag("object", "food")).apply {
                    nextLevel["fruit"] = ContextTagsMutableTree(ContextTag("object", "food", "fruit"))
                    nextLevel["vegetables"] = ContextTagsMutableTree(ContextTag("object", "food", "vegetables"))
                    nextLevel["healthy"] = ContextTagsMutableTree(ContextTag("object", "food", "healthy"))
                }
                nextLevel["device"] = ContextTagsMutableTree(ContextTag("object", "device"))
            }
            nextLevel["language"] = ContextTagsMutableTree(ContextTag("language")).apply {
                nextLevel["en"] = ContextTagsMutableTree(ContextTag("language", "en"))
            }
        }
    }

    @Test
    fun `asTree-emptyList-emptyTree`() {
        val tagsList: List<ContextTag> = emptyList()
        val tree = tagsList.asTree()
        assertNull(tree.tag)
        assertTrue(tree.nextLevel.isEmpty())
    }

    @Test
    fun `asTree-tagsList-treeOfTags`() {
        val actualTree = tagsList.asTree()
        assertEquals(
            actualTree,
            tagsTree,
        )
    }

    @Test
    fun `getSubTree-existedDirectSubTree-returnSubTree`() {
        val actualSubTree = tagsTree["object"]
        val expectedSubTree = ContextTagsMutableTree(ContextTag("object")).apply {
            nextLevel["food"] = ContextTagsMutableTree(ContextTag("object", "food")).apply {
                nextLevel["fruit"] = ContextTagsMutableTree(ContextTag("object", "food", "fruit"))
                nextLevel["vegetables"] = ContextTagsMutableTree(ContextTag("object", "food", "vegetables"))
                nextLevel["healthy"] = ContextTagsMutableTree(ContextTag("object", "food", "healthy"))
            }
            nextLevel["device"] = ContextTagsMutableTree(ContextTag("object", "device"))
        }
        assertEquals(expectedSubTree, actualSubTree)
    }


    @Test
    fun `getSubTree-currentTree-returnSameTree`() {
        val tree=tagsTree["object"]!!
        val actualSubTree = tagsTree[ContextTag("object")]
        assertEquals(tree, actualSubTree)
    }
    @Test
    fun `getSubTree-nonExistedDirectSubTree-returNull`() {
        val actualSubTree = tagsTree["not an existed value"]
        assertNull(actualSubTree)
    }

    @Test
    fun `getSubTree-existedDeepSubTree-returnSubTree`() {
        val actualSubTree = tagsTree[
            ContextTag("object", "food")
        ]
        val expectedSubTree = ContextTagsMutableTree(ContextTag("object", "food")).apply {
            nextLevel["fruit"] = ContextTagsMutableTree(ContextTag("object", "food", "fruit"))
            nextLevel["vegetables"] = ContextTagsMutableTree(ContextTag("object", "food", "vegetables"))
            nextLevel["healthy"] = ContextTagsMutableTree(ContextTag("object", "food", "healthy"))
        }
        assertEquals(expectedSubTree, actualSubTree)
    }

    @Test
    fun `getSubTree-nonExistedDeepSubTree-returnSubTree`() {
        val actualSubTree = tagsTree[
            ContextTag("object", "non existed")
        ]
        assertNull(actualSubTree)
    }
}