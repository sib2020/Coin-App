package com.example.android.coin.network

/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */



import com.example.android.coin.database.DatabaseCoin
import com.example.android.coin.domain.Coin
import com.squareup.moshi.JsonClass

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */

/**
 * VideoHolder holds a list of Videos.
 *
 * This is to parse first level of our network result which looks like
 *
 * {
 *   "videos": []
 * }
 */
@JsonClass(generateAdapter = true)
data class NetworkCoinContainer(val coins: List<NetworkCoin>)

/**
 * Videos represent a devbyte that can be played.
 */
@JsonClass(generateAdapter = true)
data class NetworkCoin(
    val title: String,
    val description: String,
    val url: String,
    val updated: String,
    val thumbnail: String,
    val closedCaptions: String?)


/**
 * Convert Network results to database objects
 */
fun NetworkCoinContainer.asDomainModel(): List<Coin> {
    return coins.map {
        Coin(
            title = it.title,
            description = it.description,
            url = it.url,
            updated = it.updated,
            thumbnail = it.thumbnail)
}
}

fun NetworkCoinContainer.asDatabaseModel(): Array<DatabaseCoin> {
return coins.map {
DatabaseCoin(
  title = it.title,
  description = it.description,
  url = it.url,
  updated = it.updated,
  thumbnail = it.thumbnail
  )
}.toTypedArray()
}
