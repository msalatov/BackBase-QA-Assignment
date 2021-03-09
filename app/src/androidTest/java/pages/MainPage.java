package pages;

import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.test.espresso.AmbiguousViewMatcherException;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;

import com.fasterxml.jackson.databind.ser.Serializers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import app.com.mobileassignment.R;
import app.com.mobileassignment.model.City;
import app.com.mobileassignment.views.MainActivity;
import utils.ConditionWatchers;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static utils.ConditionWatchers.*;

public class MainPage extends BasePage {
    private ViewInteraction searchField;

    public MainPage() {
        searchField = onView(withId(R.id.search));
    }

    // Wait until Main Page would be ready
    public MainPage waitMainPage(ActivityTestRule<MainActivity> rule) {
        waitUntilViewGone(rule.getActivity().findViewById(R.id.progress_bar));
        return this;
    }

    // Type text into Search field
    public MainPage enterInSearchField(String text) {
        for (int i = 0; i < text.length(); i++) {
                searchField.perform(typeText(String.valueOf(text.charAt(i))));
        }
        return this;
    }

    public MainPage closeKeyboardAfterSearch() {
        searchField.perform(closeSoftKeyboard());
        return this;
    }

    // Clear Search field
    public MainPage cleanSearchField() {
        searchField.perform(clearText());
        return this;
    }

    // Wait until Search results would be ready
    public MainPage waitForSearchResults(ActivityTestRule<MainActivity> rule) {
        waitUntilViewVisible(rule.getActivity().findViewById(R.id.results));
        return this;
    }

    private ListAdapter getCitiesListAdapter(ActivityTestRule<MainActivity> rule) {
        return ((ListView) rule.getActivity().findViewById(R.id.citiesList)).getAdapter();
    }

    // Get list of cities
    public List<City> getListOfCities(ActivityTestRule<MainActivity> rule) {
        ListAdapter citiesListAdapter = getCitiesListAdapter(rule);

        List<City> citiesList = new ArrayList<>();
        for (int i = 0; i < citiesListAdapter.getCount(); i++) {
            citiesList.add((City) citiesListAdapter.getItem(i));
        }
        return citiesList;
    }

    public MapPage openCityOnMap(String city) {
        onData(new TypeSafeMatcher<City>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("Matches city by name and country");
            }

            @Override
            protected boolean matchesSafely(City item) {
                return city.equals(item.getName() + ", " + item.getCountry());
            }
        }).perform(click());
        return new MapPage();
    }

    public MainPage scrollDown() {
        onView(withId(R.id.results)).perform(swipeUp());
        return this;
    }

    public boolean isCityDisplayedInList(String city) {
        ConditionWatchers.waitForSomeCondition(onView(withSubstring(city)), matches(isDisplayed()));
        try {
            onView(withSubstring(city)).check(matches(isDisplayed()));
            return true;
        } catch (AmbiguousViewMatcherException e) {
            return true;
        } catch (NoMatchingViewException | AssertionError e) {
            return false;
        }
    }

    public void isMainShown() {
        onView(withId(R.id.citiesList))
                .check(matches(isDisplayed()));
    }

    public List<String> convertCitiesToStrings(List<City> cities) {
        return cities.stream().
                map(c -> c.getName() + ", " + c.getCountry()).
                collect(Collectors.toList());
    }
}
