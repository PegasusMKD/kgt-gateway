package finki.ukim.kgt.kgtgateway.models

import java.util.stream.Collectors

data class SpringUser(
    val id: String,
    val roles: List<String>
)