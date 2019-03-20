package com.phillip.denness.scraper.webcrawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Aviva {

    private final Logger LOGGER = LoggerFactory.getLogger(Aviva.class);
    private final static String LOGIN_URL = "https://member.avivaservices.co.uk/CFEWeb/secure/doLogin";
    private final static String ERROR_BOX_SELECTOR = "#memberDashboard_form_id\\3a memberTab_updateInv_error_composite_id\\3a fieldValidation_showOnTop_error_table_id\\3a 0\\3a fieldValidation_showOnTop_error_table_errorMsg_id";

    private WebDriver driver;
    private List<Action> actions;
    private String username;
    private String password;
    private String fund;

    public Aviva(String username, String password, String fund) {
        this.username = username;
        this.password = password;
        this.fund = fund;
        this.actions = new ArrayList<>();
    }

    public List<Action> execute(WebDriver driver) {
        if (username == null || password == null || fund == null) {
            addAction("setup", "required parameter is null");
            return actions;
        }

        if (driver == null) {
            addAction("setup", "driver is null");
            return actions;
        }
        this.driver = driver;

        login();
        viewScheme();
        updateInvestmentsTab();
        updateInvestmentsBtn();

        WebElement message = SeleniumWebCrawler
                .safeGetWebElement(By.cssSelector(ERROR_BOX_SELECTOR), driver);

        if (message.getText().contains("There exists an unprocessed investment instruction on the policy")) {
            logout();
            addAction("check for unprocessed investment", message.getText());
            return actions;
        }

        logout();
        return actions;
    }

    private void login() {
        this.driver.get(LOGIN_URL);

        WebElement userID = SeleniumWebCrawler
                .safeGetWebElement(By.cssSelector("#userID"), driver);
        WebElement passwd = SeleniumWebCrawler
                .safeGetWebElement(By.cssSelector("#password"), driver);
        WebElement loginButton = SeleniumWebCrawler
                .safeGetWebElement(By.cssSelector("#loginButton"), driver);

        userID.sendKeys(username);
        passwd.sendKeys(password);
        loginButton.click();

        addAction("login");
    }

    private void viewScheme() {
        WebElement schemeType = SeleniumWebCrawler
                .safeGetWebElement(By.cssSelector("#memberDashboard_form_id\\3a policyListOfMember_dataTable_id\\3a 0\\3a policyListOfMember_schemeType_link_id"), driver, 30);
        schemeType.click();
        addAction("viewScheme");
    }

    private void updateInvestmentsTab() {
        WebElement updateInvestmentsLink = SeleniumWebCrawler
                .safeGetWebElement(By.cssSelector("#memberDashboard_form_id\\3a memberSummary_updateInv_change_btn_id"), driver);
        updateInvestmentsLink.click();
        addAction("updateInvestmentsTab");
    }

    private void updateInvestmentsBtn() {
        WebElement updateInvestmentProgramme = SeleniumWebCrawler
                .safeGetWebElement(By.cssSelector("#memberDashboard_form_id\\3a memberUpdInv_updateInvProgCheckBox_id"), driver);
        WebElement switchExistingHoldings = SeleniumWebCrawler
                .safeGetWebElement(By.cssSelector("#memberDashboard_form_id\\3a memberUpdInv_switchCheckBox_id"), driver);
        WebElement redirectFutureContributions = SeleniumWebCrawler
                .safeGetWebElement(By.cssSelector("#memberDashboard_form_id\\3a memberUpdInv_redirectCheckBox_id"), driver);

        updateInvestmentProgramme.click();
        switchExistingHoldings.click();
        redirectFutureContributions.click();

        WebElement updateInvestmentDetailsBtn = SeleniumWebCrawler
                .safeGetWebElement(By.cssSelector("#memberDashboard_form_id\\3a btn_updateInvestmentDetails_updInvDet_id"), driver);
        updateInvestmentDetailsBtn.click();
        addAction("updateInvestmentsBtn");
    }

    private void logout() {
        WebElement logoutLink = SeleniumWebCrawler
                .safeGetWebElement(By.cssSelector("#fl-globalNav > ul > li:nth-child(2) > a"), driver);
        logoutLink.click();
        addAction("logout");
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
