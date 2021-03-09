package utils;

import android.view.View;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;

import com.azimolabs.conditionwatcher.ConditionWatcher;
import com.azimolabs.conditionwatcher.Instruction;

public class ConditionWatchers {

    private static final int TIMEOUT = 2000;
    private static final int CHECK_INTERVAL = 500;

    private static void waitForSomeViewCondition(final View view, final int condition) {
        ConditionWatcher.setWatchInterval(CHECK_INTERVAL);
        ConditionWatcher.setTimeoutLimit(TIMEOUT);

        try {
            ConditionWatcher.waitForCondition(new Instruction() {
                @Override
                public String getDescription() {
                    return String.format("Wait until %s view will be %s", view.toString(), condition);
                }

                @Override
                public boolean checkCondition() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return view.getVisibility() == condition;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void waitUntilViewVisible(View view) {
        waitForSomeViewCondition(view, View.VISIBLE);
    }

    public static void waitUntilViewInvisible(View view) {
        waitForSomeViewCondition(view, View.INVISIBLE);
    }

    public static void waitUntilViewGone(View view) {
        try {
            waitForSomeViewCondition(view, View.GONE);
        } catch (NoMatchingViewException ignored) {
        }
    }

    public static void waitForSomeCondition(final ViewInteraction interaction, final ViewAssertion assertion) {
        ConditionWatcher.setWatchInterval(CHECK_INTERVAL);
        ConditionWatcher.setTimeoutLimit(TIMEOUT);

        try {
            ConditionWatcher.waitForCondition(new Instruction() {
                @Override
                public String getDescription() {
                    return String.format("Wait until %s view interaction will be %s", interaction.toString(), assertion.toString());
                }

                @Override
                public boolean checkCondition() {
                    interaction.check(assertion);
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
