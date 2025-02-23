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

public class DebitUITest {
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

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertBuyOperationIsSuccessful();

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(1, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(1, orders.size());

        Assertions.assertEquals(card.getAmount() * 100, payments.get(0).getAmount());
        Assertions.assertTrue(payments.get(0).getStatus().equalsIgnoreCase("approved"));
        Assertions.assertEquals(payments.get(0).getTransaction_id(), orders.get(0).getPayment_id());
        Assertions.assertNull(orders.get(0).getCredit_id());
    }

    @Test
    public void shouldNegativePath() {
        data = DataHelper.getValidDeclinedCard();

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertBuyOperationWithErrorNotification();

        payments = SQLHelper.getPayments();
        credits = SQLHelper.getCreditsRequest();
        orders = SQLHelper.getOrders();
        Assertions.assertEquals(1, payments.size());
        Assertions.assertEquals(0, credits.size());
        Assertions.assertEquals(1, orders.size());

        Assertions.assertEquals(card.getAmount() * 100, payments.get(0).getAmount());
        Assertions.assertTrue(payments.get(0).getStatus().equalsIgnoreCase("declined"));
        Assertions.assertEquals(payments.get(0).getTransaction_id(), orders.get(0).getPayment_id());
        Assertions.assertNull(orders.get(0).getCredit_id());
    }

    @Test
    public void shouldImmutableInputValuesAfterClickButton() {
        data = DataHelper.getValidApprovedCard();

        form = card.clickCreditButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        card.clickPayButton();
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
    }

    @Test
    public void shouldVisibleNotificationWithEmptyNumber() {
        data = DataHelper.getValidApprovedCard();
        form = card.clickPayButton();
        form.insertingValueInForm("", data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue("", data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertNumberFieldIsEmptyValue();
    }

    @Test
    public void shouldSuccessfulWithStartEndSpacerInNumber() {
        data = DataHelper.getValidApprovedCard();
        var number = " " + data.getNumber() + " ";
        var matchesNumber = data.getNumber();

        form = card.clickPayButton();
        form.insertingValueInForm(number, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(matchesNumber, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertBuyOperationIsSuccessful();
    }

    @Test
    public void shouldVisibleNotificationWith16DigitsInNumber() {
        data = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateInvalidCardNumberWith16RandomNumerals();

        form = card.clickPayButton();
        form.insertingValueInForm(number, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(number, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertNumberFieldIsInvalidValue();
    }

    @Test
    public void shouldUnsuccessfulWith10DigitsInNumber() {
        data = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateValidCardNumberWith10Numerals();

        form = card.clickPayButton();
        form.insertingValueInForm(number, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(number, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertBuyOperationWithErrorNotification();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInNumber() {
        data = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateInvalidCardNumberWithRandomSymbols();
        var matchesNumber = "";

        form = card.clickPayButton();
        form.insertingValueInForm(number, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(matchesNumber, data.getMonth(), data.getYear(), data.getHolder(), data.getCvc());
        form.assertNumberFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithEmptyMonth() {
        data = DataHelper.getValidApprovedCard();
        var month = "";
        var matchesMonth = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), month, data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), matchesMonth, data.getYear(), data.getHolder(), data.getCvc());
        form.assertMonthFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWith00InMonth() {
        data = DataHelper.getValidApprovedCard();
        var month = "00";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), month, data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), month, data.getYear(), data.getHolder(), data.getCvc());
        form.assertMonthFieldIsInvalidValue();
    }

    @Test
    public void shouldVisibleNotificationWith13InMonth() {
        data = DataHelper.getValidApprovedCard();
        var month = "13";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), month, data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), month, data.getYear(), data.getHolder(), data.getCvc());
        form.assertMonthFieldIsInvalidValue();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInMonth() {
        data = DataHelper.getValidApprovedCard();
        var month = DataHelper.generateMonthWithRandomSymbols();
        var matchesMonth = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), month, data.getYear(), data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), matchesMonth, data.getYear(), data.getHolder(), data.getCvc());
        form.assertMonthFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithEmptyYear() {
        data = DataHelper.getValidApprovedCard();
        var year = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), year, data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), year, data.getHolder(), data.getCvc());
        form.assertYearFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInYear() {
        data = DataHelper.getValidApprovedCard();
        var year = DataHelper.generateMonthWithRandomSymbols();
        var matchesYear = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), year, data.getHolder(), data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), matchesYear, data.getHolder(), data.getCvc());
        form.assertYearFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithEmptyHolder() {
        data = DataHelper.getValidApprovedCard();
        var holder = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), holder, data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), holder, data.getCvc());
        form.assertHolderFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithCyrillicInHolder() {
        data = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateInvalidHolderWithCyrillicSymbols();
        var matchesHolder = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), holder, data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), matchesHolder, data.getCvc());
        form.assertHolderFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolInHolder() {
        data = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateHolderWithInvalidSymbols();
        var matchesHolder = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), holder, data.getCvc());
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), matchesHolder, data.getCvc());
        form.assertHolderFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWithEmptyCVC() {
        data = DataHelper.getValidApprovedCard();
        var cvc = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), cvc);
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), cvc);
        form.assertCvcFieldIsEmptyValue();
    }

    @Test
    public void shouldVisibleNotificationWith2DigitsInCVC() {
        data = DataHelper.getValidApprovedCard();
        var cvc = DataHelper.generateInvalidCVCWith2Numerals();

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), cvc);
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), cvc);
        form.assertCvcFieldIsInvalidValue();
    }

    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInCVC() {
        data = DataHelper.getValidApprovedCard();
        var cvc = DataHelper.generateInvalidCVCWithRandomSymbols();
        var matchesCvc = "";

        form = card.clickPayButton();
        form.insertingValueInForm(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), cvc);
        form.matchesByInsertValue(data.getNumber(), data.getMonth(), data.getYear(), data.getHolder(), matchesCvc);
        form.assertCvcFieldIsEmptyValue();
    }
}
