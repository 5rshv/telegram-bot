package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hibernate.type.descriptor.java.JdbcDateTypeDescriptor.DATE_FORMAT;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }


    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Process update: {}", update);
            String messageText = update.message().text();
            Long chatId = update.message().chat().id();

            if (update.message().text() != null && messageText.equals("/start")) {

                sendMessage(chatId, "Привет! Введи по порядку время и название задачи в формате: 01.01.2022 20:00 Сделать домашнюю работу");
            } else {
                splitMessage(chatId, messageText);
            }
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


    private void sendWelcomeMessage(long chatId) {
        String WelcomeText = "Используйте: ДД.ММ.ГГГГ ЧЧ:MM Текст напоминания";
        System.out.println("Отправляем сообщение в чат" + chatId + ":" + WelcomeText);

        SendMessage message = new SendMessage(chatId, WelcomeText);
        try {
            SendResponse response = telegramBot.execute(message);
        } catch (Exception e) {
            logger.info("Ошибка ", e);
        }
    }


    public void splitMessage(Long chat, String message) {
        Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s+\\d{2}:\\d{2})\\s+(.+)");
        Matcher matcher = pattern.matcher(message);
        try {
            if (!matcher.find()) {
                throw new IllegalArgumentException("Неверный формат! Используйте: ДД.ММ.ГГГГ ЧЧ:MM Текст напоминания");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Не удалось распарсить дату {}, в сообщении {}", matcher, message);
            telegramBot.execute(new SendMessage(chat, "Не удалось распарсить ваши данные!"));
            return;
        }

        String dateTime = matcher.group(1);
        String reminderText = matcher.group(2);
        LocalDateTime reminderClock;
        try {
            reminderClock = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        } catch (DateTimeParseException e) {
            logger.error("Не удалось распарсить дату {}, в сообщении {}", dateTime, DATE_FORMAT);
            telegramBot.execute(new SendMessage(chat, "Ошибка ввода "));
            return;
        }

        NotificationTask task = new NotificationTask();
        task.setChat(chat);
        task.setMessage(reminderText);
        task.setClock(reminderClock);
        notificationTaskRepository.save(task);

        sendMessage(chat, "Задача добавлена");
    }


    void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        SendResponse response = telegramBot.execute(message);
        logger.info("Response: {}", response.isOk());
        logger.info("Error code: {}", response.errorCode());
    }

}
