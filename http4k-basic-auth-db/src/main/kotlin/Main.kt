import mu.KotlinLogging
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.ServerFilters
import org.http4k.lens.RequestContextKey
import org.http4k.lens.RequestContextLens
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.mindrot.jbcrypt.BCrypt

var logger = KotlinLogging.logger {}

fun main() {
    var db = Database()

    // Basic auth
    val basicAuth = ServerFilters.BasicAuth("my realm") {
        var user = db.queryRow("select * from users where username=:username",
            mapOf("username" to it.user))
        if( user == null ) false
        else BCrypt.checkpw(it.password, user["password"] as String)
    }

    // RequestContext
    fun requestContext(
        contexts: RequestContexts,
        credentials: RequestContextLens<Credentials>
    ) = Filter { next ->
        {
            contexts[it]["username"] = "steven"
            next(it)
        }
    }

    // Shared request context
    val contexts = RequestContexts()

    val credentials = RequestContextKey.required<Credentials>(contexts)

    // Application routes
    val routes: HttpHandler = routes(
        "/me" bind GET to { req -> Response(OK).body("steven") }
    )

    // Run server
    var app = ServerFilters.InitialiseRequestContext(contexts)
        .then(basicAuth)
        .then(requestContext(contexts, credentials))
        .then(routes)
        .asServer(Jetty(9000)).start()
}