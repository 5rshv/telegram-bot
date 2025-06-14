package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ScheduledClass {
    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    private TelegramBotUpdatesListener telegramBotUpdatesListener;

    @Autowired
    public void init(TelegramBotUpdatesListener telegramBotUpdatesListener){
        this.telegramBotUpdatesListener = telegramBotUpdatesListener;
    }


    @Scheduled(cron = "0 0/1 * * * *")
public void sendAndDeleteNotifications(){
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        List<NotificationTask> notificationTaskList = this.notificationTaskRepository.findByDateTime(now);
        notificationTaskList.forEach(notificationTask -> {
            Long chatId = notificationTask.getChat();
            String message = notificationTask.getMessage();
            telegramBotUpdatesListener.sendMessage(chatId, message);
            notificationTaskRepository.deleteById(notificationTask.getId());
        });
    }
}
