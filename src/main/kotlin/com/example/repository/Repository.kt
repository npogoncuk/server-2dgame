package com.example.repository

import com.example.models.Score
import com.example.models.User

interface Repository {
    suspend fun addUser(email: String,
                        displayName: String,
                        passwordHash: String): User?
    suspend fun findUser(userId: Int): User?
    suspend fun findUserByEmail(email: String): User?

    suspend fun addScore(userId: Int, victories: Int): Score?
    suspend fun getScores(userId: Int): List<Score>

}
