package users.domain

import cats.kernel.Eq

final case class Token(value: String) extends AnyVal

object Token {
  implicit val eq: Eq[Token] =
    Eq.fromUniversalEquals
}