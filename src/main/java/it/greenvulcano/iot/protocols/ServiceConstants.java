package it.greenvulcano.iot.protocols;

public class ServiceConstants {
    private ServiceConstants() { }  // Prevents instantiation

    public static final String DEVICES = "/devices";
    public static final String DEVICE = "/devices/%s";
    public static final String DEVICE_CALLBACK = "/devices/%s/input";    
    public static final String DEVICE_STATUS = "/devices/%s/status";
    
    public static final String SENSOR = "/devices/%s/sensors/%s";
    public static final String SENSOR_DATA = "/devices/%s/sensors/%s/output";    
    
    public static final String ACTUATOR = "/devices/%s/actuators/%s";
    public static final String ACTUATOR_CALLBACK = "/devices/%s/actuators/%s/input";    
    
    public static final String DEVICE_STATUS_PAYLOAD = "{\"st\":%b}";    
    public static final String DEVICE_PAYLOAD = "{\"nm\":\"%s\", \"ip\":\"%s\", \"prt\":\"%d\"}";
    public static final String SENSOR_PAYLOAD = "{\"nm\":\"%s\", \"tp\":\"%s\"}";
    public static final String SENSOR_DATA_PAYLOAD = "{\"value\":\"%s\"}";
    public static final String ACTUATOR_PAYLOAD = "{\"nm\":\"%s\", \"tp\":\"%s\"}";
}