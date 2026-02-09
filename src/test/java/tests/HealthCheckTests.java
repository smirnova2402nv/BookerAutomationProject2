package tests;

import core.clients.APIClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HealthCheckTests {
    private APIClient apiClient; // создаем переменную APIClient, для того чтобы в следующем коде положить в нее объект APIClient
    @BeforeEach //часть JUNIT, аннотация позволяющая перед каждым тестом создавать новый объект APIClient
    public void setup() {
        apiClient = new APIClient();

    }

    // Тест на метод ping
    @Test
    public void testPing() {
        // Выполняем get запрос на /ping через ApiClient
        Response response = apiClient.ping();
        assertThat(response.getStatusCode()).isEqualTo(201);//бибилиотека assertThat- сравнивает значения true?
    }
}
