package org.example;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.entity.User;
import org.example.entity.Post;
import org.example.entity.Comment;
import org.example.services.CommentService;
import org.example.services.PostService;
import org.example.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SocMedTest {
    private EntityManager entityManager ;
    private User user;
    private Post post;
    private Comment comment;


    @BeforeEach
    public void setup(){
        // Set up the EntityManagerFactory
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("socmed");

        // Create an EntityManager
        entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction transaction =   entityManager.getTransaction();
        try{
            transaction.begin();
            User testUser = new User("test", "user");
            entityManager.persist(testUser);
            user = testUser;

            Post testPost = new Post("Test post", testUser);
            entityManager.persist(testPost);
            post = testPost;

            Comment testComment = new Comment(user, post, "Test Comment");
            entityManager.persist(testComment);
            comment = testComment;
            transaction.commit();

        }catch (Exception e){
            transaction.rollback();
        }

        UserService userService = new UserService(entityManager);
        userService.login(user.getUsername(), user.getPassword());
    }



    @AfterEach
    public void teardown(){
        EntityTransaction transaction =   entityManager.getTransaction();
        try{
            transaction.begin();

            entityManager.remove(user);

            entityManager.remove(post);

            entityManager.remove(comment);

            transaction.commit();

        }catch (Exception e){
            transaction.rollback();
        }
    }


    @Test
    public void registerUser(){
        UserService userService = new UserService(entityManager);
        String response = userService.register("ola", "password");
        assertEquals( "User registered successfully", response);
        User person = userService.getAUser("ola");
        entityManager.remove(person);
    }

    @Test
    public void testRegistserUserWhenUsernameisAlreadyUsed(){
        UserService userService = new UserService(entityManager);
        String response = userService.register("test", "password");
        assertEquals("Error registering user; User already exists with that username", response);
    }

    @Test
    public void testCreatePost(){
        PostService postService = new PostService(entityManager);
       String response = postService.createPost(user.getUsername(), user.getPassword(), "postt");
        assertEquals("Post created successfully", response);
    }

    @Test
    public void testCreateComment(){
        CommentService commentService = new CommentService(entityManager);
        String response = commentService.createComment(user.getUsername(), user.getPassword(), post.getId(), "commentt");
        assertEquals("Comment created successfully", response);
    }
}
