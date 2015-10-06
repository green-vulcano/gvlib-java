package it.greenvulcano.iot.transports.mqtt;

import it.greenvulcano.iot.protocols.Protocol_IOT_v1;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public class MqttListner implements IMqttActionListener {
	private MqttTransport transport;
	private ConnectionParams params;
	
	public MqttListner(MqttTransport transport, ConnectionParams params) {
		this.transport = transport;
		this.params = params;
	}

	@Override
	public void onSuccess(IMqttToken token) {
		try {
			Protocol_IOT_v1 prot = new Protocol_IOT_v1(this.params.getDeviceInfo(), this.transport);
			prot.sendStatus();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onFailure(IMqttToken token, Throwable reasons) {			
		// TODO
	}		
}