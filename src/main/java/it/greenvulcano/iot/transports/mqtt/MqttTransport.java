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

package it.greenvulcano.iot.transports.mqtt;

import it.greenvulcano.iot.Callback;
import it.greenvulcano.iot.transports.TransportBase;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MQTT transport.
 * 
 * Using Eclipse Paho MQTT v.3.1 client
 */
public class MqttTransport extends TransportBase {
	private static final Logger LOG = Logger.getLogger(MqttTransport.class.getName());

	private ConnectionParams params;
	private MqttClient client;
	private CallbackHandler cbHandler;

	/**
	 * Creates a new MQTT transport.
	 * @param params the connection options for the transport.
	 * @throws IOException
	 */
	public MqttTransport(ConnectionParams params) throws IOException {
		this.params = params;
		String host = String.format("tcp://%s:%d", params.getServer().getHostAddress(), params.getPort());
		
		this.cbHandler = new CallbackHandler(this, params);

		try {
			client = new MqttClient(host, params.getDeviceInfo().getId());
		} catch (MqttException e) {
			throw new IOException(e);
		}
	}

	/**
	 * 
	 */
	@Override
	public boolean isConnected() {
		return this.client.isConnected();
	}

	/**
	 * 
	 */
	@Override
	protected void handleConnect() throws IOException {
		try {
			this.client.connect(buildConnectOptions());
		} catch (MqttException e) {
			throw new IOException(e);
		}	
	}
	
	/**
	 * 
	 */
	@Override
	protected void handleDisconnect() {
		try {
			if (isConnected())
				this.client.disconnect();
		} catch (MqttException e) {
			LOG.log(Level.WARNING, "Error while disconnecting.", e);
		}
	}

	/**
	 * 
	 */
	@Override
	public void send(String service, byte[] payload) throws IOException {
		if (!isConnected()) {
			connect(); // won't object if this fails.
		}
		
		try {
			this.client.publish(service, payload, params.getPublishQos(), params.isPublishRetain());
		} catch (MqttException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void send(String service, byte[] payload, boolean retain) throws IOException {
		if (!isConnected()) {
			connect(); // won't object if this fails.
		}

		try {
			this.client.publish(service, payload, params.getPublishQos(), retain);
		} catch (MqttException e) {
			throw new IOException(e);
		}
	}

	/**
	 * 
	 */
	@Override
	public boolean poll() throws IOException {
		return !params.isAsyncCallbacks() && cbHandler.processPendingIncomingMessages() > 0;
	}

	/**
	 * 
	 */
	@Override
	protected void handleSubscribe(final String topic, final Callback cb) throws IOException {
		try {
			client.subscribe(topic);
			client.setCallback(cbHandler);
		} catch (MqttException e) {
			throw new IOException(e);
		}

	}

	/**
	 * 
	 */
	@Override
	protected void handleUnsubscribe(String topic, Callback cb) throws IOException {
		if (cb == null || getRegisteredCallbacks(topic).size() == 1) {
			try {
				client.unsubscribe(topic);
			} catch (MqttException e) {
				throw new IOException(e);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private MqttConnectOptions buildConnectOptions() {
		MqttConnectOptions opts = new MqttConnectOptions();
		
		if (params.getUsername() != null) {
			opts.setUserName(params.getUsername());
		}
		
		if (params.getPassword() != null) {
			opts.setPassword(params.getPassword().toCharArray());
		}

		return opts;
	}	
}
