package com.fajar.entitymanagement.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ApplicationUtil {
	public static String getIpv4Address() {
		String ip = "127.0.0.1";
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				// filters out 127.0.0.1 and inactive interfaces
				if (iface.isLoopback() || !iface.isUp())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();

					// *EDIT*
					if (addr instanceof Inet4Address == false)
						continue;
					if (addr.getHostAddress().startsWith("192.168")) {
						ip = addr.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
//		    throw new RuntimeException(e); 
		}
		return ip;
	}
	
	
	public static MemoryInfo getMemoryInfo() {
		long heapSize = Runtime.getRuntime().totalMemory(); 

		// Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
		long heapMaxSize = Runtime.getRuntime().maxMemory();

		 // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
		long heapFreeSize = Runtime.getRuntime().freeMemory();
		
		return MemoryInfo.builder().heapFreeSize(heapFreeSize).heapMaxSize(heapMaxSize).heapSize(heapSize).build()
				;
	}
}
