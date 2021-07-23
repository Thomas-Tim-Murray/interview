package users.dto

import users.domain._

object UserResponse {
    def apply(user: User, message: String): UserResponse = 
        UserResponse(
            message,
            user.id.value, 
            user.userName.value, 
            user.emailAddress.value, 
            user.metadata
        )
}

case class UserResponse(
    val message: String,
    val userUuid: String,
    val username: String,
    val emailAddress: String,
    val metadata: User.Metadata
)