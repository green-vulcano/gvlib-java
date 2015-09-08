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

import java.io.IOException;
import java.net.InetAddress;

import it.greenvulcano.iot.Callback;
import it.greenvulcano.iot.DeviceInfo;
import it.greenvulcano.iot.GVComm;
import it.greenvulcano.iot.protocols.Protocol_IOT_v1;
import it.greenvulcano.iot.transports.MqttTransport;
import it.greenvulcano.iot.transports.MqttTransport.ConnectionParams;

public class BasicMqtt {

	public static void main(String[] args) throws IOException {
		
		/* Info about the device */
		String id = "GVDEV999";
		String name = "Test";
		byte[] ipDev = new byte[]{10, 100, 80, 99};
		InetAddress ipDevice = InetAddress.getByAddress(ipDev);
		int dPort = 9999;
		
		/* Info about mqtt server */
		byte[] ipSer = new byte[]{10, 100, 60, 103};
		InetAddress ipServer = InetAddress.getByAddress(ipSer);
		int sPort = 1883;
		
		/* */
		DeviceInfo device = new DeviceInfo(id, name, ipDevice, dPort);
		
		/* */
		ConnectionParams connectionParam = new ConnectionParams(device, ipServer, sPort);
		MqttTransport mqttTransport = new MqttTransport(connectionParam);
		
		/* */
		Protocol_IOT_v1 protocol = new Protocol_IOT_v1(device, mqttTransport);
		
		
		/* */
		GVComm gvComm = new GVComm(mqttTransport, protocol);
		gvComm.sendDeviceInfo();
		gvComm.sendSensorConfig("SED99901", "Sensor Test", "NUMERIC");
		gvComm.sendActuatorConfig("ACD99901", "Actuator Test", "NUMERIC");
		gvComm.addCallback("/devices/GVDEV999/actuators/ACD99901/input", cb);
		
		/* Send value from sensor to actuator*/
		while (true) {
			gvComm.poll();
			//value = getSensorValue();
			
			gvComm.sendData("SED99901", value);
		}
		
	}

}
