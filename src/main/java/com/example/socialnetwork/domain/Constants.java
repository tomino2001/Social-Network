package com.example.socialnetwork.domain;

import com.example.socialnetwork.config.ApplicationContext;

public class Constants {
    public static final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
    public static final String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    public static final String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    public static final String pathFriendship = "D:\\JavaProjects\\MAP\\socialnetwork\\pdfData\\Friendship.pdf";
    public static final String pathMessage = "D:\\JavaProjects\\MAP\\socialnetwork\\pdfData\\Mesaje.pdf";
    public static final String pathMessageV2 = "D:\\JavaProjects\\MAP\\socialnetwork\\pdfData\\MesajeV2.pdf";
}