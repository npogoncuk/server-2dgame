package com.example

import com.example.auth.JwtService
import com.example.auth.MySession
import com.example.auth.hash
import io.ktor.server.application.*
import com.example.plugins.*
import com.example.repository.DatabaseFactory
import com.example.repository.ScoreRepository
import com.example.routes.scores
import com.example.routes.users
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.locations.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    // 1
    DatabaseFactory.init()
    val db = ScoreRepository()
// 2
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }

    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Todo Server"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asInt()
                val user = db.findUser(claimString)
                user
            }
        }
    }
    install(Locations)

    routing {
        get("/") {
            call.respondText("hello, i'm working")
        }
        users(db, jwtService, hashFunction)
        scores(db)
    }
    configureSecurity()
    configureSerialization()
    //configureDatabases()
    //configureRouting()
}

const val API_VERSION = "/v1"