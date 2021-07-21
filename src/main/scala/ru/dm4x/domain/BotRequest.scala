package ru.dm4x.domain

final case class BotRequest(token: String,
                            team_id: String,
                            team_domain: String,
                            channel_id: String,
                            channel_name: String,
                            user_id: String,
                            user_name: String,
                            command: String,
                            text: String,
                            api_app_id: String,
                            is_enterprise_install: String,
                            response_url: String,
                            trigger_id: String)
