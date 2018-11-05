package com.axeac.android.sdk.tools;

public class RSA {

	public static String encode(String message, String publicKey, String module, int len) {
		return encode(message.getBytes(), publicKey, module, len);
	}

	public static String encode(byte[] message, String publicKey, String module, int len) {
		return encode(message, new BigInteger(publicKey, 16), new BigInteger(module, 16), len);
	}

	public static String encode(byte[] message, BigInteger publicKey, BigInteger module, int len) {
		// Replace Chinese with UTF-8 encoding
		// 将中文换成UTF-8编码
		byte[] b = StringUtil.toUTF(new String(message)).getBytes();
		int t = (len - 1) / 8;
		int dab = (b.length - 1) / t + 1, iab = 0;
		byte[][] data = new byte[dab][];
		int i, j;
		for (i = 0; i < dab - 1; i++) {
			data[i] = new byte[t];
			for (j = 0; j < t; j++, iab++) {
				data[i][j] = b[iab];
			}
		}
		i = dab - 1;
		data[i] = new byte[b.length - iab];
		for (j = 0; j < data[i].length; j++, iab++) {
			data[i][j] = b[iab];
		}
		BigInteger[] encodingM = new BigInteger[dab];
		for (i = 0; i < encodingM.length; i++) {
			encodingM[i] = new BigInteger(data[i]);
			encodingM[i] = encodingM[i].modPow(publicKey, module);
		}
		StringBuffer sb = new StringBuffer();
		for (i = 0; i < encodingM.length; i++) {
			sb.append(encodingM[i].toString(16));
			sb.append("\r\n");
		}
		return sb.toString();
	}

	public static String decode(byte[] message, String privateKey, String module) {
		return decode(new String(message), privateKey, module);
	}

	public static String decode(String message, String privateKey, String module) {
		return decode(message, new BigInteger(privateKey, 16), new BigInteger(module, 16));
	}

	public static String decode(String message, BigInteger privateKey, BigInteger module) {
		String[] tmp = StringUtil.split(message, "\r\n");
		int i;
		StringBuffer sb = new StringBuffer();
		for (i = 0; i < tmp.length; i++) {
			sb.append(new String(new BigInteger(tmp[i], 16).modPow(privateKey, module).toByteArray()));
		}
		return StringUtil.toGB2312(sb.toString());
	}
}