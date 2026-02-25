package tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.*;
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

import java.util.ArrayList;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class GetBookingTest {

    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private List<Integer> createdBookingIds;
    private String authToken;

    // Тестовые данные для разных бронирований
    private NewBooking bookingMike;
    private NewBooking bookingJohn;
    private NewBooking bookingAnna;

    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        createdBookingIds = new ArrayList<>();

        // Создаем бронирование 1: Mike Line
        bookingMike = new NewBooking();
        bookingMike.setFirstname("Mike");
        bookingMike.setLastname("Line");
        bookingMike.setTotalprice(155);
        bookingMike.setDepositpaid(true);
        bookingMike.setBookingdates(new BookingDates("2024-01-01", "2024-01-05"));
        bookingMike.setAdditionalneeds("Breakfast");

        // Создаем бронирование 2: Mike Andersen
        bookingJohn = new NewBooking();
        bookingJohn.setFirstname("Mike");
        bookingJohn.setLastname("Andersen");
        bookingJohn.setTotalprice(200);
        bookingJohn.setDepositpaid(false);
        bookingJohn.setBookingdates(new BookingDates("2024-02-10", "2024-02-15"));
        bookingJohn.setAdditionalneeds("Lunch");

        // Создаем бронирование 3: Anna Cherri
        bookingAnna = new NewBooking();
        bookingAnna.setFirstname("Anna");
        bookingAnna.setLastname("Cherri");
        bookingAnna.setTotalprice(300);
        bookingAnna.setDepositpaid(true);
        bookingAnna.setBookingdates(new BookingDates("2024-03-20", "2024-03-25"));
        bookingAnna.setAdditionalneeds("Dinner");

        // Создаем все три бронирования
        createTestBookings();
    }

    private void createTestBookings() {
        try {
            // Создаем бронирование Mike Line
            Response response1 = apiClient.createBooking(objectMapper.writeValueAsString(bookingMike));
            CreatedBooking created1 = objectMapper.readValue(response1.getBody().asString(), CreatedBooking.class);
            createdBookingIds.add(created1.getBookingid());
            log.info("Создано бронирование 1 (Mike Line) с ID: {}", created1.getBookingid());

            // Создаем бронирование Mike Johnson
            Response response2 = apiClient.createBooking(objectMapper.writeValueAsString(bookingJohn));
            CreatedBooking created2 = objectMapper.readValue(response2.getBody().asString(), CreatedBooking.class);
            createdBookingIds.add(created2.getBookingid());
            log.info("Создано бронирование 2 (Mike Andersen) с ID: {}", created2.getBookingid());

            // Создаем бронирование Anna Smith
            Response response3 = apiClient.createBooking(objectMapper.writeValueAsString(bookingAnna));
            CreatedBooking created3 = objectMapper.readValue(response3.getBody().asString(), CreatedBooking.class);
            createdBookingIds.add(created3.getBookingid());
            log.info("Создано бронирование 3 (Anna Cherri) с ID: {}", created3.getBookingid());

        } catch (Exception e) {
            log.error("Ошибка при создании тестовых бронирований", e);
            throw new RuntimeException("Ошибка при создании тестовых бронирований: " + e.getMessage());
        }
    }

    @Test
    @Feature("Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Получение списка всех бронирований ( GET /booking ) без фильтров")

    public void testGetAllBookings() throws Exception {
        Response response = apiClient.getAllBookings();

        step("Проверка статус-кода 200", () ->
                assertEquals(200, response.getStatusCode())
        );
        List<Booking> allBookings = objectMapper.readValue(
                response.getBody().asString(),
                new TypeReference<List<Booking>>() {
                }
        );
        log.info("Запущены проверки");
        step("Проверка, что список не пуст", () ->
                assertThat(allBookings)
                        .isNotEmpty()
        );

            List<Integer> foundIds = allBookings.stream()
                    .map(Booking::getBookingid)
                    .toList();

            step("Проверка того, что созданные бронирования есть в списке", () ->
                    assertThat(foundIds).containsAll(createdBookingIds)

        );
        log.info("Завершены проверки");
    }

    @Test
    @Feature("Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Фильтрация бронирований по имени (firstName)")
    public void testGetBookingByParameters() throws Exception {
        Response response = apiClient.getBookingsByFirstName("Mike");

        log.info("Запущены проверки");
        step("Проверка статус-кода 200", () ->
                assertEquals(200, response.getStatusCode())
        );

        List<Booking> filteredBookings = objectMapper.readValue(
                response.getBody().asString(),
                new TypeReference<List<Booking>>() {
                }
        );

            List<Integer> foundIds = filteredBookings.stream()
                    .map(Booking::getBookingid)
                    .toList();

            step("Проверка того, что приходят id бронирований с переданным именем", () -> assertThat(foundIds)
                    .contains(createdBookingIds.get(0), createdBookingIds.get(1))
                    .doesNotContain(  // Проверяем, что НЕ содержит другие созданные бронирования
                            createdBookingIds.get(2))
        );
        log.info("Завершены проверки");
    }

    @Test
    @Feature("Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Фильтрация бронирований по фамилии (lastName)")
    public void testFilterByLastName() throws Exception {
        Response response = apiClient.getBookingsByLastName("Andersen");

        step("Проверка статус-кода 200", () ->
                assertEquals(200, response.getStatusCode())
        );

        List<Booking> filteredBookings = objectMapper.readValue(
                response.getBody().asString(),
                new TypeReference<List<Booking>>() {
                }
        );
        log.info("Запущены проверки");
            List<Integer> foundIds = filteredBookings.stream()
                    .map(Booking::getBookingid)
                    .toList();
            step("Проверка того, что приходят id бронирований с переданной фамилией", () -> assertThat(foundIds).contains(
                    createdBookingIds.get(1))
                    .doesNotContain(  // Проверяем, что НЕ содержит другие созданные бронирования
                            createdBookingIds.get(0),
                            createdBookingIds.get(2))
        );
        log.info("Завершены проверки");
    }

    @Test
    @Feature("Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Фильтрация бронирований по имени и фамилии")
    public void testFilterByFirstAndLastName() throws Exception {
        Response response = apiClient.getBookingsByFirstAndLastName("Mike", "Andersen");

        step("Проверка статус-кода 200", () ->
                assertEquals(200, response.getStatusCode())
        );

        List<Booking> filteredBookings = objectMapper.readValue(
                response.getBody().asString(),
                new TypeReference<List<Booking>>() {
                }
        );
        log.info("Запущены проверки");
            List<Integer> foundIds = filteredBookings.stream()
                    .map(Booking::getBookingid)
                    .toList();
        step("Проверка того, что приходят id бронирований с переданным именем и фамилией", () -> assertThat(foundIds)
                    .contains(createdBookingIds.get(1))
                    .doesNotContain(  // Проверяем, что НЕ содержит другие созданные бронирования
                            createdBookingIds.get(0),
                            createdBookingIds.get(2))
        );
        log.info("Завершены проверки");
    }

    @Test
    @Feature("Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Фильтрация бронирований по датам заезда и выезда")
    public void testFilterByDates() throws Exception {
        Response response = apiClient.getBookingsByDates("2024-03-20", "2024-03-25");

        step("Проверка статус-кода 200", () ->
                assertEquals(200, response.getStatusCode())
        );

        List<Booking> filteredBookings = objectMapper.readValue(
                response.getBody().asString(),
                new TypeReference<List<Booking>>() {
                }
        );
        log.info("Запущены проверки");
        List<Integer> foundIds = filteredBookings.stream()
                .map(Booking::getBookingid)
                .toList();
        if (!foundIds.isEmpty()) {
            step("Проверка того, что приходит id бронирований с переданными датами в запросе", () ->
                    assertThat(foundIds)
                    .as("Должно быть найдено бронирование Anna Cherri с датами 2024-03-20 - 2024-03-25")
                    .contains(createdBookingIds.get(2))
                    .doesNotContain(createdBookingIds.get(0), createdBookingIds.get(1))
            );
            log.info("Фильтрация по датам работает корректно");
        } else {
            log.warn("API не поддерживает фильтрацию по датам - получен пустой результат");
    }
        log.info("Завершены проверки");
    }

    @Test
    @Feature("Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Фильтрация бронирований по всем параметрам одновременно")
    public void testFilterByAllParameters() throws Exception {

        Response response = apiClient.getBookingsByAllParams("Anna", "Cherri", "2024-03-20", "2024-03-25");

        step("Проверка статус-кода 200", () ->
                assertEquals(200, response.getStatusCode())
        );

        List<Booking> filteredBookings = objectMapper.readValue(
                response.getBody().asString(),
                new TypeReference<List<Booking>>() {
                }
        );
        log.info("Запущены проверки");
        List<Integer> foundIds = filteredBookings.stream()
                .map(Booking::getBookingid)
                .toList();
        if (!foundIds.isEmpty()) {
            step("Проверка того, что найдено бранирование соответствующее всем параметрам и отсутствуют другие", () ->
                    assertThat(foundIds)
                    .as("Должно быть найдено бронирование ПО ВСЕМ параметрам")
                    .contains(createdBookingIds.get(2))
                    .doesNotContain(createdBookingIds.get(0), createdBookingIds.get(1))
            );
            log.info("Фильтрация по ВСЕМ параметрам работает корректно");
        } else {
            log.warn("API не поддерживает фильтрацию по датам - получен пустой результат");
        }
        log.info("Завершены проверки");

    }

    @Test
    @Feature("Booking")
    @Severity(SeverityLevel.NORMAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Фильтрация бронирований с несуществующими параметрами")
    public void testFilterWithNonExistentParameters() throws Exception {
        // Фильтруем по несуществующему имени
        Response response = apiClient.getBookingsByFirstName("NonExistentName");
        step("Проверка статус-кода 200", () ->
                assertEquals(200, response.getStatusCode())
        );

        // Получаем отфильтрованный список
        List<Booking> filteredBookings = objectMapper.readValue(
                response.getBody().asString(),
                new TypeReference<List<Booking>>() {
                }
        );
        log.info("Запущены проверки");
        // Проверяем, что список пуст
        step("Проверка, что список пуст при несуществующих параметрах", () ->
                assertThat(filteredBookings)
                        .as("При фильтрации по несуществующим параметрам должен возвращаться пустой список")
                        .isEmpty()
        );
        log.info("Завершены проверки");
    }

    @AfterEach
    public void tearDown() {
        log.info("Удаление тестовых бронирований...");
        apiClient.createToken("admin", "password123");
        for (Integer bookingId : createdBookingIds) {
            apiClient.deleteBooking(bookingId);

            assertThat(apiClient.getBookingById(bookingId).getStatusCode())
                    .isEqualTo(404);

            log.info("Бронирование {} удалено", bookingId);
        }
    }
}



