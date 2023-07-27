package com.badshun.autoscript;

public class Script {
    private String taskName;
    private String scriptPath;

    public Script(String taskName, String scriptPath) {
        this.taskName = taskName;
        this.scriptPath = scriptPath;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getScriptPath() {
        return scriptPath;
    }
}
