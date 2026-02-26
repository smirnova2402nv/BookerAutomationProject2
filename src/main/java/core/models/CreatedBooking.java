package core.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatedBooking {
    @JsonProperty("bookingid")
    private int bookingid;
    @JsonProperty("booking")
    private NewBooking booking;


}
