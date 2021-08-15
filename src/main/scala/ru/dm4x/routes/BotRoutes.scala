package ru.dm4x.routes

import cats.effect._
import cats.syntax.all._
import org.http4s.client.dsl.io._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.{EntityDecoder, HttpRoutes, Method, Request, Uri}
import org.http4s.implicits.{http4sKleisliResponseSyntaxOptionT, http4sLiteralsSyntax}
import ru.dm4x.domain.{BotRequest, JsonDto, Player}
import ru.dm4x.service.{BotService, GameService}
import ru.dm4x.service.GameService.CurrentGame
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.generic.auto._
import sttp.tapir.server.http4s.Http4sServerInterpreter

class BotRoutes(repo: BotService[IO], currentGame: CurrentGame) {
  implicit val cs: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.global)
  implicit val t: Timer[IO]         = IO.timer(scala.concurrent.ExecutionContext.global)

  implicit lazy val players: Schema[Player] = Schema.derived

  val inputParams: Endpoint[Unit, Unit, Unit, Any] = endpoint.post
    .description("main endpoint")
    .in("lizard-bot")

  val whoPlaysRoute: Endpoint[BotRequest, Unit, List[Player], Any] = inputParams.post
    .description("list of actual players")
    .in("whoplays")
    .in(formBody[BotRequest])
    .out(jsonBody[List[Player]])

  val rulesRoute: Endpoint[BotRequest, Unit, String, Any] = inputParams.post
    .description("rules of the game")
    .in("rules")
    .in(formBody[BotRequest])
    .out(jsonBody[String])

  val playRoute: Endpoint[BotRequest, Unit, List[Player], Any] = inputParams.post
    .description("add new player")
    .in("play")
    .in(formBody[BotRequest])
    .out(jsonBody[List[Player]])

  val choicesRoute: Endpoint[BotRequest, Unit, Boolean, Any] = inputParams.post
    .description("check choices")
    .in("choices")
    .in(formBody[BotRequest])
    .out(jsonBody[Boolean])

  val iChooseRoute: Endpoint[BotRequest, Unit, String, Any] = inputParams.post
    .description("choose something")
    .in("ichoose")
    .in(formBody[BotRequest])
    .out(jsonBody[String])

  val playersListingRoutes: HttpRoutes[IO] =
    Http4sServerInterpreter.toRoutes(whoPlaysRoute)(_ => IO(currentGame.getCurrentPlayers().asRight[Unit]))

  val rulesRoutes: HttpRoutes[IO] =
    Http4sServerInterpreter.toRoutes(rulesRoute)(request => sendResponse(request, JsonDto.rules).asRight[Unit].sequence)

  val playRoutes: HttpRoutes[IO] =
    Http4sServerInterpreter.toRoutes(playRoute)(request =>
      IO(currentGame.put(Player(name = request.user_name)).asRight[Unit]))

  val choicesRoutes: HttpRoutes[IO] =
    Http4sServerInterpreter.toRoutes(choicesRoute)(_ => IO(currentGame.allHaveChoice.asRight[Unit]))

  val iChooseRoutes: HttpRoutes[IO] =
    Http4sServerInterpreter.toRoutes(choicesRoute)(_ => IO(currentGame.allHaveChoice.asRight[Unit]))

  private def sendResponse(request: BotRequest, message: String) = {
    val uri = Uri.fromString(request.response_url).valueOr(throw _)
    BlazeClientBuilder[IO](scala.concurrent.ExecutionContext.global).resource.use { client =>
      client.expect[String](Method.POST(message, uri))
    }
  }

  private[dm4x] val httpApp = {
    playersListingRoutes <+> rulesRoutes <+> playRoutes <+> choicesRoutes <+> iChooseRoutes
  }.orNotFound

}
