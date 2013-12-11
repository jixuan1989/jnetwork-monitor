package cn.edu.thu.hxd.net;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;

public class Test {

	/**
	 * @param args
	 * @throws SocketException 
	 */
	public static void main(String[] args) throws SocketException {
		// TODO Auto-generated method stub
		//		Enumeration<NetworkInterface> ifs=NetworkInterface.getNetworkInterfaces();
		//		while(ifs.hasMoreElements()){
		//			NetworkInterface interface1=ifs.nextElement();
		//			Enumeration<InetAddress> ips=interface1.getInetAddresses();
		////			while(ips.hasMoreElements()){
		////				System.out.println(ips.nextElement());
		////			}
		////			System.out.println("--");
		////			for(InterfaceAddress address:interface1.getInterfaceAddresses()){
		////				System.out.println(address);
		////			}
		//			System.out.println(interface1);
		////			System.err.println(Charset.defaultCharset());
		//			byte[] bs=interface1.getDisplayName().getBytes(Charset.defaultCharset());
		//			System.out.println(bs);
		//			System.out.println("------------------");
		//		}
		NetworkInterface interface1=NetworkInterface.getByIndex(1);
		System.out.println(interface1.getDisplayName());
		byte[]bs1= interface1.getDisplayName().getBytes(Charset.defaultCharset());
		interface1=NetworkInterface.getByIndex(12);
		System.out.println(interface1.getDisplayName());
		byte[]bs2= interface1.getDisplayName().getBytes(Charset.defaultCharset());	
		for(int i=0;i<bs1.length;i++){
			System.out.print(bs1[i]+"\t");
		}
		System.out.println();
		
		
		byte[]bs =new byte[]{-49,
		           -75,
		           -61,
		           -127,
		           -48,
		           -65,
		           -61,
		           -104,
		           -61,
		           -106,
		           -61,
		           -122,
		           -61,
		           -122,
		           -61,
		           -73,};
		
		
		
		System.out.println();
		for(byte b:bs2){
			String string=Integer.toBinaryString(b);
			int a=string.length();
			if(a<=8){
				System.out.print(string+"\t");
			}else{ 
				System.out.print(string.substring(string.length()-8,string.length()-4)+"\t");
				System.out.print(string.substring(string.length()-4,string.length())+"\t");
			}
		}
		System.out.println();
		for (int i = 0; i < bs2.length; i++) { 
			String hex = Integer.toHexString(bs2[i]); 
			if (hex.length() == 1) { 
			hex = '0' + hex; 
			} 
			System.out.print(hex.toUpperCase() ); 
			} 

	}
//当开始出现中文的时候，就乱码了。似乎第一个中文的前四位bit 被放到末尾去了。
	
}
