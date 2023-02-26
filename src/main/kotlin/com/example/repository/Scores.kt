package com.example.repository

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Scores : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val userId: Column<Int> = integer("userId").references(Users.userId).uniqueIndex()
    val victories = integer("userVictories")

    override val primaryKey = PrimaryKey(id)
}
