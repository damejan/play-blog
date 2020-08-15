package models;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.mapping.experimental.MorphiaReference;
import dev.morphia.query.experimental.filters.Filters;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static db.MongoConfig.datastore;

@Entity("users")
public class User {
    @Id
    private ObjectId id;
    private String email;
    private String username;
    private String password;
    private MorphiaReference<List<Post>> posts;

    public User(){}

//    public User(String email, String username, String password) {
//        this.email = email;
//        this.username = username;
//        this.password = password;
//    }

    public ObjectId getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Post> getPosts() {
        return posts.get();
    }

    public void setList(final List<Post> posts) {
        this.posts = MorphiaReference.wrap(posts);
    }

    public static boolean registerUser(String email, String username, String password) {
        User user = datastore()
                .find(User.class)
                .filter(Filters.eq("username", username))
                .first();

        if(user != null) {
            return false;
        }

        String hashPassword = BCrypt
                .withDefaults()
                .hashToString(12, password.toCharArray());

        User newUser = new User();
        newUser.setEmail(email);;
        newUser.setUsername(username);
        newUser.setPassword(hashPassword);
        newUser.setList(new ArrayList<>());

        datastore().save(newUser);

        return true;
    }

    public static boolean validateUser(String username, String password) {
        User user1 = datastore()
                .find(User.class)
                .filter(Filters.eq("username", username))
                .first();

        if(user1 != null) {
            BCrypt.Result correctPassword = BCrypt.verifyer().verify(password.toCharArray(), user1.password);
            if(correctPassword.verified) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email="+ email + ", username=" + username + ", password=" + password + "}";
    }
}
