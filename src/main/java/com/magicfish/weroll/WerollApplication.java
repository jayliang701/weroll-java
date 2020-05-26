package com.magicfish.weroll;

import com.magicfish.weroll.annotation.AnnotationRegister;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class WerollApplication {

    public static final String ENV_ARG_NAME = "spring.profiles.active";
    public static final String DEFAULT_ENV = "localdev";

    private static String[] processArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.indexOf(ENV_ARG_NAME) > 0) {
                String env = arg.replaceFirst("--" + ENV_ARG_NAME + "=", "").replaceFirst("-D" + ENV_ARG_NAME + "=", "");
                System.setProperty(ENV_ARG_NAME, env);
                break;
            }
        }
        return args;
    }

    private static void preInitialize(String[] args) throws Exception {
        AnnotationRegister.initialize();
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String[] args) throws Exception {
        args = processArguments(args);

        String env = System.getProperty(ENV_ARG_NAME);
        if (env == null || env.isEmpty()) {
            env = DEFAULT_ENV;
            System.setProperty(ENV_ARG_NAME, env);
        }

        preInitialize(args);

        ConfigurableApplicationContext applicationContext = SpringApplication.run(primarySource, args);
        return applicationContext;
    }
}
