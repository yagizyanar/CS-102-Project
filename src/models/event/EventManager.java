package models.event;

import dao.EventDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private List<Event> events;

    public EventManager() {
        events = new ArrayList<>();
        EventDAO.loadFromDatabase();
    }

    public void addEvent(String title, String type, LocalDateTime date, String notes) {
        Event event = new Event(title, type, date, notes);
        events.add(event);
        EventDAO.saveEventToDatabase(event);
        System.out.println("Event added");
    }

    public void deleteEvent(int id) {
        for(Event event : events) {
            if(event.getId() == id) {
                events.remove(event);
                EventDAO.deleteEventFromDatabase(id);
                System.out.println("Event deleted");
                return;
            }
        }
    }

    public List<Event> getUpcomingEvents() {
        List<Event> upcoming = new ArrayList<>();
        for(Event event : events) {
            if(event.isUpcoming()) {
                upcoming.add(event);
            }
        }

        return upcoming;
    }

    public List<Event> getTodaysEvents() {
        List<Event> todays = new ArrayList<>();
        for(Event event : events) {
            if(event.isToday()) {
                todays.add(event);
            }
        }
        return todays;
    }

    public List<Event> getEventsByType(String type) {
        List<Event> filtered = new ArrayList<>();
        for (Event event : events) {
            if (event.getType().equalsIgnoreCase(type)) {
                filtered.add(event);
            }
        }
        return filtered;
    }

    public List<Event> getEvents() { 
        return events; 
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

}
