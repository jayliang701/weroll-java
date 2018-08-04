package demo.web.router;

import com.magicfish.weroll.annotation.Param;
import com.magicfish.weroll.annotation.Router;
import org.springframework.ui.Model;

public class Index {

    @Router(path = "/", view = "/index", needLogin = false)
    public void renderDefaultHomePage() {
        renderIndexPage();
    }

    @Router(path = "/index", needLogin = false)
    public void renderIndexPage() {

    }

}
