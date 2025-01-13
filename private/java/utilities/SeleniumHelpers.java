package utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SeleniumHelpers {

    public void startBrowser(WebDriver webDriver, String url) {
        webDriver.get(url);
        Helpers.sleep(3);
    }

    public WebElement getWebElementById(WebDriver webDriver, String id) {
        Helpers.sleep(1);
        return webDriver.findElement(By.id(id));
    }

    public void sendKeysToWebElement(WebElement webElement, String keys) {
        // Helpers.sleep(1);
        webElement.sendKeys(keys);
        // Helpers.sleep(10);
    }

    public void clickElement(WebElement webElement) {
        // Helpers.sleep(1);
        webElement.click();
        // Helpers.sleep(10);
    }

    public WebElement getWebElementByXPath(WebDriver webDriver, String xPath) {
        Helpers.sleep(1);
        return webDriver.findElement(By.xpath(xPath));
    }

    public String getElementAttributeValue(WebElement webElement, String attribute) {
        Helpers.sleep(1);
        return webElement.getDomAttribute(attribute);
    }

    public List<WebElement> getWebElementsByXPath(WebDriver webDriver, String xPath) {
        Helpers.sleep(1);
        return webDriver.findElements(By.xpath(xPath));
    }

    public List<WebElement> getWebElementsFromWebElementByTagName(WebElement webElement, String tagName) {
        Helpers.sleep(1);
        return webElement.findElements(By.tagName(tagName));
    }

    public Map<String, String> mergeListsOfElementToMap(List<WebElement> labels, List<WebElement> values) {
        Map<String, String> elementsMap = new HashMap<String, String>();
        for (int index = 0; index < labels.size(); index++) {
            String label = labels.get(index).getText().replace(" / ", "-").replace(" ", "-").replace(":", "");
            String value = values.get(index).getText();
            elementsMap.put(label, value);
        }

        return elementsMap;
    }

    public WebElement getWebElementFromWebElementByXPath(WebElement webElement, String xPath) {
        Helpers.sleep(1);
        return webElement.findElement(By.xpath(xPath));
    }

    public String getStringFromWebElement(WebElement webElement) {
        Helpers.sleep(1);
        return webElement.getText();
    }

    public void navigateTo(WebDriver webDriver, String url) {
        Helpers.sleep(1);
        webDriver.navigate().to(url);
        Helpers.sleep(3);
    }

    public void makeScreenFullSize(WebDriver webDriver) {
        Helpers.sleep(1);
        webDriver.manage().window().maximize();
        Helpers.sleep(1);        
    }

    public String getCurrentPageUrl(WebDriver webDriver) {
        Helpers.sleep(1);
        return webDriver.getCurrentUrl();
    }
}
