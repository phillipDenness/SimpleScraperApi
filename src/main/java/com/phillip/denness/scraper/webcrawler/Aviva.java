package com.phillip.denness.scraper.webcrawler;

import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Aviva {
    private final Logger LOGGER = LoggerFactory.getLogger(Aviva.class);
    private final static String LOGIN_URL = "https://member.avivaservices.co.uk/CFEWeb/secure/doLogin";

    private WebDriver driver;
    private List<Action> actions;
    private String username;
    private String password;
    private String fund;
    private String email;

    public Aviva(String username, String password, String fund, String email) {
        this.username = username;
        this.password = password;
        this.fund = fund;
        this.email = email;
        this.actions = new ArrayList<>();
    }

    public List<Action> execute(WebDriver driver) {
        if (username == null || password == null || fund == null || email == null) {
            addAction("setup", "required parameter is null");
            return actions;
        }

        if (driver == null) {
            addAction("setup", "driver is null");
            return actions;
        }
        this.driver = driver;

        try {
            login();
            viewScheme();
            updateInvestmentsTab();
            updateInvestmentsBtn();
            updateInvestmentProgramme();

            try {
                // Switch fund holdings
                switchFundHoldings();
                addAction("switchFundHoldings");
                // Redirect Future contributions
                switchFundHoldings();
                addAction("redirectFuturecontributions");
            } catch (NotFoundException e) {
                try {
                    logout();
                } catch (NoSuchElementException ignored) {
                    return actions;
                }
                return actions;
            }

            // Confirm transfer
            confirmTransfer();

            logout();

        } catch (NoSuchElementException e) {
            try {
                logout();
            } catch (NoSuchElementException ignored) {
                return actions;
            }
            return actions;
        }
        return actions;
    }

    private void login() throws NotFoundException {
        this.driver.get(LOGIN_URL);

        WebElement userID = actionWrapper(By.cssSelector("#userID"),
                "login", "Unable to locate username input");

        WebElement passwd = actionWrapper(By.cssSelector("#password"),
                "login", "Unable to locate password input");

        WebElement loginButton = actionWrapper((By.cssSelector("#loginButton")),
                "login", "Unable to locate login Button");

        userID.sendKeys(username);
        passwd.sendKeys(password);
        loginButton.click();

        addAction("login");
    }

    private void viewScheme() throws NoSuchElementException {
        WebElement schemeType = actionWrapper(By.cssSelector("#memberDashboard_form_id\\3a policyListOfMember_dataTable_id\\3a 0\\3a policyListOfMember_schemeType_link_id"),
                "viewScheme", "Unable to view your schemes", 30);
        schemeType.click();
        addAction("viewScheme");
    }

    private void updateInvestmentsTab() throws NotFoundException {
        WebElement updateInvestmentsLink = actionWrapper(By.cssSelector("#memberDashboard_form_id\\3a memberSummary_updateInv_change_btn_id"),
                "updateInvestmentsTab", "Unable to locate update investment button");
        updateInvestmentsLink.click();
        addAction("updateInvestmentsTab");
    }

    private void updateInvestmentsBtn() throws NotFoundException {
        WebElement updateInvestmentProgramme = actionWrapper(By.cssSelector("#memberDashboard_form_id\\3a memberUpdInv_updateInvProgCheckBox_id"),
                "updateInvestmentsBtn", "Unable to locate update investment programme button");
        WebElement switchExistingHoldings = actionWrapper(By.cssSelector("#memberDashboard_form_id\\3a memberUpdInv_switchCheckBox_id"),
                "updateInvestmentsBtn", "Unable to locate switch existing holdings button");
        WebElement redirectFutureContributions = actionWrapper(By.cssSelector("#memberDashboard_form_id\\3a memberUpdInv_redirectCheckBox_id"),
                "updateInvestmentsBtn", "Unable to locate switch future contributions button");

        updateInvestmentProgramme.click();
        switchExistingHoldings.click();
        redirectFutureContributions.click();

        WebElement updateInvestmentDetailsBtn = actionWrapper(By.cssSelector("#memberDashboard_form_id\\3a btn_updateInvestmentDetails_updInvDet_id"),
                "updateInvestmentsBtn", "Unable to locate update investment submit button");
        updateInvestmentDetailsBtn.click();
        addAction("updateInvestmentsBtn");
    }

    private void updateInvestmentProgramme() throws NotFoundException {
        WebElement myFutureTargetCash = actionWrapper(By.cssSelector("#memberDashboard_form_id\\3a memberUpdInvProg_selectLSInvProgRadio_id\\3a 0"),
                "updateInvestmentProgramme", "Unable to radio button for my future target cash");
        WebElement nextBtn = actionWrapper(By.cssSelector("#memberDashboard_form_id\\3a btn_goTonextStep_next_id"),
                "updateInvestmentProgramme", "Unable to find go to next step button");

        myFutureTargetCash.click();
        nextBtn.click();
        addAction("UpdateInvestmentProgramme");
    }

    private void switchFundHoldings() throws NotFoundException {
        WebElement table = actionWrapper(By.cssSelector("[id*=section5allFundsTable]"),
                "switchFundHoldings", "Unable to find fund table");

        WebElement tbody = table.findElement(By.tagName("tbody"));
        List<WebElement> allRows = tbody.findElements(By.tagName("tr"));

        Map<String, WebElement> funds = new HashMap<>();
        for (WebElement row : allRows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            for (WebElement cell : cells) {
                try {
                    String fundName = cell.findElement(By.tagName("label")).getAttribute("for");
                    WebElement fundInput = cell.findElement(By.tagName("input"));
                    funds.put(fundName, fundInput);
                }catch (NoSuchElementException ignored) {
                }
            }
        }

        WebElement fundInput = funds.get(fund);
        if (fundInput == null) {
            // Bad request
            String foundFunds = funds.keySet().toString();
            addAction("Switch Existing holdings", fund + " not found in the list. Check your fund matches exactly to the aviva list " + foundFunds);
            throw new NotFoundException();
        }
        fundInput.sendKeys("100");

        WebElement nextBtn = actionWrapper(By.xpath("//*[@id=\"memberDashboard_form_id:btn_goTonextStep_next_id\"]"),
                "switchFundHoldings", "Unable to find go to next step button");
        nextBtn.click();
    }

    private void confirmTransfer() {
        WebElement disclaimerRadio = actionWrapper(By.cssSelector("#memberDashboard_form_id\\3a memberUpdInvConfirmChanges_disclaimerCheckBox_id"),
                "confirmTransfer", "Unable to locate disclaimer Radio button");
        WebElement emailTickbox = actionWrapper(By.cssSelector("#memberDashboard_form_id\\3a memberUpdInvConfirmChanges_section5Row3checkBox_id"),
                "confirmTransfer", "Unable to locate email Tickbox");
        WebElement emailInput = actionWrapper(By.cssSelector("#memberDashboard_form_id\\3a memberUpdInvConfirmChanges_section5Row3value_id"),
                "confirmTransfer", "Unable to locate email input");
        WebElement confirmBtn = actionWrapper(By.cssSelector("#memberDashboard_form_id\\3a btn_confirmUpdatedInvestmentDetails_confirm_id"),
                "confirmTransfer", "Unable to locate confirm button");

        disclaimerRadio.click();
        emailTickbox.click();
        emailInput.sendKeys(email);
//        confirmBtn.click();
    }

    private void logout() throws NoSuchElementException {
        WebElement logoutLink = actionWrapper(By.cssSelector("#fl-globalNav > ul > li:nth-child(2) > a"),
                "logout", "No logout button found");
        logoutLink.click();
        addAction("logout");
    }

    private WebElement actionWrapper(By selector, String step, String error, Integer seconds) {
        return SeleniumWebCrawler.safeGetWebElement(selector, driver, seconds)
                .orElseThrow(() -> {
                    addAction(step, error);
                    return new NoSuchElementException("");
                });
    }

    private WebElement actionWrapper(By selector, String step, String error) {
        return SeleniumWebCrawler.safeGetWebElement(selector, driver)
                .orElseThrow(() -> {
                    addAction(step, error);
                    return new NoSuchElementException("");
                });
    }

    private void addAction(String step) {
        addAction(step, null);
    }

    private void addAction(String step, String error) {
        actions.add(Action.builder()
                .id(actions.size() + 1)
                .step(step)
                .error(error)
                .build());
    }
}
