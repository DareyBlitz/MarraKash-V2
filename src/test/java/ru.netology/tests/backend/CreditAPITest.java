package ru.netology.tests.backend;

import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.gson.Gson;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;

import java.util.List;

import static io.restassured.RestAssured.given;

public class CreditAPITest {
    private static DataHelper.Data data;
    private static final Gson gson = new Gson();
    private static final RequestSpecification spec = new RequestSpecBuilder().setBaseUri("http://localhost").setPort(8080)
            .setAccept(ContentType.JSON).setContentType(ContentType.JSON).log(LogDetail.ALL).build();
    private static final String creditUrl = "/credit";
    private static List<SQLHelper.PaymentEntity> payments;
    private static List<SQLHelper.CreditRequestEntity> credits;
    private static List<SQLHelper.OrderEntity> orders;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @AfterEach
    public void setDownMethod() {
        SQLHelper.setDown();
    }

    @Test
    public void shouldPositivePath() {
        data = DataHelper.getValidApprovedCard();
        var body = gson.toJson(data);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(200);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(1, credits.size());
        Assertions.assertEquals(1, orders.size());

        Assertions.assertTrue(credits.get(0).getStatus().equalsIgnoreCase("approved"));
        Assertions.assertEquals(credits.get(0).getBank_id(), orders.get(0).getPayment_id());
        Assertions.assertEquals(credits.get(0).getId(), orders.get(0).getCredit_id());
    }

    @Test
    public void shouldNegativePath() {
        data = DataHelper.getValidDeclinedCard();
        var body = gson.toJson(data);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(200);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(1, credits.size());
        Assertions.assertEquals(1, orders.size());

        Assertions.assertTrue(credits.get(0).getStatus().equalsIgnoreCase("declined"));
        Assertions.assertEquals(credits.get(0).getBank_id(), orders.get(0).getPayment_id());
        Assertions.assertEquals(credits.get(0).getId(), orders.get(0).getCredit_id());
    }

    @Test
    public void shouldStatus400WithEmptyBody() {
        data = DataHelper.getValidApprovedCard();
        given().spec(spec)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(0, orders.size());
    }

    @Test
    public void shouldStatus400WithEmptyNumber() {
        data = new DataHelper.Data(null, DataHelper.generateMonth(1), DataHelper.generateYear(2),
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(data);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(0, orders.size());
    }

    @Test
    public void shouldStatus400WithEmptyMonth() {
        data = new DataHelper.Data(DataHelper.getNumberByStatus("approved"), null, DataHelper.generateYear(2),
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(data);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(0, orders.size());
    }

    @Test
    public void shouldStatus400WithEmptyYear() {
        data = new DataHelper.Data(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1), null,
                DataHelper.generateValidHolder(), DataHelper.generateValidCVC());
        var body = gson.toJson(data);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(0, orders.size());
    }

    @Test
    public void shouldStatus400WithEmptyHolder() {
        data = new DataHelper.Data(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1),
                DataHelper.generateYear(2), null, DataHelper.generateValidCVC());
        var body = gson.toJson(data);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(0, orders.size());
    }

    @Test
    public void shouldStatus400WithEmptyCvc() {
        data = new DataHelper.Data(DataHelper.getNumberByStatus("approved"), DataHelper.generateMonth(1),
                DataHelper.generateYear(2), DataHelper.generateValidHolder(), null);
        var body = gson.toJson(data);
        given().spec(spec).body(body)
                .when().post(creditUrl)
                .then().statusCode(400);

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(0, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(0, orders.size());
    }
}
