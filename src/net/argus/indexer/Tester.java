package net.argus.indexer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Tester implements Runnable {
	
	private List<String> ips;
	
	public Tester(List<String> ips) {
		this.ips = ips;
	}
	
	@Override
	public void run() {
		for(String ip : ips) {
			try {
				if(InetAddress.getByName(ip).isReachable(1000))
					System.out.println(InetAddress.getByName(ip).getHostName());
			}catch(IOException e) {}
		}
	}
	
	public static void start(List<String> ips, int maxByThread) {
		int off = 0;
		List<String> tmp = new ArrayList<String>();
		for(int i = 0; i < ips.size(); i++) {
			tmp.add(ips.get(i));
			if(i % maxByThread == 0) {
				new Thread(new Tester(new ArrayList<String>(tmp))).start();
				tmp.clear();
			}
		}
		new Thread(new Tester(new ArrayList<String>(tmp))).start();

	}
	
	public static List<String> getIps(String yourIp, int prefixLength) throws UnknownHostException {
        InetAddress networkAddress = InetAddress.getByName(getNetworkIP(yourIp, prefixLength));
        byte[] networkBytes = networkAddress.getAddress();
        int maxAddresses = (int) Math.pow(2, (32 - prefixLength));
        
        List<String> ipAddresses = new ArrayList<>();
        for (int i = 1; i < maxAddresses - 1; i++) {
            byte[] currentIPBytes = new byte[networkBytes.length];
            for (int j = 0; j < networkBytes.length; j++) {
                currentIPBytes[j] = (byte) (networkBytes[j] | (i >> (24 - (j * 8))));
            }
            InetAddress currentIP = InetAddress.getByAddress(currentIPBytes);
            String ipAddress = currentIP.getHostAddress();
            ipAddresses.add(ipAddress);
        }
        
        return ipAddresses;
	}
	
	public static String getNetworkIP(String ipAddress, int prefixLength) throws UnknownHostException {
		 InetAddress ip = InetAddress.getByName(ipAddress);
         InetAddress mask = InetAddress.getByName(getSubnetMask(prefixLength));

         byte[] ipBytes = ip.getAddress();
         byte[] maskBytes = mask.getAddress();

         byte[] networkBytes = new byte[ipBytes.length];
         for (int i = 0; i < ipBytes.length; i++)
             networkBytes[i] = (byte) (ipBytes[i] & maskBytes[i]);

         return InetAddress.getByAddress(networkBytes).getHostAddress();
	}
	
	public static String getSubnetMask(int prefixLength) throws UnknownHostException {
		if (prefixLength < 0 || prefixLength > 32) {
            throw new IllegalArgumentException("Invalid prefix length");
        }

        int mask = 0xffffffff << (32 - prefixLength);

        byte[] maskBytes = new byte[]{
                (byte) ((mask >> 24) & 0xff),
                (byte) ((mask >> 16) & 0xff),
                (byte) ((mask >> 8) & 0xff),
                (byte) (mask & 0xff)
        };
        
        return InetAddress.getByAddress(maskBytes).getHostAddress();
		
	}

}
