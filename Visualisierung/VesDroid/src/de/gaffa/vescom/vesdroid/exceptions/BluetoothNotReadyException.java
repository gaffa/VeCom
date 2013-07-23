/*
 * Copyright (C) 2012 Henning Groß
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

package de.gaffa.vescom.vesdroid.exceptions;

/**
 * thrown if an action is requested but bluetooth is not ready to fulfill it
 */
public class BluetoothNotReadyException extends Exception {

    /**
     * {@inheritDoc}
     */
    public BluetoothNotReadyException(String detailMessage) {
        super(detailMessage);
    }
}
