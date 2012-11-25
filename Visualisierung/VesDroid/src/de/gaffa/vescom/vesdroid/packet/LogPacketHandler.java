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

package de.gaffa.vescom.vesdroid.packet;

import android.util.Log;

/**
 * {@inheritDoc}
 * logs at debug-level with tag "VesDroid"
 */
public class LogPacketHandler implements VesComPacketHandler {

    /**
     * {@inheritDoc}
     * logs at debug-level with tag "VesDroid"
     */
    @Override
    public void handlePacket(VesComPacket vesComPacket) {
        Log.d("VesDroid", "Packet received: " + vesComPacket);
    }
}
