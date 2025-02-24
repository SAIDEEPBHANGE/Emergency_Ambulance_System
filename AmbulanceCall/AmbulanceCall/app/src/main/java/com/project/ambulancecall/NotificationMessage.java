package com.project.ambulancecall;

public class NotificationMessage {
    public static String message = "{" +
            "  \"to\": \"/topics/%s\"," +
            "  \"data\": {" +
            "       \"body\":\"%s\"," +
            "       \"for\":\"%s\""+
            "   }" +
            "}";

}
