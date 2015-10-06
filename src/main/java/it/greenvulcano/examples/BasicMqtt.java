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

package it.greenvulcano.examples;

import it.greenvulcano.iot.Callback;
import it.greenvulcano.iot.DeviceInfo;
import it.greenvulcano.iot.GVComm;
import it.greenvulcano.iot.protocols.Protocol_IOT_v1;
import it.greenvulcano.iot.transports.mqtt.ConnectionParams;
import it.greenvulcano.iot.transports.mqtt.MqttTransport;

import java.io.IOException;
import java.net.InetAddress;

public class BasicMqtt {
	public static final int MODE_STOP = 0;
	public static final int MODE_RUN = 1;	
	private int current_mode;
	
	public BasicMqtt() {
		this.current_mode = MODE_STOP;
	}
	
	public int getMode() {
		return current_mode;
	}
	
	/* Actuator callback */
	public class CallbackActuator implements Callback {
		private String id;
		
		public CallbackActuator(String id) {
			this.id = id;
		}
		
		@Override
		public Callback call(Object value) {
			System.out.println("ACTUATOR " + this.id + " CALLBACK CALLED: " + new String((byte[])value));
			return null;
		}
	}
	
	/* Device callback */
	public class CallbackDevice implements Callback {
		@Override
		public Callback call(Object value) {
			int pValue = -1;
			
			try {
				pValue = Integer.parseInt(new String((byte[])value));
								
				if(pValue == 0) {
					System.out.println("CURRENT MODE STOP");
					current_mode = MODE_STOP;
				}
				else if(pValue == 1) {
					System.out.println("CURRENT MODE RUN");
					current_mode = MODE_RUN;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	/* Test */
	public static void main(String[] args) throws IOException {		
		/* Info about the device */
		final String device_id = "GVDEV999";
		final String device_name = "Test";
		byte[] device_ip = {10, 100, 80, 22};
		InetAddress ipDevice = InetAddress.getByAddress(device_ip);
		int dPort = 9999;
		
		final String SENSOR1_ID = "SED99901";
		final String ACTUATOR1_ID = "ACD99901";
		final String ACTUATOR2_ID = "ACD99902";
		
		/* Info about mqtt server */
		byte[] ipSer = new byte[]{10, 100, 60, 103};
		InetAddress ipServer = InetAddress.getByAddress(ipSer);
		int sPort = 1883;
		
		try {			
			BasicMqtt bmqtt = new BasicMqtt();
			Callback cbActuator1 = bmqtt.new CallbackActuator(ACTUATOR1_ID);
			Callback cbActuator2 = bmqtt.new CallbackActuator(ACTUATOR2_ID);
			Callback cbDevice = bmqtt.new CallbackDevice(); 
			
			/* Creating a new device... */
			DeviceInfo device = new DeviceInfo(device_id, device_name, ipDevice, dPort);
		
			/* ...a transport with his connection parameters... */
			ConnectionParams connectionParam = new ConnectionParams(device, ipServer, sPort);
			MqttTransport mqttTransport = new MqttTransport(connectionParam);
				
			/* ...and a protocol */
			Protocol_IOT_v1 protocol = new Protocol_IOT_v1(device, mqttTransport);
		
			/* Use the GVComm to connect transport with protocol... */
			GVComm gvComm = new GVComm(mqttTransport, protocol);
					
			/* ... and send device, sensors and actuators info to the server */
			gvComm.addDevice(cbDevice);
			gvComm.addSensor(SENSOR1_ID, "Sensor1 Test", "NUMERIC");
			gvComm.addActuator(ACTUATOR1_ID, "Actuator1 Test", "NUMERIC", cbActuator1);
			gvComm.addActuator(ACTUATOR2_ID, "Actuator2 Test", "NUMERIC", cbActuator2);
			
			System.out.println("Configuration Done.");
			
			Thread.sleep(2000);
		
			while (true) {
				gvComm.poll();
				
				if(bmqtt.getMode() == MODE_RUN) {
					System.out.println("RUN: " + bmqtt.getMode());
					/* Simulate a sensor */
					String value = Integer.toString((int)(Math.random() * 100));
					
					Thread.sleep(250);
				}

			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
