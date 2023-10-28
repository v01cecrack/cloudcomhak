package com.example.tgbot;

import com.example.tgbot.group.GroupRepository;
import com.example.tgbot.result.ResultRepository;
import com.example.tgbot.test.TestRepository;
import com.example.tgbot.testgroup.TestGroupRepository;
import com.example.tgbot.testquestion.TestQuestionRepository;
import com.example.tgbot.user.User;
import com.example.tgbot.user.UserDto;
import com.example.tgbot.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotManager {
    private BotService botService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final TestGroupRepository testGroupRepository;
    private final TestRepository testRepository;
    private final ResultRepository resultRepository;
    private final TestData testData;
    private final TestSession testSession;
    private final UserDto userDto;
    private Map<Long, BotService> userBots = new HashMap<>();


    public BotService createBotForUser(long userId) {
        // Создать новый экземпляр бота для данного пользователя
//        TelegramLongPollingBot userBot = new Bot(botConfig, botService);
        botService = new BotService(userRepository, groupRepository, testQuestionRepository, testGroupRepository, testRepository, resultRepository, testData, testSession, userDto);

        // Сохранить его в словаре, используя идентификатор пользователя в качестве ключа
        userBots.put(userId, botService);
        return botService;
    }

    public BotService getBotForUser(long userId) {
        // Получить экземпляр бота для данного пользователя
        return userBots.get(userId);
    }

    public BotService botStart(Update update) {
        long chatId = update.getMessage().getChatId();
        User user = userRepository.findById(chatId).orElse(null);
        if (user == null) {
            return createBotForUser(chatId);
        }
        return getBotForUser(chatId);
    }
}


