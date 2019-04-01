/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package me.kalicinski.multiplatform

import org.w3c.dom.*
import kotlin.browser.localStorage

actual class MultiStorage actual constructor() {
    actual fun getString(key: String): String? {
        return localStorage.get(key)
    }

    actual fun putString(key: String, value: String?) {
        if (value != null) {
            localStorage.set(key, value)
        } else {
            localStorage.removeItem(key)
        }
    }
}