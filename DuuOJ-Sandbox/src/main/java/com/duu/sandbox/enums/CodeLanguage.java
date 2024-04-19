package com.duu.sandbox.enums;

import lombok.Getter;

/**
 * @author Duu
 * @description
 * @date 2024/04/19 19:56
 * @from https://github.com/0oHo0
 **/
@Getter
public enum CodeLanguage {
    CPP("cpp", "g++", "a.out"),
    JAVA("java", "javac", "Main.class"),
    PYTHON("python", "python", "main.py");

    private final String name;
    private final String compileCommand;
    private final String runCommand;

    CodeLanguage(String name, String compileCommand, String runCommand)
    {
        this.name = name;
        this.compileCommand = compileCommand;
        this.runCommand = runCommand;
    }

}