package com.example.tgbot.bot;

import com.example.tgbot.TestData;
import com.example.tgbot.TestSession;
import com.example.tgbot.discipline.Discipline;
import com.example.tgbot.discipline.DisciplineRepository;
import com.example.tgbot.group.Group;
import com.example.tgbot.group.GroupRepository;
import com.example.tgbot.question.Question;
import com.example.tgbot.result.Result;
import com.example.tgbot.result.ResultRepository;
import com.example.tgbot.test.Test;
import com.example.tgbot.test.TestRepository;
import com.example.tgbot.disciplinegroup.DisciplineGroup;
import com.example.tgbot.disciplinegroup.DisciplineGroupRepository;
import com.example.tgbot.testquestion.TestQuestionRepository;
import com.example.tgbot.user.UserDto;
import com.example.tgbot.user.UserMapper;
import com.example.tgbot.user.UserRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
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

@Service
@Slf4j
@Getter
@Setter
@Scope(value = "prototype")
public class BotService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final DisciplineGroupRepository disciplineGroupRepository;
    private final TestRepository testRepository;
    private final ResultRepository resultRepository;
    private final DisciplineRepository disciplineRepository;

    private TestData testData;
    private TestSession testSession;
    private UserDto userDto;
    private Long testId;

    public BotService(UserRepository userRepository, GroupRepository groupRepository, TestQuestionRepository testQuestionRepository,DisciplineRepository disciplineRepository, DisciplineGroupRepository disciplineGroupRepository, TestRepository testRepository, ResultRepository resultRepository, TestData testData, TestSession testSession, UserDto userDto) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.testQuestionRepository = testQuestionRepository;
        this.disciplineRepository = disciplineRepository;
        this.disciplineGroupRepository = disciplineGroupRepository;
        this.testRepository = testRepository;
        this.resultRepository = resultRepository;
        this.testData = testData;
        this.testSession = testSession;
        this.userDto = userDto;
    }

    public Boolean isCreated(long chatId) {
        return userRepository.findById(chatId).isEmpty();
    }

    public SendMessage findAndReturnRegistration(long chatId) {
        if (userRepository.findById(chatId).isEmpty()) {
            return startMessage(chatId);
        }
        return null;
    }

    public SendMessage sendWelcome(long chatId) {
        return sendWelcomeMessage(chatId);
    }

    public SendMessage disciplineMessage(long chatId) {
        userDto = UserMapper.toUserDto(userRepository.findById(chatId).orElseThrow(RuntimeException::new));
        List<DisciplineGroup> disciplineGroups = disciplineGroupRepository.findDisciplineGroupsByGroup_Name(userDto.getGroup());
        List<Discipline> disciplineList = disciplineGroups.stream().map(DisciplineGroup::getDiscipline).collect(Collectors.toList());
        List<String> disciplineNames = disciplineList.stream().map(Discipline::getName).collect(Collectors.toList());
        return sendDisciplineButtons(chatId, disciplineNames);
    }

    public SendMessage testsMessage(long chatId, String disciplineName) {
        List<Test> testList = testRepository.findTestsByDisciplineName(disciplineName);
        List<String> testNames = testList.stream().map(Test::getTestName).collect(Collectors.toList());
        return sendTestsButtons(chatId, testNames, disciplineName);
    }

    public SendMessage testStart(long chatId, String testName) {
        Test test = testRepository.findByTestName(testName);
        testId = test.getTestId();
        if (resultRepository.findFirstByUserAndTest(UserMapper.toUser(userDto), test).isPresent()) {
            return sendTextMessage(chatId, "Тест можно пройти только 1 раз");
        }
        List<Question> questionList = testQuestionRepository.findQuestionsByTestId(testId);

        List<String> questions = questionList.stream()
                .map(Question::getQuestionText)
                .collect(Collectors.toList());

        List<String> answers = questionList.stream()
                .map(Question::getQuestionAnswer)
                .collect(Collectors.toList());

        testData.setQuestions(questions);
        testData.setCorrectAnswers(answers);
        return sendTextMessage(chatId, "Вы выбрали тест " + testName + " \n чтобы начать отправьте любой текст");
    }

    public SendMessage saveUser(long chatId, String groupName) {
        userDto.setGroup(groupName);
        userRepository.save(UserMapper.toUser(userDto));
        if (!userRepository.findById(chatId).isEmpty()) {
            return sendWelcomeMessage(chatId);
        }
        return sendTextMessage(chatId, "Что то пошло не так, повторите позже");
    }

    public SendMessage flagStart(long chatId) {

        userDto.setChatId(chatId);
        return sendTextMessage(chatId, "Введите ваше имя");
    }

    public SendMessage flagName(long chatId, String text) {
        userDto.setName(text);
//        flag = SURNAME;
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
            if (testSession.userAnswers.get(i).equalsIgnoreCase(testData.getCorrectAnswers().get(i))) {
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

    public SendMessage sendTextMessage(Long chatId, String text) {
        return new SendMessage(chatId.toString(), text);
    }

    public SendMessage sendWelcomeMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите действие:");

        InlineKeyboardMarkup keyboardMarkup = createKeyboard();
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    private InlineKeyboardMarkup createKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Выбрать дисциплину").callbackData("disciplines").build());
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
        for (String groupName : testNames) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(InlineKeyboardButton.builder().text(groupName).callbackData(groupName).build());
            keyboard.add(row);
            inlineKeyboardMarkup.setKeyboard(keyboard);
        }
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text("Назад ↩").callbackData("назад").build());
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup disciplineButtons(List<String> disciplineNames) {
        return getInlineKeyboardMarkup(disciplineNames);
    }
    private SendMessage sendDisciplineButtons(long chatId, List<String> disciplineNames) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Список дисциплин:");
        InlineKeyboardMarkup keyboardMarkup = disciplineButtons(disciplineNames);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    private SendMessage sendTestsButtons(long chatId, List<String> testNames, String disciplineName) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Список доступных тестов " + disciplineName + " :");
        InlineKeyboardMarkup keyboardMarkup = testButtons(testNames);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    private InlineKeyboardMarkup groupButtons(List<String> groupNames) {
        return getInlineKeyboardMarkup(groupNames);
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(List<String> groupNames) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (String groupName : groupNames) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(InlineKeyboardButton.builder().text(groupName).callbackData(groupName).build());
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

