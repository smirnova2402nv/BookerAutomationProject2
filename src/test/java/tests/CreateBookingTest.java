package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingDates;
import core.models.CreatedBooking;
import core.models.NewBooking;

import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateBookingTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking;
    private NewBooking newBooking;

    //Инициализация APi клиента перед каждым тестом
    @BeforeEach //часть JUNIT, аннотация позволяющая перед каждым тестом создавать новый объект APIClient
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
    }

    @Test
    @Feature("Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Создание бронирования ( POST /booking)")
    public void createBooking() throws JsonProcessingException {
        // Выполняем запрос к эндпоинту /booking через APIClient
        String requestBody = objectMapper.writeValueAsString(newBooking);
        Response response = apiClient.createBooking(requestBody);

        // Проверяем статус код
        step("Проверка, что статус код ответа == 200", () ->
                assertThat(response.getStatusCode())
                        .as("Ожидалось, что код ответа будет 200. Статус код: " + response.getStatusCode())
                        .isEqualTo(200)
        );

        //Десереализуем тело ответа в объект BooKing
        String responseBody = response.asString();
        createdBooking = objectMapper.readValue(responseBody, CreatedBooking.class);

        //Проверяем, что тело ответа содержит объект нового бронирования
        step("Проверка, что тело ответа не пустое", () ->
                assertThat(createdBooking)
                .as("Ожидалось, что тело ответа не будет пустым")
                .isNotNull());
        step("Проверка, что присвоен корректный bookingid", () -> {
            assertThat(createdBooking.getBookingid())
                    .as("bookingid должен быть положительным числом")
                    .isPositive();  // проверяет, что > 0
        });
        step("Проверка, что объект booking присутствует в ответе", () ->
                assertThat(createdBooking.getBooking())
                        .as("Объект booking не должен быть null")
                        .isNotNull());
        step("Проверка данных firstname", () ->
                assertEquals(createdBooking.getBooking().getFirstname(), newBooking.getFirstname(), "firstname не соответствует ожидаемому значению"));
        step("Проверка данных lastName", () ->
                assertEquals(createdBooking.getBooking().getLastname(), newBooking.getLastname(), "lastname не соответствует ожидаемому значению"));
        step("Проверка данных totalprice", () ->
                assertEquals(createdBooking.getBooking().getTotalprice(), newBooking.getTotalprice(), "totalprice не соответствует ожидаемому значению"));
        step("Проверка данных depositpaid", () ->
                assertEquals(createdBooking.getBooking().getDepositpaid(), newBooking.getDepositpaid(), "depositpaid не соответствует ожидаемому значению"));
        step("Проверка данных checkin в bookingdates", () ->
                assertEquals(createdBooking.getBooking().getBookingdates().getCheckin(), newBooking.getBookingdates().getCheckin(), "checkin в bookingdates - отсутствует"));
        step("Проверка данных checkout в bookingdates", () ->
                assertEquals(createdBooking.getBooking().getBookingdates().getCheckout(), newBooking.getBookingdates().getCheckout(), "checkout в bookingdates - отсутствует"));
        step("Проверка данных additionalneeds", () ->
                assertEquals(createdBooking.getBooking().getAdditionalneeds(), newBooking.getAdditionalneeds(), "additionalneeds не соответствует ожидаемому значению"));
/*
        assertThat(createdBooking).isNotNull();
        assertEquals(createdBooking.getBooking().getFirstname(), newBooking.getFirstname());
        assertEquals(createdBooking.getBooking().getLastname(), newBooking.getLastname());
        assertEquals(createdBooking.getBooking().getTotalprice(), newBooking.getTotalprice());
        assertEquals(createdBooking.getBooking().isDepositpaid(), newBooking.isDepositpaid());
        assertEquals(createdBooking.getBooking().getBookingdates().getCheckin(), newBooking.getBookingdates().getCheckin());
        assertEquals(createdBooking.getBooking().getBookingdates().getCheckout(), newBooking.getBookingdates().getCheckout());
        assertEquals(createdBooking.getBooking().getAdditionalneeds(), newBooking.getAdditionalneeds());*/
    }

    @AfterEach
    public void tearDown() {
        //Удаляем созданное бронирование
        apiClient.createToken("admin", "password123");
        apiClient.deleteBooking(createdBooking.getBookingid());

        assertThat(apiClient.getBookingById(createdBooking.getBookingid()).getStatusCode()).isEqualTo(404);
    }
}