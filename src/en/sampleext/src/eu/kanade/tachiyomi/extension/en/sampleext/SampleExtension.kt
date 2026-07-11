package eu.kanade.tachiyomi.extension.en.sampleext

import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.HttpSource
import keiyoushi.annotation.Source
import okhttp3.Request
import okhttp3.Response

@Source
abstract class SampleExtension : HttpSource() {

    override fun popularMangaRequest(page: Int): Request = throw UnsupportedOperationException("Not implemented")

    override fun popularMangaParse(response: Response): MangasPage = throw UnsupportedOperationException("Not implemented")

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request = throw UnsupportedOperationException("Not implemented")

    override fun searchMangaParse(response: Response): MangasPage = throw UnsupportedOperationException("Not implemented")

    override fun mangaDetailsParse(response: Response): SManga = throw UnsupportedOperationException("Not implemented")

    override fun chapterListParse(response: Response): List<SChapter> = throw UnsupportedOperationException("Not implemented")

    override fun pageListParse(response: Response): List<Page> = throw UnsupportedOperationException("Not implemented")

    override fun imageUrlParse(response: Response): String = throw UnsupportedOperationException("Not implemented")

    override fun latestUpdatesRequest(page: Int): Request = throw UnsupportedOperationException("Not implemented")

    override fun latestUpdatesParse(response: Response): MangasPage = throw UnsupportedOperationException("Not implemented")

    override val supportsLatest: Boolean = false
}
