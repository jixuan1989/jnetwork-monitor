package cn.edu.thu.hxd.net;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Enumeration<NetworkInterface> ifs=NetworkInterface.getNetworkInterfaces();
		while(ifs.hasMoreElements()){
			NetworkInterface interface1=ifs.nextElement();
			Enumeration<InetAddress> ips=interface1.getInetAddresses();
			while(ips.hasMoreElements()){
				System.out.println(ips.nextElement());
			}
			System.out.println("--");
			for(InterfaceAddress address:interface1.getInterfaceAddresses()){
				System.out.println(address);
			}
			System.out.println(interface1);
			//			System.err.println(Charset.defaultCharset());
			byte[] bs=interface1.getDisplayName().getBytes(Charset.defaultCharset());
			System.out.println(bs);
			System.out.println("------------------");
		}


		//		NetworkInterface interface1=NetworkInterface.getByIndex(1);
		//		System.out.println(interface1.getDisplayName());
		//		byte[]bs1= interface1.getDisplayName().getBytes(Charset.defaultCharset());
		//		interface1=NetworkInterface.getByIndex(12);
		//		System.out.println(interface1.getDisplayName());
		//		byte[]bs2= interface1.getDisplayName().getBytes(Charset.defaultCharset());	
		//		for(int i=0;i<bs1.length;i++){
		//			System.out.print(bs1[i]+"\t");
		//		}
		//		System.out.println();
		//		
		//		
		byte[]bs =new byte[]{	
				-49,
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
		byte tmp=(byte) (bs[bs.length-1]& 0x0F);
		System.out.println(tmp);
		byte tmp2;
		System.out.println("初始二进制:\t");
		printBin(bs);
		for(int i=0;i<bs.length;i++){
			tmp2=(byte) (bs[i]& 0x0F);
			bs[i]=(byte) (( (tmp<<4)| 0xF) & (bs[i]>>4));
			tmp=tmp2;
		}
		System.out.println("位移好的二进制:\t");
		printBin(bs);
		//		for(Map.Entry<String,Charset> entry: Charset.availableCharsets().entrySet()){
		//			System.out.println(entry.getKey()+"\t:\t"+entry.getValue().displayName());
		//		}

		System.out.println(new String(bs,Charset.forName("UTF-8")));
		String correct="系列控制器";
		byte[] bc=correct.getBytes();
		System.out.println("utf8的二进制:\t");
		printBin(bc);
		System.out.println(readUTF(bc));
		//test2();

		
		byte[] unicode=new byte[]{0x7c, (byte) 0xfb, 
									0x52, 0x17,
									0x63,(byte) 0xa7, 
									0x52,0x36,
									0x56,0x68};
		byte[]test=unicodeBytes2utf8Bytes(unicode);
		printBin(test);
	}
	//当开始出现中文的时候，就乱码了。似乎第一个中文的前四位bit 被放到末尾去了。
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
	 * @param theString
	 * @return String
	 */
	public static String unicodeToUtf8(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}
	/**
	 * 根据传入的UTF-8类型的字节数组生成Unicode字符串
	 * @param      UTF-8类型的字节数组
	 * @return     Unicode字符串
	 * @exception  IOException           产生IO异常
	 * @exception  UTFDataFormatException  传入了非UTF-8类型的字节数组
	 */
	public final static String readUTF(byte[] data) throws IOException {
		int utflen = data.length;
		StringBuffer str = new StringBuffer(utflen);
		byte bytearr[] = data;
		int c, char2, char3;
		int count = 0;

		while (count < utflen) {
			c = (int) bytearr[count] & 0xff;
			switch (c >> 4) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				/* 0xxxxxxx*/
				count++;
				str.append( (char) c);
				break;
			case 12:
			case 13:

				/* 110x xxxx   10xx xxxx*/
				count += 2;
				if (count > utflen) {
					throw new UTFDataFormatException(
							"UTF Data Format Exception");
				}
				char2 = (int) bytearr[count - 1];
				if ( (char2 & 0xC0) != 0x80) {
					throw new UTFDataFormatException();
				}
				str.append( (char) ( ( (c & 0x1F) << 6) | (char2 & 0x3F)));
				break;
			case 14:

				/* 1110 xxxx  10xx xxxx  10xx xxxx */
				count += 3;
				if (count > utflen) {
					throw new UTFDataFormatException(
							"UTF Data Format Exception");
				}
				char2 = (int) bytearr[count - 2];
				char3 = (int) bytearr[count - 1];
				if ( ( (char2 & 0xC0) != 0x80) || ( (char3 & 0xC0) != 0x80)) {
					throw new UTFDataFormatException();
				}
				str.append( (char) ( ( (c & 0x0F) << 12)
						| ( (char2 & 0x3F) << 6) | ( (char3 & 0x3F) << 0)));
				break;
			default:

				/* 10xx xxxx,  1111 xxxx */
				throw new UTFDataFormatException(
						"UTF Data Format Exception");
			}
		}
		// The number of chars produced may be less than utflen
		return new String(str);
	}
	public static void test2() throws UnsupportedEncodingException{
		byte[][] bytes = { 
				// 00110001 
				{(byte)0x31}, 
				// 11000000 10110001 
				{(byte)0xC0,(byte)0xB1}, 
				// 11100000 10000000 10110001 
				{(byte)0xE0,(byte)0x80,(byte)0xB1}, 
				// 11110000 10000000 10000000 10110001 
				{(byte)0xF0,(byte)0x80,(byte)0x80,(byte)0xB1}, 
				// 11111000 10000000 10000000 10000000 10110001 
				{(byte)0xF8,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0xB1}, 
				// 11111100 10000000 10000000 10000000 10000000 10110001 
				{(byte)0xFC,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0xB1}, 
		}; 
		for (int i = 0; i < 6; i++) { 
			String str = new String(bytes[i], "UTF-8"); 
			System.out.println("原数组长度：" + bytes[i].length + 
					"\t转换为字符串：" + str + 
					"\t转回后数组长度：" + str.getBytes("UTF-8").length); 
			System.out.print("原二进制：\t");
			printBin(bytes[i]);
			System.out.print("新二进制：\t");
			printBin(str.getBytes("UTF-8"));
		} 
	}
	
	
	static int[] split=new int[]{0x80,0x800,0x10000};
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
	 */
	
	public static byte[] unicodeBytes2utf8Bytes(byte[] ubs){
		//先把两个字节的unicode改成4个字节的。。好坑啊。 目前似乎前两字节都为0 所以被省略了（似乎如此）
		byte[] newubs=new byte[ubs.length*2];
		for(int i=0,j=0;i<ubs.length;i+=2,j+=4){
			newubs[j]=0;
			newubs[j+1]=0;
			newubs[j+2]=ubs[i];
			newubs[j+3]=ubs[i+1];
		}
		List<Byte> result=new ArrayList<>();
		int next=0;
		for(int i=0;i+3<newubs.length;i+=4){
			int tmp=(newubs[i]<<24)|(newubs[i+1]<<16)|(newubs[i+2]<<8)|newubs[i+3];
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
