package demo.web;

import com.magicfish.weroll.WerollApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@MapperScan("demo.web.dao")
@ComponentScans({
    @ComponentScan("com.magicfish.weroll.utils"),
    @ComponentScan("com.magicfish.weroll.config"),
    @ComponentScan("com.magicfish.weroll.security"),
    @ComponentScan("com.magicfish.weroll.controller")
})
@SpringBootApplication
public class DemoApplication {

    WerollApplication app;

    public DemoApplication() {
        app = new WerollApplication(this);
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
