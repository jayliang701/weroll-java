package com.magicfish.weroll;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class WerollApplication {

    private static String[] processArguments(String[] args) {
        String defaultEnv = "localdev";
        String env = "";
        String prefix = "--spring.profiles.active=";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith(prefix)) {
                env = arg.replace(prefix, "");
                if (env.isEmpty()) {
                    env = defaultEnv;
                    args[i] = env;
                }
                break;
            }
        }
        if (env.isEmpty()) {
            env = defaultEnv;
            String[] newArgs = new String[args.length + 1];
            for (int i = 0; i < args.length; i++) {
                newArgs[i] = args[i];
            }
            newArgs[newArgs.length - 1] = prefix + env;
            args = newArgs;
        }
        return args;
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String[] args) {
        args = processArguments(args);
        return SpringApplication.run(primarySource, args);
    }
}
