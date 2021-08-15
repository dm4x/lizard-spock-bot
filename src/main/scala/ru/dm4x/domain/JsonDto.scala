package ru.dm4x.domain

import io.circe.{Decoder, Encoder}
import io.circe.generic.JsonCodec
import io.circe.generic.semiauto._

object JsonDto {

  val rules: String = s"""{"text": "
                        Ножницы режут бумагу.
                        Бумага заворачивает камень.
                        Камень давит ящерицу.
                        Ящерица травит Спока.
                        Спок ломает ножницы.
                        Ножницы обезглавливают ящерицу.
                        Ящерица съедает бумагу.
                        На бумаге улики против Спока.
                        Спок испаряет камень.
                        И как обычно камень затупляет ножницы."}""".stripMargin

  @JsonCodec final case class Joke(pretty: String = "\"I'm not crazy. My mother had me tested.\"")

  implicit lazy val encoder: Encoder[Player] = deriveEncoder[Player]
  implicit lazy val decoder: Decoder[Player] = deriveDecoder[Player]

}
