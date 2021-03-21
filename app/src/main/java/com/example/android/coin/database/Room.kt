package com.example.android.coin.database

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


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CoinDao {
    @Query("select * from databasecoin")
    fun getVideos(): LiveData<List<DatabaseCoin>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg coins: DatabaseCoin)
}

@Database(entities = [DatabaseCoin::class], version = 1)
abstract class CoinsDatabase : RoomDatabase() {
    abstract val coinDao: CoinDao
}

private lateinit var INSTANCE: CoinsDatabase

fun getDatabase(context: Context): CoinsDatabase {
    synchronized(CoinsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                CoinsDatabase::class.java,
                "videos").build()
        }
    }
    return INSTANCE
}
