package core.settings;

public enum ApiEndpoints {
    AUTH("/auth"),
    PING("/ping"),
    BOOKING("/booking"),
    BOOKING_BY_ID("/booking/");

    private final String path;
    //Конструктор
    ApiEndpoints(String path) {
        this.path = path;
    }
    // геттер
    public String getPath() {
        return path;
    }

    public static String getBookingByIdPath(int id) {
        return BOOKING_BY_ID.getPath() + id;
    }
}