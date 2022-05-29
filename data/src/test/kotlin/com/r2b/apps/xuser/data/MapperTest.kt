package com.r2b.apps.xuser.data

import com.r2b.apps.xuser.data.api.entity.ErrorDTO
import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.r2b.apps.xuser.data.storage.entity.UserDB
import com.r2b.apps.xuser.domain.model.DomainError
import com.r2b.apps.xuser.domain.model.User
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class MapperTest {

    @Test
    fun `DTO conversion preserves user fields and defaults nullable values`() {
        val dto = UserDTO(
            serverId = "remote-1",
            name = "Ada",
            surname = null,
            email = null,
            phone = null,
            picture = "picture",
            gender = null,
            location = null,
            registeredDate = null,
            description = null,
        )

        assertEquals(
            UserDB(
                serverId = "remote-1",
                name = "Ada",
                picture = "picture",
            ),
            dto.dtoToDBEntity(),
        )
    }

    @Test
    fun `database conversion requires id and maps nullable fields to empty values`() {
        val entity = UserDB(
            id = 7,
            serverId = "remote-7",
            name = "Grace",
            picture = "picture",
            email = null,
            description = null,
        )

        assertEquals(
            User(
                id = 7,
                serverId = "remote-7",
                name = "Grace",
                surname = "",
                email = "",
                phone = "",
                picture = "picture",
                gender = "",
                location = "",
                registeredDate = "",
                description = "",
            ),
            entity.dbEntityToDomain(),
        )
        val failure = assertThrows(IllegalArgumentException::class.java) {
            entity.copy(id = null).dbEntityToDomain()
        }
        assertEquals("Persisted user must have a database id", failure.message)
    }

    @Test
    fun `domain conversion preserves all fields including persistence metadata`() {
        val user = FAKE_USER_LIST.first()
        val expected = FAKE_USER_DB_LIST.first()

        assertEquals(expected, user.domainToDBEntity())
        assertEquals(user, expected.dbEntityToDomain())
        assertEquals(listOf(expected), listOf(user).domainToDBEntity())
        assertEquals(listOf(user), listOf(expected).dbEntityToDomain())
    }

    @Test
    fun `error mappers preserve code message and cause`() {
        val error = ErrorDTO(code = 503, message = "Unavailable", cause = "upstream")

        assertEquals(DomainError(503, "Unavailable", "upstream"), error.toDomainError())
        assertEquals(DomainError(0, "", ""), ErrorDTO().toDomainError())
    }

    @Test
    fun `exception conversion distinguishes connectivity and unknown failures`() {
        assertEquals(
            ErrorDTO(0, "No Internet connection", "offline"),
            IOException("offline").toErrorDTO(),
        )
        assertEquals(
            ErrorDTO(0, "Unknown error", "bad response"),
            IllegalStateException("bad response").toErrorDTO(),
        )
    }

    @Test
    fun `HTTP conversion changes only the URL scheme`() {
        assertEquals(
            "https://example.test/path/http?next=http://other.test",
            switchToHttps("http://example.test/path/http?next=http://other.test"),
        )
        assertEquals("HTTPS://example.test/image", switchToHttps("HTTPS://example.test/image"))
        assertEquals("ftp://example.test/image", switchToHttps("ftp://example.test/image"))
        assertEquals("relative/path", switchToHttps("relative/path"))
    }

    @Test
    fun `numeric conversion defaults missing and malformed values`() {
        assertEquals(12, "12".toIntOrDefault())
        assertEquals(5, null.toIntOrDefault(5))
        assertEquals(5, "not-a-number".toIntOrDefault(5))
    }
}
