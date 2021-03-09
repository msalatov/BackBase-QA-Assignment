package pages;

import androidx.test.rule.ActivityTestRule;

import app.com.mobileassignment.views.MainActivity;

public class BasePage {
    public BasePage changeOrientation(ActivityTestRule<MainActivity> rule, int orientation) {
        rule.getActivity().setRequestedOrientation(orientation);
        return this;
    }
}
