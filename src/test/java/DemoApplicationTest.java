
import com.magicfish.demo.DemoApplication;

import org.junit.BeforeClass;
import org.junit.Test;

public class DemoApplicationTest {

     @BeforeClass
     public static void init() throws Exception {
         DemoApplication.main(new String[] { "--spring.profiles.active=unittest" });
     }

    @Test
    public void mock() {
        
    }
}