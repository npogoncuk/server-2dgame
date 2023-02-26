package com.example.repository

import com.example.models.Score
import com.example.models.User
import com.example.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement

class ScoreRepository : Repository {

    override suspend fun addUser(
        email: String,
        displayName: String,
        passwordHash: String
    ): User? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Users.insert { user ->
                user[Users.email] = email
                user[Users.displayName] = displayName
                user[Users.passwordHash] = passwordHash
            }
        }

        return rowToUser(statement?.resultedValues?.get(0))
    }

    override suspend fun findUser(userId: Int) = dbQuery {
        Users.select { Users.userId eq userId }.map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun findUserByEmail(email: String) = dbQuery {
        Users.select { Users.email eq email }.map { rowToUser(it) }.singleOrNull()
    }

    private fun rowToUser(row: ResultRow?): User? {
        row ?: return null
        return User(
            userId = row[Users.userId],
            email = row[Users.email],
            displayName = row[Users.displayName],
            passwordHash = row[Users.passwordHash]
        )
    }


    override suspend fun addScore(userId: Int, victories: Int): Score? {
        var statement: InsertStatement<Number>? = null
        val query: () -> Unit = {
            statement = Scores.insert {
                it[Scores.userId] = userId
                it[Scores.victories] = victories
            }
        }
        dbQuery {
            try {
                query()
            } catch (e: ExposedSQLException) {
                Scores.deleteWhere { Scores.userId.eq(userId) }
                query()
            }
        }
        return rowToScore(statement?.resultedValues?.get(0))
    }

    override suspend fun getScores(userId: Int): List<Score> {
        return dbQuery {
            Scores.select { Scores.userId eq userId }.mapNotNull { rowToScore(it) }
        }
    }

    private fun rowToScore(row: ResultRow?): Score? {
        row ?: return null
        return Score(
            id = row[Scores.id],
            userId = row[Scores.userId],
            victories = row[Scores.victories]
        )
    }

}