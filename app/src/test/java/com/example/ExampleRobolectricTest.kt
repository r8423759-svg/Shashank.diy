package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.parser.BoltArtifactParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Bolt DIY", appName)
    }

    @Test
    fun `parse bolt artifact with actions`() {
        val aiResponse = """
            Here is your requested component:
            <boltArtifact id="test-app" title="Test Project">
            <boltAction type="file" filePath="src/Test.jsx">
            export default function Test() { return <div>Hello</div>; }
            </boltAction>
            <boltAction type="shell">
            npm install
            </boltAction>
            </boltArtifact>
        """.trimIndent()

        val parsed = BoltArtifactParser.parse(aiResponse)
        assertEquals("test-app", parsed.id)
        assertEquals("Test Project", parsed.title)
        assertEquals(2, parsed.actions.size)
        assertEquals("src/Test.jsx", parsed.actions[0].filePath)
    }
}
