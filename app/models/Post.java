package models;

import dev.morphia.annotations.*;
import dev.morphia.mapping.experimental.MorphiaReference;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity("posts")
public class Post {
    @Id
    private ObjectId id;
    private String title;
    private String content;
    private String authorName;
    private Date creationDate;
    private Date lastChange;
    private MorphiaReference<User> author;

    @PrePersist
    public void prePersist() {
        creationDate = (creationDate == null) ? new Date() : creationDate;
        lastChange = (lastChange == null) ? creationDate : new Date();
    }

    public Post(){}

//    public Post(String title, String content) {
//        this.title = title;
//        this.content = content;
//    }

    public ObjectId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public User getAuthor() {
        return author.get();
    }

    public void setAuthor(final User author) {
        this.author = MorphiaReference.wrap(author);
    }

    @Override
    public String toString() {
        return "{title=" + title + ", content=" + content + ", authorName=" + authorName + "}";
    }
}
