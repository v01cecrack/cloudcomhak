package com.example.tgbot.bot;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@Data
@PropertySource("config.properties")
public class BotConfig {
    @Value("${bot.name}") String botName;
    @Value("${bot.token}") String token;
}
