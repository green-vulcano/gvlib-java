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
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Practical base class for most {@link Transport} implementations.
 * @author Domenico Barra <eisenach@gmail.com>
 */
public abstract class TransportBase implements Transport {	
	/**
	 * Holds the topic subscription details. This is meant to be accessed only by
	 * the following methods of this class.
	 * @see #subscribe(String, Callback)
	 * @see #unsubscribe(String, Callback)
	 * @see #getRegisteredCallbacks(String)
	 */
	protected Map<String, List<Callback>> registeredCallbacks = new HashMap<>();

	/**
	 * Holds the registered transport listeners.
	 */
	protected Set<TransportListener> transportListeners = new HashSet<>();

	@Override
	public boolean addTransportListener(TransportListener tl) {
		return transportListeners.add(tl);
	}

	@Override
	public boolean removeTransportListener(TransportListener tl) {
		return transportListeners.remove(tl);
	}

	@Override
	public final void connect() throws IOException {
		try {
			handleConnect();
			invokeCallback(TransportListener::afterConnect, new TransportListener.Info(this));
		} catch (Exception exc) {
			invokeCallback(TransportListener::afterConnectionUnsuccessful,
					new TransportListener.Info(this, null, exc));
			throw exc;
		}
	}

	@Override
	public final void disconnect() {
		invokeCallback(TransportListener::beforeDisconnect, new TransportListener.Info(this));
		handleDisconnect();
	}

	@Override
	public final void subscribe(String topic, Callback cb) throws IOException {
		if (!isConnected()) {
			connect();
		}
		
		handleSubscribe(topic, cb);
		
		List<Callback> callbacks = registeredCallbacks.get(topic);
		if (callbacks == null) {
			callbacks = new ArrayList<>();
			registeredCallbacks.put(topic, callbacks);
		}
		callbacks.add(cb);

		invokeCallback(TransportListener::afterSubscribe, new TransportListener.Info(this, topic));
	}
	
	@Override
	public final void unsubscribe(String topic, Callback cb) throws IOException {
		if (!isConnected()) {
			connect();
		}

		invokeCallback(TransportListener::beforeUnsubscribe, new TransportListener.Info(this, topic));
		handleUnsubscribe(topic, cb);

		if (cb == null) {
			registeredCallbacks.remove(topic);
		} else {
			List<Callback> callbacks = registeredCallbacks.get(topic);
			if (callbacks != null) {
				while (callbacks.remove(cb)) {
					// do nothing: this cycle just makes sure
					// that all instances of cb are removed
				}
			}
		}
	}
	
	/**
	 * Returns all registered callbacks for a given topic, through an unmodifiable list.
	 * @param topic the topic to search callbacks for.
	 * @return an immutable list. If no callbacks are found, the list is empty
	 *         (i.e. not <code>null</code>).
	 */
	public List<Callback> getRegisteredCallbacks(String topic) {
		List<Callback> callbacks = registeredCallbacks.get(topic);
		if (callbacks != null) {
			return Collections.unmodifiableList(callbacks);
		}
		return Collections.emptyList();
	}

	protected void invokeCallback(BiConsumer<TransportListener, TransportListener.Info> cback,
								  TransportListener.Info info) {
		transportListeners.forEach( lst -> cback.accept(lst, info) );
	}

	/**
	 * Delegate for the implementation-specific details of topic subscription.
	 * Every concrete instance of a {@link Transport} need to provide an
	 * implementation of this.
	 * 
	 * @param topic the topic to subscribe to.
	 * @param cb the callback to invoke when data is received on the topic.
	 * @throws IOException if anything goes wrong during the subscription phase.
	 */
	protected abstract void handleSubscribe(String topic, Callback cb) throws IOException;

	/**
	 * Delegate for the implementation-specific details of topic unsubscription.
	 * <strong>Careful:</strong> you should only phisically unsubscribe (i.e.
	 * communicate this intention to the remote peer) when the last callback
	 * for a given topic is unsubscribed.
	 * @param topic the topic to unsubscribe from.
	 * @param cb The callback to unsubscribe. If <code>null</code> is passed, all callbacks
	 *           are being removed.
	 * @throws IOException if anything goes wrong during the unsubscription phase.
	 */
	protected abstract void handleUnsubscribe(String topic, Callback cb) throws IOException;

	protected abstract void handleConnect() throws IOException;

	protected abstract void handleDisconnect();

}
