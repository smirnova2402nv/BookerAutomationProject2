package core.clients;

import core.settings.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class APIClient {
    private final String baseUrl;

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
    // Настройка базовых параметров HTTP - запросов
    private RequestSpecification getRequestSpec() {
        return RestAssured.given()//Дано
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }
    public Response ping() {
        return getRequestSpec()
                .when()//объявление того, что будем сейчас делать (Когда)
                .get(ApiEndpoints.PING.getPath()) // Используем ENUM для эндпоинта /ping
                .then()// Затем
                .statusCode(201) // Ожидаемый статус-код 201 Created
                .extract()//распоковываем
                .response();// то что приходит в респонс
    }
    // get запрос на эндпоинт /booking
    public Response getBooking() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.BOOKING.getPath()) // Используем ENUM для эндпоинта /booking
                .then()
                .statusCode(200) // Ожидаемый статус-код 200 OK
                .extract()
                .response();
    }

    public Response getBookingById(int bookingId) {
        return getRequestSpec()
                .when()//объявление того, что будем сейчас делать (Когда)
                .get(ApiEndpoints.BOOKINGBYID.getPath() + bookingId) // Используем ENUM для эндпоинта /ping
                .then()// Затем
                .statusCode(200) // Ожидаемый статус-код 200 OK
                .extract()//распоковываем
                .response();// то что приходит в респонс
    }
}
