package com.r2b.apps.xuser.data.api.randomuser

import com.r2b.apps.lib.api.randomuser.entity.Info
import com.r2b.apps.lib.api.randomuser.entity.Location
import com.r2b.apps.lib.api.randomuser.entity.Name
import com.r2b.apps.lib.api.randomuser.entity.Picture
import com.r2b.apps.lib.api.randomuser.entity.RandomUserErrorResponse
import com.r2b.apps.lib.api.randomuser.entity.RandomUserResponse
import com.r2b.apps.lib.api.randomuser.entity.Registered
import com.r2b.apps.lib.api.randomuser.entity.Results
import com.r2b.apps.lib.api.randomuser.entity.Street
import com.r2b.apps.xuser.data.api.entity.ErrorDTO
import com.r2b.apps.xuser.data.api.entity.UserDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class MapperTest {

    @Test
    fun `result mapper trims fields formats location and upgrades image scheme`() {
        val result = Results(
            email = " user@example.test ",
            gender = " female ",
            name = Name(first = " Ada ", last = " Lovelace "),
            phone = " 123-456 ",
            picture = Picture(large = " http://images.test/ada "),
            location = Location(street = Street(number = " 12 ", name = " Main St ")),
            registered = Registered(date = " 1843-01-01 "),
        )

        assertEquals(
            UserDTO(
                serverId = result.generateId(),
                name = "Ada",
                surname = "Lovelace",
                email = "user@example.test",
                phone = "123-456",
                picture = "https://images.test/ada",
                gender = "female",
                location = "Main St, 12",
                registeredDate = "1843-01-01",
                description = "",
            ),
            result.toDTO(),
        )
    }

    @Test
    fun `generated identity ignores case and surrounding whitespace but distinguishes users`() {
        val first = Results(
            name = Name(first = " Ada ", last = " Lovelace "),
            email = " Ada@example.test ",
            phone = " 123 ",
        )
        val normalized = Results(
            name = Name(first = "ada", last = "lovelace"),
            email = "ada@example.test",
            phone = "123",
        )
        val anotherUser = normalized.copy(email = "other@example.test")

        assertEquals(first.generateId(), normalized.generateId())
        assertNotEquals(first.generateId(), anotherUser.generateId())
        assertEquals(first.generateId(), first.toDTO().serverId)
    }

    @Test
    fun `missing response data maps to empty users and missing info uses defaults`() {
        assertEquals(emptyList<UserDTO>(), RandomUserResponse(results = null).toDTO())
        assertEquals(Page(page = 0, results = 0, seed = "not found"), Info(
            page = null,
            results = null,
            seed = null,
        ).getPage())
    }

    @Test
    fun `API error preserves HTTP code and body message`() {
        assertEquals(
            ErrorDTO(503, "Unavailable", ""),
            RandomUserErrorResponse(error = "Unavailable").toDTO(503),
        )
    }
}
