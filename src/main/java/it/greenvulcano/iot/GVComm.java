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
	private Transport  transport;
	private Protocol   protocol;

	/**
	 * Constructor. No special actions taken: just simple construction.
	 * 
	 * @param transport an up-and-running transport. It will be used by
	 *                  this <code>GVComm</code> instance to send and
	 *                  receive data across the network.
	 * @param protocol a protocol implementation to be used by this
	 *                 <code>GVComm</code> instance.
	 */
	public GVComm(Transport transport, Protocol protocol) {
		this.transport = transport;
		this.protocol = protocol;
	}

	/**
	 * Sends info about the current device.
	 * @throws IOException if anything goes wrong with the underlying transport.
	 */
	public void addDevice() throws IOException {
		this.addDevice(null);
	}

	/**
	 * Sends info about the current device.
	 * @throws IOException if anything goes wrong with the underlying transport.
	 * @param cb the callback function to invoke when data is received
	 */
	public void addDevice(Callback cb) throws IOException {
		protocol.addDevice(cb); 
	}

	/**
	 * Send information about the status of current device.
	 * @throws IOException if anything goes wrong with the underlying transport.
	 */
	public void sendStatus(boolean status) throws IOException {
		protocol.sendStatus(status);
	}
	
	/**
	 * Sends information about a sensor, so the server can record it for future
	 * interaction.
	 * @param id the sensor id.
	 * @param name the sensor (human-readable) name.
	 * @param type the sensor type.
	 * @throws IOException if anything goes wrong with the underlying transport.
	 */
	public void addSensor(String id, String name, String type) throws IOException {
		protocol.addSensor(id, name, type);
	}
	
	/**
	 * Sends information about an actuator, so the server can record it for future
	 * interaction.
	 * @param id the actuator id.
	 * @param name the actuator (human-readable) name.
	 * @param type the actuator type.
	 * @param cb the callback function to invoke when data is received
	 *           on the specified <code>topic</code>, or <code>null</code> if
	 *           no callback is required.
	 * @throws IOException if anything goes wrong with the underlying transport.
	 */
	public void addActuator(String id, String name, String type, Callback cb) throws IOException {
		protocol.addActuator(id, name, type, cb);
	}
	
	/**
	 * Sends data associated to a given sensor.
	 * @param sensorId the id of the sensor sending the data.
	 * @param value the value read from the sensor.
	 * @throws IOException if anything goes wrong with the underlying transport.
	 */
	public void sendData(String sensorId, byte[] value)  throws IOException	{
		protocol.sendData(sensorId, value);
	}

	/**
	 * Checks if there is any data in the input channel, waiting to be processed.
	 * If so, any pending data is dispatched to the relevant callbacks.<br/>
	 * <strong>Note:</strong> some transport implementations have an asynchronous
	 * interaction model, so this method may not have effect, unless the said
	 * transport is enabled to switch to a synchronous interaction model.
	 * 
	 * @return if anything happened as a result of this invocation (i.e. if there
	 *         was any data waiting on the wire and it was dispatched).
	 * @throws IOException if anything goes wrong with the underlying transport.
	 */
	public boolean poll() throws IOException {
		return transport.poll();
	}

}
