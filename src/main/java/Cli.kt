import kotlinx.cli.*
import kotlin.io.path.Path

fun main(args: Array<String>) {
    val parser = ArgParser("lucene-camel-hump")

    class IndexCmd : Subcommand("index", "Index identifiers into a Lucene directory (from file or directly via --id)") {
        private val ids by option(ArgType.String, fullName = "id", description = "Identifier to index").multiple()
        private val index by option(ArgType.String, fullName = "index", description = "Path to index directory").required()
        override fun execute() {
            val indexPath = Path(index)
            val lines = ids.map { it.trim() }.filter { it.isNotEmpty() }
            if (lines.isEmpty()) {
                println("No identifiers provided to index.")
                return
            }
            Indexer.indexIdentifiers(indexPath, lines)
            println("Indexed ${lines.size} identifiers into $indexPath")
        }
    }

    class SearchCmd : Subcommand("search", "Search the Lucene index") {
        private val index by option(ArgType.String, fullName = "index", description = "Path to index directory").required()
        private val query by option(ArgType.String, fullName = "q", description = "Search query text").required()
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
