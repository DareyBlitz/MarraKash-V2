package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormPage {

    private final SelenideElement form = $x("//form");
    private final SelenideElement numberLabel = form.$x(".//span[text()='Номер карты']//ancestor::div/span");
    private final SelenideElement numberInput = numberLabel.$x(".//ancestor::span//input");
    private final SelenideElement monthLabel = form.$x(".//span[text()='Месяц']//ancestor::div/span/span[1]/span");
    private final SelenideElement monthInput = monthLabel.$x(".//input");
    private final SelenideElement yearLabel = form.$x(".//span[text()='Год']//ancestor::div/span/span[2]/span");
    private final SelenideElement yearInput = yearLabel.$x(".//input");
    private final SelenideElement holderLabel = form.$x(".//span[text()='Владелец']//ancestor::div/span/span[1]/span");
    private final SelenideElement holderInput = holderLabel.$x(".//input");
    private final SelenideElement cvcLabel = form.$x(".//span[text()='CVC/CVV']//ancestor::div/span/span[2]/span");
    private final SelenideElement cvcInput = cvcLabel.$x(".//input");
    private final SelenideElement continueButton = form.$x(".//span[text()='Продолжить']//ancestor::button");

    private final SelenideElement successNotification = $x("//div[contains(@class, 'notification_status_ok')]");
    private final SelenideElement successCloseButton = successNotification.$x("./button");
    private final SelenideElement errorNotification = $x("//div[contains(@class, 'notification_status_error')]");
    private final SelenideElement errorCloseButton = errorNotification.$x("./button");

    public FormPage() {
        SelenideElement card = $x("//div[@id='root']/div/div[contains(@class, 'card')]");
        card.should(Condition.visible);
        SelenideElement payButton = $x("//span[text()='Купить']//ancestor::button");
        payButton.should(Condition.visible);
        SelenideElement creditButton = $x("//span[text()='Купить в кредит']//ancestor::button");
        creditButton.should(Condition.visible);

        form.should(Condition.visible);
        successNotification.should(Condition.hidden);
        errorNotification.should(Condition.hidden);
    }

    public void insertingValueInForm(String number, String month, String year, String holder, String cvc) {
        numberLabel.click();
        numberInput.val(number);
        monthLabel.click();
        monthInput.val(month);
        yearLabel.click();
        yearInput.val(year);
        holderLabel.click();
        holderInput.val(holder);
        cvcLabel.click();
        cvcInput.val(cvc);
        continueButton.click();
    }

    public void matchesByInsertValue(String number, String month, String year, String holder, String cvc) {
        assertEquals(number, numberInput.getValue());
        assertEquals(month, monthInput.getValue());
        assertEquals(year, yearInput.getValue());
        assertEquals(holder, holderInput.getValue());
        assertEquals(cvc, cvcInput.getValue());
    }

    public void assertBuyOperationIsSuccessful() {
        successNotification.should(Condition.visible, Duration.ofSeconds(15));
        successNotification.should(Condition.cssClass("notification_visible"));
        successNotification.$x("./div[@class='notification__title']").should(Condition.text("Успешно"));
        successNotification.$x("./div[@class='notification__content']").should(Condition.text("Операция одобрена Банком."));
        successCloseButton.click();
        successNotification.should(Condition.hidden);
    }

    public void assertBuyOperationWithErrorNotification() {
        errorNotification.should(Condition.visible, Duration.ofSeconds(15));
        errorNotification.should(Condition.cssClass("notification_visible"));
        errorNotification.$x("/div[@class='notification__title']").should(Condition.text("Ошибка"));
        errorNotification.$x("/div[@class='notification__content']").should(Condition.text("Ошибка! Банк отказал в проведении операции."));
        errorCloseButton.click();
        errorNotification.should(Condition.hidden);
    }

    public void assertNumberFieldIsEmptyValue() {
        numberLabel.should(Condition.cssClass("input_invalid")).shouldNot(Condition.cssClass("input_has-value"));
        numberLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Неверный формат"));
    }

    public void assertNumberFieldIsInvalidValue() {
        numberLabel.should(Condition.cssClass("input_invalid"), Condition.cssClass("input_has-value"));
        numberLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Неверный формат"));
    }

    public void assertMonthFieldIsEmptyValue() {
        monthLabel.should(Condition.cssClass("input_invalid")).shouldNot(Condition.cssClass("input_has-value"));
        monthLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Неверный формат"));
    }

    public void assertMonthFieldIsInvalidValue() {
        monthLabel.should(Condition.cssClass("input_invalid"), Condition.cssClass("input_has-value"));
        monthLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Неверный формат"));
    }

    public void assertYearFieldIsEmptyValue() {
        yearLabel.should(Condition.cssClass("input_invalid"));
        yearLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Неверный формат"));
    }

    public void assertHolderFieldIsEmptyValue() {
        holderLabel.should(Condition.cssClass("input_invalid")).shouldNot(Condition.cssClass("input_has-value"));
        holderLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Неверный формат"));
    }

    public void assertHolderFieldIsInvalidValue() {
        holderLabel.should(Condition.cssClass("input_invalid"), Condition.cssClass("input_has-value"));
        holderLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Неверный формат"));
    }

    public void assertCvcFieldIsEmptyValue() {
        cvcLabel.should(Condition.cssClass("input_invalid")).shouldNot(Condition.cssClass("input_has-value"));
        cvcLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Неверный формат"));
    }

    public void assertCvcFieldIsInvalidValue() {
        cvcLabel.should(Condition.cssClass("input_invalid"), Condition.cssClass("input_has-value"));
        cvcLabel.$x(".//span[@class='input__sub']").should(Condition.visible, Condition.text("Неверный формат"));
    }
}
