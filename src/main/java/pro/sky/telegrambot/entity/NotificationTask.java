package pro.sky.telegrambot.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notification_task")
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private  Long chat;
    private String message;
    private LocalDateTime clock;

    public NotificationTask(Long id, Long chat, String message, LocalDateTime clock){
        this.id = id;
        this.chat = chat;
        this.clock = clock;
        this.message = message;
    }
    public NotificationTask(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChat() {
        return chat;
    }

    public void setChat(Long chat) {
        this.chat = chat;
    }

    public LocalDateTime getClock() {
        return clock;
    }

    public void setClock(LocalDateTime clock) {
        this.clock = clock;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NotificationTask)) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(id, that.id) && Objects.equals(chat, that.chat) && Objects.equals(message, that.message) && Objects.equals(clock, that.clock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chat, message, clock);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", chat=" + chat +
                ", message='" + message + '\'' +
                ", clock=" + clock +
                '}';
    }
}
