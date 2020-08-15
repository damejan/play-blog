package models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreatePostForm {
    @NotBlank(message = "Title cannot be empty.")
    @Size(min = 1, max = 150, message = "Title must be between 1 and 150 characters.")
    String title;

    @NotBlank(message = "Content cannot be empty.")
    @Size(min = 1, max = 6000, message = "Content must be between 1 and 6000 characters.")
    String content;

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
}
