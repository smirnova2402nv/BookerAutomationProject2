package core.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Booking {
    private int bookingid;

    // Конструктор
    @JsonCreator
    public Booking(@JsonProperty("bookingid") int bookingid) {
        this.bookingid = bookingid;
    }

    // Геттер
    public int getBookingid() {
        return bookingid;
    }

    // Сеттер
    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }
}
