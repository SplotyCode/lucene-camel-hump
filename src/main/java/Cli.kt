import kotlinx.cli.*
import kotlin.io.path.Path

fun main(args: Array<String>) {
    val parser = ArgParser("lucene-camel-hump")

    class IndexCmd : Subcommand("index", "Index identifiers into a Lucene directory") {
        private val index by argument(ArgType.String, description = "Path to index directory")
        private val ids by argument(ArgType.String, description = "Identifier to index").vararg()
        override fun execute() {
            val indexPath = Path(index)
            val identifiers = ids.map { it.trim() }.filter { it.isNotEmpty() }
            if (identifiers.isEmpty()) {
                println("No identifiers provided to index.")
                return
            }
            Indexer.indexIdentifiers(indexPath, identifiers)
            println("Indexed ${identifiers.size} identifiers into $indexPath")
        }
    }

    class SearchCmd : Subcommand("search", "Search the Lucene index") {
        private val index by argument(ArgType.String, description = "Path to index directory")
        private val query by argument(ArgType.String, description = "Search query text")
        private val topK by option(ArgType.Int, fullName = "topK", description = "Max results to return").default(10)
        override fun execute() {
            val indexPath = Path(index)
            val results = Searcher.search(indexPath, query, topK)
            if (results.isEmpty()) {
                println("No results.")
                return
            }
            results.forEachIndexed { i, hit ->
                println("${i + 1}. ${hit.raw}\t(score=${"%.4f".format(hit.score)})")
            }
        }
    }

    parser.subcommands(IndexCmd(), SearchCmd())
    parser.parse(args)
}
