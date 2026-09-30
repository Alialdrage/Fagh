package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.ArabicTextNormalizer
import com.example.data.FatwaDatabase
import com.example.data.local.FatwaEntity
import com.example.data.local.FatwaRoomDatabase
import com.example.data.repository.FatwaRepository
import com.example.model.FatwaCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var inMemoryDb: FatwaRoomDatabase
    private lateinit var repository: FatwaRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        inMemoryDb = Room.inMemoryDatabaseBuilder(context, FatwaRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = FatwaRepository(inMemoryDb.fatwaDao())
    }

    @After
    fun tearDown() {
        inMemoryDb.close()
    }

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("فتاوى السيستاني", appName)
    }

    @Test
    fun `arabic text normalizer matches diacritics and variants`() {
        val normalized = ArabicTextNormalizer.normalize("الصَّلَاةُ وَالْوُضُوء")
        assertTrue(normalized.contains("الصلاه"))
        assertTrue(ArabicTextNormalizer.matches("صلاة المسافر في السفر", "صلاه"))
        assertTrue(ArabicTextNormalizer.matches("أحكام النكاح والزواج", "احكام الزواج"))
    }

    @Test
    fun `room database offline cache insertion and query test`() = runBlocking {
        val sample = FatwaDatabase.sampleFatwas.first()
        val entity = FatwaEntity.fromFatwa(sample.copy(isOfflineCached = true))

        inMemoryDb.fatwaDao().insertFatwa(entity)
        val fetched = inMemoryDb.fatwaDao().getFatwaByIdOnce(sample.id)

        assertNotNull(fetched)
        assertEquals(sample.id, fetched?.id)
        assertEquals(sample.title, fetched?.title)
        assertTrue(fetched?.isOfflineCached == true)
    }

    @Test
    fun `repository ensureDataPopulated populates Room database for offline reading`() = runBlocking {
        assertEquals(0, inMemoryDb.fatwaDao().getFatwaCount())

        repository.ensureDataPopulated()

        val all = repository.allFatwas.first()
        assertTrue(all.isNotEmpty())
        assertTrue(all.size >= 25)
        assertEquals(all.size, inMemoryDb.fatwaDao().getFatwaCount())
    }

    @Test
    fun `repository toggle bookmark updates Room database correctly`() = runBlocking {
        repository.ensureDataPopulated()
        val all = repository.allFatwas.first()
        val target = all.first()

        assertFalse(target.isBookmarked)

        repository.toggleBookmark(target.id)
        val updated = inMemoryDb.fatwaDao().getFatwaByIdOnce(target.id)
        assertTrue(updated?.isBookmarked == true)

        val bookmarkedList = repository.bookmarkedFatwas.first()
        assertEquals(1, bookmarkedList.size)
        assertEquals(target.id, bookmarkedList.first().id)
    }

    @Test
    fun `repository updateUserNotes saves notes offline in Room database`() = runBlocking {
        repository.ensureDataPopulated()
        val target = repository.allFatwas.first().first()

        val noteText = "ملاحظة مهمة: مراجعة هذه المسألة مع الوكيل الشرعي"
        repository.updateUserNotes(target.id, noteText)

        val updated = inMemoryDb.fatwaDao().getFatwaByIdOnce(target.id)
        assertEquals(noteText, updated?.userNotes)

        val withNotes = repository.fatwasWithNotes.first()
        assertEquals(1, withNotes.size)
        assertEquals(target.id, withNotes.first().id)
    }

    @Test
    fun `fatwa database categories are loaded properly`() {
        val worshipFatwas = FatwaDatabase.sampleFatwas.filter { it.category == FatwaCategory.WORSHIP }
        val transactionsFatwas = FatwaDatabase.sampleFatwas.filter { it.category == FatwaCategory.TRANSACTIONS }
        val marriageFatwas = FatwaDatabase.sampleFatwas.filter { it.category == FatwaCategory.MARRIAGE }

        assertTrue(worshipFatwas.isNotEmpty())
        assertTrue(transactionsFatwas.isNotEmpty())
        assertTrue(marriageFatwas.isNotEmpty())
    }

    @Test
    fun `share fatwa creates formatted text with source and ruling`() {
        val sampleFatwa = FatwaDatabase.sampleFatwas.first()
        val shareText = com.example.util.FatwaShareHelper.createShareText(sampleFatwa)

        assertTrue(shareText.contains(sampleFatwa.title))
        assertTrue(shareText.contains(sampleFatwa.question))
        assertTrue(shareText.contains(sampleFatwa.answer))
        assertTrue(shareText.contains(sampleFatwa.sourceBook))
        assertTrue(shareText.contains(sampleFatwa.rulingType.labelArabic))
        assertTrue(shareText.contains("السيد علي السيستاني"))
    }
}
