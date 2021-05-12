package ru.dm4x.service

import cats.effect.Sync
import doobie.util.transactor.Transactor
import ru.dm4x.domain.{Player}
import ru.dm4x.repo.DbCommon
import doobie.implicits._
import cats.implicits._
import ru.dm4x.service.BotService.ValidationError

import java.util.UUID

trait BotService[F[_]] {
  def getAllPlayers: F[List[Player]]
  def getPlayerById(bookId: UUID): F[Option[Player]]
}

object BotService {
  abstract sealed class ValidationError(val message: String) extends Throwable

  object ValidationError {
    final case object PlayerNotFound extends ValidationError("Player not found")
  }

  def of[F[_]: Sync](tx: Transactor[F]): F[BotService[F]] = {
    for {
      _ <- DbCommon.setup().transact(tx)
    } yield new BotStoreImpl(tx)
  }
}
