package sausedemotests;

import core.BaseTest;
import org.example.BrowserTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LoginTests extends BaseTest {

    @BeforeEach
    public void beforeEachTest() {
        driver = startBrowser(BrowserTypes.CHROME);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.get("https://www.saucedemo.com/");
    }

    protected void authenticateWithUser(String username, String password) {
        WebElement usernameField = driver.findElement(By.id("user-name"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();
    }

    private void addProductsToCart() {
        WebElement backpack = driver.findElement(By.id("add-to-cart-sauce-labs-backpack"));
        WebElement tshirt = driver.findElement(By.id("add-to-cart-sauce-labs-bolt-t-shirt"));
        backpack.click();
        tshirt.click();
    }

    private void goToShoppingCart() {
        WebElement shoppingCart = driver.findElement(By.className("shopping_cart_link"));
        shoppingCart.click();
    }

    private void proceedToCheckout(String firstName, String lastName, String zipCode) {
        WebElement checkoutButton = driver.findElement(By.id("checkout"));
        checkoutButton.click();

        WebElement firstNameField = driver.findElement(By.id("first-name"));
        WebElement lastNameField = driver.findElement(By.id("last-name"));
        WebElement zipCodeField = driver.findElement(By.id("postal-code"));
        firstNameField.sendKeys(firstName);
        lastNameField.sendKeys(lastName);
        zipCodeField.sendKeys(zipCode);

        WebElement continueButton = driver.findElement(By.id("continue"));
        continueButton.click();
    }

    @Test
    public void userAuthenticated_when_validCredentialsProvided() {
        authenticateWithUser("standard_user", "secret_sauce");
        WebElement inventoryPage = driver.findElement(By.id("inventory_container"));
        Assertions.assertTrue(inventoryPage.isDisplayed(), "Login failed. Inventory page is not displayed.");
    }

    @Test
    public void productAddedToShoppingCar_when_addToCart() {
        authenticateWithUser("standard_user", "secret_sauce");
        addProductsToCart();
        goToShoppingCart();
        List<WebElement> cartItems = driver.findElements(By.className("cart_item"));
        Assertions.assertEquals(2, cartItems.size(), "Incorrect number of products in the shopping cart.");

        String firstProductName = driver.findElement(By.xpath("//div[text()='Sauce Labs Backpack']")).getText();
        String secondProductName = driver.findElement(By.xpath("//div[text()='Sauce Labs Bolt T-Shirt']")).getText();

        Assertions.assertEquals("Sauce Labs Backpack", firstProductName, "Backpack not added to the cart.");
        Assertions.assertEquals("Sauce Labs Bolt T-Shirt", secondProductName, "T-shirt not added to the cart.");
    }

    @Test
    public void userDetailsAdded_when_checkoutWithValidInformation() {
        authenticateWithUser("standard_user", "secret_sauce");
        addProductsToCart();
        goToShoppingCart();
        proceedToCheckout("Zhaneta", "Krasteva", "12345");

        WebElement summaryHeader = driver.findElement(By.className("title"));
        Assertions.assertEquals("Checkout: Overview", summaryHeader.getText(), "Not on the checkout summary page.");
    }


    @Test
    public void orderCompleted_when_addProduct_and_checkout_withConfirm() {
        authenticateWithUser("standard_user", "secret_sauce");
        addProductsToCart();
        goToShoppingCart();
        proceedToCheckout("Zhaneta", "Krasteva", "12345");

        WebElement finishButton = driver.findElement(By.id("finish"));
        finishButton.click();

        WebElement orderConfirmation = driver.findElement(By.className("complete-header"));
        Assertions.assertEquals("Thank you for your order!", orderConfirmation.getText(), "Order was not completed.");

        boolean cartBadgeAbsent = driver.findElements(By.className("shopping_cart_badge")).isEmpty();
        Assertions.assertTrue(cartBadgeAbsent, "Shopping cart is not empty after completing the order.");
    }

}
