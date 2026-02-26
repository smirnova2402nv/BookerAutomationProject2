package tests;

import core.clients.APIClient;
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

/*public class HealthCheckTests {
    private APIClient apiClient; // создаем переменную APIClient, для того чтобы в следующем коде положить в нее объект APIClient

    @BeforeEach //часть JUNIT, аннотация позволяющая перед каждым тестом создавать новый объект APIClient
    public void setup() {
        apiClient = new APIClient();

    }

    // Тест на метод ping
    @Test
    @Feature("Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Проверка того, что API запущен и работает")
    public void testPing() {
        // Выполняем get запрос на /ping через ApiClient
        Response response = apiClient.ping();
        step("Проверка, что статус код ответа == 201", () ->
                assertThat(response.getStatusCode())
                        .as("Ожидалось, что код ответа будет 201. Статус код: " + response.getStatusCode())
                        .isEqualTo(201)//бибилиотека assertThat- сравнивает значения true?
        );

    }
}
*/
