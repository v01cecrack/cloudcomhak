package com.example.tgbot;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static com.example.tgbot.Flag.*;

@Slf4j
@Service
@AllArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final UserRepository repository;
    private static Flag flag;
    private UserDto userDto;
    private TestData testData;
    private TestSession testSession;
    private SimpleTest simpleTest;

    @Override
    public String getBotUsername() {
        return "v01cecrack_bot";
    }

    @Override
    public String getBotToken() {
        return "6536901586:AAG0FL0rzEeJq-uJdVgRJO6I4KP85lh2lCk";
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId;
        if (update.hasMessage()) {
            if (update.getMessage().getText().equals("/start")) {
                flag = START;
                chatId = update.getMessage().getChatId();
                sendTextMessage(chatId, "Вам нужно зарегистрироваться");
                return;
            }
        }
        if (update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            switch (flag) {
                case START:
                    userDto.setChatId(chatId);
                    flag = NAME;
                    sendTextMessage(chatId, "Введите ваше имя");
                    break;
                case NAME:
//                    String name = update.getMessage().getText();
                    userDto.setName(text);
                    flag = SURNAME;
                    sendTextMessage(chatId, "Введите вашу фамилию");
                    break;
                case SURNAME:
//                    String surname = update.getMessage().getText();
                    userDto.setSurname(text);
                    flag = GROUP;
                    sendTextMessage(chatId, "Введите вашу группу");
                    break;
                case GROUP:
                    //TODO тут кнопки с доступными группами
//                    String group = update.getMessage().getText();
                    userDto.setGroup(text);
                    repository.save(UserMapper.toUser(userDto));
                    sendTextMessage(chatId, "Сохранено");
                    flag = CHECK;
                    break;
                case CHECK:
                    sendTextMessage(chatId, userDto.toString());
                    sendTextMessage(chatId, "Выберите тест");
                    testData.setQuestions(simpleTest.getQuestions());
                    testData.setCorrectAnswers(simpleTest.getCorrectAnswers());
                    flag = TEST;
                    break;
                //TODO flag = register
                case TEST:
                    if(testSession.getCurrentQuestion() <= testData.getQuestions().size()) {
                        if (testSession.getCurrentQuestion() != 0) {
                            testSession.userAnswers.add(text);
                        }
                            testSession.currentQuestion++;
                        if (testSession.currentQuestion <= testData.getQuestions().size()) {
                            sendTextMessage(chatId, testData.getQuestions().get(testSession.getCurrentQuestion() - 1));
                        }
                        return;
                    }
//                    testSession.currentQuestion = 0;
                    int questions = testData.getQuestions().size();
                    int correctQuestions = 0;
                    for (int i = 0; i < questions; i++) {
                        if (testSession.userAnswers.get(i).equals(testData.getCorrectAnswers().get(i))) {
                            correctQuestions++;
                        }
                    }
                    sendTextMessage(chatId, "Вы ответили правильно на " + correctQuestions + " из " + questions);
                    flag = START;
                default:
                    sendWelcomeMessage(chatId);
                    break;
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
        row1.add(InlineKeyboardButton.builder().text("Начать").callbackData("start").build());
        keyboard.add(row1);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }


}
