import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.BoostQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.PrefixQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.RegexpQuery
import org.apache.lucene.store.FSDirectory
import java.nio.file.Path

object Searcher {
    data class Hit(val raw: String, val score: Float)

    private fun subsequenceRegex(input: String): String =
        input.map { Regex.escape(it.toString()) }.joinToString(".*")

    private fun buildQuery(lower: String): Query {
        val humps = PrefixQuery(Term(Fields.HUMPS, lower))
        val parts = PrefixQuery(Term(Fields.PARTS, lower))
        val subseqeuence = RegexpQuery(Term(Fields.NAME_LC, subsequenceRegex(lower)))

        return BooleanQuery.Builder()
            .add(BoostQuery(humps, 5.0f), BooleanClause.Occur.SHOULD)
            .add(BoostQuery(parts, 3.0f), BooleanClause.Occur.SHOULD)
            .add(BoostQuery(subseqeuence, 1.0f), BooleanClause.Occur.SHOULD)
            .build()
    }

    fun search(indexPath: Path, queryText: String, topK: Int = 10): List<Hit> {
        val lower = queryText.trim().lowercase()
        if (lower.isEmpty()) return emptyList()

        FSDirectory.open(indexPath).use { dir ->
            DirectoryReader.open(dir).use { reader ->
                val searcher = IndexSearcher(reader)
                val query = buildQuery(lower)
                val hits = searcher.search(query, topK).scoreDocs
                return hits.map { sd ->
                    val document = searcher.storedFields().document(sd.doc)
                    Hit(document.get(Fields.RAW), sd.score)
                }
            }
        }
    }
}
