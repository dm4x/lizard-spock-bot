package ru.dm4x

import cats.effect.{Async, Blocker, ContextShift, ExitCode, IO, IOApp, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.blaze.BlazeServerBuilder
import ru.dm4x.routes.BotRoutes
import ru.dm4x.service.BotService

import scala.concurrent.ExecutionContext

object LizardSpokServer extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
      pooled[IO].use { tx =>
        for {
          repo <- BotService.of[IO](tx)
          booksCrud = new BotRoutes(repo)
          server <- httpServer(booksCrud.httpApp)
        } yield ExitCode.Success
    }

  private def httpServer(httpRoutes: HttpApp[IO]): IO[Unit] = {
    BlazeServerBuilder[IO](ExecutionContext.global)
      .bindHttp(port = 8080, host = "0.0.0.0")
      .withHttpApp(httpRoutes)
      .serve
      .compile
      .drain
  }

  private def pooled[F[_] : ContextShift : Async]: Resource[F, Transactor[F]] = {
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](10)
      be <- Blocker[F]
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = DbConfig.dbDriverName,
        url = DbConfig.dbUrl,
        user = DbConfig.dbUser,
        pass = DbConfig.dbPwd,
        connectEC = ce, // await connection on this EC
        blocker = be, // execute JDBC operations on this EC
      )
    } yield xa
  }

}
