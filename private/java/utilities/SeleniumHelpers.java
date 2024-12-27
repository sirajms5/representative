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
        WebElement webElement = webDriver.findElement(By.id(id));
        Helpers.sleep(1);
        return webElement;
    }

    public void sendKeysToWebElement(WebElement webElement, String keys) {
        webElement.sendKeys(keys);
        Helpers.sleep(1);
    }

    public void clickElement(WebElement webElement) {
        webElement.click();
        Helpers.sleep(1);
    }

    public WebElement getWebElementByXPath(WebDriver webDriver, String xPath) {
        WebElement webElement = webDriver.findElement(By.xpath(xPath));
        Helpers.sleep(1);
        return webElement;
    }

    public String getElementAttributeValue(WebElement webElement, String attribute) {
        String attributeValue = webElement.getDomAttribute(attribute);
        Helpers.sleep(1);
        return attributeValue;
    }

    public List<WebElement> getWebElementsByXPath(WebDriver webDriver, String xPath) {
        List<WebElement> webElements = webDriver.findElements(By.xpath(xPath));
        Helpers.sleep(1);
        return webElements;
    }

    public List<WebElement> getWebElementsFromWebElementByTagName(WebElement webElement, String tagName) {
        List<WebElement> webElements = webElement.findElements(By.tagName(tagName));
        Helpers.sleep(1);
        return webElements;
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
        WebElement newWebElement = webElement.findElement(By.xpath(xPath));
        Helpers.sleep(1);
        return newWebElement;
    }

    public String getStringFromWebElement(WebElement webElement) {
        String value = webElement.getText();
        Helpers.sleep(1);
        return value;
    }

    public void navigateTo(WebDriver webDriver, String url) {
        webDriver.navigate().to(url);
        Helpers.sleep(3);
    }

    public void makeScreenFullSize(WebDriver webDriver) {
        webDriver.manage().window().maximize();
        Helpers.sleep(1);        
    }
}
