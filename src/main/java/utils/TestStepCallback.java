package utils;

import model.TestStep;

/**
 * @author Sin
 */
@FunctionalInterface
public interface TestStepCallback {
    void onSubmit(TestStep testStep);
}

