package com.r2b.apps.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EitherTest {

    @Test
    fun `success exposes data and maps only success branch`() {
        val result = successEither(2)
        var failureMapperCalled = false

        val mapped = result.map(
            onSuccess = { it * 3 },
            onFailure = {
                failureMapperCalled = true
                it
            },
        )

        assertTrue(result.isSuccess())
        assertFalse(result.isFailure())
        assertEquals(2, result.getOrDefaultOrNull())
        assertEquals(Either.Success(6), mapped)
        assertFalse(failureMapperCalled)
    }

    @Test
    fun `failure exposes error and maps only failure branch`() {
        val result = failureEither("network")
        var successMapperCalled = false

        val mapped = result.map(
            onSuccess = {
                successMapperCalled = true
                it
            },
            onFailure = { "failed: $it" },
        )

        assertTrue(result.isFailure())
        assertFalse(result.isSuccess())
        assertNull(result.getOrDefaultOrNull())
        assertEquals("fallback", result.getOrDefaultOrNull("fallback"))
        assertEquals(Either.Failure("failed: network"), mapped)
        assertFalse(successMapperCalled)
    }

    @Test
    fun `empty success emits unit`() {
        assertEquals(Either.Success(Unit), Either.Success(1).emptySuccessEither())
    }
}
