package utils;

import model.TestStep;

@FunctionalInterface
public interface TestStepCallback {
    void onSubmit(TestStep testStep);
}

