package ru.dm4x.routes

import io.circe.generic.JsonCodec
import sttp.tapir.Schema

object RequestErrors {

  sealed trait ErrorInfo
  @JsonCodec case class NotFound(what: String)          extends ErrorInfo
  @JsonCodec case class Unauthorized(realm: String)     extends ErrorInfo
  @JsonCodec case class Unknown(code: Int, msg: String) extends ErrorInfo
  case object NoContent                                 extends ErrorInfo

  implicit lazy val notFoundSchema: Schema[NotFound]         = Schema.derived
  implicit lazy val unauthorizedSchema: Schema[Unauthorized] = Schema.derived
  implicit lazy val unknownSchema: Schema[Unknown]           = Schema.derived

}
