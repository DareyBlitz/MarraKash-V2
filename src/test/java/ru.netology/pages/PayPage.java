package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class PayPage {
    ;
    private final SelenideElement card = $x("//div[@id='root']/div/div[contains(@class, 'card')]");

    private final SelenideElement payButton = $x("//span[text()='Купить']//ancestor::button");;
    private final SelenideElement creditButton = $x("//span[text()='Купить в кредит']//ancestor::button");;
    private final SelenideElement formHead = $x("//form//preceding-sibling::h3");

    public PayPage() {
        SelenideElement head = $x("//div[@id='root']/div/h2");
        head.should(Condition.visible, Condition.text("Путешествие дня"));
        card.should(Condition.visible);

        payButton.should(Condition.visible);
        creditButton.should(Condition.visible);

        formHead.should(Condition.hidden);
        SelenideElement form = $x("//form");
        form.should(Condition.hidden);
        SelenideElement successNotification = $x("//div[contains(@class, 'notification_status_ok')]");
        successNotification.should(Condition.hidden);
        SelenideElement errorNotification = $x("//div[contains(@class, 'notification_status_error')]");
        errorNotification.should(Condition.hidden);
    }

    public FormPage clickPayButton() {
        payButton.click();
        formHead.should(Condition.visible, Condition.text("Оплата по карте"));
        return new FormPage();
    }

    public FormPage clickCreditButton() {
        creditButton.click();
        formHead.should(Condition.visible, Condition.text("Кредит по данным карты"));
        return new FormPage();
    }

    public int getAmount() {
        var str = card.$x(".//ul/li[contains(text(), 'руб')]").getText().split(" ");
        return Integer.parseInt(str[1] + str[2]);
    }
}
