package cn.edu.thu.hxd.net;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		byte[]bs =new byte[]{0x7c,(byte) 0xfb,0x52,0x17,0x63,(byte) 0xa7,0x52,0x36,0x56,0x68};
		byte[]utf8=unicodeBytes2utf8Bytes(bs);
		System.out.println(new String(utf8));
		Set<NetworkInterface> set=getUsefulNetworkInterfaces();
		System.out.println(set);
	}
	
	
	public  static Set<NetworkInterface> getUsefulNetworkInterfaces() throws SocketException{
		Set<NetworkInterface> list=new HashSet<NetworkInterface>();
		Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress ip = null;
		while (allNetInterfaces.hasMoreElements())
		{
			NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
//			Enumeration addresses = netInterface.getInetAddresses();
//			while (addresses.hasMoreElements())
//			{
//				ip = (InetAddress) addresses.nextElement();
//				if (ip != null && ip instanceof Inet4Address)
//				{
//					System.out.println("本机的IP = " + ip.getHostAddress());
//					list.add(netInterface);
//				} 
//			}
			if(netInterface.getInterfaceAddresses().size()>0){
				list.add(netInterface);
				System.out.println("本机的IP = " + netInterface.getInterfaceAddresses().get(0).getAddress());
				if(netInterface.getInterfaceAddresses().get(0).getAddress() instanceof Inet6Address && netInterface.getInterfaceAddresses().size()>1){
					for(InterfaceAddress inetAddress:netInterface.getInterfaceAddresses()){
						if(inetAddress.getAddress() instanceof Inet4Address){
							System.out.println("本机另一个IP = " + inetAddress.getAddress());
						}
					}
				}
				System.out.println(netInterface.getName()+"\t"+netInterface.isUp()+"\t"+netInterface.isVirtual());
				System.out.println("---");
			}
		}
		return list;
	}
	
	
	//将一个byte数组打印成二进制模式
	public static void printBin(byte[] bs2){
		for(byte b:bs2){
			String string=Integer.toBinaryString(b);
			int a=string.length();
			if(a<=8){
				int zero=8-a;
				StringBuilder zStringBuilder=new StringBuilder(zero);
				for(int i=0;i<zero;i++){
					zStringBuilder.append("0");
				}
				string=zStringBuilder+string;
			}
			System.out.print(string.substring(string.length()-8,string.length()-4)+"\t");
			System.out.print(string.substring(string.length()-4,string.length())+"\t");
		}
		System.out.println();
	}

	

	
	
	/**
	 * 
	 * 
Unicode符号范围 | UTF-8编码方式
(十六进制) | （二进制）
--------------------+---------------------------------------------
0000 0000-0000 007F | 0xxxxxxx
0000 0080-0000 07FF | 110xxxxx 10xxxxxx
0000 0800-0000 FFFF | 1110xxxx 10xxxxxx 10xxxxxx
0001 0000-0010 FFFF | 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
	 * @param ubs
	 * @return
	 */static int[] split=new int[]{0x80,0x800,0x10000};
	public static byte[] unicodeBytes2utf8Bytes(byte[] ubs){
		//先把两个字节的unicode改成4个字节的。。好坑啊。 目前似乎前两字节都为0 所以被省略了（似乎如此）
		byte[] newubs=new byte[ubs.length*2];
		for(int i=0,j=0;i<ubs.length;i+=2,j+=4){
			newubs[j]=0;
			newubs[j+1]=0;
			newubs[j+2]=ubs[i];
			newubs[j+3]=ubs[i+1];
		}
		List<Byte> result=new ArrayList<Byte>();
		int next=0;
		for(int i=0;i+3<newubs.length;i+=4){
			
			int tmp=((newubs[i]<<24)&0xFF000000)|((newubs[i+1]<<16)&0x00FF0000 )|( (newubs[i+2]<<8)&0x0000FF00 )|(newubs[i+3]&0x000000FF);
			
			if(tmp<split[0]){//保持不变
				result.add(newubs[i+3]);

			}else if(tmp<split[1]){//转成110 10开头的
				result.add((byte) (0xDF & ( (newubs[i+2]<<2 | (0x03 & (newubs[i+3]>>6)))  | 0xC0   ))); //第一个byte收纳第二个byte的高两位，并在最前面不上110
				result.add((byte)(0xBF & (newubs[i+3] | 0x80 ))); //第二个byte将前两位舍弃 换成10
			}else if(tmp<split[2]){//转换成1110 10 10 开头的
				//第一个byte的高四位成为一个byte 
				result.add((byte) ((newubs[i+2]>>4 | 0xE0) &0xEF)); 
				//第一个byte的低四位加上第二个byte的高两位
				result.add((byte) ((newubs[i+2]<<2 | (0x03 &(newubs[i+3]>>6))  | 0x80) & 0xBF));
				//第二个byte将前两位舍弃 换成10
				result.add( (byte)(0xBF & (newubs[i+3] | 0x80 )));
			}else{//转换成1111 10 10 10开头的
				//第二个byte的高六位（高三位为0） 1111
				result.add((byte) (0xF7 & (0xF0 |( newubs[i+1]>>2))));
				//第二个byte的低2位+第三个byte的高4位
				result.add( (byte) (0xBF &(0x80| (newubs[i+1]<<4 | (newubs[i+2]>>>2)))));
				//第三个byte的低四位+第四个高2位
				result.add((byte) (0xBF &( 0x80 |( newubs[i+2]<<2 | newubs[i+3]>>>6))));
				//第四个低6位
				result.add( (byte)(0xBF & (newubs[i+3] | 0x80 )));
			}

		}
		byte[] bs=new byte[result.size()];
		int i=0;
		for(Byte b:result){
			bs[i++]=b;
		}
		return bs;
	}
























}
