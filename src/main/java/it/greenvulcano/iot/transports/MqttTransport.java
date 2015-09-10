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
import it.greenvulcano.iot.DeviceInfo;
import it.greenvulcano.iot.protocols.Protocol_IOT_v1;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * MQTT transport.
 * 
 * Using Eclipse Paho MQTT v.3.1 client
 * @author Domenico Barra <eisenach@gmail.com>
 */
public class MqttTransport extends TransportBase {

	public static class ConnectionParams {

		DeviceInfo  deviceInfo;
		InetAddress server;
		int         port;
		String      username;
		String      password;
		int         publishQos     = 1;
		boolean     publishRetain  = false;
		boolean     asyncCallbacks = true;
		boolean     autoconnect    = true;

		public ConnectionParams(DeviceInfo deviceInfo, InetAddress server, int port) {
			this.deviceInfo = deviceInfo;
			this.server = server;
			this.port = port;
		}

		public ConnectionParams setUsername(String username) {
			this.username = username;
			return this;
		}
		
		public ConnectionParams setPassword(String password) {
			this.password = password;
			return this;
		}

		public ConnectionParams setPublishQos(int publishQos) {
			this.publishQos = publishQos;
			return this;
		}
		
		public ConnectionParams setPublishRetain(boolean publishRetain) {
			this.publishRetain = publishRetain;
			return this;
		}
		
		public ConnectionParams setAsyncCallbacks(boolean asyncCallbacks) {
			this.asyncCallbacks = asyncCallbacks;
			return this;
		}

		public ConnectionParams setAutoconnect(boolean autoconnect) {
			this.autoconnect = autoconnect;
			return this;
		}

		
	}

	public MqttTransport(ConnectionParams params) throws IOException {
		this.params = params;
		try {
			client = new MqttClient(String.format("tcp:/%s:%d",
					params.server.toString(), params.port),
					params.deviceInfo.getId());
			client.setCallback(cbHandler);
			if (params.autoconnect) {
				connect();
				if (this.isConnected()) {
					Protocol_IOT_v1 prot = new Protocol_IOT_v1(params.deviceInfo, this);
					prot.sendStatus();
				}
			}

		} catch (MqttException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean isConnected() {
		return client.isConnected();
	}

	@Override
	public void connect() throws IOException {
		try {
			client.connect(buildConnectOptions());
		} catch (MqttException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void disconnect() {
		try {
			if (isConnected())
				client.disconnect();
		} catch (MqttException e) {
			// TODO: don't just swallow it, add logging
		}
	}

	@Override
	public void send(String service, byte[] payload) throws IOException {
		if (!isConnected()) connect(); // won't object if this fails.
		try {
			client.publish(service, payload, params.publishQos, params.publishRetain);
		} catch (MqttException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean poll() throws IOException {
		if (params.asyncCallbacks) return false;
		return cbHandler.processPendingIncomingMessages() > 0;
	}

	@Override
	protected void handleSubscribe(String topic, Callback cb)
			throws IOException {
		if (!getRegisteredCallbacks(topic).isEmpty()) {
			try {
				client.subscribe(topic);
			} catch (MqttException e) {
				throw new IOException(e);
			}
		}

	}

	@Override
	protected void handleUnsubscribe(String topic, Callback cb)
			throws IOException {
		if (cb == null || getRegisteredCallbacks(topic).size() == 1) {
			try {
				client.unsubscribe(topic);
			} catch (MqttException e) {
				throw new IOException(e);
			}
		}
	}

	private MqttConnectOptions buildConnectOptions() {
		MqttConnectOptions opts = new MqttConnectOptions();
		if (params.username != null) opts.setUserName(params.username);
		if (params.password != null) opts.setPassword(params.password.toCharArray());
		opts.setWill("/devices/" + params.deviceInfo.getId() + "/status",
				"{\"st\":false}".getBytes(), 1, false);
		return opts;
	}

	private ConnectionParams params;
	private MqttClient client;
	private CallbackHandler cbHandler = new CallbackHandler();

	private class CallbackHandler implements MqttCallback {

		@Override
		public void connectionLost(Throwable reason) {
			// TODO handle re-connect

		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken tok) {
		}

		@Override
		public void messageArrived(String topic, MqttMessage msg)
				throws Exception {
			if (params.asyncCallbacks) {
				dispatch(topic, msg);
			} else {
				incomingQueue.add(new IncomingMessage(topic, msg));
			}

		}
		
		public int processPendingIncomingMessages() {
			int processed = 0;
			for (IncomingMessage im : incomingQueue) {
				dispatch(im.topic, im.message);
				++processed;
			}
			return processed;
		}

		private void dispatch(String topic, MqttMessage msg) {
			Object payload = msg.getPayload();
			for (Callback cb : getRegisteredCallbacks(topic)) {
				payload = cb.call(payload);
			}
		}

		ConcurrentLinkedQueue<IncomingMessage> incomingQueue = new ConcurrentLinkedQueue<>();
	}

	private static class IncomingMessage {
		String topic;
		MqttMessage message;

		public IncomingMessage(String topic, MqttMessage message) {
			super();
			this.topic = topic;
			this.message = message;
		}
	}

}
