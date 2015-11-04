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
 */
public class Protocol_IOT_v1 implements Protocol {	
	protected Transport  transport;
	protected DeviceInfo deviceInfo;

	public Protocol_IOT_v1(DeviceInfo deviceInfo, Transport transport) {
		this.deviceInfo = deviceInfo;
		this.transport = transport;
	}
	
	@Override
	public void addDevice(Callback cb) throws IOException {
		String service = String.format(ServiceConstants.DEVICE, deviceInfo.getId());
		String payload = String.format(ServiceConstants.DEVICE_PAYLOAD, deviceInfo.getName(), deviceInfo.getIp().getHostAddress(), deviceInfo.getPort());

		if(cb != null) {
			String cb_service = String.format(ServiceConstants.DEVICE_CALLBACK, deviceInfo.getId());
			this.transport.subscribe(cb_service, cb);
		}

		this.transport.send(service, payload.getBytes());
	}
	
	@Override
	public void sendStatus() throws IOException {
		String service = String.format(ServiceConstants.DEVICE_STATUS, deviceInfo.getId());
		String payload = String.format(ServiceConstants.DEVICE_STATUS_PAYLOAD, true);
		transport.send(service, payload.getBytes(),true);
	}
	
	@Override
	public void addSensor(String id, String name, String type) throws IOException {
		String service = String.format(ServiceConstants.SENSOR, deviceInfo.getId(), id);
		String payload = String.format(ServiceConstants.SENSOR_PAYLOAD, name, type);
		this.transport.send(service, payload.getBytes());
	}
	
	@Override
	public void addActuator(String id, String name, String type, Callback cb) throws IOException {
		String service = String.format(ServiceConstants.ACTUATOR, deviceInfo.getId(), id);		
		String payload = String.format(ServiceConstants.ACTUATOR_PAYLOAD, name, type);
		String cb_service = String.format(ServiceConstants.ACTUATOR_CALLBACK, deviceInfo.getId(), id);
		
		this.transport.subscribe(cb_service, cb);
		this.transport.send(service, payload.getBytes());
	}
		
	@Override
	public void sendData(String sensorId, byte[] value) throws IOException {
		String service = String.format(ServiceConstants.SENSOR_DATA, deviceInfo.getId(), sensorId);		
		String payload = String.format(ServiceConstants.SENSOR_DATA_PAYLOAD, new String(value));
		this.transport.send(service, payload.getBytes());
	}
}
