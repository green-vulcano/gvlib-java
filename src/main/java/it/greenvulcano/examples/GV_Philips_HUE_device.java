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

import org.json.JSONObject;

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
		this.current_mode = MODE_RUN;
	}
	
	public int getMode() {
		return current_mode;
	}
	
	
	/* 
	 * Device callback 
	 */
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
					
					// rest call on group lights
					url = new URL("http://10.100.80.26/api/2466d24d2e9caa072ccf2b09882ce23/groups/1/action");
					
					HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
					hurl.setRequestMethod("PUT");
		            hurl.setDoOutput(true);
		            hurl.setRequestProperty("Content-Type", "application/json");
		            hurl.setRequestProperty("Accept", "application/json");
		            
		            // Create a jsonObject message 
		            JSONObject message = new JSONObject();
		            message.put("on", false);
		            
		            System.out.println("Message to groups 1: " + message.toString());
		            
		            OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
		            osw.write(message.toString());
		            osw.flush();
		            osw.close();
		            
		            System.out.println("Response device: " + hurl.getResponseCode());
		            
					
				} else if(pValue.equals("{\"value\":\"ON\"}")) {
					System.out.println("CURRENT MODE RUN");
					current_mode = MODE_RUN;
					reset_light();
					
				} else if(pValue.equals("{\"value\":\"DEMO\"}")) {
					System.out.println("CURRENT MODE DEMO");
					current_mode = MODE_DEMO;
					demo();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	
	/*
	 *  Actuator callback 
	 */
	public class CallbackActuator implements Callback {
		private String id;
		
		public CallbackActuator(String id) {
			this.id = id;
		}
		
		@Override
		public Callback call(Object value) {
			String receivedMessage = new String((byte[])value);
			int tidac = 0;
			
			if (current_mode == 1) {
				System.out.println("ACTUATOR " + this.id + " CALLBACK CALLED: " + receivedMessage);
				
				if (this.id == "ACD00901") {
				    tidac = 1;
				} else if (this.id == "ACD00902") {
				    tidac = 2;
				} else if (this.id == "ACD00903") {
				    tidac = 3;
				}
				
				try {
					
					JSONObject obj = new JSONObject(receivedMessage);
					String col = obj.getString("value");
					float[] xy = calc(col);
					
					// rest call
					url = new URL("http://10.100.80.26/api/2466d24d2e9caa072ccf2b09882ce23/lights/" + tidac + "/state");
					
					HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
					hurl.setRequestMethod("PUT");
		            hurl.setDoOutput(true);
		            hurl.setRequestProperty("Content-Type", "application/json");
		            hurl.setRequestProperty("Accept", "application/json");
		            
		            // Create a jsonObject message 
		            JSONObject message = new JSONObject();
		            message.put("on", true);
		            message.put("sat", 255);
		            message.put("bri", 255);
		            message.put("xy", xy);
		            
		            System.out.println("Message to actuator " + this.id + ": " + message.toString());
		            
		            OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
		            osw.write(message.toString());
		            osw.flush();
		            osw.close();
		            
		            System.out.println("Response actuator " + this.id + ": " + hurl.getResponseCode());
					
				} catch(MalformedURLException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } catch(Exception e) {
		        	e.printStackTrace();
		        }
				
			} else if (current_mode == 0) {
				System.out.println("MODALITY OFF: the actuator do not response!!!");
				
			} else if (current_mode == 2) {
				System.out.println("MODALITY DEMO");
			}
			
			return null;
		}
	}
	
	/*
	 *  Conversion from Hexadecimal color to x,y color for HUE Philips
	 */
	public static float[] calc(String col) {
		
		int r = Integer.parseInt(col.substring(0,2),16);
		int g = Integer.parseInt(col.substring(2,4),16);
		int b = Integer.parseInt(col.substring(4,6),16);
		
		System.out.println("R: " + r + " G: " + g + " B: " + b);
		
		float X = r * 0.664511f + g * 0.154324f + b * 0.162028f;
		float Y = r * 0.283881f + g * 0.668433f + b * 0.047685f;
		float Z = r * 0.000088f + g * 0.072310f + b * 0.986039f;

		float x = X / (X + Y + Z);
		float y = Y / (X + Y + Z);
		
		System.out.println(x + "," + y);
		
		float[] xy = {0,0};
		xy[0] = x;
		xy[1] = y;
		
		return xy;
		
	}
	
	
	/*
	 *  Run
	 */
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
	
	/*
	 * 
	 */
	public void demo() {
		try {
				// rest call
				url = new URL("http://10.100.80.26/api/2466d24d2e9caa072ccf2b09882ce23/groups/1/action");
				
				System.out.println("URL: " + url.toString());
				
				HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
				hurl.setRequestMethod("PUT");
	            hurl.setDoOutput(true);
	            hurl.setRequestProperty("Content-Type", "application/json");
	            hurl.setRequestProperty("Accept", "application/json");
	            
	            // Create a jsonObject message 
	            JSONObject message = new JSONObject();
	            message.put("on", true);
	            message.put("effect", "colorloop");
	            
	            System.out.println("Message: " + message.toString());
	            
	            OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
	            osw.write(message.toString());
	            osw.flush();
	            osw.close();
	            
	            System.out.println("Response demo modality: " + hurl.getResponseCode());
			
		} catch(MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
        	e.printStackTrace();
        }
		
	}
	
	/*
	 * 
	 */
	public void reset_light() {
		try {
				// rest call
				URL url1 = new URL("http://10.100.80.26/api/2466d24d2e9caa072ccf2b09882ce23/lights/1/state");
				URL url2 = new URL("http://10.100.80.26/api/2466d24d2e9caa072ccf2b09882ce23/lights/2/state");
				URL url3 = new URL("http://10.100.80.26/api/2466d24d2e9caa072ccf2b09882ce23/lights/3/state");
	            
				int hue1 = 25550;
				int hue2 = 46920;
				int hue3 = 56100;
				
				
	            // Create a jsonObject message 
	            JSONObject message1 = new JSONObject();
	            message1.put("on", true);
	            message1.put("sat", 255);
	            message1.put("bri", 255);
	            message1.put("hue", hue1);
	            message1.put("effect","none");
	            
	            // Create a jsonObject message 
	            JSONObject message2 = new JSONObject();
	            message2.put("on", true);
	            message2.put("sat", 255);
	            message2.put("bri", 255);
	            message2.put("hue", hue2);
	            message2.put("effect","none");
	            
	            // Create a jsonObject message 
	            JSONObject message3 = new JSONObject();
	            message3.put("on", true);
	            message3.put("sat", 255);
	            message3.put("bri", 255);
	            message3.put("hue", hue3);
	            message3.put("effect","none");
	            
	            put_call(url1,message1.toString());
	            put_call(url2,message2.toString());
	            put_call(url3,message3.toString());
	            
	            
			
		} catch(MalformedURLException e) {
            e.printStackTrace();
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	/*
	 *  Generic put call
	 */
	public void put_call(URL url, String message){
		
		try {
			HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
			hurl.setRequestMethod("PUT");
	        hurl.setDoOutput(true);
	        hurl.setRequestProperty("Content-Type", "application/json");
	        hurl.setRequestProperty("Accept", "application/json");
	        
	        OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
	        osw.write(message.toString());
	        osw.flush();
	        osw.close();
	        
	        System.out.println("Response demo modality: " + hurl.getResponseCode());
	        
		} catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}
}
