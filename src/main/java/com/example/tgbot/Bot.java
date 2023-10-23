package com.example.tgbot;

import com.example.tgbot.group.Group;
import com.example.tgbot.group.GroupRepository;
import com.example.tgbot.question.Question;
import com.example.tgbot.test.Test;
import com.example.tgbot.testgroup.TestGroup;
import com.example.tgbot.testgroup.TestGroupRepository;
import com.example.tgbot.testquestion.TestQuestionRepository;
import com.example.tgbot.user.UserDto;
import com.example.tgbot.user.UserMapper;
import com.example.tgbot.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@AllArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final UserRepository repository;
    private final GroupRepository groupRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final TestGroupRepository testGroupRepository;
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
                chatId = update.getMessage().getChatId();
                if (repository.findById(chatId).isEmpty()) {
                    sendTextMessage(chatId, "Вам нужно зарегистрироваться");
                    flag = START;
                    startMessage(chatId);
                    return;
                } else {
                    sendWelcomeMessage(chatId);
//                    flag = CHECK;
                }
            }
        }
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            //TODO CHECK
            if (update.getCallbackQuery().getData().equals("tests")) {
                sendTextMessage(chatId, "Список доступных тестов: \n ТУТ ТИПО БУДУТ КНОПКИ С НАЗВАНИЯМИ ТЕСТОВ");
                List<TestGroup> testGroup = testGroupRepository.findTestGroupsByGroup_Name(userDto.getGroup());
                List<Test> testList = testGroup.stream().map(TestGroup::getTest).collect(Collectors.toList());
                List<String> testNames = testList.stream().map(Test::getTestName).collect(Collectors.toList());
                sendTextMessage(chatId,testNames.toString());
                flag = CHECK;
//                return;
            }
        }
//        if (update.getMessage().hasText()) {
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
                List<Group> groups = groupRepository.findAll();
                List<String> groupsNames = groups.stream().map(Group::getName).collect(Collectors.toList());
                sendTextMessage(chatId, groupsNames.toString());
                sendTextMessage(chatId, "Введите вашу группу");
                break;
            case GROUP:
                //TODO тут кнопки с доступными группами
//                    String group = update.getMessage().getText();
                userDto.setGroup(text);
                repository.save(UserMapper.toUser(userDto));
                sendTextMessage(chatId, "Сохранено");
//                flag = CHECK;
                sendWelcomeMessage(chatId);
                break;
            case CHECK:
                sendTextMessage(chatId, userDto.toString()); //TODO delete this
//                sendTextMessage(chatId, "Выберите тест");
                //TODO Подгружаем тест из базы и сохраняем в testData
                List<Question> questionList = testQuestionRepository.findQuestionsByTestId(1L);

                List<String> questions = questionList.stream()
                        .map(Question::getQuestionText)
                        .collect(Collectors.toList());

                List<String> answers = questionList.stream()
                        .map(Question::getQuestionAnswer)
                        .collect(Collectors.toList());

                testData.setQuestions(questions);
                testData.setCorrectAnswers(answers);
                flag = TEST;
                break;
            //TODO flag = register
            case TEST:
                if (testSession.getCurrentQuestion() <= testData.getQuestions().size()) {
                    if (testSession.getCurrentQuestion() != 0) {
                        testSession.userAnswers.add(text);
                    }
                    testSession.currentQuestion++;
                    if (testSession.currentQuestion <= testData.getQuestions().size()) {
                        sendTextMessage(chatId, testData.getQuestions().get(testSession.getCurrentQuestion() - 1));
                    return;
                    }
                }
//                    testSession.currentQuestion = 0;
                int questionsCount = testData.getQuestions().size();
                int correctQuestions = 0;
                for (int i = 0; i < questionsCount; i++) {
                    if (testSession.userAnswers.get(i).equals(testData.getCorrectAnswers().get(i))) {
                        correctQuestions++;
                    }
                }
                sendTextMessage(chatId, "Вы ответили правильно на " + correctQuestions + " из " + questionsCount);
                flag = START;
                sendWelcomeMessage(chatId);
            default:
                sendWelcomeMessage(chatId);
                break;
        }
    }

//}

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
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        String text = "Старт";
        message.setText(String.format("Нажмите \"%s\", чтобы начать", text));
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
