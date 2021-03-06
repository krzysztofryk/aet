/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
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
package com.cognifide.aet.job.common.modifiers.cookie;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import org.junit.Test;

public class ModifyActionTest {

  @Test
  public void fromStringTest() throws ParametersException {
    ModifyAction action;

    action = ModifyAction.fromString("ADD");
    assertThat(action, is(ModifyAction.ADD));

    action = ModifyAction.fromString("REMOVE");
    assertThat(action, is(ModifyAction.REMOVE));
  }

  @Test
  public void fromStringTest_lowerCase() throws ParametersException {
    ModifyAction action;

    action = ModifyAction.fromString("add");
    assertThat(action, is(ModifyAction.ADD));

    action = ModifyAction.fromString("remove");
    assertThat(action, is(ModifyAction.REMOVE));

    action = ModifyAction.fromString("Add");
    assertThat(action, is(ModifyAction.ADD));

    action = ModifyAction.fromString("Remove");
    assertThat(action, is(ModifyAction.REMOVE));
  }

  @Test(expected = ParametersException.class)
  public void fromStringTest_invalidActionName() throws ParametersException {
    ModifyAction.fromString("non-existing");
  }

}
