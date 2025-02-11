package com.example.myapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE login = :login AND password = :password LIMIT 1")
    fun authenticate(login: String, password: String): User?

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    fun getUserByLogin(login: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User> // Новый метод

    @Delete
    fun delete(user: User)  // Метод для удаления пользователя

    @Update
    suspend fun updateUser(user: User) // Ensure you have this method
}
