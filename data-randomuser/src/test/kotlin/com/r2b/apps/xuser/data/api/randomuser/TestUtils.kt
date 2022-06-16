package com.r2b.apps.xuser.data.api.randomuser

import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi


val FAKE_USER_DTO_LIST: List<UserDTO> = listOf(
    UserDTO("24d63e4cc9eb93f80814200ec01a15dc", "Ricky", "Olson", "ricky.olson@example.com", "02-7385-9798", "https://randomuser.me/api/portraits/men/78.jpg", "male", "Blossom Hill Rd, 1619", "2009-09-20T00:26:23.824Z", ""),
)

inline fun <reified T> parseJson(jsonString: String): T? {
    val moshi = Moshi.Builder().build()
    val jsonAdapter: JsonAdapter<T> = moshi.adapter(T::class.java)
    return jsonAdapter.fromJson(jsonString)
}