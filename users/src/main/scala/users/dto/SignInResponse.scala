package users.dto

final case class SignInResponse(
    message: String,
    token: String
)