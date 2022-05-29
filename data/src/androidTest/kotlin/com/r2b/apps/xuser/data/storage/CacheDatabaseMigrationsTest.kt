package com.r2b.apps.xuser.data.storage

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.r2b.apps.xuser.data.storage.room.MIGRATION_1_2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CacheDatabaseMigrationsTest {

    @Test
    fun migrationAddsPagingMetadataAndIndexWithoutLosingCachedUsers() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(null)
                .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(database: SupportSQLiteDatabase) {
                        database.execSQL(
                            "CREATE TABLE `User` (`id` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, PRIMARY KEY(`id`))"
                        )
                        database.execSQL("INSERT INTO `User` (`id`, `deleted`) VALUES (1, 0)")
                    }

                    override fun onUpgrade(
                        database: SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int,
                    ) = Unit
                })
                .build()
        )

        try {
            val database = helper.writableDatabase
            MIGRATION_1_2.migrate(database)

            database.query("SELECT COUNT(*) FROM `User`").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(1, cursor.getInt(0))
            }
            database.query("SELECT `sortOrder` FROM `User` WHERE `id` = 1").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(0, cursor.getInt(0))
            }
            database.query(
                "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'UserRemoteKey'"
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
            }
            database.query(
                "SELECT name FROM sqlite_master WHERE type = 'index' AND name = 'index_User_deleted_sortOrder_id'"
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
            }
        } finally {
            helper.close()
        }
    }
}
