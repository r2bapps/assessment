package com.r2b.apps.xuser.data.filter

import com.r2b.apps.xuser.data.FAKE_USER_LIST
import com.r2b.apps.xuser.data.UserRepositoryImpl
import io.mockk.MockKAnnotations
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class UserFilterDelegateTest {

    private lateinit var filterDelegate: UserFilterDelegate

    @Before
    fun setUp() {
        filterDelegate = UserFilterDelegate()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun filterBy() {
        assert(filterDelegate.filterBy(FAKE_USER_LIST, "r").isNotEmpty())
        assert(filterDelegate.filterBy(FAKE_USER_LIST, "o").isNotEmpty())
        assert(filterDelegate.filterBy(FAKE_USER_LIST, "p").isEmpty())
        assert(filterDelegate.filterBy(FAKE_USER_LIST, "lso").isEmpty())
        assert(filterDelegate.filterBy(FAKE_USER_LIST, "").isNotEmpty())
    }
}