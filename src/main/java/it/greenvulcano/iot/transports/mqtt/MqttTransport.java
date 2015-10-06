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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * MQTT transport.
 * 
 * Using Eclipse Paho MQTT v.3.1 client
 */
public class MqttTransport extends TransportBase {
	private ConnectionParams params;
	private MqttAsyncClient client;
	private CallbackHandler cbHandler;
	private MqttListner mqttListner;
	
	/**
	 * 
	 * @param params
	 * @throws IOException
	 */
	public MqttTransport(ConnectionParams params) throws IOException {
		this.params = params;
		String host = String.format("tcp://%s:%d", params.getServer().getHostAddress(), params.getPort());
		
		this.cbHandler = new CallbackHandler(this, params);
		this.mqttListner = new MqttListner(this, params);
			
		try {
			client = new MqttAsyncClient(host, params.getDeviceInfo().getId());
			this.connect();
			
			while(!this.isConnected());
	        
		} catch (MqttException e) {
			throw new IOException(e);
		}
	}	

	/**
	 * 
	 */
	@Override
	public boolean isConnected() {
		try {
			Thread.sleep(10);
			return this.client.isConnected();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 */
	@Override
	public void connect() throws IOException {	
		try {
			this.client.connect(buildConnectOptions(), null, mqttListner);
		} catch (MqttException e) {
			throw new IOException(e);
		}	
	}
	
	/**
	 * 
	 */
	@Override
	public void disconnect() {
		try {
			if (isConnected())
				this.client.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
			// TODO: don't just swallow it, add logging
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void subscribe(final String topic, final Callback cb) throws IOException {	
		try {
            client.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
        			try {
						handleSubscribe(topic, cb);						
						client.setCallback(cbHandler);
					} catch (IOException e) {
						try {
							client.unsubscribe(topic);
						} catch (MqttException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                	System.out.println("Subscription FAILED");
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
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
	public boolean poll() throws IOException {
		if (params.isAsyncCallbacks()) {
			return false;
		}
		
		return cbHandler.processPendingIncomingMessages() > 0;
	}

	/**
	 * 
	 */
	@Override
	protected void handleSubscribe(final String topic, final Callback cb) throws IOException {			
		List<Callback> callbacks = registeredCallbacks.get(topic);
		if (callbacks == null) {
			callbacks = new ArrayList<>();
			registeredCallbacks.put(topic, callbacks);
		}
		
		callbacks.add(cb);
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
		
		opts.setWill("/devices/" + params.getDeviceInfo().getId() + "/status", "{\"st\":false}".getBytes(), 1, false);
		
		return opts;
	}	
}
