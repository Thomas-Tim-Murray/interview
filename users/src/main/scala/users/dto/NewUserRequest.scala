package users.dto

case class NewUserRequest(
    username: String,
    emailAddress: String,
    password: Option[String] = None
)

// I want to use this format to simplify mapping the JSON to the entities, but
// the compiler complains that this doesn't work with type erasure.
// Trying to use just the case class with types causes the JSON decoder to fail.
/*
object NewUserRequest{
    def apply(
        username: String,
        emailAddress: String,
        password: Option[String] = None
    ): NewUserRequest = NewUserRequest(
        UserName(username),
        EmailAddress(emailAddress),
        password.map(pw => Password(pw))
    )
}

case class NewUserRequest(
    username: UserName,
    emailAddress: EmailAddress,
    password: Option[Password],
)
*/