import org.jdbi.v3.core.Jdbi

/**
 * Simple database connection.
 */
class Database(
    url: String = "jdbc:mysql://localhost:3306/http4k_mysql",
    username: String = "root",
    password: String = "rootpassword") {

    private var loadDriver = Class.forName("com.mysql.cj.jdbc.Driver")
    private var jdbi: Jdbi = Jdbi.create(url, username, password)

    /**
     * Query for one row.
     */
    fun queryRow(sql: String, params: Map<String, Any>): Map<String, Any>? =
        jdbi.withHandle<Map<String, Any>, Exception> { handle ->
            val q = handle.createQuery(sql)
            params.forEach { p -> q.bind(p.key, p.value) }
            return@withHandle try{
                q.mapToMap().one()
            }catch(ex: IllegalStateException){
                if( ex.message == "Expected one element, but found none" ){
                    null
                }else{
                    throw ex
                }
            }
        }

    /**
     * Query for a list of rows.
     */
    fun queryList(sql: String, params: Map<String, Any>): List<Map<String, Any>> =
        jdbi.withHandle<List<Map<String, Any>>, Exception> { handle ->
            val q = handle.createQuery(sql)
            params.forEach { p -> q.bind(p.key, p.value) }
            q.mapToMap().list()
        }

    /**
     * Stored Procedure
     */
    fun call(proc: String, vararg params: Any): List<Map<String, Any>> =
        jdbi.withHandle<List<Map<String, Any>>, Exception> { handle ->
            val q = handle.createQuery("CALL $proc(${"?".repeat(params.size)})")
            params.forEachIndexed { index, p ->q.bind(index, p) }
            q.mapToMap().list()
        }
}