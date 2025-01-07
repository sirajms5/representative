package selenium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import classes.Representative;
import utilities.Constants;
import utilities.LogKeeper;
import utilities.RepresentativeLevelEnum;
import utilities.RepresentativePositionEnum;
import utilities.SeleniumHelpers;

public class MppOntarioSelenium {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public Map<String, List<Representative>> validateMembers(List<Representative> fetchedMpps) {
        logKeeper.appendLog(
                "======================================== Executing Ontario MPP Selenium ========================================");
        Map<String, List<Representative>> representativesMap = new HashMap<>();
        List<Representative> updatedOntarioMpps = new ArrayList<>();
        List<Representative> unavilableOntarioMpps = new ArrayList<>();
        WebDriver webDriver = null;
        try {
            webDriver = new ChromeDriver();
            String ontarioMppUrl = Constants.ONTARIO_MEMBERS_URL;
            SeleniumHelpers seleniumHelpers = new SeleniumHelpers();
            seleniumHelpers.startBrowser(webDriver, ontarioMppUrl);
            seleniumHelpers.makeScreenFullSize(webDriver);
            WebElement listButton = seleniumHelpers.getWebElementById(webDriver, "listBtn");
            seleniumHelpers.clickElement(listButton);
            List<WebElement> ontarioMppNamesWebElements = seleniumHelpers.getWebElementsByXPath(webDriver,
                    "//*[contains(@class, 'current-members-member-name-list')]/h3");
            List<String> ontarioMppNames = new ArrayList<>();
            int mppCounter = 0;
            for (WebElement element : ontarioMppNamesWebElements) {
                String representativeName = seleniumHelpers.getStringFromWebElement(element);
                mppCounter = mppCounter + 1;
                logKeeper.appendLog("Extracting MPP number " + mppCounter + " name: " + representativeName);
                ontarioMppNames.add(representativeName);
            }

            Set<String> matchedMpps = new HashSet<>();
            Set<String> fetchedNames = new HashSet<>();
            for(Representative fetchedMpp : fetchedMpps) {
                fetchedNames.add(fetchedMpp.getFullName());
            }

            Set<String> unavilableNames = new HashSet<>();
            for (String mppScrappedName : ontarioMppNames) {
                boolean isMatched = false;
                for (Representative fetchedMpp : fetchedMpps) {
                    String mppFullName = fetchedMpp.getFullName();
                    if (mppScrappedName.toLowerCase().contains(mppFullName.toLowerCase())) {
                        // Check if MPP is Honourable
                        if (mppScrappedName.toLowerCase().contains("hon.")) {
                            fetchedMpp.setHonourable(true);
                        }

                        updatedOntarioMpps.add(fetchedMpp);
                        matchedMpps.add(mppFullName);
                        isMatched = true;
                        break;
                    }

                    String baseName = mppScrappedName;
                    if (mppScrappedName.toLowerCase().startsWith("hon.")) {
                        baseName = mppScrappedName.substring(4).trim();
                    }

                    if (!isMatched && !fetchedNames.contains(baseName)) {
                        Representative representative = new Representative(baseName,
                                RepresentativePositionEnum.MPP.getValue(),
                                RepresentativeLevelEnum.getProvincialOrTerritorial(Constants.ONTARIO));
                        unavilableOntarioMpps.add(representative);
                        unavilableNames.add(baseName);
                    }
                }
            }

            for (Representative fetchedMpp : fetchedMpps) {
                String mppFullName = fetchedMpp.getFullName();
                if (!matchedMpps.contains(mppFullName)) {
                    unavilableOntarioMpps.add(fetchedMpp);
                }
            }

            representativesMap.put(Constants.AVAILABLE, updatedOntarioMpps);
            representativesMap.put(Constants.UNAVILABLE, unavilableOntarioMpps);
        } catch (Exception e) {
            logKeeper.appendLog(e.getMessage());
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }

        logKeeper.appendLog(
                "======================================== Finished Ontario MPP Selenium ========================================");

        return representativesMap;
    }
}
