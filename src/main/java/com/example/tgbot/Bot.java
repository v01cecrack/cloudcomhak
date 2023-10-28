package com.example.tgbot;

import com.example.tgbot.group.Group;
import com.example.tgbot.group.GroupRepository;
import com.example.tgbot.question.Question;
import com.example.tgbot.result.Result;
import com.example.tgbot.result.ResultRepository;
import com.example.tgbot.test.Test;
import com.example.tgbot.test.TestRepository;
import com.example.tgbot.testgroup.TestGroupRepository;
import com.example.tgbot.testquestion.TestQuestionRepository;
import com.example.tgbot.user.UserDto;
import com.example.tgbot.user.UserMapper;
import com.example.tgbot.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

import static com.example.tgbot.Flag.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final UserRepository repository;
    private final GroupRepository groupRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final TestGroupRepository testGroupRepository;
    private final TestRepository testRepository;
    private final ResultRepository resultRepository;
    private Flag flag;
    @Autowired
    private UserDto userDto;
    private final TestData testData;
    private final TestSession testSession;
    private Long testId;

    private final BotService botService;

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
            log.info("Пользователь {} ввел: {}", update.getMessage().getFrom().getUserName(), update.getMessage().getText());
            if (update.getMessage().getText().equals("/start")) {
                chatId = update.getMessage().getChatId();
                if (botService.isCreated(chatId)) {
                    try {
                        execute(botService.messageStart1(chatId));
                        flag = START;
                        return;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
                } else {
                    try {
                        execute(botService.messageStart2(chatId));
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
                    flag = CHECK;
                    return;
                } catch (TelegramApiException e) {
                    throw new RuntimeException();
                }
            }

            //TODO загрузить тест по названию кнопки НУЖЕН ТЕСТ ID
            if (flag == CHECK) {
                String testName = update.getCallbackQuery().getData();
                try {
                    SendMessage sendMessage = botService.testStart(chatId, testName);
                    execute(sendMessage);
                    if (!sendMessage.getText().equals("Тест можно пройти только 1 раз")) { //TODO проверка на чат айди чтобы не было конфликтов с другими
                        flag = TEST;
                    }
                } catch (TelegramApiException e) {
                    throw new RuntimeException();
                }
                return;
            }

            if (flag == GROUP) {
                String groupName = update.getCallbackQuery().getData();
                try {
                    execute(botService.saveUser(chatId, groupName));
                    return;
                } catch (TelegramApiException e) {
                    throw new RuntimeException();
                }
            }
            if (flag == ZERO) {
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

            switch (flag) {
                case START:
                    try {
                        execute(botService.flagStart(chatId));
                        flag = NAME;
                        break;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
                case NAME:
                    try {
                        execute(botService.flagName(chatId, text));
                        flag = SURNAME;
                        break;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
                case SURNAME:
                    try {
                        execute(botService.flagSurname(chatId, text));
                        flag = GROUP;
                        break;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException();
                    }
//                    break;
                case TEST:
                    try {
                        SendMessage sendMessage = botService.flagTest(chatId, text);
                        execute(sendMessage);
                        String regex = "Вы ответили правильно на \\d+ из \\d+";
                        if (sendMessage.getText().matches(regex)) {
                            flag = ZERO;
                        }
                        else {
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
