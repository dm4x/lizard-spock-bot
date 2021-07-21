package ru.dm4x.domain

import io.circe.generic.JsonCodec

import java.util.UUID

trait Result
object Win extends Result
object Lose extends Result

@JsonCodec final case class Player(id: String = UUID.randomUUID().toString, name: String, totalScore: Int = 0)

trait Figure {
  def checkWin(enemy: Figure): Result
}

object Stone extends Figure {
  override def checkWin(enemy: Figure): Result = enemy match {
    case Scissors => Win
    case Lizard => Win
    case _ => Lose
  }
}
object Paper extends Figure {
  override def checkWin(enemy: Figure): Result = enemy match {
    case Spock => Win
    case Stone => Win
    case _ => Lose
  }
}
object Scissors extends Figure {
  override def checkWin(enemy: Figure): Result = enemy match {
    case Paper => Win
    case Lizard => Win
    case _ => Lose
  }
}
object Lizard extends Figure{
  override def checkWin(enemy: Figure): Result = enemy match {
    case Paper => Win
    case Spock => Win
    case _ => Lose
  }
}
object Spock extends Figure{
  override def checkWin(enemy: Figure): Result = enemy match {
    case Stone => Win
    case Scissors => Win
    case _ => Lose
  }
}
