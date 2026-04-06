package util;

import java.util.function.Consumer;
import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class Hibernate {
  private static final SessionFactory sessionFactory;

  static {
    try {
      sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    } catch (Throwable ex) {
      System.err.println("Initial SessionFactory creation failed." + ex);
      throw new ExceptionInInitializerError(ex);
    }
  }

  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public static <T> T executeTransaction(Function<Session, T> function) {
    Transaction tx = null;
    try (Session session = sessionFactory.openSession()) {
      tx = session.beginTransaction();
      T result = function.apply(session);
      tx.commit();
      return result;
    } catch (Exception e) {
      if (tx != null) {
        try {
          tx.rollback();
        } catch (Exception rollbackEx) {
          e.addSuppressed(rollbackEx);
        }
      }
      throw new RuntimeException("Transaction execution failed", e);
    }
  }

  public static void executeInTransactionVoid(Consumer<Session> function) {
    executeTransaction(session -> {
      function.accept(session);
      return null;
    });
  }

  public static <T> T executeSession(Function<Session, T> function) {
    try (Session session = sessionFactory.openSession()) {
      return function.apply(session);
    } catch (Exception e) {
      throw new RuntimeException("Session execution failed", e);
    }
  }

  public static void executeInSessionVoid(Consumer<Session> function) {
    executeSession(session -> {
      function.accept(session);
      return null;
    });
  }
}
