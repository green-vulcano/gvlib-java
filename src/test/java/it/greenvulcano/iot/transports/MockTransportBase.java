/*
 * Copyright (c) 2015, GreenVulcano Open Source Project. All rights reserved.
 *
 * This file is part of the GreenVulcano Communication Library for IoT.
 *
 * This is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package it.greenvulcano.iot.transports;


import it.greenvulcano.iot.Callback;
import java.io.IOException;

/**
 * Created by Domenico Barra <eisenach@gmail.com> on 28/01/16.
 */
class MockTransportBase extends TransportBase {

    private boolean connected = false;

    @Override
    protected void handleSubscribe(String topic, Callback cb) throws IOException {}
    @Override
    protected void handleUnsubscribe(String topic, Callback cb) throws IOException {}
    @Override
    public boolean isConnected() { return connected; }
    @Override
    public void handleConnect() throws IOException { connected = true; }
    @Override
    public void handleDisconnect() { connected = false; }
    @Override
    public void send(String service, byte[] payload) throws IOException {}
    @Override
    public void send(String service, byte[] payload, boolean retain) throws IOException {}
    @Override
    public boolean poll() throws IOException { return true; }
}
