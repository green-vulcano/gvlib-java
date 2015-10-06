package it.greenvulcano.iot.transports.mqtt;

import it.greenvulcano.iot.Callback;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * 
 *
 */
public class CallbackHandler implements MqttCallback {
	private MqttTransport transport;
	private ConnectionParams params;
	private ConcurrentLinkedQueue<IncomingMessage> incomingQueue;
	
	/**
	 * 
	 * @param transport
	 * @param params
	 */
	public CallbackHandler(MqttTransport transport, ConnectionParams params) {
		this.transport = transport;
		this.params = params;
		this.incomingQueue = new ConcurrentLinkedQueue<>();
	} 

	/**
	 * 
	 */
	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception {			
		if (params.isAsyncCallbacks()) {
			dispatch(topic, msg);
		} else {
			incomingQueue.add(new IncomingMessage(topic, msg));
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int processPendingIncomingMessages() {
		int processed = 0;
		for (IncomingMessage im : incomingQueue) {
			dispatch(im.getTopic(), im.getMessage());
			++processed;
		}
		return processed;
	}

	/**
	 * 
	 * @param topic
	 * @param msg
	 */
	private void dispatch(String topic, MqttMessage msg) {
		Object payload = msg.getPayload();
		for (Callback cb : this.transport.getRegisteredCallbacks(topic)) {
			payload = cb.call(payload);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void connectionLost(Throwable reason) {
		// TODO handle re-connect
	}

	/**
	 * 
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken tok) {
	}
}
