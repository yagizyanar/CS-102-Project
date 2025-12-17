package productivity.event;
/**
 * @author  Elshan Iskandarli
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event {
    private int id;
    private String title;
    private String type;
    private LocalDateTime date;
    private String notes;

    public Event(int id, String title, String eventType, LocalDateTime date, String notes) {
        this.id = id;
        this.title = title;
        this.type = eventType;
        this.date = date;
        this.notes = notes;
    }

    public Event(String title, String eventType, String eventDateStr, String notes) {
        this.title = title;
        this.type = eventType;
        this.date = LocalDateTime.parse(eventDateStr);
        this.notes = notes;
    }

    public static Event createEvent( String title, String type, LocalDateTime date, String notes) {
        String dateStr = date.toString();
        return new Event(title, type, date, notes);
    }

    public boolean isUpcoming() {
        if (date.isAfter(LocalDateTime.now()) && date.isBefore(LocalDateTime.now().plusDays(7))) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isToday() {
        LocalDateTime now = LocalDateTime.now();
        if(date.toLocalDate().equals(now.toLocalDate())) {
            return true;
        } else {
            return false;
        }
    }

    public String getTimeUntil() {
        LocalDateTime now = LocalDateTime.now();
        long daysUntil = java.time.Duration.between(now, date).toDays();
        long hoursUntil = java.time.Duration.between(now, date).toHours();
        
        if(date.isBefore(now)) {
            return "OVERDUE";
        }

        if(daysUntil > 0) {
            if(daysUntil > 1) {
                return "Event due in " + daysUntil + " days.";
            } else {
                return "Event due in 1 day.";
            }
        } else {
            if(hoursUntil > 1) {
                return "Event due in " + hoursUntil + " hours.";
            } else {
                return "Event due in 1 hour.";
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return type + " event " + title + ". Deadline:  " + dateStr;
    }
    
}