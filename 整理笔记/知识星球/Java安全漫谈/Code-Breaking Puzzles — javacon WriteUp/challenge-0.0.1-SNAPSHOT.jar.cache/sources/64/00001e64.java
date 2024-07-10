package org.springframework.core.env;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/SimpleCommandLineArgsParser.class */
class SimpleCommandLineArgsParser {
    public CommandLineArgs parse(String... args) {
        String optionName;
        CommandLineArgs commandLineArgs = new CommandLineArgs();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String optionText = arg.substring(2, arg.length());
                String optionValue = null;
                if (optionText.contains("=")) {
                    optionName = optionText.substring(0, optionText.indexOf(61));
                    optionValue = optionText.substring(optionText.indexOf(61) + 1, optionText.length());
                } else {
                    optionName = optionText;
                }
                if (optionName.isEmpty() || (optionValue != null && optionValue.isEmpty())) {
                    throw new IllegalArgumentException("Invalid argument syntax: " + arg);
                }
                commandLineArgs.addOptionArg(optionName, optionValue);
            } else {
                commandLineArgs.addNonOptionArg(arg);
            }
        }
        return commandLineArgs;
    }
}