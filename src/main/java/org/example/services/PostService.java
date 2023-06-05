package org.example.services;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.entity.*;

import java.util.List;

public class PostService {

    private JPAQueryFactory queryFactory;
    private EntityManager entityManager;
    QPost qPost = QPost.post1;
    QUser qUser = QUser.user;
    QComment qComment = QComment.comment1;

    public PostService(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
        this.entityManager = em;
    }

    public String createPost(String username, String password, String post){
        String returnMesage = "Post created successfully";
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

            // Create post
            Post newPost = new Post();
            newPost.setPost(post);
            newPost.setUser(user);

            // Save the post to the database

            entityManager.persist(newPost);

            transaction.commit();
        } catch (Exception e) {
            // Handle exception or re-throw
            returnMesage = "Error creating post; " + e.getMessage();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        System.out.println(returnMesage);
        return returnMesage;
    }

    public String getAllPost(String username, String password){
        String returnMesage = "Post fetched successfully";
        try {

            // Validate user details
            UserService userService = new UserService(entityManager);
            Boolean isLoggedIn = userService.isLoggedIn(username, password);

            if(!isLoggedIn){
                throw new Exception("User is not logged in");
            }

            User user = userService.getAUser(username);

            // Fetch posts
            List<Post> posts = queryFactory.selectFrom(qPost)
                    .join(qPost.user, qUser)
                    .leftJoin(qPost.comments, qComment)
                    .fetchJoin()
                    .fetch();;

           for(Post post: posts){
               System.out.println("post id: " + post.getId() +"\npost: " + post.getPost());
               System.out.println("comments: ");
               for(Comment comment: post.getComments()){
                   System.out.println(comment.getComment());
                   System.out.println("----------------------------------------");
               }
               System.out.println("====================================================================================");
           }

        } catch (Exception e) {
            // Handle exception or re-throw
            returnMesage = "Error fetching posts; " + e.getMessage();
        }
        System.out.println(returnMesage);
        return returnMesage;
    }

    public Post getAPost(Long postId){

        try{
            List<Post> posts = queryFactory.selectFrom(qPost)
                    .join(qPost.user, qUser)
                    .leftJoin(qPost.comments, qComment)
                    .where(qPost.id.eq(postId))
                    .fetchJoin()
                    .fetch();



            if(posts.size() < 1){
                throw new Exception("There's no post with that id");
            }

            return posts.get(0);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
