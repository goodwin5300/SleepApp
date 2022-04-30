package com.example.sleepapp.ui.main

import androidx.room.*
import java.util.*
import androidx.room.RoomDatabase

import androidx.room.TypeConverters

import androidx.room.Database
import java.sql.Time
import java.time.LocalDateTime


@Database(entities = [com.example.sleepapp.ui.main.Database.Night::class], version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun userDao(): UserDao

    @Entity
    data class Night(
        @PrimaryKey(autoGenerate = true) var nid: Int = 0,
        @ColumnInfo(name = "date") var date: String? = null,
        //this is the date of the night they fell asleep, even if they fall asleep after midnight it will be the prev night still
        @ColumnInfo(name = "start_tracking") var startTracking: String? = null,
        @ColumnInfo(name = "asleep") var asleep: String? = null,
        @ColumnInfo(name = "awake") var awake: String? = null
    )

    @Dao
    interface UserDao {
        @Query("SELECT * FROM night WHERE nid = :id")
        fun getByNID(id: Int): Night

        @Query("SELECT * FROM night WHERE date=:date")
        fun getByDate(date: Date): Night

        @Insert
        fun insert(night: Night)

        @Delete
        fun delete(night: Night)
    }
}

object Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}