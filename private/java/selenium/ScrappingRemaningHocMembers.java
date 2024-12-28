package selenium;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import api.BoundariesApiFetch;
import classes.HOCMember;
import classes.Office;
import utilities.LogKeeper;
import utilities.SeleniumHelpers;

public class ScrappingRemaningHocMembers {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public List<HOCMember> scrapHocMembers(List<HOCMember> hocMembers) {
        List<HOCMember> hocMembersUpdated = new ArrayList<>();
        WebDriver webDriver = null;        
        try {    
            webDriver = new ChromeDriver();
            String hocUrl = "https://www.ourcommons.ca/Members/en";
            SeleniumHelpers seleniumHelpers = new SeleniumHelpers();
            seleniumHelpers.startBrowser(webDriver, hocUrl);
            seleniumHelpers.makeScreenFullSize(webDriver);        
            for(HOCMember hocMember : hocMembers) {
                logKeeper.appendLog("Web scrapping for: " + hocMember.getFirstName() + " " + hocMember.getLastName());
                WebElement searchField = seleniumHelpers.getWebElementByXPath(webDriver, "(//div/input)[2]");
                String hocMemberName = hocMember.getFirstName() + " " + hocMember.getLastName();
                seleniumHelpers.sendKeysToWebElement(searchField, hocMemberName);
                WebElement searchSuggestion = seleniumHelpers.getWebElementById(webDriver, "downshift-0-item-0");
                seleniumHelpers.clickElement(searchSuggestion);
                WebElement contactTab = seleniumHelpers.getWebElementById(webDriver, "contact-tab");
                seleniumHelpers.clickElement(contactTab);
                WebElement hocImgElement = seleniumHelpers.getWebElementByXPath(webDriver, "(//img)[2]");
                String hocImgUrl = seleniumHelpers.getElementAttributeValue(hocImgElement, "src"); // HOC member photo url
                WebElement hocDetailsOverview = seleniumHelpers.getWebElementByXPath(webDriver, "//dl");
                List<WebElement> detailsLabels = seleniumHelpers.getWebElementsFromWebElementByTagName(hocDetailsOverview, "dt");
                List<WebElement> detailsValues = seleniumHelpers.getWebElementsFromWebElementByTagName(hocDetailsOverview, "dd");
                Map<String, String> hocOverview = seleniumHelpers.mergeListsOfElementToMap(detailsLabels, detailsValues);
                String politicalAffiliation = hocOverview.get("Political-Affiliation"); // HOC political affiliation
                String constituency = hocOverview.get("Constituency"); // HOC constituency
                String provinceOrTerritory = hocOverview.get("Province-Territory"); // HOC provinceOrTerritory
                String languages = hocOverview.get("Preferred Language"); // HOC languages
                WebElement emailWebElement = seleniumHelpers.getWebElementByXPath(webDriver, "(//*[@id = 'contact']//div//p[1])[1]");
                String email = seleniumHelpers.getStringFromWebElement(emailWebElement); // HOC email
                WebElement legislativeOffice = seleniumHelpers.getWebElementByXPath(webDriver, "(//*[@id = 'contact']//div//p[1])[2]");
                String legislativeAddress = seleniumHelpers.getStringFromWebElement(legislativeOffice); // legislative office postal code
                WebElement legislativeTelAndFaxElement = seleniumHelpers.getWebElementByXPath(webDriver, "//*[@id = 'contact']/div/div/div[1]/p[2]");
                String legislativeTelAndFax = seleniumHelpers.getStringFromWebElement(legislativeTelAndFaxElement);
                Map<String, String> legislativeTelAndFaxMap = ExtractTelAndFax(legislativeTelAndFax);
                String legislativeTel = legislativeTelAndFaxMap.get("telephone"); // legislative tel
                String legislativeFax = legislativeTelAndFaxMap.get("fax"); // legislative fax
                Office legislativeOfficeObj = new Office(legislativeFax, legislativeTel, "legislature", legislativeAddress);
                WebElement constituencyOffice = seleniumHelpers.getWebElementByXPath(webDriver, "(//*[@id = 'contact']//div//p[1])[3]");
                String constituencyAddress = seleniumHelpers.getStringFromWebElement(constituencyOffice); // constituency office postal code
                WebElement constituencyTelAndFaxElement = seleniumHelpers.getWebElementByXPath(webDriver, "//*[@id = 'contact']/div/div/div[2]/div/div/p[2]");
                String constituencyTelAndFax = seleniumHelpers.getStringFromWebElement(constituencyTelAndFaxElement);
                Map<String, String> constituencyTelAndFaxMap = ExtractTelAndFax(constituencyTelAndFax);
                String constituencyTel = constituencyTelAndFaxMap.get("telephone"); // constituency tel
                String constituencyFax = constituencyTelAndFaxMap.get("fax"); // constituency fax
                Office constituencyOfficeObj = new Office(constituencyFax, constituencyTel, "constituency", constituencyAddress);
                hocMember.setPhotoUrl(hocImgUrl);
                hocMember.setPoliticalAffiliation(politicalAffiliation);
                hocMember.setConstituency(constituency);
                hocMember.setProvinceOrTerritory(provinceOrTerritory);
                hocMember.setLanguages(languages);
                hocMember.setEmail(email);
                List<Office> hocOffices = Arrays.asList(legislativeOfficeObj, constituencyOfficeObj);
                hocMember.setOffices(hocOffices);
                BoundariesApiFetch boundariesApiFetch = new BoundariesApiFetch();
                String boundaryExternalId = boundariesApiFetch.fetchBoundaryExternalIdByConstituency(constituency);
                hocMember.setBoundaryExternalId(boundaryExternalId);   
                seleniumHelpers.navigateTo(webDriver, hocUrl);
                hocMembersUpdated.add(hocMember);
            }           
        } catch (Exception e) {
            logKeeper.appendLog(e.getMessage());
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
        return hocMembersUpdated;
    }

    private Map<String, String> ExtractTelAndFax(String telAndFax) {
        Map<String, String> telAndFaxMap = new HashMap<String, String>();
        // Regular expressions to capture telephone and fax
        Pattern pattern = Pattern.compile("Telephone:\\s*(\\d{3}-\\d{3}-\\d{4}).*?Fax:\\s*(\\d{3}-\\d{3}-\\d{4})", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(telAndFax);
        if (matcher.find()) {
            String telephone = matcher.group(1); 
            telAndFaxMap.put("telephone", telephone);
            String fax = matcher.group(2);  
            telAndFaxMap.put("fax", fax);
        } else {
            logKeeper.appendLog("No matches found for telephone and fax!");
        }

        return telAndFaxMap;
    }
}
