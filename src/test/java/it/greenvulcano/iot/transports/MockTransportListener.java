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

/**
 * Created by Domenico Barra <eisenach@gmail.com> on 28/01/16.
 */
public class MockTransportListener implements TransportListener {
    int afterConnectCalled;
    int beforeDisconnectCalled;
    int afterSubscribeCalled;
    int beforeUnsubscribeCalled;
    int afterConnectionLostCalled;
    int afterConnectionUnsuccessfulCalled;
    Transport lastTransport;

    @Override
    public void afterConnect(Info i) {
        ++afterConnectCalled;
        lastTransport = i.transport;
    }

    @Override
    public void beforeDisconnect(Info i) {
        ++beforeDisconnectCalled;
        lastTransport = i.transport;
    }

    @Override
    public void afterSubscribe(Info i) {
        ++afterSubscribeCalled;
        lastTransport = i.transport;
    }

    @Override
    public void beforeUnsubscribe(Info i) {
        ++beforeUnsubscribeCalled;
        lastTransport = i.transport;
    }

    @Override
    public void afterConnectionLost(Info i) {
        ++afterConnectionLostCalled;
        lastTransport = i.transport;
    }

    @Override
    public void afterConnectionUnsuccessful(Info i) {
        ++afterConnectionUnsuccessfulCalled;
        lastTransport = i.transport;
    }
}
