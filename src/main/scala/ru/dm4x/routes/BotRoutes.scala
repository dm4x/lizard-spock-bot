package ru.dm4x.routes

import cats.Applicative
import cats.effect.IO
import cats.implicits.toSemigroupKOps
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.{EntityEncoder, HttpRoutes}
import org.http4s.dsl.io._
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import ru.dm4x.service.BotService

class BotRoutes(repo: BotService[IO]) {

  private def fromOption[T](value: Option[T])(implicit encoder: EntityEncoder[IO, T]) =
    value match {
      case Some(v) => Ok(v)(IO.ioEffect, encoder)
      case None    => NotFound()(Applicative[IO])
    }

  val rules: String =
    """Ножницы режут бумагу.\r
      |Бумага заворачивает камень.\r
      |Камень давит ящерицу.\r
      |Ящерица травит Спока.\r
      |Спок ломает ножницы.\r
      |Ножницы обезглавливают ящерицу.\r
      |Ящерица съедает бумагу.\r
      |На бумаге улики против Спока.\r
      |Спок испаряет камень.\r
      |И как обычно камень затупляет ножницы.""".stripMargin

  private val readRules: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case req @ POST -> Root / "rules" =>
      Ok(req.as[String].map(_ => rules))

    case GET -> Root / "rules" =>
      Ok(rules)

  }

  private[dm4x] val httpApp = {
    readRules
  }.orNotFound

}
