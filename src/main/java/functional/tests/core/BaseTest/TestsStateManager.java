package functional.tests.core.BaseTest;

import functional.tests.core.Appium.Client;
import functional.tests.core.Find.FindHelper;
import functional.tests.core.Log.Log;
import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestsStateManager {
    private ITestResult status;
    private int navigationLevel;
    private String mainPage;
    private String currentPage;
    private Map<String, Integer> pages;
    private ArrayList<String> usedPages;
    private Client client;

    public TestsStateManager(Client client) {
        this.navigationLevel = 0;
        this.pages = new HashMap<String, Integer>();
        this.usedPages = new ArrayList<String>();
        this.client = client;
    }

    public int getLevel() {
        return this.navigationLevel;
    }

    public void setMainPage(String mainPage) {
        if (mainPage.contains("/") || mainPage.contains(("."))) {
            String splitSeparator = mainPage.contains("/") ? "/" : ".";
            this.mainPage = mainPage.split(splitSeparator)[0];
        } else {
            this.mainPage = mainPage;
        }
    }

    public String getMainPage() {
        return this.mainPage;
    }

    public void setCurrentPage(String page) {
        this.currentPage = page;
        this.setNewPage();
    }

    public String getCurrentPage() {
        return this.currentPage;
    }

    public int getPageIndex(String page) {
        try {
            return this.pages.get(page);
        } catch (Exception e) {
            return 0;
        }
    }

    public void resetNavigationToLastOpenedPage() {
        this.navigationLevel = 0;
        if (this.usedPages.size() > 0) {
            String pagesAsString = "";
            String pagesToNavigateToAsString = "";

            for (int i = 0; i < this.usedPages.size(); i++) {
                pagesAsString += this.usedPages.get(i) + "/";
            }
            this.mainPage = this.usedPages.get(0);

            this.usedPages = new ArrayList<String>();
            if (pagesAsString.endsWith("/")) {
                pagesToNavigateToAsString = pagesAsString.substring(0, pagesAsString.length() - 1);
            } else {
                pagesToNavigateToAsString = pagesAsString;
            }

            ActionHelper.navigateTo(pagesToNavigateToAsString, this, this.client);
        } else {
            if (this.mainPage != null && this.mainPage != "") {
                ActionHelper.navigateTo(this.mainPage, this, this.client);
            }
        }
    }

    public void resetNavigationMainPage() {
        this.navigationLevel = 0;
        ActionHelper.navigateTo(this.mainPage, this, this.client);
    }

    public void removeCurrentPage() {
        this.pages.remove(this.currentPage);
        if (this.navigationLevel < 0) {
            Log.info("See the navigation");
        }

        if (this.usedPages.size() > 0) {
            this.usedPages.remove(this.currentPage);
        }

        if (this.usedPages.size() > 0) {
            this.currentPage = this.usedPages.get(this.usedPages.size() - 1);
        }
    }

    public void navigateBack(Client client) {
        this.removeCurrentPage();
        this.decreaseNavigationLevel();
        ActionHelper.navigateBack(client);
    }

    public void navigateForward(Client client) {
        this.increaseNavigationLevel();
        ActionHelper.navigateForward(client);
    }

    private void setNewPage() {
        this.increaseNavigationLevel();

        this.pages.put(this.currentPage, this.navigationLevel);
        this.usedPages.add(this.currentPage);
    }

    private int increaseNavigationLevel() {
        this.navigationLevel++;
        return this.navigationLevel;
    }

    private int decreaseNavigationLevel() {
        this.navigationLevel--;

        return this.navigationLevel;
    }
}
