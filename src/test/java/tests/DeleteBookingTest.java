package tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.clients.APIClient;
import core.models.Booking;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteBookingTest {

    private APIClient apiClient; // создаем переменную APIClient, для того чтобы в следующем коде положить в нее объект APIClient
    private ObjectMapper objectMapper;

    //Инициализация APi клиента перед каждым тестом
    @BeforeEach //часть JUNIT, аннотация позволяющая перед каждым тестом создавать новый объект APIClient
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        apiClient.createToken("admin", "password123");
    }

    @Test
    @Feature("Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Удаление существующего бронирования")
    public void testDeleteBookingTest() throws Exception {
        Response responseGetBooking = apiClient.getBooking();

        //Десериализуем тело ответа в список объектов Booking
        String responseBody = responseGetBooking.getBody().asString();
        List<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<List<Booking>>() {
        });

        if (bookings != null) {
            int deleteBookingId = bookings.get(0).getBookingid();

            Response responseDeleteBooking = apiClient.deleteBooking(deleteBookingId);
            step("Проверка, что статус-код ответа == 201", () ->
                    assertEquals(201, responseDeleteBooking.getStatusCode(),
                            "Код ответа не совпал с ожидаемым. Ответ: Статус код " + responseDeleteBooking.getStatusCode())
            );

            Response responseGetBooking2 = apiClient.getBooking();
            //Десериализуем тело ответа в список объектов Booking
            String responseBody2 = responseGetBooking2.getBody().asString();
            List<Booking> bookings2 = objectMapper.readValue(responseBody2, new TypeReference<List<Booking>>() {
            });
            Response responseGetBookingById = apiClient.getBookingById(deleteBookingId);
            step("Проверка отсутствия удаленного id = %d в списке", () ->
                    assertThat(bookings2)
                            .extracting(Booking::getBookingid)
                            .as("ID %d присутствует после удаления", deleteBookingId)
                            .doesNotContain(deleteBookingId)
            );
        }
    }
}