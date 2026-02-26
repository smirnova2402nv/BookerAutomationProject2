package tests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class PatchBookingByIdTests {
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking;
    private NewBooking newBooking;
    private NewBooking updatedBooking;

    //Инициализация APi клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

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
        updatedBooking = new NewBooking();

        updatedBooking.setFirstname("Johny");           // Изменили
        updatedBooking.setLastname("Deep");             // Изменили

    }

    @Test
    @Feature("Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Частичное обновление бронирования ( PATCH /booking/{bookingId} )")
    public void testPatchBookingById() throws Exception {
        int bookingId = createdBooking.getBookingid();
        apiClient.createToken("admin", "password123");

        String patchRequestBody = objectMapper.writeValueAsString(updatedBooking);
        Response patchResponse = apiClient.patchBookingById(patchRequestBody, bookingId);
        log.info("✅ Отправлен запрос на обновление бронирования с id= " + bookingId + " через PATCH");

        BookingResponse bookingResponse = objectMapper.readValue(patchResponse.asString(), BookingResponse.class);

        step("Проверка обновленных данных бронирования", () -> {
            assertThat(patchResponse.getStatusCode())
                    .as("Cтатус код ответа != 200")
                    .isEqualTo(200);
            assertThat(updatedBooking)
                    .as("Ожидалось, что тело ответа не будет пустым")
                    .isNotNull();
            assertThat(createdBooking.getBookingid())
                    .as("bookingid должен быть положительным числом")
                    .isPositive();
            assertThat(createdBooking.getBooking())
                    .as("Объект booking не должен быть null")
                    .isNotNull();
            assertThat(bookingResponse.getFirstName())
                    .as("firstname не соответствует ожидаемому значению")
                    .isEqualTo(updatedBooking.getFirstname());
            assertThat(bookingResponse.getLastName())
                    .as("lastname не соответствует ожидаемому значению")
                    .isEqualTo(updatedBooking.getLastname());
            assertThat(bookingResponse.getTotalPrice())
                    .as("totalprice не должно измениться")
                    .isEqualTo(155);
            assertThat(bookingResponse.isDepositPaid())
                    .as("depositpaid не должно измениться")
                    .isTrue();
            assertThat(bookingResponse.getAdditionalNeeds())
                    .as("additionalneeds не должно измениться")
                    .isEqualTo("Breakfast");
            assertThat(bookingResponse.getBookingDates().getCheckout())
                    .as("checkout не должно измениться")
                    .isEqualTo("2024-01-05");
            assertThat(bookingResponse.getBookingDates().getCheckin())
                    .as("checkin не должно измениться")
                    .isEqualTo("2024-01-01");
        });
        log.info("✅ Проверка обновленных данных бронирования - завершена");
    }


    @AfterEach
    public void tearDown() {
        //Удаляем созданное бронирование
        apiClient.createToken("admin", "password123");
        apiClient.deleteBooking(createdBooking.getBookingid());

        assertThat(apiClient.getBookingById(createdBooking.getBookingid()).getStatusCode()).isEqualTo(404);


    }
}
