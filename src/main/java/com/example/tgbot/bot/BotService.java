package com.example.tgbot.bot;

import com.example.tgbot.QuestionData;
import com.example.tgbot.TestData;
import com.example.tgbot.TestSession;
import com.example.tgbot.discipline.Discipline;
import com.example.tgbot.discipline.DisciplineRepository;
import com.example.tgbot.disciplinegroup.DisciplineGroup;
import com.example.tgbot.disciplinegroup.DisciplineGroupRepository;
import com.example.tgbot.group.Group;
import com.example.tgbot.group.GroupRepository;
import com.example.tgbot.question.Question;
import com.example.tgbot.result.Result;
import com.example.tgbot.result.ResultRepository;
import com.example.tgbot.test.Test;
import com.example.tgbot.test.TestRepository;
import com.example.tgbot.testquestion.TestQuestionRepository;
import com.example.tgbot.university.University;
import com.example.tgbot.university.UniversityRepository;
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
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
    private final UniversityRepository universityRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final DisciplineGroupRepository disciplineGroupRepository;
    private final TestRepository testRepository;
    private final ResultRepository resultRepository;
    private final DisciplineRepository disciplineRepository;

    private TestData testData;
    private List<QuestionData> questionDatas;
    private TestSession testSession;
    private UserDto userDto;
    private Long testId;

    public BotService(UserRepository userRepository, GroupRepository groupRepository, UniversityRepository universityRepository, TestQuestionRepository testQuestionRepository, DisciplineRepository disciplineRepository, DisciplineGroupRepository disciplineGroupRepository, TestRepository testRepository, ResultRepository resultRepository, TestData testData, List<QuestionData> questionDatas, TestSession testSession, UserDto userDto) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.universityRepository = universityRepository;
        this.testQuestionRepository = testQuestionRepository;
        this.disciplineRepository = disciplineRepository;
        this.disciplineGroupRepository = disciplineGroupRepository;
        this.testRepository = testRepository;
        this.resultRepository = resultRepository;
        this.questionDatas = questionDatas;
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
        List<DisciplineGroup> disciplineGroups = disciplineGroupRepository.findByGroup_Name(userDto.getGroup().getName());
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

//        List<QuestionData> questionDataList = new ArrayList<>();
        for (Question question : questionList) {
            List<String> wrongAnswers = new ArrayList<>();
            if (question.getIncorrectAnswer1() != null)
                wrongAnswers.add(question.getIncorrectAnswer1());
            if (question.getIncorrectAnswer2() != null)
                wrongAnswers.add(question.getIncorrectAnswer2());
            if (question.getIncorrectAnswer3() != null)
                wrongAnswers.add(question.getIncorrectAnswer3());
            if (question.getIncorrectAnswer4() != null)
                wrongAnswers.add(question.getIncorrectAnswer4());
            QuestionData questionData = new QuestionData(question.getQuestionText(), question.getQuestionAnswer(), wrongAnswers);
            questionDatas.add(questionData);
        }

        List<String> questions = questionList.stream()
                .map(Question::getQuestionText)
                .collect(Collectors.toList());

        List<String> answers = questionList.stream()
                .map(Question::getQuestionAnswer)
                .collect(Collectors.toList());

        testData.setQuestions(questionDatas);
//        testData.setCorrectAnswers(answers);
        return sendTextMessage(chatId, "Вы выбрали тест " + testName + " \n чтобы начать отправьте любой текст");
    }

    public SendMessage saveUser(long chatId, String groupName) {
        userDto.setGroup(groupRepository.findByName(groupName));
        userRepository.save(UserMapper.toUser(userDto));
        if (!userRepository.findById(chatId).isEmpty()) {
            return sendWelcomeMessage(chatId);
        }
        return sendTextMessage(chatId, "Что то пошло не так, повторите позже");
    }

    public SendMessage flagStart(long chatId) {

        userDto.setChatId(chatId);
        return sendTextMessage(chatId, "Введите вашу фамилию");
    }

    public SendMessage flagName(long chatId, String text) {
        userDto.setSurname(text);
//        flag = SURNAME;
        return sendTextMessage(chatId, "Введите ваше имя");
    }

    public SendMessage flagSurname(long chatId, String text) {
        userDto.setName(text);
        return sendTextMessage(chatId, "Введите ваше отчество");
    }

    public SendMessage flagFatherName(long chatId, String text) {
        userDto.setFatherName(text);
        List<University> universities = universityRepository.findAll();
        List<String> universitiesNames = universities.stream().map(University::getName).collect(Collectors.toList());
        return sendGroupButtons(chatId, universitiesNames, "Выберите университет:"); //TODO переименовать метод на более общий
    }

    public SendMessage flagUniversity(long chatId, String universityName) {
        userDto.setUniversity(universityRepository.findByName(universityName));
        List<Group> groups = groupRepository.findAll(); //TODO перенести в универ
        List<String> groupsNames = groups.stream().map(Group::getName).collect(Collectors.toList());
        return sendGroupButtons(chatId, groupsNames, "Выберите свою группу:");
    }

    public SendMessage flagTest(long chatId, String text) {
        if (testSession.getCurrentQuestion() <= questionDatas.size()) {
            if (testSession.getCurrentQuestion() != 0) {
                testSession.userAnswers.add(text);
            }
            testSession.currentQuestion++;
            if (testSession.currentQuestion <= questionDatas.size()) {
                if (questionDatas.get(testSession.getCurrentQuestion() - 1).getIncorrectAnswers().isEmpty()) {
                    return sendTextMessage(chatId, questionDatas.get(testSession.getCurrentQuestion() - 1).getQuestionText());
                } else {
                    List<String> answerOptions = questionDatas.get(testSession.getCurrentQuestion() - 1).getIncorrectAnswers();
                    answerOptions.add(questionDatas.get(testSession.getCurrentQuestion() - 1).getCorrectAnswer());
                    Collections.shuffle(answerOptions);
                    return sendGroupButtons(chatId, answerOptions, questionDatas.get(testSession.getCurrentQuestion() - 1).getQuestionText());
                }
            }
        }
        int questionCount = testData.getQuestions().size();
        int correctQuestions = 0;
        for (int i = 0; i < questionCount; i++) {
            if (testSession.userAnswers.get(i).equalsIgnoreCase(questionDatas.get(i).getCorrectAnswer())) {
                correctQuestions++;
            }
        }
        String textResult = "Вы ответили правильно на " + correctQuestions + " из " + questionCount;
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
        questionDatas.clear();
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

    public SendMessage statisticsByDiscipline(long chatId, String disciplineName) {
        List<Object[]> resultsList = resultRepository.getUserStatisticsByDiscipline(chatId, disciplineName);
        StringBuilder result = new StringBuilder();
        BigInteger totalAnswers = BigInteger.ZERO;
        BigInteger totalCorrectAnswers = BigInteger.ZERO;

        for (Object[] object : resultsList) {
            BigInteger correctAnswers = (BigInteger) object[4];
            BigInteger total = (BigInteger) object[3];
            totalCorrectAnswers = totalCorrectAnswers.add(correctAnswers);
            totalAnswers = totalAnswers.add(total);
            double percentage = total.doubleValue() > 0 ? (correctAnswers.doubleValue() / total.doubleValue()) * 100.0 : 0.0;
            result.append(object[1]).append(": ").append(correctAnswers).append("/").append(total).append(" (").append(String.format("%.1f%%", percentage)).append(")\n");
        }

        double totalPercentage = totalAnswers.doubleValue() > 0 ? (totalCorrectAnswers.doubleValue() / totalAnswers.doubleValue()) * 100.0 : 0.0;
        result.append("Общий итог: ").append(totalCorrectAnswers).append("/").append(totalAnswers).append(" (").append(String.format("%.1f%%", totalPercentage)).append(")");
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(String.valueOf(result));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text("Назад ↩").callbackData("Назад").build());
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;

    }

    private InlineKeyboardMarkup createKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Выбрать дисциплину").callbackData("disciplines").build());
        row2.add(InlineKeyboardButton.builder().text("Статистика").callbackData("statistics").build());
        keyboard.add(row1);
        keyboard.add(row2);
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
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (String groupName : disciplineNames) {
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

    private SendMessage sendGroupButtons(long chatId, List<String> groupNames, String text) { //TODO переименовать
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        InlineKeyboardMarkup keyboardMarkup = groupButtons(groupNames);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }
}

