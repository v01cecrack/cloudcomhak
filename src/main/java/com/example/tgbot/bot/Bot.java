package com.example.tgbot.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
@Scope(value = "prototype")
public class Bot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private BotService botService;
    private final BotManager botManager;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            botService = botManager.botStart(update);
            log.info("Пользователь {} ввел: {}", update.getMessage().getFrom().getUserName(), update.getMessage().getText());
            if (update.getMessage().getText().equals("/start")) {
                if (botService.isCreated(chatId)) {
                    try {
                        execute(botService.findAndReturnRegistration(chatId));
                        botService.getUserDto().setStateStart();
                        return;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
                } else {
                    try {
                        execute(botService.sendWelcome(chatId));
                        return;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
                }
            }
        }
        if (update.hasCallbackQuery()) {
            log.info("Пользователь {} нажал кнопку {}", update.getCallbackQuery().getFrom().getUserName(), update.getCallbackQuery().getData());
            chatId = update.getCallbackQuery().getMessage().getChatId();
            //TODO CHECK
            if (update.getCallbackQuery().getData().equals("tests")) {
                try {
                    execute(botService.testsMessage(chatId));
                    botService.getUserDto().setStateCheck();
                    return;
                } catch (TelegramApiException e) {
                    throw new RuntimeException();
                }
            }

            if (botService.getUserDto().getState().equals("CHECK")) {
                String testName = update.getCallbackQuery().getData();
                try {
                    SendMessage sendMessage = botService.testStart(chatId, testName);
                    execute(sendMessage);
                    if (!sendMessage.getText().equals("Тест можно пройти только 1 раз")) {
                        botService.getUserDto().setStateTest();
                    }
                } catch (TelegramApiException e) {
                    throw new RuntimeException();
                }
                return;
            }

            if (botService.getUserDto().getState().equals("GROUP")) {
                String groupName = update.getCallbackQuery().getData();
                try {
                    execute(botService.saveUser(chatId, groupName));
                    return;
                } catch (TelegramApiException e) {
                    throw new RuntimeException();
                }
            }
            if (botService.getUserDto().getState().equals("ZERO")) {
                try {
                    execute(botService.sendWelcomeMessage(chatId));
                } catch (TelegramApiException e) {
                    throw new RuntimeException();
                }
            }
        }

        if (update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            chatId = update.getMessage().getChatId();

            switch (botService.getUserDto().getState()) {
                case "START":
                    try {
                        execute(botService.flagStart(chatId));
                        botService.getUserDto().setStateName();
                        break;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
                case "NAME":
                    try {
                        execute(botService.flagName(chatId, text));
                        botService.getUserDto().setStateSurname();
                        break;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
                case "SURNAME":
                    try {
                        execute(botService.flagSurname(chatId, text));
                        botService.getUserDto().setStateGroup();
                        break;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
                case "TEST":
                    try {
                        SendMessage sendMessage = botService.flagTest(chatId, text);
                        execute(sendMessage);
                        String regex = "Вы ответили правильно на \\d+ из \\d+";
                        if (sendMessage.getText().matches(regex)) {
                            botService.getUserDto().setStateZero();
                        } else {
                            break;
                        }
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
                default:
                    try {
                        execute(botService.sendTextMessage(chatId, "Неизвестная команда"));
                        break;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
            }
        }

    }

}



