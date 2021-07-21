package ru.dm4x

object AppConfig {
  /*
  *
  * token=wvzfsUCIbUMpqpbzy81Xr36A
  * &team_id=T020SMBKBT9
  * &team_domain=dm4x-workspace
  * &channel_id=C020SME3L95
  * &channel_name=testing-bot-for-bootcamp&user_id=U021L3B9YRX
  * &user_name=kindze
  * &command=%2Fplay&text=
  * &api_app_id=A021X907T8Q
  * &is_enterprise_install=false
  * &response_url=https%3A%2F%2Fhooks.slack.com%2Fcommands%2FT020SMBKBT9%2F2136073764000%2FxVgDQew6xoOqvKTxGdzzJgcl
  * &trigger_id=2124888827777.2026725657927.3fe598d26f6332d5ea3a91ae6781ca4f
  *
  * */
  val tokenRegex = "(token=\\[a-zA-Z0-9\\])&".r
  val teamIdRegex = "(team_id=\\[a-zA-Z0-9\\])&".r
}
