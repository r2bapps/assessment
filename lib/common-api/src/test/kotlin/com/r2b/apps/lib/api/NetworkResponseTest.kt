package com.r2b.apps.lib.api

import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkResponseTest {

    @Test
    fun `success maps body and leaves error mapper unused`() {
        val response = successNetworkResponse("42")
        var errorMapperCalled = false

        val mapped = response.map(
            onSuccess = { it.toInt() },
            onApiError = { _, _ ->
                errorMapperCalled = true
                "error"
            },
        )

        assertTrue(response.isSuccess())
        assertFalse(response.isFailure())
        assertEquals("42", response.getOrDefaultOrNull())
        assertEquals(NetworkResponse.Success(42), mapped)
        assertNull(response.getFailureOrNull())
        assertFalse(errorMapperCalled)
    }

    @Test
    fun `api error maps body while retaining status code`() {
        val response: NetworkResponse<String, String> =
            NetworkResponse.ApiError("bad request", 400)
        var successMapperCalled = false

        val mapped = response.map(
            onSuccess = {
                successMapperCalled = true
                it.length
            },
            onApiError = { body, code -> "$code: $body" },
        )

        assertTrue(response.isApiError())
        assertTrue(response.isFailure())
        assertEquals("default", response.getOrDefaultOrNull("default"))
        assertEquals(NetworkResponse.ApiError("400: bad request", 400), mapped)
        assertNull(response.getFailureOrNull())
        assertFalse(successMapperCalled)
    }

    @Test
    fun `network and unknown errors survive mapping and expose their cause`() {
        val ioFailure = IOException("offline")
        val networkError: NetworkResponse<Int, String> = NetworkResponse.NetworkError(ioFailure)
        val unknownFailure = IllegalArgumentException("invalid response")
        val unknownError: NetworkResponse<Int, String> = NetworkResponse.UnknownError(unknownFailure)

        val mappedNetworkError = networkError.map({ it.toString() }, { body, code -> "$code: $body" })
        val mappedUnknownError = unknownError.map({ it.toString() }, { body, code -> "$code: $body" })

        assertTrue(networkError.isNetworkError())
        assertSame(ioFailure, networkError.getFailureOrNull())
        assertEquals(NetworkResponse.NetworkError(ioFailure), mappedNetworkError)
        assertTrue(unknownError.isUnknownError())
        assertSame(unknownFailure, unknownError.getFailureOrNull())
        assertEquals(NetworkResponse.UnknownError(unknownFailure), mappedUnknownError)
    }
}
