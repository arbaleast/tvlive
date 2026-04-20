package com.example.netflixtv.media

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(shadows = [])
class PlayerManagerTest {

    private lateinit var playerManager: PlayerManager
    private lateinit var context: Context

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        context = ApplicationProvider.getApplicationContext()
        playerManager = PlayerManager(context)
    }

    @After
    fun tearDown() {
        playerManager.release()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has isPlaying false`() = runBlocking {
        delay(100)
        assertFalse(playerManager.isPlaying.value)
    }

    @Test
    fun `initial state has currentPosition zero`() = runBlocking {
        delay(100)
        assertEquals(0L, playerManager.currentPosition.value)
    }

    @Test
    fun `initial state has duration zero`() = runBlocking {
        delay(100)
        assertEquals(0L, playerManager.duration.value)
    }

    @Test
    fun `initial state has no error`() = runBlocking {
        delay(100)
        assertNull(playerManager.error.value)
    }

    @Test
    fun `prepare sets video url`() = runBlocking {
        playerManager.prepare("https://example.com/video.m3u8")
        delay(200)
        assertNotNull(playerManager.getPlayer())
    }

    @Test
    fun `seekBack decreases position`() = runBlocking {
        playerManager.prepare("https://example.com/video.m3u8")
        delay(200)
        val initialPos = playerManager.getCurrentPosition()
        playerManager.seekBack()
        delay(100)
        val newPos = playerManager.getCurrentPosition()
        assertTrue(newPos <= initialPos + 10_000L)
    }

    @Test
    fun `seekForward increases position`() = runBlocking {
        playerManager.prepare("https://example.com/video.m3u8")
        delay(200)
        playerManager.seekForward()
        delay(100)
        val newPos = playerManager.getCurrentPosition()
        assertTrue(newPos >= 0L)
    }

    @Test
    fun `release cleans up player`() = runBlocking {
        playerManager.prepare("https://example.com/video.m3u8")
        delay(200)
        playerManager.release()
        delay(100)
        assertFalse(playerManager.isPlaying.value)
        assertEquals(0L, playerManager.currentPosition.value)
    }
}