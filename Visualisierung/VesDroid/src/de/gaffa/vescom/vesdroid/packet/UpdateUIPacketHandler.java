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

import android.widget.TextView;

/**
 * {@inheritDoc}
 * updates the ui´s textviews
 */
public class UpdateUIPacketHandler implements VesComPacketHandler {

    private TextView rpmView;
    private TextView vView;
    private TextView egtView;
    private TextView packetNumberView;

    /**
     * handler that updates the ui´s textviews
     *
     * @param rpmView          the view representing rounds per minute
     * @param vView            the view representing velocity
     * @param egtView          the view representing exhaust gas temperature
     * @param packetNumberView the view representing the packet number
     */
    public UpdateUIPacketHandler(TextView rpmView, TextView vView, TextView egtView, TextView packetNumberView) {
        this.rpmView = rpmView;
        this.egtView = egtView;
        this.vView = vView;
        this.packetNumberView = packetNumberView;
    }

    /**
     * {@inheritDoc}
     * updates the ui´s textviews
     */
    @Override
    public void handlePacket(VesComPacket vesComPacket) {
        /**
         * set views values
         */
        rpmView.setText(vesComPacket.getRpm().toString());
        egtView.setText(vesComPacket.getEgt().toString());
        vView.setText(vesComPacket.getV().toString());
        packetNumberView.setText(vesComPacket.getPacketNumber().toString());
    }
}
