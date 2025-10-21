package filter

import org.apache.lucene.analysis.TokenFilter
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute

class CamelHumpsInitialsFilter(input: TokenStream) : TokenFilter(input) {
    private val humpRegex = Regex("([A-Z]?[a-z]+|[A-Z]+(?![a-z])|[0-9]+)")
    private val termAttribute = addAttribute(CharTermAttribute::class.java)

    override fun incrementToken(): Boolean {
        if (!input.incrementToken()) return false
        val token = termAttribute.toString()
        val initials = humpRegex.findAll(token)
            .filter { it.value.isNotEmpty() }
            .map { it.value[0].toString() }
            .joinToString("")
        termAttribute.setEmpty().append(initials)
        return true
    }
}
