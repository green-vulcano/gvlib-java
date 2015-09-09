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
package it.greenvulcano.iot.protocols;

import java.io.IOException;

import it.greenvulcano.iot.Callback;
import it.greenvulcano.iot.DeviceInfo;
import it.greenvulcano.iot.transports.Transport;

/**
 * GreenVulcano IOT Protocol v.1
 * @author Domenico Barra <eisenach@gmail.com>
 */
public class Protocol_IOT_v1 implements Protocol {

	public Protocol_IOT_v1(DeviceInfo deviceInfo, Transport transport) {
		this.deviceInfo = deviceInfo;
		this.transport = transport;
	}
	
	@Override
	public void addDevice() throws IOException {
		String payload = String.format(
				"{\"nm\":\"%s\", \"ip\":\"%s\", \"prt\":\"%d\"}",
				deviceInfo.getName(), deviceInfo.getIp().toString(), deviceInfo.getPort());
		String service = String.format("/devices/%s", deviceInfo.getId());
		transport.send(service, payload.getBytes());
	}
	
	@Override
	public void sendStatus() throws IOException {
		String payload = String.format("{\"st\":true}");
		String service = String.format("/devices/%s/status", deviceInfo.getId());
		transport.send(service, payload.getBytes());
	}
	
	@Override
	public void addSensor(String id, String name, String type) throws IOException {
		String payload = String.format("{\"nm\":\"%s\", \"tp\":\"%s\"}", name, type);
		String service = String.format("/devices/%s/sensors/%s", deviceInfo.getId(), id);
		transport.send(service, payload.getBytes());
	}
	
	@Override
	public void addActuator(String id, String name, String type, Callback cb) throws IOException {
		String payload = String.format(
				"{\"nm\":\"%s\", \"tp\":\"%s\"}", name, type);
		String service = String.format("/devices/%s/actuators/%s", deviceInfo.getId(), id);
		transport.send(service, payload.getBytes());
	}
	
	@Override
	public void addActuator(String id, String name, String type,
			String topic) throws IOException {
		String payload = String.format(
				"{\"nm\":\"%s\", \"tp\":\"%s\", \"to\":\"%s\"}", name, type, topic);
		String service = String.format("/devices/%s/actuators/%s", deviceInfo.getId(), id);
		transport.send(service, payload.getBytes());
	}
	
	@Override
	public void sendData(String sensorId, byte[] value) throws IOException {
		String payload = String.format("{\"value\":\"%s\"}", new String(value));
		String service = String.format("/devices/%s/sensors/%s/output", deviceInfo.getId(), sensorId);
		transport.send(service, payload.getBytes());
	}
	
	protected Transport  transport;
	protected DeviceInfo deviceInfo;
}
