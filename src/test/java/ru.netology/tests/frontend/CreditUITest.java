package ru.netology.tests.frontend;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.pages.FormPage;
import ru.netology.pages.PayPage;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreditUITest {
    private static DataHelper.Data data;

    private static PayPage card;
    private static FormPage form;
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

    @BeforeEach
    public void setupMethod() {
        open("http://localhost:8080/");
        card = new PayPage();
    }

    @AfterEach
    public void setDownMethod() {
        SQLHelper.setDown();
    }

    @Test
    public void shouldPositivePath() {
        data = DataHelper.getValidApprovedCard();

        form = card.clickCreditButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertBuyOperationIsSuccessful();

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(1, credits.size());
        assertEquals(1, orders.size());

        assertTrue(credits.get(0).getStatus().equalsIgnoreCase("approved"));
        assertEquals(credits.get(0).getBank_id(), orders.get(0).getPayment_id());
        assertEquals(credits.get(0).getId(), orders.get(0).getCredit_id());
    }

    @Test
    public void shouldNegativePath() {
        data = DataHelper.getValidDeclinedCard();

        form = card.clickCreditButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertBuyOperationWithErrorNotification();

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        assertEquals(0, payments.size());
        assertEquals(1, credits.size());
        assertEquals(1, orders.size());

        assertTrue(credits.get(0).getStatus().equalsIgnoreCase("declined"));
        assertEquals(credits.get(0).getBank_id(), orders.get(0).getPayment_id());
        assertEquals(credits.get(0).getId(), orders.get(0).getCredit_id());
    }

    @Test
    public void shouldImmutableInputValuesAfterClickButton() {
        data = DataHelper.getValidApprovedCard();

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form = card.clickCreditButton();
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
    }
}
