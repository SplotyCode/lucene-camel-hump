package filter

import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.core.KeywordTokenizer
import org.apache.lucene.analysis.core.WhitespaceTokenizer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import kotlin.test.assertEquals
import kotlin.test.Test
import java.io.StringReader

class CamelHumpsInitialsFilterTest {
    @Test
    fun `initials for single keyword token`() {
        val tokenizer = KeywordTokenizer()
        tokenizer.setReader(StringReader("HTTPRequest2XX"))
        val filter: TokenStream = CamelHumpsInitialsFilter(tokenizer)
        val tokens = collectTerms(filter)
        assertEquals(listOf("HR2X"), tokens)
    }

    @Test
    fun `initials for multiple whitespace-separated tokens`() {
        val tokenizer = WhitespaceTokenizer()
        tokenizer.setReader(StringReader("HTTPRequest2XX fooBar 123ABC snake_case XML HTTP2 ok"))
        val filter: TokenStream = CamelHumpsInitialsFilter(tokenizer)
        val tokens = collectTerms(filter)
        assertEquals(listOf("HR2X", "fB", "1A", "sc", "X", "H2", "o"), tokens)
    }

    @Test
    fun `empty input yields no tokens`() {
        val tokenizer = WhitespaceTokenizer()
        tokenizer.setReader(StringReader(""))
        val filter: TokenStream = CamelHumpsInitialsFilter(tokenizer)
        val tokens = collectTerms(filter)
        assertEquals(emptyList(), tokens)
    }

    private fun collectTerms(stream: TokenStream): List<String> {
        stream.reset()
        val termAttr = stream.addAttribute(CharTermAttribute::class.java)
        val out = mutableListOf<String>()
        while (stream.incrementToken()) {
            out += termAttr.toString()
        }
        stream.end()
        stream.close()
        return out
    }
}
