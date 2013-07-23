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

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import com.example.android.BluetoothChat.BluetoothService;
import de.gaffa.vescom.vesdroid.exceptions.BluetoothNotAvailableException;
import de.gaffa.vescom.vesdroid.exceptions.BluetoothNotReadyException;
import de.gaffa.vescom.vesdroid.util.Properties;

/**
 * this class is loosely based on the BluetoothChat-class that can be found in the sdk-examples
 * and serves as a facade for the vesdroid-app to the (modified) bluetoothchat-BluetoothService
 */
public class BluetoothManagement {

    // the devices bluetooth adapter
    private BluetoothAdapter bluetoothAdapter;

    // instance of sdk examples bluetoothservice for basic bluetooth-interaction
    private BluetoothService bluetoothService;

    // bluetooth-messagehandler
    private MessageHandler messageHandler;

    /**
     * initializes everything nessecary to set up a bluetooth connection
     *
     * @param messageHandler handler-instance that takes care of messages received from bluetooth
     * @throws BluetoothNotAvailableException if the device does not support bluetooth
     */
    public BluetoothManagement(MessageHandler messageHandler) throws BluetoothNotAvailableException {
        // access bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        this.messageHandler = messageHandler;

        if (bluetoothAdapter == null) {
            // no bluetooth adapter found
            throw new BluetoothNotAvailableException("This device does not support bluetooth.");
        }
    }

    /**
     * call this on your activities onStart-event and pass the activity
     * this will enable bluetooth if it isnt yet.
     * if you dont do this, exceptions will occur later
     *
     * @param activity the activity that shall receive the notification after the bluetooth-enable-request
     */
    public void onStart(Activity activity) {
        if (!bluetoothAdapter.isEnabled()) {
            // bluetooth off, try and enable
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableIntent, Properties.REQUEST_ENABLE_BT);
        } else {
            if (bluetoothService == null) {
                // set up bluetooth
                setupConnection();
            }
        }
    }

    /**
     * initializes bluetooth-service
     */
    public void setupConnection() {
        bluetoothService = new BluetoothService(messageHandler);
    }

    /**
     * check if service is ready to send a message
     *
     * @return true if ready
     */
    public Boolean isReadyToSend() {
        return bluetoothService != null && BluetoothService.STATE_CONNECTED == bluetoothService.getState();
    }

    /**
     * sends a given message to the coupled bluetooth-device (if any)
     *
     * @param message the message to send
     * @throws BluetoothNotReadyException if bluetooth is not ready to send (check using isReadyToSend() first)
     */
    public void sendMessage(String message) throws BluetoothNotReadyException {
        if (!isReadyToSend()) {
            // not ready
            throw new BluetoothNotReadyException("Not ready to send");
        }
        // send message
        bluetoothService.write(message.getBytes());
    }

    /**
     * call this method on your activies onResume-event
     * it will start the service if it was dropped meanwhile
     */
    public void onResume() {
        if (bluetoothService != null) {
            if (bluetoothService.getState() == BluetoothService.STATE_NONE) {
                // (re-)start service
                bluetoothService.start();
            }
        }
    }

    /**
     * call this method on your activities onDestroy-event
     * it will stop the bluetooth-service
     */
    public void onDestroy() {
        if (bluetoothService != null) {
            // stop bluetooth-service
            bluetoothService.stop();
        }
    }

    /**
     * connects a device
     *
     * @param address device address
     */
    public void connectDevice(String address) {
        // get device
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        // connect
        bluetoothService.connect(device);
    }
}
