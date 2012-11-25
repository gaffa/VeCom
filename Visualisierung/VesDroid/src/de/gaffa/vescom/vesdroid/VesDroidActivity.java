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

package de.gaffa.vescom.vesdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.BluetoothChat.DeviceListActivity;
import com.example.android.BluetoothChat.R;
import de.gaffa.vescom.vesdroid.bluetoothmanagement.BluetoothManagement;
import de.gaffa.vescom.vesdroid.bluetoothmanagement.MessageHandler;
import de.gaffa.vescom.vesdroid.exceptions.BluetoothNotAvailableException;
import de.gaffa.vescom.vesdroid.packet.LogPacketHandler;
import de.gaffa.vescom.vesdroid.packet.UpdateUIPacketHandler;
import de.gaffa.vescom.vesdroid.util.Properties;

/**
 * the entry class representing the main android activity
 */
public class VesDroidActivity extends Activity {

    // management instance for bluetooth-operations
    private BluetoothManagement bluetoothManagement;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        /**
         * "standard" android activity-initialization
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vesdroid);

        /**
         * find activities textviews
         */
        TextView v = (TextView) findViewById(R.id.v);
        TextView rpm = (TextView) findViewById(R.id.rpm);
        TextView egt = (TextView) findViewById(R.id.egt);
        TextView packetNumber = (TextView) findViewById(R.id.packetnumber);


        // initialize Messagehandler-instance
        MessageHandler messageHandler = new MessageHandler(15625, 2, 133);

        // register handlers for vescom-packets that are received
        messageHandler.withVesComPacketHandler(new LogPacketHandler());
        messageHandler.withVesComPacketHandler(new UpdateUIPacketHandler(rpm, v, egt, packetNumber));

        try {
            // initialize/instantiate bluetooth-management
            bluetoothManagement = new BluetoothManagement(messageHandler);
        } catch (BluetoothNotAvailableException b) {
            /**
             * handle devices on which bluetooth is not available
             */
            Toast.makeText(this, "Bluetooth is not available on this device", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();
        bluetoothManagement.onStart(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void onResume() {
        super.onResume();
        bluetoothManagement.onResume();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothManagement.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * handles messages from other activities
         */
        switch (requestCode) {
            case Properties.REQUEST_CONNECT_DEVICE:
                // deviceListActivity from com.example.android.BluetoothChat returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // get address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // connect
                    bluetoothManagement.connectDevice(address);
                }
                break;
            case Properties.REQUEST_ENABLE_BT:
                // the request to enable bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // bluetooth enabled, setup connection
                    bluetoothManagement.setupConnection();
                } else {
                    // bluetooth still off, exit
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /**
         * creates options menu
         */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * handles device selection on options menu
         */
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, Properties.REQUEST_CONNECT_DEVICE);
                return true;
        }
        return false;
    }
}