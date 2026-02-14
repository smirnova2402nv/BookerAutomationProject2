package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.clients.APIClient;
import core.models.BookingResponse;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingByIdTests {

    private APIClient apiClient; // создаем переменную APIClient, для того чтобы в следующем коде положить в нее объект APIClient
    private ObjectMapper objectMapper;

    //Инициализация APi клиента перед каждым тестом
    @BeforeEach //часть JUNIT, аннотация позволяющая перед каждым тестом создавать новый объект APIClient
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @Feature("Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Получение существующего бронирования")
    public void testGetBookingById() throws Exception {

        // Тестовый ID
        int bookingId = 12;

        // Выполняем запрос к эндпоинту /booking через APIClient
        Response response = apiClient.getBookingById(bookingId);


        //Проверяем, что статус код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);
        BookingResponse bookingResponse = objectMapper.readValue(response.asString(), BookingResponse.class);

        step("Проверка, что поле FirstName не пустое", () ->
                assertThat(bookingResponse.getFirstName())
                        .as("Поле FirstName не должно быть пустым")
                        .isNotEmpty()
        );
        step("Проверка, что поле LastName не пустое", () ->
                assertThat(bookingResponse.getLastName())
                        .as("Поле LastName не должно быть пустым")
                        .isNotEmpty()
        );
        step("Проверка, что поле AdditionalNeeds не пустое", () ->
                assertThat(bookingResponse.getAdditionalNeeds())
                        .as("Поле AdditionalNeeds не должно быть пустым")
                        .isNotEmpty()
        );
        step("Проверка, что поле TotalPrice не пустое", () ->
                assertThat(bookingResponse.getTotalPrice())
                        .as("Поле TotalPrice не должно быть пустым")
                        .isGreaterThan(0)
        );
        step("Проверка, что поле DepositPaid не пустое", () ->
                assertThat(bookingResponse.isDepositPaid())
                        .as("Поле DepositPaid не должно быть пустым")
                        .isTrue()
        );
        step("Проверка, что поле BookingDates не пустое", () ->
                assertThat(bookingResponse.getBookingDates())
                        .as("Поле BookingDates не должно быть пустым")
                        .isNotNull()
        );
    }
}
