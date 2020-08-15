package controllers;

import models.LoginForm;
import models.SignUpForm;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Map;

public class AuthController extends Controller {
    private final AssetsFinder assetsFinder;
    private final FormFactory formFactory;

    @Inject
    public AuthController(AssetsFinder assetsFinder, FormFactory formFactory) {
        this.assetsFinder = assetsFinder;
        this.formFactory = formFactory;
    }

    public Result loginPage(Http.Request request) {
        if(request.session().get("username").isPresent()) {
            return redirect(routes.HomeController.index());
        }

        Form<LoginForm> loginForm = formFactory.form(LoginForm.class);
        return ok(views.html.login.render(loginForm, assetsFinder, request));
    }

    public Result registrationPage(Http.Request request) {
        if(request.session().get("username").isPresent()) {
            return redirect(routes.HomeController.index());
        }

        Form<SignUpForm> signUpForm = formFactory.form(SignUpForm.class);
        return ok(views.html.register.render(signUpForm, null,  assetsFinder, request));
    }

    public Result login(Http.Request request) {
        if(request.session().get("username").isPresent()) {
            return redirect(routes.HomeController.index());
        }

        Form<LoginForm> loginForm = formFactory.form(LoginForm.class).bindFromRequest(request);

        if(loginForm.hasErrors()) {
            return badRequest(views.html.login.render(loginForm, assetsFinder, request));
        }

        String username = loginForm.field("username").value().get();

        return redirect(routes.HomeController.index())
                .addingToSession(request, Map.of("username", username));
    }

    public Result registration(Http.Request request) {
        if(request.session().get("username").isPresent()) {
            return redirect(routes.HomeController.index());
        }

        Form<SignUpForm> signUpFormForm = formFactory.form(SignUpForm.class).bindFromRequest(request);
        if(signUpFormForm.hasErrors()) {
            return badRequest(views.html.register.render(signUpFormForm, null,  assetsFinder, request));
        }

        String email = signUpFormForm.field("email").value().get();
        String username = signUpFormForm.field("username").value().get();
        String password = signUpFormForm.field("password").value().get();

        if(User.registerUser(email, username, password)) {
            return redirect(routes.AuthController.loginPage());
        }

        return badRequest(views.html.register.render(signUpFormForm, username, assetsFinder, request));
    }

    public Result logout(Http.Request request) {
        return redirect(routes.HomeController.index()).withNewSession();
    }

}
