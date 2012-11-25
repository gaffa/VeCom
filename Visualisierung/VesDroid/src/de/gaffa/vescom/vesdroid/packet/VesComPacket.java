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

/**
 * representation of a packet received from measure-unit
 */
public class VesComPacket {

    // unique packet identifier
    private Integer packetNumber;

    // rounds per minute
    private Integer rpm;

    // velocity
    private Integer v;

    // exhaust gas temperature
    private Integer egt;

    /**
     * representation of a packet received from measure-unit
     *
     * @param packetNumber unique packet identifier
     * @param rpm          rounds per minute
     * @param v            velocity
     * @param egt          exhaust gas temperature
     */
    public VesComPacket(Integer packetNumber, Integer rpm, Integer v, Integer egt) {
        this.packetNumber = packetNumber;
        this.rpm = rpm;
        this.v = v;
        this.egt = egt;
    }

    /**
     * getter for packet number
     *
     * @return unique packet identifier
     */
    public Integer getPacketNumber() {
        return packetNumber;
    }

    /**
     * getter for rpm
     *
     * @return rounds per minute
     */
    public Integer getRpm() {
        return rpm;
    }

    /**
     * getter for v
     *
     * @return velocity
     */
    public Integer getV() {
        return v;
    }

    /**
     * getter for egt
     *
     * @return exhaust gas temperature
     */
    public Integer getEgt() {
        return egt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "VesCom Packet with number: " + packetNumber + ". rpm: " + rpm + ". v: " + v + ". egt: " + egt + ".";
    }
}
