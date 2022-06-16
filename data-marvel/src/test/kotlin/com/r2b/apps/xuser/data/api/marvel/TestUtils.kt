package com.r2b.apps.xuser.data.api.marvel

import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi


val FAKE_USER_DTO_LIST: List<UserDTO> = listOf(
    UserDTO(serverId="1011334", name="3-D Man", "", "", "", "https://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784.jpg", "", "", "", ""),
    UserDTO(serverId="1017100", name="A-Bomb (HAS)", "", "", "", "https://i.annihil.us/u/prod/marvel/i/mg/3/20/5232158de5b16.jpg", "", "", "", "Rick Jones has been Hulk's best bud since day one, but now he's more than a friend...he's a teammate! Transformed by a Gamma energy explosion, A-Bomb's thick, armored skin is just as strong and powerful as it is blue. And when he curls into action, he uses it like a giant bowling ball of destruction! "),
    UserDTO(serverId="1009144", name="A.I.M.", "", "", "", "https://i.annihil.us/u/prod/marvel/i/mg/6/20/52602f21f29ec.jpg", "", "", "", "AIM is a terrorist organization bent on destroying the world."),
)

inline fun <reified T> parseJson(jsonString: String): T? {
    val moshi = Moshi.Builder().build()
    val jsonAdapter: JsonAdapter<T> = moshi.adapter(T::class.java)
    return jsonAdapter.fromJson(jsonString)
}