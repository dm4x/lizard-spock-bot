package ru.dm4x.routes

import cats.effect._
import cats.syntax.all._
import org.http4s.HttpRoutes
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import ru.dm4x.domain.JsonDto.{CurrentPlayers, Rules}
import ru.dm4x.domain.{BotRequest, Player}
import ru.dm4x.service.BotService
import ru.dm4x.service.GameService.CurrentGame
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.generic.auto._
import sttp.tapir.server.http4s.Http4sServerInterpreter

class BotRoutes(repo: BotService[IO], currentGame: CurrentGame) {
  implicit val cs: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.global)
  implicit val t: Timer[IO]         = IO.timer(scala.concurrent.ExecutionContext.global)

  implicit lazy val rules: Schema[Rules]    = Schema.derived
  implicit lazy val players: Schema[Player] = Schema.derived

  val inputParams: Endpoint[Unit, Unit, Unit, Any] = endpoint.post
    .description("main endpoint")
    .in("lizard-bot")

  val whoPlaysRoute: Endpoint[BotRequest, Unit, Player, Any] = inputParams.post
    .description("list of actual players")
    .in("whoplays")
    .in(formBody[BotRequest])
    .out(jsonBody[Player])

  val rulesRoute: Endpoint[BotRequest, Unit, String, Any] = inputParams.post
    .description("rules of the game")
    .in("rules")
    .in(formBody[BotRequest])
    .out(jsonBody[String])

  val playRoute: Endpoint[BotRequest, Unit, String, Any] = inputParams.post
    .description("add new player")
    .in("play")
    .in(formBody[BotRequest])
    .out(jsonBody[String])

  val playersListingRoutes: HttpRoutes[IO] =
    Http4sServerInterpreter.toRoutes(whoPlaysRoute)(_ => IO(currentGame.getPlayers.asRight[Unit]))
  val rulesRoutes: HttpRoutes[IO] =
    Http4sServerInterpreter.toRoutes(rulesRoute)(_ => IO(Rules().pretty.asRight[Unit]))
  val playRoutes: HttpRoutes[IO] =
    Http4sServerInterpreter.toRoutes(playRoute)(request =>
      IO(CurrentPlayers(currentGame.put(Player(name = request.user_name))).toString.asRight[Unit]))

  private[dm4x] val httpApp = {
    playersListingRoutes <+> rulesRoutes <+> playRoutes
  }.orNotFound

}
