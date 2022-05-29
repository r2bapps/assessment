package com.r2b.apps.xuser.data.api.marvel

import com.r2b.apps.lib.api.marvel.entity.MarvelDatabaseApiError
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabaseApiErrorResponse
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabasePage
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabaseQuery
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabaseResponse
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabaseThumbnail
import com.r2b.apps.xuser.data.api.entity.ErrorDTO
import com.r2b.apps.xuser.data.api.entity.UserDTO
import org.junit.Assert.assertEquals
import org.junit.Test

class MapperTest {

    @Test
    fun `character mapper maps page details and upgrades image scheme`() {
        val response = MarvelDatabaseResponse(
            query = MarvelDatabaseQuery(
                pages = listOf(
                    MarvelDatabasePage(
                        pageId = 42,
                        title = "Spider-Man",
                        fullUrl = "https://marvel.fandom.com/wiki/Spider-Man",
                        thumbnail = MarvelDatabaseThumbnail("http://images.test/spider-man.jpg"),
                    ),
                ),
            ),
        )

        assertEquals(
            listOf(
                UserDTO(
                    serverId = "42",
                    name = "Spider-Man",
                    picture = "https://images.test/spider-man.jpg",
                    description = "Marvel Database: https://marvel.fandom.com/wiki/Spider-Man",
                ),
            ),
            response.toDTO(),
        )
    }

    @Test
    fun `pages are sorted in search result order and missing fields are safe`() {
        val response = MarvelDatabaseResponse(
            query = MarvelDatabaseQuery(
                pages = listOf(
                    MarvelDatabasePage(pageId = 2, index = 2, title = "Iron Man"),
                    MarvelDatabasePage(pageId = 1, index = 1, title = "Hulk"),
                    MarvelDatabasePage(),
                ),
            ),
        )

        assertEquals(
            listOf("Hulk", "Iron Man", ""),
            response.toDTO().map(UserDTO::name),
        )
    }

    @Test
    fun `missing pages map to an empty list`() {
        assertEquals(emptyList<UserDTO>(), MarvelDatabaseResponse().toDTO())
        assertEquals(
            emptyList<UserDTO>(),
            MarvelDatabaseResponse(query = MarvelDatabaseQuery(pages = null)).toDTO(),
        )
    }

    @Test
    fun `API error maps message and preserves HTTP status when present`() {
        assertEquals(
            ErrorDTO(403, "Access denied", "readapidenied"),
            MarvelDatabaseApiErrorResponse(
                MarvelDatabaseApiError("readapidenied", "Access denied"),
            ).toDTO(403),
        )
        assertEquals(
            ErrorDTO(0, "Invalid query", "badvalue"),
            MarvelDatabaseApiErrorResponse(
                MarvelDatabaseApiError("badvalue", "Invalid query"),
            ).toDTO(),
        )
    }
}
