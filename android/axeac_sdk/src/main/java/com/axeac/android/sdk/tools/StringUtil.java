package com.axeac.android.sdk.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
/**
 * 字符串工具类
 * @author axeac
 * @version 1.0.0
 * */
public class StringUtil {

	public static final int BUFFER = 10240;

	private final static String ENTRY_KEY = ")@KMZQK*UBkkqqwJJ";

	public static void load(byte[] data, OutputStream out) {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		load(in, out, BUFFER, true);
	}

	public static byte[] load(InputStream in) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		load(in, bos);
		byte[] data = bos.toByteArray();
		try {
			bos.close();
			in.close();
		} catch (IOException e) {

		}
		return data;
	}

	public static void load(InputStream in, OutputStream out) {
		load(in, out, BUFFER, true);
	}

	public static void load(InputStream in, OutputStream out, boolean flag) {
		load(in, out, BUFFER, flag);
	}

	public static void load(InputStream in, OutputStream out, int buffer) {
		load(in, out, buffer, true);
	}

	public static void load(InputStream in, OutputStream out, int bufferSize, boolean close) {
		byte[] buffer = new byte[bufferSize];
		int len;
		try {
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			if (close) {
				in.close();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String[] split(String bufferstr, String regex) {
		if (bufferstr == null)
			return null;
		Vector split = new Vector();
		while (true) {
			int index = bufferstr.indexOf(regex);
			if (index == -1) {
				if (bufferstr != null && !bufferstr.equals(""))
					split.addElement(bufferstr);
				break;
			}
			split.addElement(bufferstr.substring(0, index));
			bufferstr = bufferstr.substring(index + regex.length(), bufferstr.length());
		}
		String[] s = new String[split.size()];
		split.copyInto(s);
		return s;
	}

	/**
	 * describe:Converts bytes on the network to Chinese
	 * 描述：转换网络上的字节为中文
	 */
	public static String getStringToGBK(byte[] bytes, int start) {
		byte[] rt = new byte[bytes.length - start];
		for (int i = 0; i < rt.length; i++)
			rt[i] = bytes[i + start];
		try {
			return new String(rt, "ASCII");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new String(rt);
		}
	}

	public static String toUTF(String src) {
		StringBuffer re = new StringBuffer();
		int code;
		for (int i = 0; i < src.length(); i++) {
			code = src.charAt(i);
			if (code > 255)
				re.append("\\u" + Integer.toHexString(code & 0xffff));
			else
				re.append((char) code);
		}
		String s = new String(re);
		re.delete(0, re.length());
		return s;
	}

	public static String toGB2312(String dataStr) {
		if (dataStr == null)
			return null;
		int start = 0;
		dataStr = dataStr.trim();
		int len = dataStr.length();
		final StringBuffer buffer = new StringBuffer();
		start = dataStr.indexOf("\\u");
		if (start == -1)
			return dataStr;
		if (start > 0)
			buffer.append(dataStr.substring(0, start));
		int last = 0;
		while (start < len) {
			if (start == -1) {
				break;
			}
			if (start + 6 > len) {
				buffer.append(dataStr.substring(start));
				break;
			}
			try {
				char letter = (char) Integer.parseInt(dataStr.substring(start + 2, start + 6), 16);
				buffer.append(new Character(letter).toString());
				start = start + 6;
			} catch (Exception e) {
				buffer.append("\\u");
				start += 2;
			}
			last = start;
			start = dataStr.indexOf("\\u", start);
			if (start > last)
				buffer.append(dataStr.substring(last, start));
		}
		if (last < len)
			buffer.append(dataStr.substring(last));
		String s = new String(buffer);
		buffer.delete(0, buffer.length());
		return s;
	}

	public static String replace(String src, String tar, String des) {
		StringBuffer sb = new StringBuffer();
		int pos = src.indexOf(tar);
		if (pos == -1)
			return src;
		if (pos > 0) {
			sb.append(src.substring(0, pos));
		}
		sb.append(des);
		if (pos + tar.length() < src.length()) {
			sb.append(src.substring(pos + tar.length()));
		}
		String s = new String(sb);
		sb.delete(0, sb.length());
		return s;
	}

	public static String replaceAll(String src, String tar, String des) {
		StringBuffer sb = new StringBuffer();
		int pos = src.indexOf(tar);
		if (pos == -1)
			return src;
		while (pos != -1) {
			if (pos > 0) {
				sb.append(src.substring(0, pos));
			}
			sb.append(des);
			src = src.substring(pos + tar.length());
			pos = src.indexOf(tar);
		}
		sb.append(src);
		String s = new String(sb);
		sb.delete(0, sb.length());
		return s;
	}

	public static byte[] encryptXOR(byte[] src, byte[] key) {
		byte[] tmp = new byte[src.length];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = (byte) (src[i] ^ key[i % key.length]);
		}
		return tmp;
	}

	public static String encryptXOR(String src) {
		return encryptXOR(src, ENTRY_KEY);
	}

	public static String encryptXOR(String src, String key) {
		if (src == null)
			return null;
		char[] tmp = new char[src.length()];
		for (int i = 0; i < src.length(); i++) {
			tmp[i] = (char) (src.charAt(i) ^ key.charAt(i % key.length()));
		}
		return new String(tmp);
	}

	/* Base 64 */
	public static byte[] fromBase64(String sdata) {
		if (sdata == null || sdata.length() < 2)
			return new byte[0];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int length = sdata.length();
		char data[] = new char[length + 2];
		sdata.getChars(0, length, data, 0);
		data[length] = '=';
		data[length + 1] = '=';
		int i = nextCharIndex(data, 0);
		do {
			if (i >= data.length)
				break;
			int char0 = data[i = nextCharIndex(data, i)];
			if (char0 == 61)
				break;
			int char1 = data[i = nextCharIndex(data, i + 1)];
			if (char1 == 61)
				break;
			int char2 = data[i = nextCharIndex(data, i + 1)];
			int char3 = data[i = nextCharIndex(data, i + 1)];
			i = nextCharIndex(data, i + 1);
			out.write(BASE64_BYTES[char0] << 2 | BASE64_BYTES[char1] >> 4);
			if (char2 != 61) {
				int value = BASE64_BYTES[char1] << 4 | BASE64_BYTES[char2] >> 2;
				out.write(value & 0xff);
				if (char3 != 61) {
					value = BASE64_BYTES[char2] << 6 | BASE64_BYTES[char3];
					out.write(value & 0xff);
				}
			}
		} while (true);
		return out.toByteArray();
	}

	private static int nextCharIndex(char data[], int i) {
		for (; i < data.length && (data[i] > '\177' || BASE64_BYTES[data[i]] == -1); i++)
			;
		return i;
	}

	public static String toBase64(byte data[], int lineLength, int indent) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		int charsInLine = 0;
		do {
			if (i >= data.length)
				break;
			int byte0 = data[i++] & 0xff;
			int byte1 = i >= data.length ? 256 : data[i++] & 0xff;
			int byte2 = i >= data.length ? 256 : data[i++] & 0xff;
			sb.append(BASE64_CHARS[byte0 >> 2]);
			if (byte1 == 256) {
				sb.append(BASE64_CHARS[byte0 << 4 & 0x30]);
				sb.append("==");
			} else {
				sb.append(BASE64_CHARS[(byte0 << 4 | byte1 >> 4) & 0x3f]);
				if (byte2 == 256) {
					sb.append(BASE64_CHARS[byte1 << 2 & 0x3f]);
					sb.append('=');
				} else {
					sb.append(BASE64_CHARS[(byte1 << 2 | byte2 >> 6) & 0x3f]);
					sb.append(BASE64_CHARS[byte2 & 0x3f]);
				}
			}
			if ((charsInLine += 4) + 4 > lineLength && i < data.length) {
				sb.append("\r\n");
				int j = 0;
				while (j < indent) {
					sb.append(' ');
					j++;
				}
			}
		} while (true);
		String s = new String(sb);
		sb.delete(0, sb.length());
		return s;
	}

	private static char BASE64_CHARS[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', '+', '/' };

	private static byte BASE64_BYTES[] = { -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1,
			-1, 0, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
			14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, -1, -1, -1, -1,
			-1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41,
			42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };
	/* Base 64 - ENd */

	/* MD5 */
	// The following S11-S44 is actually a 4 * 4 matrix, so the write is easy to modify
	// 下面这些S11-S44实际上是一个4*4的矩阵，这样写是方便修改
	static final int S11 = 7;
	static final int S12 = 12;
	static final int S13 = 17;
	static final int S14 = 22;
	static final int S21 = 5;
	static final int S22 = 9;
	static final int S23 = 14;
	static final int S24 = 20;
	static final int S31 = 4;
	static final int S32 = 11;
	static final int S33 = 16;
	static final int S34 = 23;
	static final int S41 = 6;
	static final int S42 = 10;
	static final int S43 = 15;
	static final int S44 = 21;

	static final byte[] PADDING = { -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0 };

	/*
 		The following three members are the three core data used in the MD5 calculation process and
 		are defined in the MD5_CTX structure in the original C implementation

	   下面的三个成员是MD5计算过程中用到的3个核心数据，在原始的C实现中
	   被定义到MD5_CTX结构中
	 */
	private long[] state = new long[4]; // state (ABCD)

	private long[] count = new long[2]; // number of bits, modulo 2^64 (lsb first)

	private byte[] buffer = new byte[64]; // input buffer

	private static MD5 md5;

	// b2iu is a "raising" program that the byte in accordance with do not consider the sign,Java does not have unsigned
	// b2iu是一个把byte按照不考虑正负号的原则的＂升位＂程序，因为java没有unsigned运算
	public static long b2iu(byte b) {
		return b < 0 ? b & 0x7F + 128 : b;
	}

	// ByteHEX (), used to convert a byte type into hexadecimal ASCII representation,
	// Because java in the byte toString can not achieve this, we have no C language
	// Sprintf (outbuf, "% 02X", ib)
	/**
	 * byteHEX()，用来把一个byte类型的数转换成十六进制的ASCII表示，
	 * <br>因为java中的byte的toString无法实现这一点，我们又没有C语言中的
	 * <br>sprintf(outbuf,"%02X",ib)
	 * */
	public static String byteHEX(byte ib) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F' };
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F];
		ob[1] = Digit[ib & 0X0F];
		String s = new String(ob);
		return s;
	}

	public static String encodeMD5(String source) {
		if (md5 == null)
			md5 = new StringUtil().new MD5();
		return md5.getMD5OfStr(source);
	}

	public static String encodeMD5(byte[] source) {
		if (md5 == null)
			md5 = new StringUtil().new MD5();
		return md5.getMD5OfStr(source);
	}

	// DigestHexStr is the only public member of MD5 and is the hexadecimal ASCII representation of the latest calculation.
	/**
	 * digestHexStr是MD5的唯一一个公共成员，是最新一次计算结果的16进制ASCII表示.
	 * */
	public class MD5 {
		public String digestHexStr;

		// Digest, is the latest calculation of the results of the binary internal representation, said 128bit MD5 value.
		// digest,是最新一次计算结果的2进制内部表示，表示128bit的MD5值.
		private byte[] digest = new byte[16];

		/*
		   getMD5ofStr is the main public method of class MD5, the entry parameter is the string you want to make MD5 transform
		   Returns the result of the transformation, which is obtained from the public member digestHexStr.

		   getMD5ofStr是类MD5最主要的公共方法，入口参数是你想要进行MD5变换的字符串
		   返回的是变换完的结果，这个结果是从公共成员digestHexStr取得的．
		 */
		public synchronized String getMD5OfStr(String inbuf) {
			md5Init();
			md5Update(inbuf.getBytes(), inbuf.length());
			md5Final();
			digestHexStr = "";
			for (int i = 0; i < 16; i++) {
				digestHexStr += byteHEX(digest[i]);
			}
			return digestHexStr;
		}

		public String getMD5OfStr(byte[] inbuf) {
			if (inbuf == null)
				return null;
			md5Init();
			md5Update(inbuf, inbuf.length);
			md5Final();
			digestHexStr = "";
			for (int i = 0; i < 16; i++) {
				digestHexStr += byteHEX(digest[i]);
			}
			return digestHexStr;
		}

		// This is the standard constructor for this class of MD5, which requires a public constructor with no arguments
		// 这是MD5这个类的标准构造函数，JavaBean要求有一个public的并且没有参数的构造函数
		public MD5() {
			md5Init();
			return;
		}

		// md5Init is an initialization function that initializes the core variable and loads the standard magic number
		// md5Init是一个初始化函数，初始化核心变量，装入标准的幻数
		private void md5Init() {
			count[0] = 0L;
			count[1] = 0L;
			// Load magic initialization constants.
			// 加载幻数 初始化常量
			state[0] = 0x67452301L;
			state[1] = 0xefcdab89L;
			state[2] = 0x98badcfeL;
			state[3] = 0x10325476L;
			return;
		}

		/*
		   F, G, H, I are four basic MD5 functions. In the original MD5 C implementation,
		   because they are simple bit operations, they may be implemented as a macro for efficiency
		   reasons. In java, we To achieve them into a private method, to maintain the original
		   name of the C.

		   F, G, H ,I 是4个基本的MD5函数，在原始的MD5的C实现中，由于它们是
		   简单的位运算，可能出于效率的考虑把它们实现成了宏，在java中，我们把它们
		   实现成了private方法，名字保持了原来C中的。
		 */
		private long F(long x, long y, long z) {
			return (x & y) | ((~x) & z);
		}

		private long G(long x, long y, long z) {
			return (x & z) | (y & (~z));
		}

		private long H(long x, long y, long z) {
			return x ^ y ^ z;
		}

		private long I(long x, long y, long z) {
			return y ^ (x | (~z));
		}

		// FF, GG, HH and II will call F, G, H, I for further transformation
		// FF,GG,HH和II将调用F,G,H,I进行近一步变换
		private long FF(long a, long b, long c, long d, long x, long s, long ac) {
			a += F(b, c, d) + x + ac;
			a = ((int) a << s) | ((int) a >>> (32 - s));
			a += b;
			return a;
		}

		private long GG(long a, long b, long c, long d, long x, long s, long ac) {
			a += G(b, c, d) + x + ac;
			a = ((int) a << s) | ((int) a >>> (32 - s));
			a += b;
			return a;
		}

		private long HH(long a, long b, long c, long d, long x, long s, long ac) {
			a += H(b, c, d) + x + ac;
			a = ((int) a << s) | ((int) a >>> (32 - s));
			a += b;
			return a;
		}

		private long II(long a, long b, long c, long d, long x, long s, long ac) {
			a += I(b, c, d) + x + ac;
			a = ((int) a << s) | ((int) a >>> (32 - s));
			a += b;
			return a;
		}
		/*
		   md5Update MD5 is the main calculation process, inbuf is to change the byte string,
		   inputlen is the length of the function by the getMD5ofStr call, call before the need
		   to call md5init, so it is designed to private

		   md5Update是MD5的主计算过程，inbuf是要变换的字节串，inputlen是长度，这个
		   函数由getMD5ofStr调用，调用之前需要调用md5init，因此把它设计成private的
		 */

		private void md5Update(byte[] inbuf, int inputLen) {
			int i, index, partLen;
			byte[] block = new byte[64];
			index = (int) (count[0] >>> 3) & 0x3F;
			// Update number of bits
			// 更新位数
			if ((count[0] += (inputLen << 3)) < (inputLen << 3))
				count[1]++;
			count[1] += (inputLen >>> 29);

			partLen = 64 - index;

			// Transform as many times as possible.
			// 尽可能多的转换
			if (inputLen >= partLen) {
				md5Memcpy(buffer, inbuf, index, 0, partLen);
				md5Transform(buffer);
				for (i = partLen; i + 63 < inputLen; i += 64) {
					md5Memcpy(block, inbuf, 0, i, 64);
					md5Transform(block);
				}
				index = 0;
			} else
				i = 0;
			// Buffer remaining input
			// 缓冲区剩余输入
			md5Memcpy(buffer, inbuf, index, i, inputLen - i);
		}

		private void md5Final() {
			byte[] bits = new byte[8];
			int index, padLen;

			// Save number of bits
			// 保存位数
			Encode(bits, count, 8);

			index = (int) (count[0] >>> 3) & 0x3f;
			padLen = (index < 56) ? (56 - index) : (120 - index);
			md5Update(PADDING, padLen);

			// Append length (before padding)
			// 加入长度（填充之前）
			md5Update(bits, 8);

			// Store state in digest
			// 保存状态到digest
			Encode(digest, state, 16);
		}

		/*
		   md5Memcpy is an internal use of the byte array block copy function, from the input of
		   the inpos began to len length of the byte copy to the output outpos position to start

		   md5Memcpy是一个内部使用的byte数组的块拷贝函数，从input的inpos开始把len长度的
		   字节拷贝到output的outpos位置开始
		 */
		private void md5Memcpy(byte[] output, byte[] input, int outpos, int inpos, int len) {
			int i;
			for (i = 0; i < len; i++)
				output[outpos + i] = input[inpos + i];
		}

		// md5Transform is the MD5 core transformation program, called by md5Update, block is the original byte of the block
		// md5Transform是MD5核心变换程序，由md5Update调用，block是分块的原始字节
		private void md5Transform(byte block[]) {
			long a = state[0], b = state[1], c = state[2], d = state[3];
			long[] x = new long[16];

			Decode(x, block, 64);

			/* Round 1 */
			a = FF(a, b, c, d, x[0], S11, 0xd76aa478L); /* 1 */
			d = FF(d, a, b, c, x[1], S12, 0xe8c7b756L); /* 2 */
			c = FF(c, d, a, b, x[2], S13, 0x242070dbL); /* 3 */
			b = FF(b, c, d, a, x[3], S14, 0xc1bdceeeL); /* 4 */
			a = FF(a, b, c, d, x[4], S11, 0xf57c0fafL); /* 5 */
			d = FF(d, a, b, c, x[5], S12, 0x4787c62aL); /* 6 */
			c = FF(c, d, a, b, x[6], S13, 0xa8304613L); /* 7 */
			b = FF(b, c, d, a, x[7], S14, 0xfd469501L); /* 8 */
			a = FF(a, b, c, d, x[8], S11, 0x698098d8L); /* 9 */
			d = FF(d, a, b, c, x[9], S12, 0x8b44f7afL); /* 10 */
			c = FF(c, d, a, b, x[10], S13, 0xffff5bb1L); /* 11 */
			b = FF(b, c, d, a, x[11], S14, 0x895cd7beL); /* 12 */
			a = FF(a, b, c, d, x[12], S11, 0x6b901122L); /* 13 */
			d = FF(d, a, b, c, x[13], S12, 0xfd987193L); /* 14 */
			c = FF(c, d, a, b, x[14], S13, 0xa679438eL); /* 15 */
			b = FF(b, c, d, a, x[15], S14, 0x49b40821L); /* 16 */

			/* Round 2 */
			a = GG(a, b, c, d, x[1], S21, 0xf61e2562L); /* 17 */
			d = GG(d, a, b, c, x[6], S22, 0xc040b340L); /* 18 */
			c = GG(c, d, a, b, x[11], S23, 0x265e5a51L); /* 19 */
			b = GG(b, c, d, a, x[0], S24, 0xe9b6c7aaL); /* 20 */
			a = GG(a, b, c, d, x[5], S21, 0xd62f105dL); /* 21 */
			d = GG(d, a, b, c, x[10], S22, 0x2441453L); /* 22 */
			c = GG(c, d, a, b, x[15], S23, 0xd8a1e681L); /* 23 */
			b = GG(b, c, d, a, x[4], S24, 0xe7d3fbc8L); /* 24 */
			a = GG(a, b, c, d, x[9], S21, 0x21e1cde6L); /* 25 */
			d = GG(d, a, b, c, x[14], S22, 0xc33707d6L); /* 26 */
			c = GG(c, d, a, b, x[3], S23, 0xf4d50d87L); /* 27 */
			b = GG(b, c, d, a, x[8], S24, 0x455a14edL); /* 28 */
			a = GG(a, b, c, d, x[13], S21, 0xa9e3e905L); /* 29 */
			d = GG(d, a, b, c, x[2], S22, 0xfcefa3f8L); /* 30 */
			c = GG(c, d, a, b, x[7], S23, 0x676f02d9L); /* 31 */
			b = GG(b, c, d, a, x[12], S24, 0x8d2a4c8aL); /* 32 */

			/* Round 3 */
			a = HH(a, b, c, d, x[5], S31, 0xfffa3942L); /* 33 */
			d = HH(d, a, b, c, x[8], S32, 0x8771f681L); /* 34 */
			c = HH(c, d, a, b, x[11], S33, 0x6d9d6122L); /* 35 */
			b = HH(b, c, d, a, x[14], S34, 0xfde5380cL); /* 36 */
			a = HH(a, b, c, d, x[1], S31, 0xa4beea44L); /* 37 */
			d = HH(d, a, b, c, x[4], S32, 0x4bdecfa9L); /* 38 */
			c = HH(c, d, a, b, x[7], S33, 0xf6bb4b60L); /* 39 */
			b = HH(b, c, d, a, x[10], S34, 0xbebfbc70L); /* 40 */
			a = HH(a, b, c, d, x[13], S31, 0x289b7ec6L); /* 41 */
			d = HH(d, a, b, c, x[0], S32, 0xeaa127faL); /* 42 */
			c = HH(c, d, a, b, x[3], S33, 0xd4ef3085L); /* 43 */
			b = HH(b, c, d, a, x[6], S34, 0x4881d05L); /* 44 */
			a = HH(a, b, c, d, x[9], S31, 0xd9d4d039L); /* 45 */
			d = HH(d, a, b, c, x[12], S32, 0xe6db99e5L); /* 46 */
			c = HH(c, d, a, b, x[15], S33, 0x1fa27cf8L); /* 47 */
			b = HH(b, c, d, a, x[2], S34, 0xc4ac5665L); /* 48 */

			/* Round 4 */
			a = II(a, b, c, d, x[0], S41, 0xf4292244L); /* 49 */
			d = II(d, a, b, c, x[7], S42, 0x432aff97L); /* 50 */
			c = II(c, d, a, b, x[14], S43, 0xab9423a7L); /* 51 */
			b = II(b, c, d, a, x[5], S44, 0xfc93a039L); /* 52 */
			a = II(a, b, c, d, x[12], S41, 0x655b59c3L); /* 53 */
			d = II(d, a, b, c, x[3], S42, 0x8f0ccc92L); /* 54 */
			c = II(c, d, a, b, x[10], S43, 0xffeff47dL); /* 55 */
			b = II(b, c, d, a, x[1], S44, 0x85845dd1L); /* 56 */
			a = II(a, b, c, d, x[8], S41, 0x6fa87e4fL); /* 57 */
			d = II(d, a, b, c, x[15], S42, 0xfe2ce6e0L); /* 58 */
			c = II(c, d, a, b, x[6], S43, 0xa3014314L); /* 59 */
			b = II(b, c, d, a, x[13], S44, 0x4e0811a1L); /* 60 */
			a = II(a, b, c, d, x[4], S41, 0xf7537e82L); /* 61 */
			d = II(d, a, b, c, x[11], S42, 0xbd3af235L); /* 62 */
			c = II(c, d, a, b, x[2], S43, 0x2ad7d2bbL); /* 63 */
			b = II(b, c, d, a, x[9], S44, 0xeb86d391L); /* 64 */

			state[0] += a;
			state[1] += b;
			state[2] += c;
			state[3] += d;
		}

		/*
		   Encode put the long array in order to split into a byte array, because the java long type is 64bit,
		   only demolition 32bit, to adapt to the original C to achieve the purpose

		   Encode把long数组按顺序拆成byte数组，因为java的long类型是64bit的，
		   只拆低32bit，以适应原始C实现的用途
		 */
		private void Encode(byte[] output, long[] input, int len) {
			int i, j;
			for (i = 0, j = 0; j < len; i++, j += 4) {
				output[j] = (byte) (input[i] & 0xffL);
				output[j + 1] = (byte) ((input[i] >>> 8) & 0xffL);
				output[j + 2] = (byte) ((input[i] >>> 16) & 0xffL);
				output[j + 3] = (byte) ((input[i] >>> 24) & 0xffL);
			}
		}

		/*
		   Decode put the byte array in order into a long array, because the java long type is 64bit,
		   only the synthesis of low 32bit, high 32bit clear to zero, to adapt to the original C
		   to achieve the purpose

		   Decode把byte数组按顺序合成成long数组，因为java的long类型是64bit的，
		   只合成低32bit，高32bit清零，以适应原始C实现的用途
		 */
		private void Decode(long[] output, byte[] input, int len) {
			int i, j;
			for (i = 0, j = 0; j < len; i++, j += 4)
				output[i] = b2iu(input[j]) | (b2iu(input[j + 1]) << 8)
						| (b2iu(input[j + 2]) << 16) | (b2iu(input[j + 3]) << 24);
			return;
		}
	}
}