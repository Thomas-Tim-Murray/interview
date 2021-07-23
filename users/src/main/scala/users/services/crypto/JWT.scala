package users.services.crypto

import users.domain._

import pdi.jwt.{JwtCirce, JwtAlgorithm, JwtClaim}

import io.circe.Json
import io.circe.syntax._
import io.circe.generic.auto._

import java.time.Instant

object JWT {
  private val key = "PaidyIsPrettyCool"
  private val algo = JwtAlgorithm.HS256

  def getToken(userUuid: User.Id): Token = {
    val claim = JwtClaim(
      content = userUuid.asJson.toString,
      issuedAt = Some(Instant.now.getEpochSecond)
    )
    
    Token(JwtCirce.encode(claim, key, algo))
  }

  // Unfortunately I was not able to get this working, and ran out of time.
  // Ideally the token would be passed as a header by the user, and I would
  // use that header to find the user making the call.
  /*def getUserUuid(token: Token): User.Id = {
    val content = JwtCirce.decode(token.value, key, algo)
    
    User.Id(content.value)
  }*/
}
