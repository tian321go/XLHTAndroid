package com.axeac.android.sdk.scanner;

/**
 * describe:This class provides the constants to use when sending an Intent to Barcode
 *          <br>Scanner.These strings are effectively API and cannot be changed.
 * <br>此类提供了在将条形码扫描程序发送时使用的常量。这些字符串实际上是API，不能更改。
 * @author axeac
 * @version 1.0.0
 */
public final class Intents {
	private Intents() {
	}

	public static final class Scan {
		// describe:Send this intent to open the Barcodes app in scanning mode, find a
		//          barcode, and return the results.
		/**
		 * 发送此意图以扫描模式打开条形码应用程序，查找条形码并返回结果。
		 */
		public static final String ACTION = "com.google.zxing.client.android.SCAN";

		// describe:By default, sending this will decode all barcodes that we understand.
		//          However it may be useful to limit scanning to certain formats. Use
		//          {@link android.content.Intent#putExtra(String, String)} with one of
		//          the values below.
		//          Setting this is effectively shorthand for setting explicit formats
		//          with {@link #FORMATS}. It is overridden by that setting.
		/**
		 * 默认情况下，发送将解码的所有条形码。 但是，将扫描限制为某些格式可能是有用的。
		 * <br>使用下列值之一{@link android.content.Intent＃putExtra（String，String）}。
		 * <br>使用{@link #FORMATS}设置显式格式，可以有效地简化设置。
		 */
		public static final String MODE = "SCAN_MODE";

		// describe:Decode only UPC and EAN barcodes. This is the right choice for
		//          shopping apps which get prices, reviews, etc. for products.
		/**
		 * 仅解码UPC和EAN条形码。 这是商店应用程序的正确选择，可以获得产品的价格，评论等。
		 */
		public static final String PRODUCT_MODE = "PRODUCT_MODE";

		// describe:Decode only 1D barcodes.
		/**
		 * 仅解码1D条形码
		 */
		public static final String ONE_D_MODE = "ONE_D_MODE";

		// describe:Decode only QR codes.
		/**
		 * 仅解码QR码
		 */
		public static final String QR_CODE_MODE = "QR_CODE_MODE";

		// describe:Decode only Data Matrix codes.
		/**
		 * 仅解码数据矩阵码
		 */
		public static final String DATA_MATRIX_MODE = "DATA_MATRIX_MODE";

		// describe：Decode only Aztec.
		/**
		 * 仅解码Aztec
		 */
		public static final String AZTEC_MODE = "AZTEC_MODE";

		// describe:Decode only PDF417.
		/**
		 * 仅解码PDF417
		 */
		public static final String PDF417_MODE = "PDF417_MODE";

		// describe:Comma-separated list of formats to scan for. The values must match
		//          the names of {@link com.google.zxing.BarcodeFormat}s,
		//          e.g.{@link com.google.zxing.BarcodeFormat#EAN_13}.
		//          Example:"EAN_13,EAN_8,QR_CODE". This overrides {@link #MODE}.
		/**
		 * 逗号分隔的扫描格式列表。 值必须与{@link com.google.zxing.BarcodeFormat}的名称匹配，
		 * <br>例如 {@link com.google.zxing.BarcodeFormat＃EAN_13}。
		 * <br>示例：“EAN_13，EAN_8，QR_CODE”。 这将覆盖{@link #MODE}。
		 */
		public static final String FORMATS = "SCAN_FORMATS";

		// describe:Optional parameter to specify the id of the camera from which to
		//          recognize barcodes. Overrides the default camera that would otherwise
		//          would have been selected. If provided, should be an int.
		/**
		 * 可选参数，指定从识别条形码的相机ID。覆盖默认相机，否则将被选中。如果提供，应该是一个int。
		 */
		public static final String CAMERA_ID = "SCAN_CAMERA_ID";

		/**
		 * @see com.google.zxing.DecodeHintType#CHARACTER_SET
		 */
		public static final String CHARACTER_SET = "CHARACTER_SET";

		// describe:Optional parameters to specify the width and height of the scanning
		//          rectangle in pixels. The app will try to honor these, but will clamp
		//          them to the size of the preview frame. You should specify both or
		//          neither, and pass the size as an int.
		/**
		 * 用于指定扫描矩形的宽度和高度的可选参数，以像素为单位。 该应用程序将尝试尊重这些，
		 * <br>但会将它们夹在预览框架的大小上。同时指定两者或两者都不指定，并传递int类型尺寸。
		 */
		public static final String WIDTH = "SCAN_WIDTH";
		public static final String HEIGHT = "SCAN_HEIGHT";

		// describe:Desired duration in milliseconds for which to pause after a
		//          successful scan before returning to the calling intent. Specified as
		//          a long, not an integer! For example: 1000L, not 1000.
		/**
		 * 所需的持续时间（毫秒），在成功扫描之后暂停，然后返回到呼叫意图。
		 * <br>指定为long，不是integer！ 例如：1000L，而不是1000。
		 */
		public static final String RESULT_DISPLAY_DURATION_MS = "RESULT_DISPLAY_DURATION_MS";

		// describe:Prompt to show on-screen when scanning by intent. Specified as a {@link String}.
		/**
		 * 在意图扫描时提示显示屏幕。 指定为{@link String}
		 */
		public static final String PROMPT_MESSAGE = "PROMPT_MESSAGE";

		// describe:If a barcode is found, Barcodes returns
		//          {@link android.app.Activity#RESULT_OK} to
		//          {@link android.app.Activity#onActivityResult(int, int, android.content.Intent)}
		// 			of the app which requested the scan via
		// 			{@link android.app.Activity#startActivityForResult(android.content.Intent, int)}
		// 			The barcodes contents can be retrieved with
		// 			{@link android.content.Intent#getStringExtra(String)}. If the user
		// 			presses Back, the result code will be
		// 			{@link android.app.Activity#RESULT_CANCELED}.
		/**
		 * 如果找到条形码，条形码返回{@link android.app.Activity＃RESULT_OK}通过
		 * <br>{@link android.app.Activity＃startActivityForResult（android.content.Intent，int））
		 * <br>请求扫描的应用程序的
		 * <br>{@link android.app.Activity＃onActivityResult（int，int，android.content.Intent）}}
		 * <br>可以使用{@link android.content.Intent＃getStringExtra（String）}检索条形码内容。
		 * <br>如果用户按Back，结果代码将为{@link android.app.Activity＃RESULT_CANCELED}。
		 */
		public static final String RESULT = "SCAN_RESULT";

		// describe:Call {@link android.content.Intent#getStringExtra(String)} with
		// 			{@link #RESULT_FORMAT} to determine which barcode format was found.
		// 			See {@link com.google.zxing.BarcodeFormat} for possible values.

		/**
		 * 使用{@link #RESULT_FORMAT}调用{@link android.content.Intent＃getStringExtra（String）}，
		 * <br>以确定找到了哪个条形码格式。 有关可能的值，请参阅{@link com.google.zxing.BarcodeFormat}。
		 */
		public static final String RESULT_FORMAT = "SCAN_RESULT_FORMAT";

		// describe:Call {@link android.content.Intent#getStringExtra(String)} with
		// 			{@link #RESULT_UPC_EAN_EXTENSION} to return the content of any UPC
		// 			extension barcode that was also found. Only applicable to
		// 			{@link com.google.zxing.BarcodeFormat#UPC_A} and
		// 			{@link com.google.zxing.BarcodeFormat#EAN_13} formats.
		/**
		 * 使用{@link #RESULT_UPC_EAN_EXTENSION}调用{@link android.content.Intent＃getStringExtra（String）}，
		 * <br>以返回还找到的任何UPC扩展条形码的内容。
		 * <br>仅适用于{@link com.google.zxing.BarcodeFormat＃UPC_A}和
		 * <br>{@link com.google.zxing.BarcodeFormat＃EAN_13}格式。
		 */
		public static final String RESULT_UPC_EAN_EXTENSION = "SCAN_RESULT_UPC_EAN_EXTENSION";

		// describe:Call {@link android.content.Intent#getByteArrayExtra(String)} with
		// 			{@link #RESULT_BYTES} to get a {@code byte[]} of raw bytes in the
		// 			barcode, if available.
		/**
		 * 调用{@link android.content.Intent＃getByteArrayExtra（String）}
		 * <br>@link #RESULT_BYTES}以获取原始字节的{@code byte []}条形码，如果有的话。
		 */
		public static final String RESULT_BYTES = "SCAN_RESULT_BYTES";

		// describe:Key for the value of
		// 			{@link com.google.zxing.ResultMetadataType#ORIENTATION}, if
		// 			available. Call
		// 			{@link android.content.Intent#getIntArrayExtra(String)} with
		// 			{@link #RESULT_ORIENTATION}.
		/**
		 * 值为{@link com.google.zxing.ResultMetadataType＃ORIENTATION}的价值（如果有）。
		 * <br>使用{@link #RESULT_ORIENTATION}调用{@link android.content.Intent＃getIntArrayExtra（String）}。
		 */
		public static final String RESULT_ORIENTATION = "SCAN_RESULT_ORIENTATION";

		// describe:Key for the value of
		// 			{@link com.google.zxing.ResultMetadataType#ERROR_CORRECTION_LEVEL},
		// 			if available. Call
		// 			{@link android.content.Intent#getStringExtra(String)} with
		// 			{@link #RESULT_ERROR_CORRECTION_LEVEL}.
		/**
		 * {@link com.google.zxing.ResultMetadataType＃ERROR_CORRECTION_LEVEL}的键值（如果有）。
		 * <br>使用{@link #RESULT_ERROR_CORRECTION_LEVEL}
		 * <br>调用{@link android.content.Intent＃getStringExtra（String）}。
		 */
		public static final String RESULT_ERROR_CORRECTION_LEVEL = "SCAN_RESULT_ERROR_CORRECTION_LEVEL";

		// describe:Prefix for keys that map to the values of
		// 			{@link com.google.zxing.ResultMetadataType#BYTE_SEGMENTS}, if
		// 			available. The actual values will be set under a series of keys
		// 			formed by adding 0, 1, 2, ... to this prefix. So the first byte
		// 			segment is under key "SCAN_RESULT_BYTE_SEGMENTS_0" for example. Call
		// 			{@link android.content.Intent#getByteArrayExtra(String)} with these
		// 			keys.
		/**
		 * 用于映射{@link com.google.zxing.ResultMetadataType＃BYTE_SEGMENTS}值的键的前缀（如果可用）。
		 * <br>实际值将被设置在通过将0,1,2，...添加到该前缀形成的一系列键上。
		 * <br>因此，第一个字节段位于密钥“SCAN_RESULT_BYTE_SEGMENTS_0”下。
		 * <br>使用这些键调用{@link android.content.Intent＃getByteArrayExtra（String）}。
		 */
		public static final String RESULT_BYTE_SEGMENTS_PREFIX = "SCAN_RESULT_BYTE_SEGMENTS_";

		// describe:Setting this to false will not save scanned codes in the history.
		// 			Specified as a {@code boolean}.
		/**
		 * 将此设置为false将不会将扫描的代码保存在历史记录中。指定为{@code boolean}。
		 */
		public static final String SAVE_HISTORY = "SAVE_HISTORY";

		private Scan() {
		}
	}

	public static final class History {

		public static final String ITEM_NUMBER = "ITEM_NUMBER";

		private History() {
		}
	}

	public static final class Encode {
		// describe:Send this intent to encode a piece of data as a QR code and display
		// 			it full screen, so that another person can scan the barcode from your
		// 			screen.
		/**
		 * 发送此意图将一条数据编码为QR码并将其全屏显示，以便另一个人可以从屏幕扫描条形码。
		 */
		public static final String ACTION = "com.google.zxing.client.android.ENCODE";

		// describe:The data to encode. Use
		// 			{@link android.content.Intent#putExtra(String, String)} or
		// 			{@link android.content.Intent#putExtra(String, android.os.Bundle)},
		// 			depending on the type and format specified. Non-QR Code formats
		// 			should just use a String here. For QR Code, see Contents for details.
		/**
		 * 要编码的数据。 根据指定的类型和格式，使用{@link android.content.Intent＃putExtra（String，String）}
		 * <br>或{@link android.content.Intent＃putExtra（String，android.os.Bundle）}。
		 * <br>非QR码格式应该在这里使用一个String。 有关QR码，详见内容。
		 */
		public static final String DATA = "ENCODE_DATA";

		// describe:The type of data being supplied if the format is QR Code. Use
		// 			{@link android.content.Intent#putExtra(String, String)} with one of

		/**
		 * 如果格式为QR码，则提供的数据类型。
		 * <br>使用{@link android.content.Intent＃putExtra（String，String）}
		 */
		public static final String TYPE = "ENCODE_TYPE";

		// describe:The barcode format to be displayed. If this isn't specified or is
		// 			blank, it defaults to QR Code. Use
		// 			{@link android.content.Intent#putExtra(String, String)}, where format
		// 			is one of {@link com.google.zxing.BarcodeFormat}.
		/**
		 * 要显示的条形码格式。 如果未指定或为空，则默认为QR码。
		 * <br>使用{@link android.content.Intent＃putExtra（String，String）}，
		 * <br>其中format是{@link com.google.zxing.BarcodeFormat}之一。
		 */
		public static final String FORMAT = "ENCODE_FORMAT";

		// describe:Normally the contents of the barcode are displayed to the user in a
		//          TextView. Setting this boolean to false will hide that TextView,
		// 			showing only the encode barcode.
		/**
		 * 通常，条形码的内容将在TextView中显示给用户。 将此布尔值设置为false将隐藏该TextView，
		 * <br>仅显示编码条形码。
		 */
		public static final String SHOW_CONTENTS = "ENCODE_SHOW_CONTENTS";

		private Encode() {
		}
	}

	public static final class SearchBookContents {
		// describe:Use Google Book Search to search the contents of the book provided.
		/**
		 * 使用Google图书搜索来搜索提供的图书的内容。
		 */
		public static final String ACTION = "com.google.zxing.client.android.SEARCH_BOOK_CONTENTS";

		// describe:The book to search, identified by ISBN number.
		/**
		 * 要搜索的书，由ISBN号码标识。
		 */
		public static final String ISBN = "ISBN";

		// describe:An optional field which is the text to search for.
		/**
		 * 可选字段是要搜索的文本。
		 */
		public static final String QUERY = "QUERY";

		private SearchBookContents() {
		}
	}

	public static final class WifiConnect {
		// describe:Internal intent used to trigger connection to a wi-fi network.
		/**
		 * 用于触发连接到WiFi网络的内部意图。
		 */
		public static final String ACTION = "com.google.zxing.client.android.WIFI_CONNECT";

		// describe:The network to connect to, all the configuration provided here.
		/**
		 * 网络连接，这里提供的所有配置。
		 */
		public static final String SSID = "SSID";

		// describe:The network to connect to, all the configuration provided here.
		/**
		 * 网络连接配置
		 */
		public static final String TYPE = "TYPE";

		// describe:The network to connect to, all the configuration provided here.
		/**
		 * 网络连接配置
		 */
		public static final String PASSWORD = "PASSWORD";

		private WifiConnect() {
		}
	}

	public static final class Share {
		// describe:Give the user a choice of items to encode as a barcode, then render
		// 			it as a QR Code and display onscreen for a friend to scan with their
		// 			phone.
		/**
		 * 让用户选择要编码的条形码，然后将其作为QR码显示，并显示在屏幕上，供他人用手机扫描。
		 */
		public static final String ACTION = "com.google.zxing.client.android.SHARE";

		private Share() {
		}
	}
}
