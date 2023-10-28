package com.example.tgbot;

import com.example.tgbot.group.GroupRepository;
import com.example.tgbot.result.ResultRepository;
import com.example.tgbot.test.TestRepository;
import com.example.tgbot.testgroup.TestGroupRepository;
import com.example.tgbot.testquestion.TestQuestionRepository;
import com.example.tgbot.user.User;
import com.example.tgbot.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Slf4j
//@Component
public class BotManager {
    private final BotConfig botConfig;
    private final UserRepository repository;
    private final GroupRepository groupRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final TestGroupRepository testGroupRepository;
    private final TestRepository testRepository;
    private final ResultRepository resultRepository;
    private final TestData testData;
    private final TestSession testSession;
    private final BotService botService;
    private Map<Long, TelegramLongPollingBot> userBots = new HashMap<>();

    public BotManager(BotConfig botConfig, UserRepository repository, GroupRepository groupRepository, TestQuestionRepository testQuestionRepository,
                      TestGroupRepository testGroupRepository, TestRepository testRepository, ResultRepository resultRepository,
                      TestData testData, TestSession testSession, BotService botService) {
        this.botConfig = botConfig;
        this.repository = repository;
        this.groupRepository = groupRepository;
        this.testQuestionRepository = testQuestionRepository;
        this.testGroupRepository = testGroupRepository;
        this.testRepository = testRepository;
        this.resultRepository = resultRepository;
        this.testData = testData;
        this.testSession = testSession;
        this.botService = botService;
    }

    public TelegramLongPollingBot createBotForUser(long userId) {
        // Создать новый экземпляр бота для данного пользователя
        TelegramLongPollingBot userBot = new Bot(botConfig, repository, groupRepository, testQuestionRepository, testGroupRepository, testRepository, resultRepository, testData, testSession, botService);

        // Сохранить его в словаре, используя идентификатор пользователя в качестве ключа
        userBots.put(userId, userBot);

        return userBot;
    }

    public TelegramLongPollingBot getBotForUser(long userId) {
        // Получить экземпляр бота для данного пользователя
        return userBots.get(userId);
    }

    public void botStart(Update update) {
        long chatId = update.getMessage().getChatId();
        User user = repository.findById(chatId).orElse(null);
        if (user == null) {
            createBotForUser(chatId).onUpdateReceived(update);
        } else {
            getBotForUser(chatId).onUpdateReceived(update);
        }
    }
}


