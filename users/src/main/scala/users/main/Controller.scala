package users.main

import users.dto._
import users.domain._
import users.services.usermanagement._
import users.services.crypto._

import scala.concurrent.Await
import scala.concurrent.duration._

class Controller(application: Application, timeout: Int) {
  def signUp(
    newUser: NewUserRequest
  ): Error Either User = {
    try {
      Await.result(
        application.services.userManagement.signUp(
          UserName(newUser.username), 
          EmailAddress(newUser.emailAddress), 
          newUser.password.map(password => Password(password))
        ),
        Duration(timeout, MILLISECONDS)
      )
    } catch {
      case e: Throwable => Left(Error.System(e))
    }
  }

  def signIn(
    signInRequest: SignInRequest
  ): Error Either Token = {
    try {
      val maybeUser = Await.result(
        application.services.userManagement.getByUserName(UserName(signInRequest.username)),
        Duration(timeout, MILLISECONDS)
      )
      maybeUser match {
        case Left(error) => Left(error)
        case Right(user) => {
          if (user.password.isDefined && user.password.get.value == signInRequest.password) {
            Right(JWT.getToken(user.id))
          }
          else {
            Left(Error.IncorrectCredentials)
          }
        }
      }
    } catch {
      case e: Throwable => Left(Error.System(e))
    }
  }

  def get(
    id: User.Id
  ): Error Either User = {
    try {
      Await.result(
        application.services.userManagement.get(id),
        Duration(timeout, MILLISECONDS)
      )
    } catch {
      case e: Throwable => Left(Error.System(e))
    }
  }

  def getAll(): Error Either List[User] = {
    try {
      Await.result(
        application.services.userManagement.all(),
        Duration(timeout, MILLISECONDS)
      )
    } catch {
      case e: Throwable => Left(Error.System(e))
    }
  }

  def updateEmail(
      id: User.Id,
      updateRequest: UpdateEmailRequest
  ): Error Either User = {
    try {
      Await.result(
        application.services.userManagement.updateEmail(
          id,
          EmailAddress(updateRequest.emailAddress)
        ),
        Duration(timeout, MILLISECONDS)
      )
    } catch {
      case e: Throwable => Left(Error.System(e))
    }
  }

  def updatePassword(
      id: User.Id,
      updateRequest: UpdatePasswordRequest
  ): Error Either User = {
    try {
      Await.result(
        application.services.userManagement.updatePassword(
          id,
          Password(updateRequest.password)
        ),
        Duration(timeout, MILLISECONDS)
      )
    } catch {
      case e: Throwable => Left(Error.System(e))
    }
  }

  def resetPassword(
      id: User.Id
  ): Error Either User = {
    try {
      Await.result(
        application.services.userManagement.resetPassword(id),
        Duration(timeout, MILLISECONDS)
      )
    } catch {
      case e: Throwable => Left(Error.System(e))
    }
  }

  def block(
      id: User.Id
  ): Error Either User = {
    try {
      Await.result(
        application.services.userManagement.block(id),
        Duration(timeout, MILLISECONDS)
      )
    } catch {
      case e: Throwable => Left(Error.System(e))
    }
  }

  def unblock(
      id: User.Id
  ): Error Either User = {
    try {
      Await.result(
        application.services.userManagement.unblock(id),
        Duration(timeout, MILLISECONDS)
      )
    } catch {
      case e: Throwable => Left(Error.System(e))
    }
  }

  def delete(
      id: User.Id
  ): Error Either Done = {
    try {
      Await.result(
        application.services.userManagement.delete(id),
        Duration(timeout, MILLISECONDS)
      )
    } catch {
      case e: Throwable => Left(Error.System(e))
    }
  }
}