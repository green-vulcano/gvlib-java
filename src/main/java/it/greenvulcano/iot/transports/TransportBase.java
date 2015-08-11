package it.greenvulcano.iot.transports;

import it.greenvulcano.iot.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TransportBase implements Transport {


	@Override
	public final void subscribe(String topic, Callback cb) throws IOException {
		handleSubscribe(topic, cb);
		List<Callback> callbacks = registeredCallbacks.get(topic);
		if (callbacks == null) {
			callbacks = new ArrayList<>();
			registeredCallbacks.put(topic, callbacks);
		}
		callbacks.add(cb);
	}
	
	@Override
	public void unsubscribe(String topic, Callback cb) throws IOException {
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
	protected List<Callback> getRegisteredCallbacks(String topic) {
		List<Callback> callbacks = registeredCallbacks.get(topic);
		if (callbacks != null) {
			return Collections.unmodifiableList(callbacks);
		}
		return Collections.emptyList();
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
	
	/**
	 * Holds the topic subscription details. This is meant to be accessed only by
	 * the following methods of this class.
	 * @see #subscribe(String, Callback)
	 * @see #unsubscribe(String, Callback)
	 * @see #getRegisteredCallbacks(String)
	 */
	private Map<String, List<Callback>> registeredCallbacks = new HashMap<>();
}
