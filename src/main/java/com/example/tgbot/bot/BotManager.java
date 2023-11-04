package com.example.tgbot.bot;

import com.example.tgbot.TestData;
import com.example.tgbot.TestSession;
import com.example.tgbot.discipline.DisciplineRepository;
import com.example.tgbot.disciplinegroup.DisciplineGroupRepository;
import com.example.tgbot.group.GroupRepository;
import com.example.tgbot.result.ResultRepository;
import com.example.tgbot.test.TestRepository;
import com.example.tgbot.testquestion.TestQuestionRepository;
import com.example.tgbot.user.UserDto;
import com.example.tgbot.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@Scope(value = "prototype")
public class BotManager {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final DisciplineGroupRepository disciplineGroupRepository;
    private final TestRepository testRepository;
    private final ResultRepository resultRepository;
    private final DisciplineRepository disciplineRepository;
    public Map<Long, BotService> userBots = new HashMap<>();

    public BotService createBotForUser(long userId) {
        UserDto userDto = new UserDto();
        TestData testData = new TestData();
        TestSession testSession = new TestSession();
        BotService botService = new BotService(userRepository, groupRepository, testQuestionRepository, disciplineRepository, disciplineGroupRepository, testRepository, resultRepository, testData, testSession, userDto);

        userBots.put(userId, botService);
        return botService;
    }

    public BotService getBotForUser(long userId) {
        return userBots.get(userId);
    }

    public BotService botStart(Update update) {
        long chatId = update.getMessage().getChatId();

        if (!userBots.containsKey(chatId)) {
            return createBotForUser(chatId);
        }
        return getBotForUser(chatId);
    }
}


