package core.clients;

import core.models.ParametersGetBooking;
import core.settings.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class APIClient {
    private final String baseUrl;
    private String token;

    public APIClient() {
        this.baseUrl = determineBaseUrl();
    }

    private String determineBaseUrl() {
        String environment = System.getProperty("env", "test");
        String configFileName = "application-" + environment + ".properties";

        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                throw new IllegalStateException("Configuration file not found: " + configFileName);
            }
            properties.load(input);
        } catch (
                IOException e) {
            throw new IllegalStateException("Unable to load configuration file: " + configFileName, e);
        }
        return properties.getProperty("baseUrl");
    }

    private RequestSpecification getRequestSpecWithoutAuth() {
        return RestAssured.given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
        // ❌ УБРАН .filter(addAuthTokenFilter())
    }

    // Настройка базовых параметров HTTP - запросов = спецификация в рест ажурд
    private RequestSpecification getRequestSpec() {
        return RestAssured.given()//Дано
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .filter(addAuthTokenFilter());
    }

    //Метод плучения токена
    public void createToken(String username, String password) {
        log.info("Запущено получение токена");
        //Тело запроса для получения токена
        String requestBody = String.format("{\"username\": \"%s\", \"password\": \"%s\" }", username, password);

        Response response = getRequestSpecWithoutAuth()
                .body(requestBody)
                .when()
                .post(ApiEndpoints.AUTH.getPath())
                .then()
                .statusCode(200)
                .extract()
                .response();

        //Извлекаем токен из ответа
        token = response.jsonPath().getString("token");
        log.info("✅ Токен успешно получен");
    }

    //Фильтр добавления токена в заголовок Authorization
    private Filter addAuthTokenFilter() {
        return (FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext
                ctx) -> {
            if (token != null) {
                requestSpec.header("Cookie", "token=" + token);
            }
            return ctx.next(requestSpec, responseSpec);
        };
    }

    public Response ping() {
        log.info("Получение информации по проверке работоспособности API через /ping");
        return getRequestSpec()
                .when()//объявление того, что будем сейчас делать (Когда)
                .get(ApiEndpoints.PING.getPath()) // Используем ENUM для эндпоинта /ping
                .then()// Затем
                .statusCode(201) // Ожидаемый статус-код 201 Created
                .extract()//распоковываем
                .response();// то что приходит в респонс
    }

    // get запрос на эндпоинт /booking
    public Response getBooking(String parametersGetBooking) {
        log.info("Запущено получение списка всех бронирований GET /booking");
        return getRequestSpec()
                .body(parametersGetBooking)
                .log().all()
                .when()
                .log().all()
                .get(ApiEndpoints.BOOKING.getPath()) // Используем ENUM для эндпоинта /booking
                .then()
                .log().all()
                .statusCode(200) // Ожидаемый статус-код 200 OK
                .extract()
                .response();

    }

    public Response getBookingById(int bookingId) {
        log.info("Запущен поиск бронирования по ID GET /booking/ " + bookingId);
        return getRequestSpec()
                .when()//объявление того, что будем сейчас делать (Когда)
                .get(ApiEndpoints.BOOKINGBYID.getPath() + bookingId) // Используем ENUM для эндпоинта /ping
                .then()
                .log().all()// Затем
                .extract()//распоковываем
                .response();// то что приходит в респонс
    }

    // DELETE запрос на эндпоинт /booking
    public Response deleteBooking(int bookingId) {
        log.info("Запущено удаление бронирования DELETE /booking/" + bookingId);
        return getRequestSpec()
                .pathParam("id", bookingId)
                .when()
                .delete(ApiEndpoints.BOOKINGBYID.getPath() + "{id}")
                .then()
                //.log().all()
                .statusCode(201)
                .extract()
                .response();
    }

    public Response createBooking(String newBooking) {
        log.info("Запущено создание бронирования POST /booking");
        return getRequestSpec()
                .body(newBooking)
                //.log().all()
                .when()
                .post(ApiEndpoints.BOOKING.getPath())
                .then()
                //.log().all()
                .extract()
                .response();
    }

    public Response patchBookingById(String requestBody, int bookingId) {
        log.info("Запущено обновление бронирования PATCH /booking/{bookingId}");
        return getRequestSpec()
                .pathParam("id", bookingId)
                .body(requestBody)
                .log().all()
                .when()
                .patch(ApiEndpoints.BOOKINGBYID.getPath() + "{id}")
                .then()
                .log().all()
                .extract()
                .response();
    }
    public Response putBookingById(String requestBody, int bookingId) {
        log.info("Запущено обновление бронирования PUT /booking/" + bookingId);
        return getRequestSpec()
                .pathParam("id", bookingId)
                .body(requestBody)
                .when()
                .put(ApiEndpoints.BOOKINGBYID.getPath() + "{id}")
                .then()
                //.log().all()
                .extract()
                .response();
    }

}
