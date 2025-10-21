import analyser.CamelHumpAnalyzers
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.FSDirectory
import java.nio.file.Files
import java.nio.file.Path

object Indexer {
    fun indexIdentifiers(indexPath: Path, identifiers: List<String>, analyzer: Analyzer = CamelHumpAnalyzers.perField) {
        Files.createDirectories(indexPath)
        FSDirectory.open(indexPath).use { dir ->
            val config = IndexWriterConfig(analyzer).apply {
                openMode = IndexWriterConfig.OpenMode.CREATE
            }
            IndexWriter(dir, config).use { writer ->
                identifiers.forEachIndexed { id, raw ->
                    if (raw.isBlank()) return@forEachIndexed
                    println("Indexing $raw ($id)")
                    val doc = Document().apply {
                        add(StoredField(Fields.RAW, raw))
                        add(StringField(Fields.NAME_LC, raw.lowercase(), Field.Store.NO))
                        add(TextField(Fields.PARTS, raw, Field.Store.NO))
                        add(TextField(Fields.HUMPS, raw, Field.Store.NO))
                        add(IntPoint(Fields.ID_POINT, id))
                        add(StoredField(Fields.ID, id))
                    }
                    writer.addDocument(doc)
                }
                writer.commit()
            }
        }
    }
}
