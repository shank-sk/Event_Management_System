package dao;

import java.util.List;

import models.Event;
import models.Registration;
import models.Users;
import util.Hibernate;

public class RegisterDAO extends BaseDAO<Registration> {
  public RegisterDAO() {
    super(Registration.class);
  }

  public Registration registerEvent(int userId, int eventId) {
    return Hibernate.executeTransaction(session -> {
      Users user = session.get(Users.class, (long) userId);
      Event event = session.get(Event.class, (long) eventId);
      if (user == null || event == null) {
        throw new IllegalArgumentException("Invalid userId or eventId");
      }

      Registration existing = session.createQuery(
          "from Registration r where r.user.id = :userId and r.event.id = :eventId", Registration.class)
          .setParameter("userId", (long) userId)
          .setParameter("eventId", (long) eventId)
          .uniqueResult();
      if (existing != null) {
        return existing;
      }

      Registration registration = new Registration();
      registration.setUser(user);
      registration.setEvent(event);
      session.persist(registration);
      return registration;
    });
  }

  public boolean cancelRegistration(int userId, int eventId) {
    return Hibernate.executeTransaction(session -> {
      Registration reg = session.createQuery(
          "from Registration r where r.user.id = :userId and r.event.id = :eventId", Registration.class)
          .setParameter("userId", (long) userId)
          .setParameter("eventId", (long) eventId)
          .uniqueResult();
      if (reg != null) {
        session.remove(reg);
        return true;
      }
      return false;
    });
  }

  public List<Event> getEventsByUser(int userId) {
    return Hibernate.executeSession(session -> session
        .createQuery("select r.event from Registration r where r.user.id = :userId", Event.class)
        .setParameter("userId", (long) userId)
        .list());
  }

  public boolean isUserRegistered(int userId, int eventId) {
    return Hibernate.executeSession(session -> session
        .createQuery("select count(r) from Registration r where r.user.id = :userId and r.event.id = :eventId",
            Long.class)
        .setParameter("userId", (long) userId)
        .setParameter("eventId", (long) eventId)
        .uniqueResultOptional()
        .orElse(0L) > 0);
  }

  public List<Users> getParticipantsByEvent(int eventId) {
    return Hibernate.executeSession(session -> session
        .createQuery("select r.user from Registration r where r.event.id = :eventId", Users.class)
        .setParameter("eventId", (long) eventId)
        .list());
  }

  public int countRegistrations(int eventId) {
    return Hibernate.executeSession(session -> session
        .createQuery("select count(r) from Registration r where r.event.id = :eventId", Long.class)
        .setParameter("eventId", (long) eventId)
        .uniqueResultOptional()
        .orElse(0L)
        .intValue());
  }
}
