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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.tgbot.Flag.START;
import static com.example.tgbot.Flag.SURNAME;

@Service
@RequiredArgsConstructor
public class BotService {
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


    public Boolean isCreated(long chatId) {
        return repository.findById(chatId).isEmpty();
    }
    public SendMessage messageStart1(long chatId) {
        if (repository.findById(chatId).isEmpty()) {
//            sendTextMessage(chatId, "Вам нужно зарегистрироваться");
            flag = START;
            return startMessage(chatId);
        }
        return null;
    }

    public SendMessage messageStart2(long chatId) {
        return sendWelcomeMessage(chatId);
    }

    public SendMessage testsMessage(long chatId) {
        userDto = UserMapper.toUserDto(repository.findById(chatId).orElseThrow(RuntimeException::new));
        List<TestGroup> testGroup = testGroupRepository.findTestGroupsByGroup_Name(userDto.getGroup());
        List<Test> testList = testGroup.stream().map(TestGroup::getTest).collect(Collectors.toList());
        List<String> testNames = testList.stream().map(Test::getTestName).collect(Collectors.toList());
        return sendTestsButtons(chatId, testNames);
    }

    public SendMessage testStart(long chatId, String testName) {
        Test test = testRepository.findByTestName(testName);
        testId = test.getTestId();
        if (resultRepository.findFirstByUserAndTest(UserMapper.toUser(userDto), test).isPresent()) {
            return sendTextMessage(chatId, "Тест можно пройти только 1 раз");
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
//        flag = TEST;
        return sendTextMessage(chatId, "Вы выбрали тест " + testName + " \n чтобы начать отправьте любой текст");
    }

    public SendMessage saveUser(long chatId, String groupName) {
        userDto.setGroup(groupName);
        repository.save(UserMapper.toUser(userDto));
//        sendWelcomeMessage(chatId);
        return sendTextMessage(chatId, "Сохранено");
    }

    public SendMessage flagStart(long chatId) {
        userDto.setChatId(chatId);
        return sendTextMessage(chatId, "Введите ваше имя");
    }

    public SendMessage flagName(long chatId, String text) {
        userDto.setName(text);
        flag = SURNAME;
        return sendTextMessage(chatId, "Введите вашу фамилию");
    }

    public SendMessage flagSurname(long chatId, String text) {
        userDto.setSurname(text);
        List<Group> groups = groupRepository.findAll();
        List<String> groupsNames = groups.stream().map(Group::getName).collect(Collectors.toList());
        return sendGroupButtons(chatId, groupsNames);
    }

    public SendMessage flagTest(long chatId, String text) {
        if (testSession.getCurrentQuestion() <= testData.getQuestions().size()) {
            if (testSession.getCurrentQuestion() != 0) {
                testSession.userAnswers.add(text);
            }
            testSession.currentQuestion++;
            if (testSession.currentQuestion <= testData.getQuestions().size()) {
                return sendTextMessage(chatId, testData.getQuestions().get(testSession.getCurrentQuestion() - 1));
            }
        }
        int questionsCount = testData.getQuestions().size();
        int correctQuestions = 0;
        for (int i = 0; i < questionsCount; i++) {
            if (testSession.userAnswers.get(i).equals(testData.getCorrectAnswers().get(i))) {
                correctQuestions++;
            }
        }
        String textResult = "Вы ответили правильно на " + correctQuestions + " из " + questionsCount;
        clearSession();
        return sendTextMessage(chatId, textResult);

    }

    public void clearSession() {
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
    }

    private SendMessage sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        return message;
    }

    public SendMessage sendWelcomeMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Добро пожаловать! Выберите действие:");

        InlineKeyboardMarkup keyboardMarkup = createKeyboard();
        message.setReplyMarkup(keyboardMarkup);
        return message;
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

    private SendMessage startMessage(long chatId) {
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
        return message;
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

    private SendMessage sendTestsButtons(long chatId, List<String> testNames) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Список доступных тестов:");
        InlineKeyboardMarkup keyboardMarkup = testButtons(testNames);
        message.setReplyMarkup(keyboardMarkup);
        return message;
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

    private SendMessage sendGroupButtons(long chatId, List<String> groupNames) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберете свою группу:");
        InlineKeyboardMarkup keyboardMarkup = groupButtons(groupNames);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }
}
