import static id.izzanfi.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AmazonUIAutomationTest {

    WebDriver webDriver;

    @BeforeAll
    static void setupTestSuite() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTestCase() {
        webDriver = new ChromeDriver();
        webDriver.manage().window().maximize();
    }

    @Test
    public void runTest() {
        webDriver.get(BASE_URL);

        searchItemOnSearchBox(SEARCH_KEYWORD);

        clickOnBrandFilterCheckbox(TARGET_BRAND);

        clickOnDesiredProduct(TARGET_PRODUCT_NAME);

        changeQty(2);

        addToCart();

        goToCart();

        emptyCart();
    }

    @AfterEach
    public void afterAction() {
        webDriver.quit();
    }

    private void searchItemOnSearchBox(String keyword) {
        WebElement searchTextBox = webDriver.findElement(By.xpath(SEARCH_TEXT_BOX_XPATH));
        assertThat(searchTextBox.isDisplayed()).isTrue();

        searchTextBox.sendKeys(keyword);
        searchTextBox.sendKeys(Keys.RETURN);

        webDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
    }

    private void clickOnBrandFilterCheckbox(String brandName) {
        WebElement brandsChoicesSection = webDriver.findElement(By.id(BRANDS_LIST_SECTION_ID));
        assertThat(brandsChoicesSection.isDisplayed()).isTrue();
        WebElement checkbox = webDriver.findElement(By.xpath(String.format("//*[@id=\"p_89/%s\"]/span/a/div", brandName)));
        assertThat(checkbox.isDisplayed()).isTrue();

        if (!checkbox.isSelected()) {
            checkbox.click();
        }
    }

    private void clickOnDesiredProduct(String productName) {
        List<WebElement> productNames = webDriver.findElements(By.xpath("//*[@class=\"a-size-medium a-color-base a-text-normal\"]"));
        boolean success = false;
        for (WebElement product : productNames) {
            try {
                scrollToElement(product);
            } catch (InterruptedException e) {
                fail("There are exceptions happened in test case!");
            }

            String productNameText = product.getText();
            if (productNameText.contains(productName)) {
                success = true;
                product.click();
                break;
            }
        }

        if (!success) {
            fail("Desired object not found!");
        }
    }

    private void changeQty(int qty) {
        Select qtyDropdown = new Select(webDriver.findElement(By.id("quantity")));
        qtyDropdown.selectByVisibleText(Integer.toString(qty));
        List<WebElement> qtyDropdownOptions = qtyDropdown.getOptions();
        qtyDropdownOptions.get(qty-1).click();

        webDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
    }

    private void addToCart() {
        WebElement atcButton = webDriver.findElement(By.xpath("//*[@id=\"atc-declarative\"]"));
        atcButton.click();

        webDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
    }

    private void goToCart() {
        WebElement goToCartBtn = webDriver.findElement(By.xpath("//*[@id=\"nav-cart\"]"));
        goToCartBtn.click();

        webDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
    }

    private void emptyCart() {
        WebElement itemsListSection = webDriver.findElement(By.xpath("//*[@class=\"sc-list-item-content\"]"));
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofMillis(10000));
        if (itemsListSection.isDisplayed()) {
            List<WebElement> deleteButtons = webDriver.findElements(By.xpath("//span[@data-action=\"delete\" and @data-feature-id=\"delete\"]"));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value=\"Delete\"]"))).click();
            for (WebElement deleteBtn : deleteButtons) {
                deleteBtn.click();
                webDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(15000));
            }
        } else {
            System.out.println("Cart page are already empty!");
        }
    }

    private void scrollToElement(WebElement element) throws InterruptedException {
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(500);
    }
}
