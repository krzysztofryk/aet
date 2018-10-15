/**
 * AET
 *
 * Copyright (C) 2018 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.job.common.utils.javaScript;

public class JavaScriptJobResult {

  private final Object executionResult;

  public JavaScriptJobResult(Object executionResult) {
    this.executionResult = executionResult;
  }

  public Object getExecutionResult() {
    return executionResult;
  }

  public boolean isException() {
    return executionResult instanceof Throwable;
  }

  public String getExceptionMessage() {
    return ((Throwable) executionResult).getMessage();
  }
}
