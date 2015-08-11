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
import it.greenvulcano.iot.GVComm;

import java.io.IOException;

/**
 * Interface to abstract transport-related details.
 * 
 * The GreenVulcano Communication Library keeps protocol and transmission
 * separated, so that either part can have multiple implementations which
 * can all be combined to obtain maximum flexibility.
 * <br/>
 * <strong>Note:</strong> By design, a <code>Transport</code> is responsible
 * for its callbacks (i.e. it manages and owns subscripton and unsubscription)
 * while other classes having callback-related methods, including
 * {@link GVComm}, will only eventually delegate all non-trivial aspects to
 * their <code>Transport</code>.
 *  
 * @see Callback
 * @author Domenico Barra <eisenach@gmail.com>
 */
public interface Transport {

	/**
	 * Checks the availability of the network connection.
	 * @return <code>true</code> if the underlying connection is established and usable.
	 */
	boolean isConnected();
	
	/**
	 * Connects to the remote peer (/server).
	 * @throws IOException if anything goes wrong on the network level.
	 */
	void    connect() throws IOException;
	
	/**
	 * Disconnects from the remote peer (/server).
	 * If already disconnected, the call has no effect.
	 */
	void    disconnect();

	/**
	 * Sends a payload to the remote peer (/server).
	 * @param service the remote GreenVulcan service to send the payload to.
	 * @param payload the data to send.
	 * @throws IOException if anything goes wront on the network level.
	 */
	void send(String service, byte[] payload) throws IOException;
	
	/**
	 * Informs the remote peer (/server) of the wish to receive information
	 * being published for a certain topic. When data is received, it shall
	 * be passed to the provided {@link Callback}.<br/>
	 * <strong>Note:</strong> more than one callback is allowed to exist for
	 * a given topic. In this case, the <code>Transport</code> object has the
	 * responsibility to invoke all callbacks, in the order they subscribed,
	 * with data chaining (i.e. the result of the n-th invocation shall be
	 * passed as a parameter to the n+1th invocation).
	 * 
	 * @param topic the topic in which you are interested.
	 * @param cb the callback to invoke when data is received on the given topic.
	 * @throws IOException if anything goes wrong on the network level while subscribing.
	 */
	void subscribe(String topic, Callback cb) throws IOException;

	/**
	 * Removes callbacks from a topic subscription. When the last callback is
	 * removed, this method is also responsible for communicating to the remote
	 * peer (/server) the wish to unsubscribe 
	 * @param topic the topic to unsubscribe the callback(s) from
	 * @param cb the callback to unsubscribe. If <code>cb</code> is found multiple
	 *           times in the subscriptors, all instances are removed from subscription.
	 *           If <code>null</code> is passed, all callbacks are unsubscribed.
	 * @throws IOException if anything goes wrong on the network level while unsubscribing.
	 */
	void unsubscribe(String topic, Callback cb) throws IOException;

	/**
	 * Checks - non-blockingly - whether there's any incoming data to be processed.
	 * If so, it calls any registered callback for the incoming data.
	 * @return boolean <code>true</code> if any data was received during the poll operation.
	 * @throws IOException if anything goes wrong on the network level.
	 */
	boolean poll() throws IOException;
}
