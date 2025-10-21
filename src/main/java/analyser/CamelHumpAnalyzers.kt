package analyser

import filter.CamelHumpsInitialsFilter
import org.apache.lucene.analysis.*
import org.apache.lucene.analysis.core.KeywordTokenizer
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter

object CamelHumpAnalyzers {
    private const val MIN_GRAM = 1
    private const val MAX_GRAM = 32
    private const val PRESERVE_ORIGINAL_NGRAM = false

    private const val WORD_DELIM_FLAGS = (
        WordDelimiterGraphFilter.SPLIT_ON_CASE_CHANGE
            or WordDelimiterGraphFilter.SPLIT_ON_NUMERICS
            or WordDelimiterGraphFilter.GENERATE_WORD_PARTS
            or WordDelimiterGraphFilter.PRESERVE_ORIGINAL
    )

    val nameLc: Analyzer = buildAnalyzer { tokenizer ->
        LowerCaseFilter(tokenizer)
    }

    val parts: Analyzer = buildAnalyzer { tokenizer ->
        val split = WordDelimiterGraphFilter(tokenizer, WORD_DELIM_FLAGS, null)
        EdgeNGramTokenFilter(
            LowerCaseFilter(split),
            MIN_GRAM,
            MAX_GRAM,
            PRESERVE_ORIGINAL_NGRAM
        )
    }

    val humps: Analyzer = buildAnalyzer { tokenizer ->
        EdgeNGramTokenFilter(
            LowerCaseFilter(CamelHumpsInitialsFilter(tokenizer)),
            MIN_GRAM,
            MAX_GRAM,
            PRESERVE_ORIGINAL_NGRAM
        )
    }

    val perField: Analyzer = PerFieldAnalyzerWrapper(
        nameLc,
        mapOf(
            Fields.PARTS to parts,
            Fields.HUMPS to humps,
            Fields.NAME_LC to nameLc,
        )
    )

    private inline fun buildAnalyzer(
        crossinline build: (Tokenizer) -> TokenStream
    ): Analyzer = object : Analyzer() {
        override fun createComponents(fieldName: String): TokenStreamComponents {
            val tokenizer = KeywordTokenizer()
            val stream = build(tokenizer)
            return TokenStreamComponents(tokenizer, stream)
        }
    }
}
