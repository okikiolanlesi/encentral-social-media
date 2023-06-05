package org.example.services;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.entity.Comment;
import org.example.entity.Post;
import org.example.entity.QComment;
import org.example.entity.User;

public class CommentService {

    private JPAQueryFactory queryFactory;
    private EntityManager entityManager;
    QComment qComment = QComment.comment1;

    public CommentService(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
        this.entityManager = em;
    }

    public String createComment(String username, String password, Long postId, String comment){
        String returnMesage = "Comment created successfully";
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            // Validate user details
            UserService userService = new UserService(entityManager);
            Boolean isLoggedIn = userService.isLoggedIn(username, password);

            if(!isLoggedIn){
                throw new Exception("User is not logged in");
            }

            User user = userService.getAUser(username);

            Post post = new PostService(entityManager).getAPost(postId);

            if(post == null){
                throw new Exception("Post doesn't exist");
            }

            // Create comment
            Comment newComment = new Comment();
            newComment.setPost(post);
            newComment.setUser(user);
            newComment.setComment(comment);

            // Save the comment to the database

            entityManager.persist(newComment);

            transaction.commit();
        } catch (Exception e) {
            // Handle exception or re-throw
            returnMesage = "Error creating comment; " + e.getMessage();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        System.out.println(returnMesage);
        return returnMesage;
    }
}
