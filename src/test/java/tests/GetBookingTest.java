package tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class GetBookingTest {

    private APIClient apiClient; // создаем переменную APIClient, для того чтобы в следующем коде положить в нее объект APIClient
    private ObjectMapper objectMapper;

    //Инициализация APi клиента перед каждым тестом
    @BeforeEach //часть JUNIT, аннотация позволяющая перед каждым тестом создавать новый объект APIClient
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
    }

    @Test
    @Feature("Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Получение бронирований без передачи парамметров")

    public void testGetBooking() throws Exception {

        // Выполняем запрос к эндпоинту /booking через APIClient
        Response response = apiClient.getBooking();

        //Проверяем, что статус код ответа == 200
        step("Проверка, что статус-код ответа == 200", () ->
                assertEquals(200, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: Статус код " + response.getStatusCode())
        );

        //Десериализуем тело ответа в список объектов Booking
        String responseBody = response.getBody().asString();
        List<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<List<Booking>>() {
        });

        // Проверяем, что тело ответа содержит объекты Booking
        step("Проверка, что список объектов Booking НЕ пуст", () ->
                assertThat(bookings)
                        .as("Ожидалось, что список объектов Booking НЕ пуст")
                        .isNotEmpty()
        );

        //Проверяем, что каждый объект Booking содержит валидное значение booking
        step("Проверка, что bookingid > 0 для всех элементов", () -> {
            for (Booking booking : bookings) {
                assertThat(booking.getBookingid())
                        .as("Ожидался bookingid > 0, но получен: %d", booking.getBookingid())
                        .isGreaterThan(0);
            }
        });
    }
}

