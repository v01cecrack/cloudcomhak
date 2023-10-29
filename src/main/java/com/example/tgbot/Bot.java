package com.example.tgbot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Scope(value = "prototype")
public class Bot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private BotService botService;
    private final BotManager botManager;
//    private Flag flag;

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
//                chatId = update.getMessage().getChatId();
                if (botService.isCreated(chatId)) {
                    try {
                        execute(botService.messageStart1(chatId));
//                        botService.flag = START;
//                        botService.state.setStateStart();
                        botService.getUserDto().setStateStart();
//                        botManager.userBots.put(chatId, botService);
                        return;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
                } else {
                    try {
                        execute(botService.messageStart2(chatId));
//                        botManager.userBots.put(chatId, botService);
                        return;
//                        flag = START;
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
//                    botService.flag = CHECK;
                    botService.getUserDto().setStateCheck();

//                    botManager.userBots.put(chatId, botService);
                    return;
                } catch (TelegramApiException e) {
                    throw new RuntimeException();
                }
            }

            //TODO загрузить тест по названию кнопки НУЖЕН ТЕСТ ID
            if (botService.getUserDto().getState().equals("CHECK")) {
                String testName = update.getCallbackQuery().getData();
                try {
                    SendMessage sendMessage = botService.testStart(chatId, testName);
                    execute(sendMessage);
                    if (!sendMessage.getText().equals("Тест можно пройти только 1 раз")) { //TODO проверка на чат айди чтобы не было конфликтов с другими
//                        botService.flag = TEST;
                        botService.getUserDto().setStateTest();
//                        botManager.userBots.put(chatId, botService);
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
//                    botManager.userBots.put(chatId, botService);
                    return;
                } catch (TelegramApiException e) {
                    throw new RuntimeException();
                }
            }
            if (botService.getUserDto().getState().equals("ZERO")) {
                try {
                    execute(botService.sendWelcomeMessage(chatId));
//                    botManager.userBots.put(chatId, botService);
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
//                        botService.flag = NAME;
                        botService.getUserDto().setStateName();
//                        botManager.userBots.put(chatId, botService);
                        break;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
                case "NAME":
                    try {
                        execute(botService.flagName(chatId, text));
//                        botService.flag = SURNAME;
                        botService.getUserDto().setStateSurname();
//                        botManager.userBots.put(chatId, botService);
                        break;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
                case "SURNAME":
                    try {
                        execute(botService.flagSurname(chatId, text));
//                        botService.flag = GROUP;
                        botService.getUserDto().setStateGroup();
//                        botManager.userBots.put(chatId, botService);
                        break;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
//                    break;
                case "TEST":
                    try {
                        SendMessage sendMessage = botService.flagTest(chatId, text);
                        execute(sendMessage);
                        String regex = "Вы ответили правильно на \\d+ из \\d+";
                        if (sendMessage.getText().matches(regex)) {
//                            botService.flag = ZERO;
                            botService.getUserDto().setStateZero();
//                            botManager.userBots.put(chatId, botService);
                        } else {
//                            botManager.userBots.put(chatId, botService);
                            break;
                        }
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
//                    botService.clearSession();
//                    break;
                default:
                    try {
                        execute(botService.sendWelcomeMessage(chatId));
//                        botManager.userBots.put(chatId, botService);
                        break;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
            }
        }

    }

    private void sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendWelcomeMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Добро пожаловать! Выберите действие:");

        InlineKeyboardMarkup keyboardMarkup = createKeyboard();
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Выбрать тест").callbackData("tests").build());
        keyboard.add(row1);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    private void startMessage(long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Начать регистрацию"));
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Нажмите: Начать регистрацию");
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup testButtons(List<String> testNames) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (int i = 0; i < testNames.size(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(InlineKeyboardButton.builder().text(testNames.get(i)).callbackData(testNames.get(i)).build());
            keyboard.add(row);
            inlineKeyboardMarkup.setKeyboard(keyboard);
        }
        return inlineKeyboardMarkup;
    }

    private void sendTestsButtons(long chatId, List<String> testNames) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Список доступных тестов:");
        InlineKeyboardMarkup keyboardMarkup = testButtons(testNames);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup groupButtons(List<String> groupNames) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (int i = 0; i < groupNames.size(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(InlineKeyboardButton.builder().text(groupNames.get(i)).callbackData(groupNames.get(i)).build());
            keyboard.add(row);
            inlineKeyboardMarkup.setKeyboard(keyboard);
        }
        return inlineKeyboardMarkup;
    }

    private void sendGroupButtons(long chatId, List<String> groupNames) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберете свою группу:");
        InlineKeyboardMarkup keyboardMarkup = groupButtons(groupNames);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}



