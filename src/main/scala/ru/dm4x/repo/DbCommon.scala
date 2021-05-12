package ru.dm4x.repo

import doobie.{ConnectionIO, Fragment}

object DbCommon {

//  val ddl1 = Fragment.const(createTablePlayersSql)
//  val ddl2 = Fragment.const(createTableGamesSql)

  def setup(): ConnectionIO[Unit] =
    for {
      _ <- Fragment.const(createTablePlayersSql).update.run
      _ <- Fragment.const(createTableGamesSql).update.run
    } yield ()

  val createTablePlayersSql: String =
    """CREATE TABLE players (
      |  id UUID PRIMARY KEY,
      |  name VARCHAR(100) NOT NULL,
      |  total_score INT);""".stripMargin

  val createTableGamesSql: String =
    """CREATE TABLE scores (
      |  id UUID PRIMARY KEY,
      |  game_id INT,
      |  player_id INT,
      |  score INT,
      |  FOREIGN KEY (player_id) REFERENCES players(id));""".stripMargin

}
