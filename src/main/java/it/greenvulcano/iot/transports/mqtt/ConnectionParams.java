package it.greenvulcano.iot.transports.mqtt;

import it.greenvulcano.iot.DeviceInfo;

import java.net.InetAddress;

public class ConnectionParams {
	private DeviceInfo  deviceInfo;
	private InetAddress server;
	private int         port;
	private String      username;
	private String      password;
	private int         publishQos     = 1;
	private boolean     publishRetain  = false;
	private boolean     asyncCallbacks = true;
	private boolean     autoconnect    = true;

	public ConnectionParams(DeviceInfo deviceInfo, InetAddress server, int port) {
		this.deviceInfo = deviceInfo;
		this.server = server;
		this.port = port;
	}

	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public InetAddress getServer() {
		return server;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public int getPublishQos() {
		return publishQos;
	}

	public boolean isPublishRetain() {
		return publishRetain;
	}

	public boolean isAsyncCallbacks() {
		return asyncCallbacks;
	}

	public boolean isAutoconnect() {
		return autoconnect;
	}

	public ConnectionParams setUsername(String username) {
		this.username = username;
		return this;
	}
	
	public ConnectionParams setPassword(String password) {
		this.password = password;
		return this;
	}

	public ConnectionParams setPublishQos(int publishQos) {
		this.publishQos = publishQos;
		return this;
	}
	
	public ConnectionParams setPublishRetain(boolean publishRetain) {
		this.publishRetain = publishRetain;
		return this;
	}
	
	public ConnectionParams setAsyncCallbacks(boolean asyncCallbacks) {
		this.asyncCallbacks = asyncCallbacks;
		return this;
	}

	public ConnectionParams setAutoconnect(boolean autoconnect) {
		this.autoconnect = autoconnect;
		return this;
	}
}