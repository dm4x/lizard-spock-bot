package ru.dm4x.service

import cats.effect._
import cats.implicits._
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import doobie.util.meta.Meta
import ru.dm4x.domain.Player

import java.util.UUID
import java.time.{LocalDate, Year}

private final class BotStoreImpl[F[_]: Sync](tx: Transactor[F])(implicit ev: Bracket[F, Throwable]) extends BotService[F] {
  private implicit val uuidMeta: Meta[UUID] = Meta[String].timap(UUID.fromString)(_.toString)
  private implicit val yearMeta: Meta[Year] = Meta[Int].timap(Year.of)(_.getValue)
  private implicit val localDateMeta: Meta[LocalDate] =
    Meta[String].timap(LocalDate.parse)(_.toString)

  private val players: Fragment = fr"SELECT id, name, total_score FROM players"

  override def getPlayerById(id: UUID): F[Option[Player]] =
    (players ++ fr"WHERE id = $id").query[Player].option.transact(tx)

//  override def createBook(book: EditBook): F[Either[ValidationError, Book]] = {
//    for {
//      author <- getAuthor(book.authorId)
//      id <- Sync[F].delay(UUID.randomUUID())
//      res <- author match {
//        case Some(_) => {
//          sql"INSERT INTO books (id, title, author_id, year, genre) VALUES ($id, ${book.title}, ${book.authorId}, ${book.year}, ${book.genre})".update.run
//            .transact(tx) *>
//            Sync[F].pure(
//              Right[ValidationError, Book](
//                Book(id, book.authorId, book.title, book.year, book.genre)
//              )
//            )
//        }
//        case None => Sync[F].pure(Left(ValidationError.UnknownAuthor))
//      }
//    } yield res
//  }
//
//  override def updateBook(id: UUID, book: EditBook): F[Either[ValidationError, Book]] = {
//    for {
//      existing <- getBook(id)
//      author <- getAuthor(book.authorId)
//      res <- existing match {
//        case Some(_) =>
//          author match {
//            case Some(_) => {
//              sql"UPDATE books SET title = ${book.title}, author_id = ${book.authorId}, year = ${book.year}, genre = ${book.genre} WHERE id = $id".update.run
//                .transact(tx) *>
//                Sync[F].pure(
//                  Right[ValidationError, Book](
//                    Book(id, book.authorId, book.title, book.year, book.genre)
//                  )
//                )
//            }
//            case None => Sync[F].pure(Left(ValidationError.UnknownAuthor))
//          }
//        case None => Sync[F].pure(Left(ValidationError.BookNotFound))
//      }
//    } yield res
//  }
//
//  override def deleteBook(id: UUID): F[Option[ValidationError]] = {
//    for {
//      existing <- getBook(id)
//      res <- existing match {
//        case Some(_) =>
//          sql"DELETE books WHERE id = $id".update.run.transact(tx) *> Sync[F].pure(none)
//        case None => Sync[F].pure(Some(ValidationError.BookNotFound))
//      }
//    } yield res
//  }

  override def getAllPlayers: F[List[Player]] =
    players.query[Player].to[List].transact(tx)

  def createPlayer(player: Player): F[Player] = {
    for {
      id <- Sync[F].delay(UUID.randomUUID())
      res <-
        sql"INSERT INTO players (id, name, total_score) VALUES ($id, ${player.name}, 0)".update.run
          .transact(tx) *>
          Sync[F].pure(Player(id, player.name, player.totalScore))

    } yield res
  }
}
