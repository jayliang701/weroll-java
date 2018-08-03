package com.magicfish.weroll.router;

import com.magicfish.weroll.aspect.Param;
import com.magicfish.weroll.aspect.Router;
import org.springframework.ui.Model;

public class Test {

    @Router(path = "/test", needLogin = false)
    public void renderTestPage() {

    }

    @Router(path = "/test2", view = "/test",
            needLogin = true,
            params = {
                @Param(name = "id")
            }
    )
    public void renderTest2Page(String id, Model model) {
        model.addAttribute("msg", "Hello, " + id);
    }

}
