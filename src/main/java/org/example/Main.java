package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.services.CommentService;
import org.example.services.PostService;
import org.example.services.UserService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;



public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args){

        // Set up the EntityManagerFactory
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("socmed");

        // Create an EntityManager
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // Create service instances with the EntityManager
        UserService userService = new UserService(entityManager);
        userService.isLoggedIn("Okiki", "password");
        userService.login("Okiki", "password");

        PostService postService = new PostService(entityManager);
        postService.createPost("Okiki", "password", "Post 1");
        postService.getAllPost("Okiki", "password");

        CommentService commentService = new CommentService(entityManager);
        commentService.createComment("Okiki", "password", 1L, "comment 1");
    }
}
