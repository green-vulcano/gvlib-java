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

import java.io.IOException;

/**
 * Interface to abstract transport-related details.
 * 
 * The GreenVulcano Communication Library keeps protocol and transmission
 * separated, so that either part can have multiple implementations which
 * can all be combined to obtain maximum flexibility.
 * <br/>
 * <strong>Note:</strong> By design, a <code>Transport</code> is responsible
 * for its callbacks (i.e. it manages and owns subscripton and unsubscription)
 * while other classes having callback-related methods, including
 * {@link GVComm}, will only eventually delegate all non-trivial aspects to
 * their <code>Transport</code>.
 *  
 * @see Callback
 * @author Domenico Barra <eisenach@gmail.com>
 */
public interface Transport {

	boolean isConnected();
	void    connect() throws IOException;
	void    disconnect();

	void send(String service, byte[] payload) throws IOException;
	void subscribe(String topic, Callback cb) throws IOException;
	
	/**
	 * Checks - non-blockingly - whether there's any incoming data to be processed.
	 * If so, it calls any registered callback for the incoming data.
	 * @return boolean <code>true</code> if any data was received during the poll operation.
	 * @throws IOException if anything goes wrong on the network level.
	 */
	boolean poll() throws IOException;
}
