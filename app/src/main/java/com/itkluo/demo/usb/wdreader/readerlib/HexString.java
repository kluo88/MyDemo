package com.itkluo.demo.usb.wdreader.readerlib;



public class HexString
{
	private static byte toByte(char c)
	{
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		if(b<0)
		{
			b = (byte) "0123456789abcdef".indexOf(c);
		}
		return b;
	}
	public static byte[] hexStringToByte(String hex)
	{
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++)
		{
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}
	public static String ByteToHexString(byte[] b, int len)
	{
		String stmp = "";
		StringBuilder sb = new StringBuilder("");
		for (int n = 0; n < len; n++)
		{
			stmp = Integer.toHexString(b[n] & 0xFF);
			sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
			//sb.append(" ");
		}
		return sb.toString().toUpperCase().trim();
	}

}
