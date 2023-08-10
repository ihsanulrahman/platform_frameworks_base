/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.wm.flicker.launch

import android.platform.test.annotations.Presubmit
import android.tools.common.traces.component.ComponentNameMatcher
import android.tools.device.flicker.junit.FlickerParametersRunnerFactory
import android.tools.device.flicker.legacy.LegacyFlickerTest
import android.tools.device.flicker.legacy.LegacyFlickerTestFactory
import androidx.test.filters.RequiresDevice
import com.android.server.wm.flicker.helpers.TransferSplashscreenAppHelper
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.junit.runners.Parameterized

/**
 * Test cold launching an app from launcher
 *
 * To run this test: `atest FlickerTests:OpenTransferSplashscreenAppFromLauncherTransition`
 *
 * Actions:
 * ```
 *     Inherit from OpenAppFromIconColdTest, Launch an app [testApp] with an animated splash screen
 *     by clicking it's icon on all apps, and wait for transfer splash screen complete
 * ```
 *
 * Notes:
 * ```
 *     1. Some default assertions (e.g., nav bar, status bar and screen covered)
 *        are inherited [OpenAppTransition]
 *     2. Verify no flickering when transfer splash screen to app window.
 * ```
 */

@RequiresDevice
@RunWith(Parameterized::class)
@Parameterized.UseParametersRunnerFactory(FlickerParametersRunnerFactory::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class OpenTransferSplashscreenAppFromLauncherTransition(flicker: LegacyFlickerTest) :
        OpenAppFromIconColdTest(flicker) {
    override val testApp = TransferSplashscreenAppHelper(instrumentation)

    /**
     * Checks that [ComponentNameMatcher.LAUNCHER] window is the top window at the start of the
     * transition, and is replaced by [ComponentNameMatcher.SPLASH_SCREEN], then [testApp] remains
     * visible until the end
     */
    @Presubmit
    @Test
    fun appWindowAfterSplash() {
        flicker.assertWm {
            this.isAppWindowOnTop(ComponentNameMatcher.LAUNCHER)
                    .then()
                    .isAppWindowOnTop(ComponentNameMatcher.SPLASH_SCREEN)
                    .then()
                    .isAppWindowOnTop(testApp)
                    .isAppWindowInvisible(ComponentNameMatcher.SPLASH_SCREEN)
        }
    }

    companion object {
        /**
         * Creates the test configurations.
         *
         * See [LegacyFlickerTestFactory.nonRotationTests] for configuring screen orientation and
         * navigation modes.
         */
        @Parameterized.Parameters(name = "{0}")
        @JvmStatic
        fun getParams() = LegacyFlickerTestFactory.nonRotationTests()
    }
}