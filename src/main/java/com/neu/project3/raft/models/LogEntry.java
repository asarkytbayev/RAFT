package com.neu.project3.raft.models;

public class LogEntry {
    public String command;
    public int term;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }
}
