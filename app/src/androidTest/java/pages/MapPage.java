package pages;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;

public class MapPage extends BasePage {
    private static final String CONTENT_DESC = "Google Map";

    public void isMapShown() {
        onView(withContentDescription(CONTENT_DESC)).check(matches(isDisplayed()));
    }
}
