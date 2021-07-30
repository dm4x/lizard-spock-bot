package ru.dm4x.service

import ru.dm4x.domain.Player

import java.util.concurrent.atomic.AtomicReference

object GameService {
  trait Game {
    def getSize: Int

    def put(p: Player): List[Player]

    def getCurrentPlayers(): List[Player]
  }

  def run(game: Game): Unit = {
    def myRunnable(name: String): Runnable = new Runnable {
      override def run(): Unit = {
        println(s"| Thread ${Thread.currentThread().getName}| game size now: ${game.getSize}")
        println(s"| Thread ${Thread.currentThread().getName}| trying to add a player $name")
        game.put(Player(name = name))
        println(s"| Thread ${Thread.currentThread().getName}| game size now: ${game.getSize}")
        println(s"| Thread ${Thread.currentThread().getName}| current players: ${game.getCurrentPlayers()}")
      }
    }
  }

  class CurrentGame extends Game {
    private val list                                   = List.empty
    private val gameRef: AtomicReference[List[Player]] = new AtomicReference(List.empty)
    override def getSize: Int                          = gameRef.get().length
    override def put(p: Player): List[Player] =
      gameRef.updateAndGet { playersList =>
        println(s"Before $playersList")
        if (!playersList.contains(p)) {
          playersList ++ List(p)
        } else playersList
      }

    override def getCurrentPlayers(): List[Player] = gameRef.getPlain
  }
}
