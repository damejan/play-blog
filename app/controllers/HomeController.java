package controllers;

import static db.MongoConfig.datastore;

import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.Sort;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import models.CreatePostForm;
import models.Post;
import models.User;
import org.bson.types.ObjectId;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import javax.inject.Inject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class HomeController extends Controller {
    private final AssetsFinder assetsFinder;
    private final FormFactory formFactory;

    @Inject
    public HomeController(AssetsFinder assetsFinder, FormFactory formFactory) {
        this.assetsFinder = assetsFinder;
        this.formFactory = formFactory;
    }

    public Result index(Http.Request request) {
        Query<Post> query = datastore().find(Post.class);
        List<Post> posts = query.iterator(new FindOptions().sort(Sort.descending("creationDate"))).toList();

        Optional<String> currentUserUsername = request.session().get("username");

        if(currentUserUsername.isPresent()) {
            return ok(views.html.index.render(currentUserUsername.get(), posts, assetsFinder, request));
        }

        return ok(views.html.index.render(null, posts, assetsFinder, request));
    }

    public Result createPostPage(Http.Request request) {
        return request
                .session()
                .get("username")
                .map(username -> ok(views.html.createPost.render(username, null, null, assetsFinder, request)))
                .orElseGet(() -> redirect(routes.HomeController.index()));
    }

    @Security.Authenticated(Secured.class)
    public Result createPost(Http.Request request) {
        Form<CreatePostForm> createPostForm = formFactory.form(CreatePostForm.class).bindFromRequest(request);

        String title = createPostForm.field("title").value().get();
        String content = createPostForm.field("content").value().get();

        String username = request.session().get("username").get();

        if(createPostForm.hasErrors()) {
            return badRequest(views.html.createPost.render(username, null, createPostForm, assetsFinder, request));
        }

        User user = datastore().find(User.class).filter(Filters.eq("username", username)).first();

        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);

        Post post = new Post();
        post.setTitle(encodedTitle);
        post.setContent(content);
        post.setAuthor(user);
        post.setAuthorName(user.getUsername());
        datastore().save(post);

        Query<User> updateUserQuery = datastore().find(User.class).filter(Filters.eq("_id", user.getId()));
        updateUserQuery.update(UpdateOperators.addToSet("posts", post)).execute();

        return redirect(routes.HomeController.index());
    }

    public Result viewPost(String title, Http.Request request) {
        List<Extension> extensions = Arrays.asList(TablesExtension.create());

        Post post = datastore().find(Post.class).filter(Filters.eq("title", title)).first();
        System.out.println("Post id: " + post.getId().toString());
        // parse markdown
        Parser parser = Parser.builder()
                .extensions(extensions)
                .build();
        Node document = parser.parse(post.getContent());
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .build();
        post.setContent(renderer.render(document));

        Optional<String> currentUserUsername = request.session().get("username");

        if(currentUserUsername.isPresent()) {
            return ok(views.html.viewPost.render(currentUserUsername.get(), post, assetsFinder, request));
        }

        return ok(views.html.viewPost.render(null, post, assetsFinder, request));
    }

    @Security.Authenticated(Secured.class)
    public Result editPostPage(String id, Http.Request request) {
        String sessionUsername = request.session().get("username").get();

        Post post = verifyPostOwner(id, request);
        System.out.println(post);

        if(post == null) {
            return redirect(routes.HomeController.index());
        }

        return ok(views.html.createPost.render(sessionUsername, post, null, assetsFinder, request));
    }

    @Security.Authenticated(Secured.class)
    public Result updatePost(String id, Http.Request request){
        String sessionUsername = request.session().get("username").get();
        Post post = verifyPostOwner(id, request);
        System.out.println(post);

        if(post == null) {
            return redirect(routes.HomeController.index());
        }

        Form<CreatePostForm> createPostForm = formFactory.form(CreatePostForm.class).bindFromRequest(request);
        String title = createPostForm.field("title").value().get();
        String content = createPostForm.field("content").value().get();

        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);

        post.setTitle(encodedTitle);
        post.setContent(content);

        if(createPostForm.hasErrors()) {
            return badRequest(views.html.createPost.render(sessionUsername, post, createPostForm, assetsFinder, request));
        }

        Query<Post> updatePostQuery = datastore().find(Post.class).filter(Filters.eq("_id", post.getId()));
        updatePostQuery.update(UpdateOperators.set("title", post.getTitle()), UpdateOperators.set("content", post.getContent())).execute();

        return redirect(routes.HomeController.viewPost(post.getTitle()));
    }

    @Security.Authenticated(Secured.class)
    public Result deletePost(String id, Http.Request request) {
        String sessionUsername = request.session().get("username").get();
        Post post = verifyPostOwner(id, request);

        if(post == null) {
            return redirect(routes.HomeController.index());
        }

        ObjectId postId = new ObjectId(id);

//        User currentUser = datastore().find(User.class)
//                .filter(Filters.eq("username", sessionUsername))
//                .first();
//
//        System.out.println("test 1");
//
//        List<Post> userPosts = currentUser.getPosts();
//        System.out.println(userPosts.toString());
//        userPosts.remove(postId);
//
//        System.out.println("test 2");

//        Query<User> updateUserQuery = datastore().find(User.class).filter(Filters.eq("username", sessionUsername));
//        updateUserQuery.update(UpdateOperators.set("posts", userPosts)).execute();

        datastore().find(Post.class)
                .filter(Filters.eq("_id", postId))
                .delete();

        return redirect(routes.HomeController.index());
    }

    private Post verifyPostOwner(String id, Http.Request request) {
        String sessionUsername = request.session().get("username").get();
        User user;
        Post post;

        try {
            user = datastore().find(User.class).filter(Filters.eq("username", sessionUsername)).first();
            ObjectId postId = new ObjectId(id);
            post = datastore().find(Post.class).filter(Filters.eq("_id", postId)).first();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println(post);
//        System.out.println(user.getId().toString());
//        System.out.println(user.getId());
//        System.out.println(post.getAuthor().getId());
        if(!user.getId().equals(post.getAuthor().getId())) {
//            System.out.println("test");
            return null;
        }
        return post;
    }

}
