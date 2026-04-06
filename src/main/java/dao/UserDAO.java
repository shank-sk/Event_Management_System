package dao;

import java.util.List;

import models.Users;
import util.Hibernate;

public class UserDAO extends BaseDAO<Users> {

  public UserDAO() {
    super(Users.class);
  }

  public Users saveUser(Users user) {
    return save(user);
  }

  public Users findUserByEmail(String email) {
    return Hibernate.executeSession(session -> session
        .createQuery("from Users u where u.email = :email", Users.class)
        .setParameter("email", email)
        .uniqueResult());
  }

  public Users getUserById(int userId) {
    return findById((long) userId);
  }

  public boolean isEmailExists(String email) {
    return findUserByEmail(email) != null;
  }

  public List<Users> getAllUsers() {
    return findAll();
  }

  public Users findById(Long id) {
    return super.findById(id);
  }

  public boolean isEmailExist(String email) {
    return isEmailExists(email);
  }
}
