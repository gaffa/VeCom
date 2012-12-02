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

package de.gaffa.vescom.vesdroid.bluetoothmanagement;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import de.gaffa.vescom.vesdroid.packet.VesComPacket;
import de.gaffa.vescom.vesdroid.packet.VesComPacketHandler;
import de.gaffa.vescom.vesdroid.util.Properties;

import java.util.ArrayList;
import java.util.List;

/**
 * handles bluetooth messages and transforms them into vesdroid-packets
 * also calls their handlers
 */
public class MessageHandler extends Handler {


    private final static char START = '*';
    private final static char END = '#';
    private final static String SEPARATOR = ",";

    // clock ticks per second of the controller
    private Integer controllerTicksPerSecond;

    // ignition signals to controller per round
    private Integer ignitionSignalsPerRound;

    // the wheels circumference
    private Integer wheelCircumference;

    // packet handlers to notify
    private List<VesComPacketHandler> vesComPacketHandlers = new ArrayList<VesComPacketHandler>();

    // packets are build here until they are fully received
    private String messageBuffer;

    /**
     * initializes the handler
     *
     * @param controllerTicksPerSecond clock ticks per second of the controller
     * @param ignitionSignalsPerRound  ignition signals to controller per round
     * @param wheelCircumference       the wheels circumference
     */
    public MessageHandler(Integer controllerTicksPerSecond, Integer ignitionSignalsPerRound, Integer wheelCircumference) {
        this.controllerTicksPerSecond = controllerTicksPerSecond;
        this.ignitionSignalsPerRound = ignitionSignalsPerRound;
        this.wheelCircumference = wheelCircumference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(Message msg) {
        // handle message received event
        if (msg.what == Properties.MESSAGE_READ) {
            byte[] readBuf = (byte[]) msg.obj;
            // construct a string from the valid bytes in the buffer
            String readMessage = new String(readBuf, 0, msg.arg1);
            //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
            for (char c : readMessage.toCharArray()) {
                if (c == START) {
                    messageBuffer = new String();
                } else if (c == END) {
                    handlePacket();
                } else if (messageBuffer != null) {
                    messageBuffer += c;
                }
            }
        }
    }

    /**
     * calculates rpm from diff value
     *
     * @param diff how many ticks since last measure
     * @return
     */
    private Integer convertDiffToRpm(Integer diff) {
        if(diff == 0){
            return 0;
        }
        return controllerTicksPerSecond / diff / ignitionSignalsPerRound * 60;
    }

    /**
     * calculates velocity from diff value
     *
     * @param diff how many ticks since last measure
     * @return
     */
    private Integer convertDiffToV(Integer diff) {
        if(diff == 0){
            return 0;
        }
        return controllerTicksPerSecond / diff / 3600 * wheelCircumference / 10 ^ 5;
    }

    /**
     * converts a bluetooth-message to a vesdroid-packet and notifies all handlers
     */
    private void handlePacket() {
        // create value-array from string by splitting at the separator-character
        String[] fields = messageBuffer.split(SEPARATOR);
        try {
            if (fields.length != 4) {
                // message seems to be invalid as 4 values are expected
                throw new Exception("Message contained more or less than 4 fields");
            }
            /**
             * assign values
             */
            Integer packetNumber = Integer.parseInt(fields[0]);
            Integer rpm = convertDiffToRpm(Integer.parseInt(fields[1]));
            Integer v = convertDiffToV(Integer.parseInt(fields[2]));
            Integer egt = Integer.parseInt(fields[3]);

            // create packet with values
            VesComPacket packet = new VesComPacket(packetNumber, rpm, v, egt);

            /**
             * notify all registered handlers
             */
            for (VesComPacketHandler vesComPacketHandler : vesComPacketHandlers) {
                /**
                 * just put entries in the log if anything goes wrong with a single handler. we still want the other
                 * handlers to get notified
                 */
                try {
                    vesComPacketHandler.handlePacket(packet);
                } catch (Throwable th) {
                    Log.e("VesDroid", "The handler: " + vesComPacketHandler + " caused an Exception: ", th);
                }
            }
        } catch (Throwable tr) {
            // log all exceptions that occur (even
            Log.e("VesDroid", "Fatal. A Message could not be converted to a VesDroid-Packet.", tr);
        }
    }

    /**
     * builder-style add-accessor for a packet handler
     *
     * @param vesComPacketHandler the handler to register
     * @return the instance itself (builder-style)
     */
    public MessageHandler withVesComPacketHandler(VesComPacketHandler vesComPacketHandler) {
        vesComPacketHandlers.add(vesComPacketHandler);
        return this;
    }
}
