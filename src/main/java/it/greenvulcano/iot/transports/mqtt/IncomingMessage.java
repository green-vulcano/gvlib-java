package it.greenvulcano.iot.transports.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class IncomingMessage {
	private String topic;
	private MqttMessage message;
	
	public IncomingMessage(String topic, MqttMessage message) {
		this.topic = topic;
		this.message = message;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public MqttMessage getMessage() {
		return message;
	}

	public void setMessage(MqttMessage message) {
		this.message = message;
	}
}
