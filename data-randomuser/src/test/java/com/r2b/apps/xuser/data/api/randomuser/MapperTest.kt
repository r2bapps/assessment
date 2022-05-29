package com.r2b.apps.xuser.data.api.randomuser

import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class MapperTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun switchToHttpsTest() {
        assertEquals("https", switchToHttps("http"))
        assertEquals("https", switchToHttps("https"))
        assertEquals("ftp", switchToHttps("ftp"))
    }

}
