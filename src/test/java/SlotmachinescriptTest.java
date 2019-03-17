import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class SlotmachinescriptTest {

    private WebDriver driver;
    private String url = "http://slotmachinescript.com/";
    private String driverLocation = "D:\\geckodriver\\geckodriver.exe";
    private WebDriverWait wait;

    @BeforeClass
    public void beforeClass() {
        System.setProperty("webdriver.gecko.driver", driverLocation);
        driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, 10);
    }

    @Test
    public void spinButtonTest() throws InterruptedException {
        driver.get(url);
        WebElement spinButton = driver.findElement(By.id("spinButton"));
        wait.until(ExpectedConditions.elementToBeClickable(spinButton));
        spinButton.click();

        Assert.assertEquals(spinButton.getAttribute("class"), "disabled");

        wait.until(ExpectedConditions.attributeToBe(spinButton, "class", ""));
        Assert.assertEquals(spinButton.getAttribute("class"), "");
    }

    @Test
    public void upBetTest() throws InterruptedException {
        driver.get(url);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        int startTotal = Integer.valueOf(driver.findElement(By.id("credits")).getText());

        for (int i = 0; i < 2; i ++) {
            driver.findElement(By.id("betSpinUp")).click();
        }
        int bet = Integer.valueOf(driver.findElement(By.id("bet")).getText());

        WebElement spinButton = driver.findElement(By.id("spinButton"));
        spinButton.click();
        wait.until(ExpectedConditions.attributeToBe(spinButton, "class", ""));
        int total = Integer.valueOf(driver.findElement(By.id("credits")).getText());

        if (driver.findElement(By.id("SlotsOuterContainer")).getAttribute("class").equals("won")) {
            waitForWonCounted(10000);
            Assert.assertEquals(total, startTotal - bet + Integer.valueOf(driver.findElement(By.id("lastWin")).getText()));
        } else {
            Assert.assertEquals(total, startTotal - bet);
        }
    }

    @Test
    public void wonCreditsTest() throws InterruptedException {
        driver.get(url);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);

        int total = Integer.valueOf(driver.findElement(By.id("credits")).getText());
        int bet = Integer.valueOf(driver.findElement(By.id("bet")).getText());
        WebElement spinButton = driver.findElement(By.id("spinButton"));

        while (!driver.findElement(By.id("SlotsOuterContainer")).getAttribute("class").equals("won")) {
            total -= bet;
            spinButton.click();
            wait.until(ExpectedConditions.attributeToBe(spinButton, "class", ""));
        }

        waitForWonCounted(10000);
        total += Integer.valueOf(driver.findElement(By.id("lastWin")).getText());

        Assert.assertEquals(total, (int) Integer.valueOf(driver.findElement(By.id("credits")).getText()));
    }

    @AfterClass
    public void afterClass() {
        driver.close();
    }

    private void waitForWonCounted(int timeout) {
        WebElement lastWin = driver.findElement(By.id("lastWin"));
        int time = 0;
        while (lastWin.getText().equals("") && time < timeout) {
            try {
                Thread.sleep(500);
                time += 500;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}