package com.example.tgbot;

import com.example.tgbot.group.Group;
import com.example.tgbot.group.GroupRepository;
import com.example.tgbot.question.Question;
import com.example.tgbot.result.Result;
import com.example.tgbot.result.ResultRepository;
import com.example.tgbot.test.Test;
import com.example.tgbot.test.TestRepository;
import com.example.tgbot.testgroup.TestGroup;
import com.example.tgbot.testgroup.TestGroupRepository;
import com.example.tgbot.testquestion.TestQuestionRepository;
import com.example.tgbot.user.UserDto;
import com.example.tgbot.user.UserMapper;
import com.example.tgbot.user.UserRepository;
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
public class Bot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final UserRepository repository;
    private final GroupRepository groupRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final TestGroupRepository testGroupRepository;
    private final TestRepository testRepository;
    private final ResultRepository resultRepository;
    private static Flag flag;
    private UserDto userDto;
    private final TestData testData;
    private final TestSession testSession;
    private Long testId;

    public Bot(BotConfig botConfig, UserRepository repository, GroupRepository groupRepository, TestQuestionRepository testQuestionRepository, TestGroupRepository testGroupRepository, TestRepository testRepository, ResultRepository resultRepository,UserDto userDto, TestData testData, TestSession testSession) {
        this.botConfig = botConfig;
        this.repository = repository;
        this.groupRepository = groupRepository;
        this.testQuestionRepository = testQuestionRepository;
        this.testGroupRepository = testGroupRepository;
        this.testRepository = testRepository;
        this.resultRepository = resultRepository;
        this.userDto = userDto;
        this.testData = testData;
        this.testSession = testSession;
    }

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
                if (repository.findById(chatId).isEmpty()) {
                    sendTextMessage(chatId, "Вам нужно зарегистрироваться");
                    flag = START;
                    startMessage(chatId);
                    return;
                } else {
                    sendWelcomeMessage(chatId);
                    return;
//                    flag = CHECK;
                }
            }
        }
        if (update.hasCallbackQuery()) {
            log.info("Пользователь {} нажал кнопку {}", update.getCallbackQuery().getFrom().getUserName(), update.getCallbackQuery().getData());
            chatId = update.getCallbackQuery().getMessage().getChatId();
            //TODO CHECK
            if (update.getCallbackQuery().getData().equals("tests")) {
                userDto = UserMapper.toUserDto(repository.findById(chatId).orElseThrow(RuntimeException::new));
                flag = CHECK;
                List<TestGroup> testGroup = testGroupRepository.findTestGroupsByGroup_Name(userDto.getGroup());
                List<Test> testList = testGroup.stream().map(TestGroup::getTest).collect(Collectors.toList());
                List<String> testNames = testList.stream().map(Test::getTestName).collect(Collectors.toList());
                sendTestsButtons(chatId, testNames);
                return;
            }

            //TODO загрузить тест по названию кнопки НУЖЕН ТЕСТ ID
            if (flag == CHECK) {
                String testName = update.getCallbackQuery().getData();
                Test test = testRepository.findByTestName(testName);
                testId = test.getTestId();
                if(resultRepository.findFirstByUserAndTest(UserMapper.toUser(userDto), test).isPresent()) {
                    sendTextMessage(chatId, "Тест можно пройти только 1 раз");
                    return;
                }
                //TODO Подгружаем тест из базы и сохраняем в testData
                List<Question> questionList = testQuestionRepository.findQuestionsByTestId(testId);

                List<String> questions = questionList.stream()
                        .map(Question::getQuestionText)
                        .collect(Collectors.toList());

                List<String> answers = questionList.stream()
                        .map(Question::getQuestionAnswer)
                        .collect(Collectors.toList());

                testData.setQuestions(questions);
                testData.setCorrectAnswers(answers);
                flag = TEST;
                sendTextMessage(chatId, "Вы выбрали тест " + testName + " \n чтобы начать отправьте любой текст");
                return;
            }

            if (flag == GROUP) {
                String groupName = update.getCallbackQuery().getData();
                userDto.setGroup(groupName);
                repository.save(UserMapper.toUser(userDto));
                sendTextMessage(chatId, "Сохранено");
                sendWelcomeMessage(chatId);
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
                    userDto.setName(text);
                    flag = SURNAME;
                    sendTextMessage(chatId, "Введите вашу фамилию");
                    break;
                case SURNAME:
                    userDto.setSurname(text);
                    flag = GROUP;
                    List<Group> groups = groupRepository.findAll();
                    List<String> groupsNames = groups.stream().map(Group::getName).collect(Collectors.toList());
                    sendGroupButtons(chatId, groupsNames);
                    break;
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
                    int questionsCount = testData.getQuestions().size();
                    int correctQuestions = 0;
                    for (int i = 0; i < questionsCount; i++) {
                        if (testSession.userAnswers.get(i).equals(testData.getCorrectAnswers().get(i))) {
                            correctQuestions++;
                        }
                    }
                    sendTextMessage(chatId, "Вы ответили правильно на " + correctQuestions + " из " + questionsCount);
                    List<Result> resultList = new ArrayList<>();
                    List<Question> questionList = testQuestionRepository.findQuestionsByTestId(testId);
                    Test test = testRepository.findById(testId).orElseThrow(RuntimeException::new);
                    for (int i = 0; i < testSession.userAnswers.size(); i++) {
                        resultList.add(Result.builder()
                                .answer(testSession.userAnswers.get(i))
                                .user(UserMapper.toUser(userDto))
                                .test(test)
                                .question(questionList.get(i))
                                .build());
                    }
                    resultRepository.saveAll(resultList);
                    resultList.clear();
                    testSession.currentQuestion = 0;
                    testSession.userAnswers.clear();
                    testData.getQuestions().clear();
                    testData.getCorrectAnswers().clear();
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
