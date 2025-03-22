package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class PayPage {

    public void openPage() {
        open("http://localhost:8080");
        $("selector").shouldBe(visible);
    }


    private final SelenideElement card = $x("//div[@id='root']/div/div[contains(@class, 'card')]");

    private final SelenideElement payButton = $x("//*[@id=\"root\"]/div/button[1]/span/span");
    private final SelenideElement creditButton = $x("/html/body/div/div/button[2]/span/span");
    private final SelenideElement formHead = $x("//form//preceding-sibling::h3");
    private final SelenideElement buyContinue = $x("//*[@id=\"root\"]/div/button[2]/span/span");


    public PayPage() {
        SelenideElement head = $x("//div[@id='root']/div/h2");
        head.should(visible, Condition.text("Путешествие дня"));
        card.should(visible);

        payButton.should(visible);
        creditButton.should(visible);

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
        formHead.should(visible, Condition.text("Купить"));
        return new FormPage();
    }

    public FormPage clickCreditButton() {
        creditButton.click();
        formHead.should(visible, Condition.text("Купить в кредит"));
        return new FormPage();
    }

    public FormPage clickBuyContinue() {
        buyContinue.click();
        $(".notification.success").shouldBe(visible, Duration.ofSeconds(5));

        return new FormPage();
    }


    public int getAmount() {
        var str = card.$x(".//ul/li[contains(text(), 'руб')]").getText().split(" ");
        return Integer.parseInt(str[1] + str[2]);
    }
}
