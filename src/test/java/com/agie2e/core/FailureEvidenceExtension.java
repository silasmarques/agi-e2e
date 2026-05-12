package com.agie2e.core;

import com.agie2e.utils.EvidenceCollector;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

public class FailureEvidenceExtension implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        Object testInstance = context.getRequiredTestInstance();
        if (testInstance instanceof BaseTest base && base.getDriver() != null) {
            EvidenceCollector.attach(base.getDriver());
        }
    }
}
