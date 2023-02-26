package com.example.routes

import com.example.API_VERSION
import com.example.auth.MySession
import com.example.repository.Repository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.locations.*
import io.ktor.server.locations.post
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

const val TODOS = "$API_VERSION/scores"

@KtorExperimentalLocationsAPI
@Location(TODOS)
class TodoRoute

@KtorExperimentalLocationsAPI
fun Route.scores(db: Repository) {
    authenticate("jwt") { // 1
        post<TodoRoute> { // 2
            val todosParameters = call.receive<Parameters>()
            val victories = todosParameters["victories"]
                ?: return@post call.respond(
                    HttpStatusCode.BadRequest, "Missing Victories")

            val user = call.sessions.get<MySession>()?.let {
                db.findUser(it.userId)
            }
            if (user == null) {
                call.respond(
                    HttpStatusCode.BadRequest, "Problems retrieving User")
                return@post
            }

            try {
                // 4
                val currentTodo = db.addScore(
                    user.userId, victories = victories.toInt())
                currentTodo?.id?.let {
                    call.respond(HttpStatusCode.OK, currentTodo)
                }
            } catch (e: Throwable) {
                application.log.error("Failed to add todo", e)
                call.respond(HttpStatusCode.BadRequest, "Problems Saving Todo")
            }
        }

        get<TodoRoute> {
            val user = call.sessions.get<MySession>()?.let { db.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }
            try {
                val todos = db.getScores(user.userId)
                call.respond(todos)
            } catch (e: Throwable) {
                application.log.error("Failed to get Todos", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting Todos")
            }
        }

    }
}
