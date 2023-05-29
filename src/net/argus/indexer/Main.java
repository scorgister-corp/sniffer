package net.argus.indexer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

public class Main {
	
	public static void main(String[] args) throws IOException {
		boolean list = true;
		boolean index = false;
		
		String inter = "lo";
		
		for(int i = 0; i < args.length; i++) {
			String str = args[i];
			
			if(str.toLowerCase().equals("-l") || str.toLowerCase().equals("--list")) {
				list = true;
				index = false;
			}
			
			if(str.toLowerCase().equals("-i") || str.toLowerCase().equals("--index")) {
				list = false;
				index = true;
				
				inter = args[i+1];
			}
		}
		
		if(list) {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                
                if (networkInterface.isUp()) {
                    System.out.println("Interface: " + networkInterface.getName());
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        
                        if (address instanceof Inet4Address)
                            System.out.println("IPv4 Address: " + address.getHostAddress());
                    }
                    
                    System.out.println();
                }
            }
		}
		if(index) {
			NetworkInterface net = NetworkInterface.getByName(inter);
			if(net == null) {
				System.err.println("NetworkInterface unknown");
				return;
			}
			for(InterfaceAddress ia : net.getInterfaceAddresses()) {
				if(ia.getAddress() instanceof Inet6Address)
					continue;
				
				System.out.println("Starting sniff on " + net.getName() + ": " + Tester.getNetworkIP(ia.getAddress().getHostAddress(), ia.getNetworkPrefixLength()) + "/" + ia.getNetworkPrefixLength());
				List<String> ips = Tester.getIps(ia.getAddress().getHostAddress(), ia.getNetworkPrefixLength());
				Tester.start(ips, 16);
			}
		}

	}

}
