package finki.ukim.kgt.kgtgateway.filters.config

data class AuthenticationFilterConfig(
    val errorMessage: String = "You need to be logged in to access this page!",
    val errorCode: Int = 401
)
