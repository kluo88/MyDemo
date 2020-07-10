package com.itkluo.demo.usb.wdreader.readerlib;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

/**
 * Used to convert to hex from byte arrays and back.
 * 
 * 
 */
public final class HexSupport {

	private static final String[] HEX_TABLE = new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08",
			"09", "0A", "0B", "0C", "0D", "0E", "0F", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1A",
			"1B", "1C", "1D", "1E", "1F", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2A", "2B", "2C",
			"2D", "2E", "2F", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E",
			"3F", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D", "4E", "4F", "50",
			"51", "52", "53", "54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F", "60", "61", "62",
			"63", "64", "65", "66", "67", "68", "69", "6A", "6B", "6C", "6D", "6E", "6F", "70", "71", "72", "73", "74",
			"75", "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E", "7F", "80", "81", "82", "83", "84", "85", "86",
			"87", "88", "89", "8A", "8B", "8C", "8D", "8E", "8F", "90", "91", "92", "93", "94", "95", "96", "97", "98",
			"99", "9A", "9B", "9C", "9D", "9E", "9F", "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA",
			"AB", "AC", "AD", "AE", "AF", "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "BA", "BB", "BC",
			"BD", "BE", "BF", "C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB", "CC", "CD", "CE",
			"CF", "D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF", "E0",
			"E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED", "EE", "EF", "F0", "F1", "F2",
			"F3", "F4", "F5", "F6", "F7", "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF" };
	private static final int[] INT_OFFSETS = new int[] { 24, 16, 8, 0 };

	private HexSupport() {
	}

	/**
	 * hex字符转换成byte[]
	 * @param hex
	 * @return
	 */
	public static byte[] toBytesFromHex(String hex) {
		if (hex == null || hex.isEmpty()) {
			return null;
		}
		if (hex.length() % 2 != 0) {
			throw new RuntimeException("Hex string length must be even!");
		}
		int len = hex.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * 转换byte[]为hex字符
	 * @param bytes
	 * @return
	 */
	public static String toHexFromBytes(byte[] bytes) {
		if (null == bytes || bytes.length == 0) {
			return "";
		}
		StringBuffer rc = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			rc.append(HEX_TABLE[0xFF & bytes[i]]);
		}
		return rc.toString();
	}

	/**
	 * 单个byte转为hex字符
	 * @param byteArg
	 * @return
	 */
	public static String toHexFromByte(byte byteArg) {
		return HEX_TABLE[0xFF & byteArg];
	}

	/**
	 * 将整形转换成hex字符（trim为true前面的00会被删除）
	 * @param value
	 * @param trim
	 *            if the leading 0's should be trimmed off.
	 * @return
	 */
	public static String toHexFromInt(int value, boolean trim) {
		StringBuffer rc = new StringBuffer(INT_OFFSETS.length * 2);
		for (int i = 0; i < INT_OFFSETS.length; i++) {
			int b = 0xFF & (value >> INT_OFFSETS[i]);
			if (!(trim && b == 0)) {
				rc.append(HEX_TABLE[b]);
				trim = false;
			}
		}
		return rc.toString();
	}

	/**
	 * 将整形转换成2个byte
	 * @param i
	 * @return
	 */
	public static byte[] intToBytes2(int i) {
		byte[] abyte = new byte[2];
		abyte[1] = (byte) (0xff & i);
		abyte[0] = (byte) ((0xff00 & i) >> 8);
		return abyte;
	}

	/**
	 * 将整形转换成2个byte的hex字符
	 * @param i
	 * @return
	 */
	public static String toHex2FromInt(int i) {
		return HexSupport.toHexFromBytes(intToBytes2(i));
	}

	/**
	 * 将整形转换成4个byte
	 * @param i
	 * @return
	 */
	public static byte[] intToBytes4(int i) {
		byte[] abyte = new byte[4];
		abyte[3] = (byte) (0xff & i);
		abyte[2] = (byte) ((0xff00 & i) >> 8);
		abyte[1] = (byte) ((0xff0000 & i) >> 16);
		abyte[0] = (byte) ((0xff000000 & i) >> 24);
		return abyte;
	}

	/**
	 * 将整形转换成4个byte的hex字符
	 * @param i
	 * @return
	 */
	public static String toHex4FromInt(int i) {
		return HexSupport.toHexFromBytes(intToBytes4(i));
	}

	/**
	 * 将2个byte的hex字符转换成整形（最大FFFF-65535）
	 * @param hexString
	 * @return
	 */
	public static int toIntFromHex2(String hexString) {
		if (hexString == null || hexString.length() != 4) {
			throw new IllegalArgumentException("hexString len is 4!");
		}
		byte[] tmp = HexSupport.toBytesFromHex(hexString);
		return ((0xff & tmp[0]) << 8) | ((0xff & tmp[1]));
	}
	
	/**
	 * 将4个byte的hex字符转换成整形（最大FFFFFFFF,--存在负值）
	 * @param hexString
	 * @return
	 */
	public static int toIntFromHex4(String hexString) {
		if (hexString == null || hexString.length() != 8) {
			throw new IllegalArgumentException("hexString len is 8!");
		}
		try {
			ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(HexSupport.toBytesFromHex(hexString));
			DataInputStream inputStream = new DataInputStream(arrayInputStream);
			return inputStream.readInt();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

    /**
     * 转换为整形，最多支持4位
     * @param arg
     * @return
     */
    public static int toIntFromBytes(byte[] arg) {
        if (arg == null || arg.length == 0 || arg.length > 4) {
            throw new IllegalArgumentException("bytes len should less than 4!");
        }
        int dataResult = 0;
        int maxR = 0;
        for (int i = arg.length - 1; i >= 0; i--) {
            dataResult |= ((0xff & arg[i]) << maxR);
            maxR += 8;
        }
        return dataResult;
    }

	/**
	 * 将2个byte的hex字符转换成带符号的整形
	 * @param hexString
	 * @return
	 */
	public static int toSignedIntFromHex2(String hexString) {
		if (hexString == null || hexString.length() != 4) {
			throw new IllegalArgumentException("hexString len is 4!");
		}
		byte[] tmp = HexSupport.toBytesFromHex(hexString);
		short dataResult = (short) (((0xff & tmp[0]) << 8) | ((0xff & tmp[1])));
		return dataResult;
	}

	/**
	 * 强转byte为int
	 * @param byteData
	 * @return
	 */
	public static int toIntFromByteSigned(byte byteData) {
		return (int) byteData;
	}

	/**
	 * 强转byte为int
	 * @param byteData
	 * @return
	 */
	public static int toIntFromByte(byte byteData) {
		return (int) byteData;
	}

	/**
	 * 强转int为byte
	 * @param intData
	 * @return
	 */
	public static byte toByteFromInt(int intData) {
		return (byte) intData;
	}	

	/**
	 * long的后四位byte
	 *
	 * @author gaowei.jia 2016年3月23日  下午5:49:00
	 *
	 * @param i
	 * @return
	 */
	public static byte[] longToBytes4(long i) {
		byte[] abyte = new byte[4];
		abyte[3] = (byte) (0xff & i);
		abyte[2] = (byte) ((0xff00 & i) >> 8);
		abyte[1] = (byte) ((0xff0000 & i) >> 16);
		abyte[0] = (byte) ((0xff000000 & i) >> 24);
		return abyte;
	}
	
	/**
	 * long的后四位byte
	 *
	 * @author gaowei.jia 2016年3月23日  下午5:49:06
	 *
	 * @param i
	 * @return long的后四位byte
	 */
	public static String toHex4FromLong(long i) {
		return HexSupport.toHexFromBytes(longToBytes4(i));
	}

	/**
	 * 输入是长度为8的0、1字符串，返回byte值
	 * @param str
	 * @return
     */
	public static byte toByteFromBinaryStr(String str){
		if(null == str || str.length()!=8){
			return 0;
		}
		int tempInt = 0;
		for(int i=0;i<str.length();i++){
			String temp = str.substring(i,i+1);
			if("1".equals(temp)){
				tempInt = (int)(tempInt+ Math.pow((double)2, (double) (7-i)));
			}
		}
		return (byte)tempInt;
	}

}
