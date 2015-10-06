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

import it.greenvulcano.iot.DeviceInfo;
import it.greenvulcano.iot.transports.Transport;

public enum ProtocolFactory {	
	INSTANCE;
	
	static ProtocolFactory getInstance() { return INSTANCE; }
	
	Protocol createDefaultProtocol(DeviceInfo info, Transport transport) {
		return new Protocol_IOT_v1(info, transport);
	}
}
