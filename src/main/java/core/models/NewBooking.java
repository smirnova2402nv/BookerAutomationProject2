package core.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class NewBooking {
    @JsonProperty("firstname")
    private String firstname;
    @JsonProperty("lastname")
    private String lastname;
    @JsonProperty("totalprice")
    private Integer totalprice;
    @JsonProperty("depositpaid")
    private Boolean depositpaid;
    @JsonProperty("bookingdates")
    private BookingDates bookingdates;
    @JsonProperty("additionalneeds")
    private String additionalneeds;
}
