package tests;

import android.content.pm.ActivityInfo;

import androidx.test.espresso.Espresso;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.google.common.collect.Comparators;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import app.com.mobileassignment.views.MainActivity;
import pages.MainPage;
import pages.MapPage;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Every.everyItem;
import static org.hamcrest.core.StringStartsWith.startsWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class AppTests {

    MainPage mainPage = new MainPage();
    MapPage mapPage = new MapPage();

    private static final String[] CITIES_FOR_SEARCH = {
            "porto", "A Estrada, ES", "Porto Moniz, PT", "Aalburg, NL"
    };

    @Rule
    public ActivityTestRule<MainActivity> mainRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void waitForMainPage() {
        mainPage.waitMainPage(mainRule);
    }

    @Test
    public void appCanBeOpened() {
        mainPage.isMainShown();
    }

    @Test
    public void checkCitiesListIsSortedAlphabetically() {
        assertTrue("Default cities list are not ordered alphabetically",
                Comparators.isInOrder(mainPage.convertCitiesToStrings(mainPage.getListOfCities(mainRule)),
                        Comparator.naturalOrder()));
    }

    @Test
    public void checkSearchResultIsSortedAlphabetically() {
        mainPage.enterInSearchField(CITIES_FOR_SEARCH[0]).waitForSearchResults(mainRule);
        assertTrue("Search results are not ordered alphabetically",
                Comparators.isInOrder(mainPage.convertCitiesToStrings(mainPage.getListOfCities(mainRule)),
                        Comparator.naturalOrder()));
    }

    @Test
    public void checkCitiesListAfterCleaningSearchIsSortedAlphabetically() {
        mainPage.enterInSearchField(CITIES_FOR_SEARCH[0]).closeKeyboardAfterSearch().cleanSearchField().waitForSearchResults(mainRule);
        assertTrue("Cities list after clearing search are not ordered alphabetically",
                Comparators.isInOrder(mainPage.convertCitiesToStrings(mainPage.getListOfCities(mainRule)),
                        Comparator.naturalOrder()));
    }

    @Test
    public void checkCitiesListAfterClosingMapIsSortedAlphabetically() {
        mainPage.openCityOnMap(CITIES_FOR_SEARCH[1]);
        Espresso.pressBack();
        mainPage.isMainShown();
        assertTrue("Cities list after closing map are not ordered alphabetically",
                Comparators.isInOrder(mainPage.convertCitiesToStrings(mainPage.getListOfCities(mainRule)),
                        Comparator.naturalOrder()));
    }

    @Test
    public void checkCitiesListIsScrollable() {
        String forSearch = CITIES_FOR_SEARCH[3];

        assertFalse(String.format("City %s shouldn't be displayed", forSearch),
                mainPage.isCityDisplayedInList(forSearch)
        );
        mainPage.scrollDown();
        assertTrue(String.format("City %s should be displayed", forSearch),
                mainPage.isCityDisplayedInList(forSearch)
        );
    }

    @Test
    public void checkSearchResultIsScrollable() {
        String forFiltering = CITIES_FOR_SEARCH[0];
        String forSearch = CITIES_FOR_SEARCH[2];

        mainPage.enterInSearchField(forFiltering).closeKeyboardAfterSearch().waitForSearchResults(mainRule);
        assertFalse(String.format("City %s shouldn't be displayed", forSearch),
                mainPage.isCityDisplayedInList(forSearch)
        );
        mainPage.scrollDown();
        assertTrue(String.format("City %s should be displayed", forSearch),
                mainPage.isCityDisplayedInList(forSearch)
        );
    }

    @Test
    public void checkSearchByName() {
        String forFiltering = CITIES_FOR_SEARCH[3].substring(0, 7);

        mainPage.enterInSearchField(forFiltering).closeKeyboardAfterSearch().waitForSearchResults(mainRule);
        assertTrue("No cities found", mainPage.getListOfCities(mainRule).size() > 0);
        assertThat(String.format("Not all found cities start with %s", forFiltering),
                mainPage.convertCitiesToStrings(mainPage.getListOfCities(mainRule)),
                everyItem(startsWith(forFiltering)));
    }

    @Test
    public void checkSearchByNameAndCountry() {
        String forFiltering = CITIES_FOR_SEARCH[3];

        mainPage.enterInSearchField(forFiltering).closeKeyboardAfterSearch().waitForSearchResults(mainRule);
        assertTrue("No cities found", mainPage.getListOfCities(mainRule).size() > 0);
        assertThat(String.format("Not all found cities start with %s", forFiltering),
                mainPage.convertCitiesToStrings(mainPage.getListOfCities(mainRule)),
                everyItem(startsWith(forFiltering)));
    }

    @Test
    public void checkForDuplicateCities() {
        String forFiltering = CITIES_FOR_SEARCH[0];

        mainPage.enterInSearchField(forFiltering).closeKeyboardAfterSearch().waitForSearchResults(mainRule);
        List<String> allCities = mainPage.convertCitiesToStrings(mainPage.getListOfCities(mainRule));
        Set<String> uniqueCities = new HashSet<>(allCities);
        assertEquals("Cities list contains duplicate cities", uniqueCities.size(), allCities.size());
    }

    @Test
    public void checkSearchForUnrealCity() {
        String forFiltering = "unreal city";

        mainPage.enterInSearchField(forFiltering).closeKeyboardAfterSearch().waitForSearchResults(mainRule);
        assertTrue(String.format("Search result is not empty for %s", forFiltering),
                mainPage.getListOfCities(mainRule).isEmpty());
    }

    @Test
    public void checkCapitalisationDoesNotAffect() {
        String forFiltering = CITIES_FOR_SEARCH[0].substring(0, 3);

        mainPage.enterInSearchField(forFiltering).waitForSearchResults(mainRule).closeKeyboardAfterSearch();
        List<String> searchWithLowerCase = mainPage.convertCitiesToStrings(mainPage.getListOfCities(mainRule));

        mainPage.cleanSearchField().enterInSearchField(forFiltering.toUpperCase()).waitForSearchResults(mainRule).closeKeyboardAfterSearch();
        List<String> searchWithUpperCase = mainPage.convertCitiesToStrings(mainPage.getListOfCities(mainRule));

        assertEquals("Capitalisation affects search result", searchWithLowerCase, searchWithUpperCase);
    }

    @Test
    public void checkCityCanBeOpenedOnMap() {
        mainPage.openCityOnMap(CITIES_FOR_SEARCH[3]);
        mapPage.isMapShown();
    }

    @Test
    public void checkBackButton() {
        checkCityCanBeOpenedOnMap();
        Espresso.pressBack();
        mainPage.isMainShown();
        Espresso.pressBackUnconditionally();
        assertTrue(mainRule.getActivity().isDestroyed());
    }

    @Test
    public void checkDeviceOrientationChangingOnMain() {
        mainPage.changeOrientation(mainRule, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mainPage.isMainShown();
    }

    @Test
    public void checkDeviceOrientationChangingOnMap() {
        mainPage.changeOrientation(mainRule, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mapPage.isMapShown();
    }
}
