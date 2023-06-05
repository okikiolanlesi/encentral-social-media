package org.example.services;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.entity.User;
import org.example.entity.QUser;

import java.util.List;

public class UserService {
    private JPAQueryFactory queryFactory;
    private EntityManager entityManager;
    QUser qUser = QUser.user;

    public UserService(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
        this.entityManager = em;
    }

    public String register(String username, String password ){
        String returnMesage = "User registered successfully";
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            // Validate user details
            List<User> users = queryFactory.selectFrom(qUser)
                    .where(qUser.username.eq(username))
                    .fetch();
            if(users.size() > 0){
                throw new Exception("User already exists with that username");
            }
            // Register a user
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setLoggedIn(false);
            // Save the user to the database

            entityManager.persist(user);

            transaction.commit();
        } catch (Exception e) {
            // Handle exception or re-throw
            returnMesage = "Error registering user; " + e.getMessage();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        System.out.println(returnMesage);
        return returnMesage;
    }

    public String login(String username, String password ){
        String returnMesage = "User logged in successfully";
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            // Validate user details
            List<User> users = queryFactory.selectFrom(qUser)
                    .where(qUser.username.eq(username))
                    .fetch();
            if(users.size() < 1){
                throw new Exception("There's no user with that username");
            }
            // Login user
           User existingUser =  users.get(0);
            if(!existingUser.getPassword().equals(password)){
                throw new Exception("Invalid credentials");
            }

            existingUser.setLoggedIn(true);
            // Save the user to the database

            entityManager.persist(existingUser);

            transaction.commit();
        } catch (Exception e) {
            // Handle exception or re-throw
            returnMesage = "Error logging user in; " + e.getMessage();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        System.out.println(returnMesage);
        return returnMesage;
    }

    public String logout(String username, String password ){
        String returnMesage = "User logged in successfully";
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            // Validate user details
            List<User> users = queryFactory.selectFrom(qUser)
                    .where(qUser.username.eq(username))
                    .fetch();
            if(users.size() < 1){
                throw new Exception("There's no user with that username");
            }
            // Login user
            User existingUser =  users.get(0);
            if(!existingUser.getPassword().equals(password)){
                throw new Exception("Invalid credentials");
            }

            existingUser.setLoggedIn(false);

            // Save the user to the database
            entityManager.persist(existingUser);

            transaction.commit();
        } catch (Exception e) {
            // Handle exception or re-throw
            returnMesage = "Error logging user in; " + e.getMessage();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        System.out.println(returnMesage);
        return returnMesage;
    }

    public Boolean isLoggedIn(String username, String password){
        try {

            // Validate user details
            List<User> users = queryFactory.selectFrom(qUser)
                    .where(qUser.username.eq(username))
                    .fetch();
            if(users.size() < 1){
                throw new Exception("There's no user with that username");
            }

            User existingUser =  users.get(0);

            if(!existingUser.getPassword().equals(password)){
                throw new Exception("Invalid password");
            }

            if(!existingUser.getLoggedIn()){
                throw new Exception("User is not logged in");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public User getAUser(String username){

        try{
            List<User> users = queryFactory.selectFrom(qUser)
                    .where(qUser.username.eq(username))
                    .fetch();

            if(users.size() < 1){
                throw new Exception("There's no user with that username");
            }

            return users.get(0);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
