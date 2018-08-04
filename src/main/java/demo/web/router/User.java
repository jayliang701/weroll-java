package demo.web.router;


import com.magicfish.weroll.annotation.Router;

public class User {

    @Router(path = "/login")
    public void renderLoginPage() {

    }

    @Router(path = "/register")
    public void renderRegisterPage() {

    }
}
