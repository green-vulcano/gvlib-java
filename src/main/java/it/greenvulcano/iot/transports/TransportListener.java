/*
 * Copyright (c) 2015-2016, GreenVulcano Open Source Project. All rights reserved.
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
 * Interface for transport listeners.
 * Any subsystem willing to receive (and react to) transport-related events (such as
 * connect, disconnect, ...) need to implement this interface and register
 * themselves with the specific {@link Transport} instance they are interested into.
 *
 * Listeners are not expected (and should never try) to throw exceptions from callback
 * methods, as this would interfere with the normal functioning of the transport. However,
 * <code>Transport</code> implementations are encouraged to wrap any invocation of such
 * callbacks into <code>try { ... } catch (Exception exc) {}</code>. In other words, whenever
 * a <code>TransportListener</code> needs to react in order to modify the Transport's behavior,
 * it should use normal method invocation.
 *
 * @author Domenico Barra <eisenach@gmail.com>
 */
public interface TransportListener {

    class Info {
        public Transport transport;
        public String    topic;
        public Exception failureReason;

        public Info(Transport transport) {
            this.transport = transport;
        }

        public Info(Transport transport, String topic) {
            this.transport = transport;
            this.topic = topic;
        }

        public Info(Transport transport, String topic, Exception failureReason) {
            this.transport = transport;
            this.topic = topic;
            this.failureReason = failureReason;
        }
    }

    /**
     * Called after a connection is successfully established.
     * @param i <code>Info</code> object with the following fields valued:
     *          <ul>
     *              <li>transport: the transport that has established the connection.</li>
     *          </ul>
     */
    default void afterConnect(Info i) { }

    /**
     * Called before an established connection is closed.
     * @param i <code>Info</code> object with the following fields valued:
     *          <ul>
     *              <li>transport: the transport that is about to initiate disconnection.</li>
     *          </ul>
     */
    default void beforeDisconnect(Info i) { }

    /**
     * Called after a transport has successfully subscribed to a topic
     * @param i <code>Info</code> object with the following fields valued:
     *          <ul>
     *              <li>transport: the transport that made the subscription.</li>
     *              <li>topic: the topic for which the subscription has happened</li>
     *          </ul>
     */
    default void afterSubscribe(Info i) { }

    /**
     * Called before a transport terminates the subscription to a topic
     *
     * @param i <code>Info</code> object with the following fields valued:
     *          <ul>
     *          <li>transport: the transport that is about to terminate the subscription.</li>
     *          <li>topic: the topic for which the subscription is about to terminate</li>
     *          </ul>
     */
    default void beforeUnsubscribe(Info i) { }

    /**
     * Called after connection is abruptly lost.
     * @param i <code>Info</code> object with the following fields valued:
     *          <ul>
     *              <li>transport: the transport that lost the connection.</li>
     *              <li>failureReason: the exception that caused the failure.</li>
     *          </ul>
     */
    default void afterConnectionLost(Info i) { }

    /**
     * Called after an attempt to connect proved unsuccessful.
     * @param i <code>Info</code> object with the following fields valued:
     *          <ul>
     *              <li>transport: the transport that attempted the connection.</li>
     *              <li>failureReason: the exception that caused the failure.</li>
     *          </ul>
     */
    default void afterConnectionUnsuccessful(Info i) { }

}
