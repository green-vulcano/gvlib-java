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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import it.greenvulcano.iot.Callback;
import it.greenvulcano.iot.DeviceInfo;
import it.greenvulcano.iot.GVComm;
import it.greenvulcano.iot.protocols.Protocol_IOT_v1;
import it.greenvulcano.iot.transports.mqtt.ConnectionParams;
import it.greenvulcano.iot.transports.mqtt.MqttTransport;

public class GV_Philips_HUE_device {
	public static final int MODE_STOP = 0;
	public static final int MODE_RUN = 1;
	public static final int MODE_DEMO = 2;
	private int current_mode;
	
	URL url;
	
	public GV_Philips_HUE_device() {
		this.current_mode = MODE_STOP;
	}
	
	public int getMode() {
		return current_mode;
	}
	
	/* Device callback */
	public class CallbackDevice implements Callback {
		@Override
		public Callback call(Object value) {
			String pValue = "";
			
			try {
				pValue = new String((byte[])value);
				System.out.println("VALORE: " + pValue);
								
				if(pValue.equals("{\"value\":\"OFF\"}")) {
					System.out.println("CURRENT MODE STOP");
					current_mode = MODE_STOP;
				} else if(pValue.equals("{\"value\":\"ON\"}")) {
					System.out.println("CURRENT MODE RUN");
					current_mode = MODE_RUN;
				} else if(pValue.equals("{\"value\":\"DEMO\"}")) {
					System.out.println("CURRENT MODE DEMO");
					current_mode = MODE_DEMO;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
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
			int tidac = 0;
			
			if (this.id == "ACD00901") {
			    tidac = 1;
			} else if (this.id == "ACD00902") {
			    tidac = 2;
			} else if (this.id == "ACD00903") {
			    tidac = 3;
			}
			
			try {
				url = new URL("http://10.100.80.26/api/2466d24d2e9caa072ccf2b09882ce23/lights/" + tidac + "/state");
				
				HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
				hurl.setRequestMethod("PUT");
	            hurl.setDoOutput(true);
	            hurl.setRequestProperty("Content-Type", "application/json");
	            hurl.setRequestProperty("Accept", "application/json");
	            
	            String message = new String((byte[])value);
	            
	            OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
	            osw.write(message);
	            osw.flush();
	            osw.close();
	            
	            System.out.println(hurl.getResponseCode());
				
			} catch(MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch(Exception e) {
	        	e.printStackTrace();
	        }
			return null;
		}
	}
	
	// run
	public static void main(String[] args) throws IOException {
		
		final String device_id = "GVDEV009";
		final String device_name = "Philips HUE";
		byte[] device_ip = {10, 100, 80, 26};
		InetAddress ipDevice = InetAddress.getByAddress(device_ip);
		int dPort = 8080;
		
		final String ACTUATOR1_ID = "ACD00901";
		final String ACTUATOR2_ID = "ACD00902";
		final String ACTUATOR3_ID = "ACD00903";
		
		/* Info about mqtt server */
		byte[] ipSer = new byte[]{10, 100, 80, 39};
		InetAddress ipServer = InetAddress.getByAddress(ipSer);
		int sPort = 1883;
		
		try {			
			GV_Philips_HUE_device gvhue = new GV_Philips_HUE_device();
			Callback cbDevice = gvhue.new CallbackDevice();
			Callback cbActuator1 = gvhue.new CallbackActuator(ACTUATOR1_ID);
			Callback cbActuator2 = gvhue.new CallbackActuator(ACTUATOR2_ID);
			Callback cbActuator3 = gvhue.new CallbackActuator(ACTUATOR3_ID);
			
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
			gvComm.addActuator(ACTUATOR1_ID, "Lamp one", "NUMERIC", cbActuator1);
			gvComm.addActuator(ACTUATOR2_ID, "Lamp two", "NUMERIC", cbActuator2);
			gvComm.addActuator(ACTUATOR3_ID, "Lamp three", "NUMERIC", cbActuator3);
			
			System.out.println("Configuration Done.");
			Thread.sleep(2000);
			
			while (true) {
				gvComm.poll();
				
				if(gvhue.getMode() == MODE_RUN) {
					System.out.println("RUN: " + gvhue.getMode());
					Thread.sleep(250);
				}

			}
		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
