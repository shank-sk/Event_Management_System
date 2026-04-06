package dao;

import java.util.List;

import models.Event;
import util.Hibernate;

public class EventDAO extends BaseDAO<Event> {

  public EventDAO() {
    super(Event.class);
  }

  public Event saveEvent(Event event) {
    return save(event);
  }

  public Event updateEvent(Event event) {
    return update(event);
  }

  public boolean deleteEvent(int eventId) {
    return delete((long) eventId);
  }

  public Event getEventById(int eventId) {
    return findById((long) eventId);
  }

  public List<Event> getAllEvents() {
    return findAll();
  }

  // Methods for pagination and search
  public List<Event> getEventsWithPagination(int page, int pageSize) {
    int safePage = Math.max(1, page);
    int safeSize = Math.max(1, pageSize);
    int offset = (safePage - 1) * safeSize;

    return Hibernate.executeSession(session -> session
        .createQuery("from Event e order by e.eventDate desc", Event.class)
        .setFirstResult(offset)
        .setMaxResults(safeSize)
        .list());
  }

  public List<Event> searchEvents(String keyword) {
    String normalized = keyword == null ? "" : keyword.toLowerCase();
    return Hibernate.executeSession(session -> session
        .createQuery("from Event e where lower(e.title) like :keyword or lower(e.description) like :keyword",
            Event.class)
        .setParameter("keyword", "%" + normalized + "%")
        .list());
  }

  public List<Event> searchEvents(String keyword, int page, int pageSize) {
    int safePage = Math.max(1, page);
    int safeSize = Math.max(1, pageSize);
    int offset = (safePage - 1) * safeSize;
    String normalized = keyword == null ? "" : keyword.toLowerCase();

    return Hibernate.executeSession(session -> session
        .createQuery("from Event e where lower(e.title) like :keyword or lower(e.description) like :keyword "
            + "order by e.eventDate desc", Event.class)
        .setParameter("keyword", "%" + normalized + "%")
        .setFirstResult(offset)
        .setMaxResults(safeSize)
        .list());
  }

  public int getEventCapacity(int eventId) {
    Event event = findById((long) eventId);
    if (event == null || event.getCapacity() == null) {
      return 0;
    }
    return event.getCapacity();
  }

  public int getRegisteredCount(int eventId) {
    return Hibernate.executeSession(session -> session
        .createQuery("select count(r) from Registration r where r.event.id = :eventId", Long.class)
        .setParameter("eventId", (long) eventId)
        .uniqueResultOptional()
        .orElse(0L)
        .intValue());
  }

  public Event findEventById(Long eventId) {
    return findById(eventId);
  }

  public List<Event> findAllEvents() {
    return findAll();
  }

  public int getRegisterCount(Long eventId) {
    return getRegisteredCount(eventId.intValue());
  }

}
