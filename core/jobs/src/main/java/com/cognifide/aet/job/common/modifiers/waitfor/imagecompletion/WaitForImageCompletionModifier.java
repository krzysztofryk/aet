/**
 * AET
 * <p>
 * Copyright (C) 2013 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.modifiers.waitfor.imagecompletion;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.modifiers.WebElementsLocatorParams;
import com.cognifide.aet.job.common.modifiers.waitfor.WaitForHelper;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class WaitForImageCompletionModifier extends WebElementsLocatorParams implements CollectorJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaitForImageCompletionModifier.class);

    static final String NAME = "wait-for-image-completion";

    private final WebDriver webDriver;

    WaitForImageCompletionModifier(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public CollectorStepResult collect() throws ProcessingException {
        CollectorStepResult result;
        try {
            result = WaitForHelper.waitForExpectedCondition(webDriver, getTimeoutInSeconds(),
                    new ExpectedCondition<Boolean>() {
                        public Boolean apply(WebDriver webDriver) {
                            return (Boolean) ((JavascriptExecutor) webDriver).executeScript(
                                    "return arguments[0].complete",
                                    webDriver.findElement(getLocator()));
                        }
                    });
        } catch (Exception e) {
            final String message =
                    String.format("Failed to wait for image to be loaded with provided locator. Error: %s",
                            e.getMessage());
            result = CollectorStepResult.newProcessingErrorResult(message);
            LOGGER.warn(message, e);
        }
        return result;
    }

    @Override
    public void setParameters(Map<String, String> parameters) throws ParametersException {
    }
}
