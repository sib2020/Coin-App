package com.example.android.coin.repository

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



import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.android.coin.database.CoinsDatabase
import com.example.android.coin.database.asDomainModel
import com.example.android.coin.domain.Coin
import com.example.android.coin.network.Network
import com.example.android.coin.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CoinsRepository(private val database: CoinsDatabase) {

    /**
     * A playlist of videos that can be shown on the screen.
     */
    val coins: LiveData<List<Coin>> =
        Transformations.map(database.coinDao.getVideos()) {
            it.asDomainModel()
        }

    /**
     * Refresh the videos stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the videos for use, observe [videos]
     */
    suspend fun refreshCoins() {
        withContext(Dispatchers.IO) {
            val playlist = Network.devbytes.getPlaylist().await()
            database.coinDao.insertAll(*playlist.asDatabaseModel())
        }
    }
}
