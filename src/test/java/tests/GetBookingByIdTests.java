package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.clients.APIClient;
import core.models.BookingDates;
import core.models.BookingResponse;
import core.models.CreatedBooking;
import core.models.NewBooking;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class GetBookingByIdTests {

    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking;
    private NewBooking newBooking;

    //Инициализация APi клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        // Создаем объект Booking с необходимыми данными
        newBooking = new NewBooking();
        newBooking.setFirstname("Mike");
        newBooking.setLastname("Line");
        newBooking.setTotalprice(155);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2024-01-01", "2024-01-05"));
        newBooking.setAdditionalneeds("Breakfast");

        try {
            String requestBody = objectMapper.writeValueAsString(newBooking);
            Response response = apiClient.createBooking(requestBody);

            // Сохраняем созданное бронирование
            String responseBody = response.getBody().asString();
            createdBooking = objectMapper.readValue(responseBody, CreatedBooking.class);
            log.info("Создано бронирование с ID: {}", createdBooking.getBookingid());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при создании бронирования в BeforeEach: " + e.getMessage());
        }
    }

    @Test
    @Feature("Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Получение бронирования по ID ( GET /booking/{bookingId} )")
    public void testGetBookingById() throws Exception {

        int bookingId = 7;
        // Выполняем запрос к эндпоинту /booking через APIClient
        Response response = apiClient.getBookingById(bookingId);


        //Проверяем, что статус код ответа равен 200
        step("Проверка, что статус-код ответа == 200", () ->
                assertEquals(200, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: Статус код \" + response.getStatusCode()")
        );
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
    @AfterEach
    public void tearDown() {
        //Удаляем созданное бронирование
        apiClient.createToken("admin", "password123");
        apiClient.deleteBooking(createdBooking.getBookingid());

        assertThat(apiClient.getBookingById(createdBooking.getBookingid()).getStatusCode()).isEqualTo(404);
    }
}
