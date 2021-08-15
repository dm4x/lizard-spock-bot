package ru.dm4x.service

import ru.dm4x.domain.Player

import java.util.concurrent.atomic.AtomicReference

object GameService {
  trait Game {
    def getSize: Int

    def put(p: Player): List[Player]

    def getCurrentPlayers(): List[Player]

    def modifsyPlayer(id: String): List[Player]
  }

  def run(game: Game): Unit = {
    def myRunnable(name: String): Runnable = new Runnable {
      override def run(): Unit =
        game.put(Player(name = name))
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

    override def modifsyPlayer(id: String): List[Player] = gameRef.getAcquire

    override def getCurrentPlayers(): List[Player] = gameRef.getPlain

    def allHaveChoice: Boolean = gameRef.getPlain.exists(_.currentChoice == "")
  }

}
