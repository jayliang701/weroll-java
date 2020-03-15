package com.magicfish.weroll;

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

    public static ConfigurableApplicationContext run(Class<?> primarySource, String[] args) {
        args = processArguments(args);

        String env = System.getProperty(ENV_ARG_NAME);
        if (env == null || env.isEmpty()) {
            env = DEFAULT_ENV;
            System.setProperty(ENV_ARG_NAME, env);
        }

        return SpringApplication.run(primarySource, args);
    }
}
