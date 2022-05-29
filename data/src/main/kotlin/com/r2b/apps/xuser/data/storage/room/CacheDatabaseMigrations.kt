package com.r2b.apps.xuser.data.storage.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `UserRemoteKey` (
                `id` TEXT NOT NULL,
                `nextPage` INTEGER,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        database.execSQL(
            "ALTER TABLE `User` ADD COLUMN `sortOrder` INTEGER NOT NULL DEFAULT 0"
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_User_deleted_sortOrder_id` ON `User` (`deleted`, `sortOrder`, `id`)"
        )
    }
}
