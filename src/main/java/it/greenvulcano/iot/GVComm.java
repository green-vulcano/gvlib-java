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
package it.greenvulcano.iot;

import it.greenvulcano.iot.protocols.Protocol;
import it.greenvulcano.iot.transports.Transport;

import java.io.IOException;

/**
 * Main entry point for the GreenVulcano Communication Library.
 * Construct a <code>GVComm</code> object passing the relevant objects
 * to its constructor, then manage every aspect of the library through
 * this object.
 * 
 * @author Domenico Barra <eisenach@gmail.com>
 */
public class GVComm {

	public GVComm(DeviceInfo deviceInfo, Transport transport, Protocol protocol) {
		this.deviceInfo = deviceInfo;
		this.transport = transport;
		this.protocol = protocol;
	}

	void addCallback(String topic, Callback cb) throws IOException {
		transport.subscribe(topic, cb);
	}
	
	void sendDeviceInfo() throws IOException {
		protocol.sendDeviceInfo(); 
	}
	
	void sendSensorConfig(int id, String name, String type) throws IOException {
		protocol.sendSensorConfig(id, name, type);
	}
	
	void sendActuatorConfig(int id, String name, String type,
	                        String topic, Callback fn) throws IOException {
		protocol.sendActuatorConfig(id, name, type, topic);
		if (fn != null) {
			addCallback(topic, fn);
		}
	}
	
	void sendData(int sensorId, byte[] value)  throws IOException
	{
		protocol.sendData(sensorId, value);
	}

	boolean poll() throws IOException {
		return transport.poll();
	}
	
	private DeviceInfo deviceInfo;
	private Transport  transport;
	private Protocol   protocol;
	
	
}
