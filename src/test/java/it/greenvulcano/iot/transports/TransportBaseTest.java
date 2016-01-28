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
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by Domenico Barra <eisenach@gmail.com> on 28/01/16.
 */
public class TransportBaseTest {

    @Test
    public void testInvokeCallback() throws Exception {
        MockTransportBase mtb = new MockTransportBase();
        MockTransportListener tl1 = new MockTransportListener(),
                tl2 = new MockTransportListener();

        mtb.addTransportListener(tl1);
        mtb.addTransportListener(tl2);

        mtb.invokeCallback(TransportListener::afterConnect, new TransportListener.Info(mtb));

        assertEquals(1, tl1.afterConnectCalled);
        assertEquals(1, tl2.afterConnectCalled);
        assertEquals(mtb, tl1.lastTransport);
        assertEquals(mtb, tl2.lastTransport);
    }
}