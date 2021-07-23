package users.dto

// Normally this should be handled by an IDP, and we just use
// the signed token for tracking, but that's not available.
final case class SignInRequest(
    username: String,
    password: String
)