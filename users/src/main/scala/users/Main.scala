package users

import cats.data._
import cats.implicits._
import cats.effect._

import users.config._
import users.main._
import users.domain._
import users.dto._
import users.services.usermanagement._

import org.http4s.Response
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.circe._

import io.circe.syntax._
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext.global


object Main extends IOApp {
  // Config
  val config = ApplicationConfig(
    executors = ExecutorsConfig(
      services = ExecutorsConfig.ServicesConfig(
        parallellism = 4
      )
    ),
    services = ServicesConfig(
      users = ServicesConfig.UsersConfig(
        failureProbability = 0.1,
        timeoutProbability = 0.1
      )
    )
  )
  val timeout = 2000

  // Initialization
  val application = Application.fromApplicationConfig.run(config)
  val controller = new Controller(application, timeout)

  // Implicit variables
  implicit val newUserDecoder = jsonOf[IO, NewUserRequest]
  implicit val updateEmailDecoder = jsonOf[IO, UpdateEmailRequest]
  implicit val updatePasswordDecoder = jsonOf[IO, UpdatePasswordRequest]
  implicit val signInDecoder = jsonOf[IO, SignInRequest]

  // Define HTTP routes
  val userManagementApi = HttpRoutes.of[IO] {
    case GET -> Root / "user" / userUuid =>
      getErrorOrResponse(
        controller.get(User.Id(userUuid)),
        "Get user by ID"
      )
    case GET -> Root / "user" =>
      getErrorOrResponse(
        controller.getAll(),
        "Get all users"
      )
    case body @ POST -> Root / "user" => 
      for {
        // This JSON decoding does not return actionable errors to the user, but I can't figure
        // out how to extract the actual decoding errors from the http4s.circe integration
        request <- body.as[NewUserRequest]
        response <- getErrorOrResponse(
          controller.signUp(request),
          "User created successfully"
        )
      } yield response
    case body @ POST -> Root / "login" => 
      for {
        request <- body.as[SignInRequest]
        response <- getErrorOrResponse(
          controller.signIn(request),
          "Sign in successful"
        )
      } yield response
    case body @ PATCH -> Root / "user" / userUuid / "email" => 
      for {
        request <- body.as[UpdateEmailRequest]
        response <- getErrorOrResponse(
          controller.updateEmail(User.Id(userUuid), request),
          "Email changed successfully"
        )
      } yield response
    case body @ PATCH -> Root / "user" / userUuid / "password" => 
      for {
        request <- body.as[UpdatePasswordRequest]
        response <- getErrorOrResponse(
          controller.updatePassword(User.Id(userUuid), request),
          "Password changed successfully"
        )
      } yield response
    case PATCH -> Root / "user" / userUuid / "resetPassword" =>
      getErrorOrResponse(
        controller.resetPassword(User.Id(userUuid)),
        "Password reset successfully"
      )
    case PATCH -> Root / "user" / userUuid / "block" =>
      getErrorOrResponse(
        controller.block(User.Id(userUuid)),
        "User blocked successfully"
      )
    case PATCH -> Root / "user" / userUuid / "unblock" =>
      getErrorOrResponse(
        controller.unblock(User.Id(userUuid)),
        "User unblocked successfully"
      )
    case DELETE -> Root / "user" / userUuid =>
      getErrorOrResponse(
        controller.delete(User.Id(userUuid)),
        "User deleted successfully"
      )
  }.orNotFound

  // Start server
  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(userManagementApi)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)

  // Convert response to HTTP format
  def getErrorOrResponse[T](errorOrResult: Either[Error, T], successMessage: String): IO[Response[IO]] = {
    errorOrResult match {
      case Left(error) => getErrorMessage(error)
      case Right(result) => result match {
        case user: User => Ok(UserResponse(user, successMessage).asJson)
        case users: List[User @unchecked] => Ok(users.map(user => UserResponse(user, successMessage)).asJson)
        case token: Token => Ok(SignInResponse(successMessage, token.value).asJson)
        case _: Done => Ok(MessageResponse(successMessage).asJson)
      }
    }
  }

  // Convert errors to human-readable messages
  def getErrorMessage(error: Error): IO[Response[IO]] = {
    error match {
      case Error.Exists => BadRequest(MessageResponse("User already exists").asJson)
      case Error.NotFound => NotFound(MessageResponse("User not found").asJson)
      case Error.Deleted => NotFound(MessageResponse("User not found").asJson)
      case Error.Active => BadRequest(MessageResponse("User is already active").asJson)
      case Error.Blocked => BadRequest(MessageResponse("User is already blocked").asJson)
      case Error.IncorrectCredentials => BadRequest(MessageResponse("Username or password incorrect").asJson)
      case Error.AccessDenied => Forbidden(MessageResponse("You do not have permission to do this").asJson)
      case e: Error.System => {
        Console.println(s"Unexpected error: ${e.underlying}")
        InternalServerError(MessageResponse("Unexpected error occurred").asJson)
      }
    }
  }

}
