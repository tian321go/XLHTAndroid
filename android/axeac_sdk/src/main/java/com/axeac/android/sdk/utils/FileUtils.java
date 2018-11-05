package com.axeac.android.sdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import org.apache.http.util.EncodingUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * 操作文件的工具类
 * @author axeac
 * @version 1.0.0
 * */
public class FileUtils {

    public static final String CONFIG_FILENAME = "khmap5_config";
    public static final String KHPATH = "/KHDownload";

    public static boolean isStoreLogToSD = false;

    public static String getApplicationPath(Context ctx) {
        return ctx.getApplicationContext().getFilesDir().getAbsolutePath();
    }

    // Get the absolute path of the SD card
    /**
     * 获得SD卡绝对路径
     * @return
     * SD卡绝对路径
     * */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    // Check if the SD card is present
    /**
     * 检查SD卡是否存在
     * @return
     * true代表存在
     * */
    public static boolean checkSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * describe:return string of file path
     * 返回文件路径字符串
     * @param fileName
     * name of the file
     * 文件名字
     *
     * @param path
     * path
     * 路径
     *
     * @return path
     * 文件路径字符串
     * */
    private static String allPath(String fileName, String path) {
        if (!path.endsWith("/"))
            path += "/";
        return path + fileName;
    }

    /**
     * describe:create file with the fileName and path
     * 根据文件名字和路径创建文件
     *
     * @param fileName
     * name of the file
     * 文件名字
     *
     * @param path
     * path
     * 路径
     * */
    public static File createFile(String fileName, String path) throws IOException {
        return createFile(allPath(fileName, path));
    }

    /**
     * describe:create file with the fileName and path
     * 根据文件名字和路径创建文件
     *
     * @param allPath
     * path of the file
     * 文件路径
     *
     * @return file
     * 返回创建的文件
     * */
    public static File createFile(String allPath) throws IOException {
        File file = new File(allPath);
        if (file.exists())
            file.delete();
        file.createNewFile();
        return file;
    }

    /**
     * describe:create directory
     * 创建目录
     *
     * @param dirName
     * name of the directory
     * 目录名称
     *
     * @param path
     * path
     * 路径
     * */
    public static File createDir(String dirName, String path) {
        return createDir(allPath(dirName, path));
    }

    /**
     * describe:create directory
     * 创建目录
     *
     * @param allPath
     * path
     * 包含目录名称的路径
     * */
    public static File createDir(String allPath) {
        File dir = new File(allPath);
        dir.mkdirs();
        return dir;
    }

    /**
     * describe:Determine if the file / folder exists
     * 判断文件/文件夹是否存在
     *
     * @param fileName
     * name of the file
     * 文件名字
     *
     * @param path
     * path
     * 路径
     */
    public static boolean isFileExist(String fileName, String path) {
        return isFileExist(allPath(fileName, path));
    }

    /**
     * describe:Determine if the file / folder exists
     * 判断文件/文件夹是否存在
     *
     * @param allPath
     * path
     * 路径
     */
    public static boolean isFileExist(String allPath) {
        File file = new File(allPath);
        return file.exists();
    }

    public static void storeInSD(String allPath, Bitmap bitmap) {
        try {
            File imageFile = createFile(allPath);
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            if (!bitmap.isRecycled())
                bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void storeInSD(String allPath, byte[] bytes) {
        try {
            FileOutputStream fos = null;
            File file = new File(allPath);
            if (!file.exists())
                file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 字符编码
     * <br>默认为UTF-8
     * */
    private static final String TEXT_ENCODING = "UTF-8";

    /**
     * describe:Read the contents of the file to return the string
     * 读取文件中的内容返回字符串
     *
     * @param path
     * path
     * 路径
     */
    public static String readFile(String path) {
        String str = "";
        try {
            File file = new File(path);
            FileInputStream in = new FileInputStream(file);
            int length = (int) file.length();
            byte[] temp = new byte[length];
            in.read(temp, 0, length);
            str = EncodingUtils.getString(temp, TEXT_ENCODING);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * describe:Write an InputStream data into the SD card
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param is
     * InputStreem object
     * InputStream对象
     *
     * @param allPath
     * path
     * 路径
     */
    public static File writeFile(InputStream is, String allPath) {
        File file = null;
        OutputStream output = null;
        try {
            file = createFile(allPath);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            int temp;
            while ((temp = is.read(buffer)) != -1) {
                output.write(buffer, 0, temp);
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (!(output == null))
                    output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * describe:Write an InputStream data into the SD card
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param is
     * InputStream object
     * InputStream对象
     *
     * @param fileName
     * name of the file
     * 文件名字
     *
     * @param path
     * path
     * 路径
     */
    public static File writeFile(InputStream is, String fileName, String path) {
        return writeFile(is, allPath(fileName, path));
    }

    /**
     * describe:Write the data back to the file
     * 把数据写回文件
     *
     * @param str
     * data need to be written
     * 需要写入的数据
     *
     * @param allPath
     * path
     * 路径
     */
    public static File writeFile(String str, String allPath) {
        File file = null;
        FileOutputStream output = null;
        try {
            file = createFile(allPath);
            output = new FileOutputStream(file);
            String infoToWrite = str;
            output.write(infoToWrite.getBytes());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (!(output == null))
                    output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * describe:Write the data back to the file
     * 把数据写回文件
     *
     * @param str
     * data need to be written
     * 需要写入的数据
     *
     * @param fileName
     * name of the file
     * 文件名字
     *
     * @param path
     * path
     * 路径
     */
    public static File writeFile(String str, String fileName, String path) {
        return writeFile(str, allPath(fileName, path));
    }

    /**
     * describe:delete the file
     * 删除文件
     *
     * @param filePath
     * 文件路径
     */
    public static void deleteFiles(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            deleteAll(file);
        }
    }
    /**
     * describe:delete all files
     * 删除全部文件
     *
     * @param file
     * file object
     * file对象
     */
    private static void deleteAll(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children.length > 0) {
                for (File childFile : children) {
                    deleteAll(childFile);
                }
            } else {
                file.delete();
            }
        } else {
            file.delete();
        }
    }

    /**
     * describe:get apk file
     * 获取 apk 文件
     *@param context
     * Context对象
     * @param version
     * apk版本号
     * @return
     * File对象
     */
    public static File getApkFile(Context context, String version) {
        String appName;
        try {
            appName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.loadLabel(context.getPackageManager()).toString();
        } catch (Exception e) {
            // Using the system api getPackageName () get the package name, this exception simply can not happen
            // 利用系统api getPackageName()得到的包名，这个异常根本不可能发生
            appName = "";
        }
        return new File(getApkFileDir(context), appName + "_v" + version + ".apk");
    }

    /**
     * describe:get apk folder
     * 获取 apk 文件夹
     * @param context
     * Context对象
     * @return
     * File对象
     */
    public static File getApkFileDir(Context context) {
        return context.getExternalFilesDir(DIR_NAME_APK);
    }

    /**
     * 文件夹名称
     * */
    private static final String DIR_NAME_APK = "KHDownload";


    /**
     * describe:save apk file
     * 保存 apk 文件
     * @param context
     * Context对象
     * @param is
     * InputStream对象
     * @param version
     * apk版本号
     * @return
     * File对象
     */
    public static File saveApk(Context context, InputStream is, String version) {
        File file = getApkFile(context, version);

        if (writeFile(file, is)) {
            return file;
        } else {
            return null;
        }
    }

    /**
     * describe:save file with InputStream object
     * 根据输入流，保存文件
     *
     * @param file
     * File对象
     * @param is
     * InputStream对象
     * @return
     * true写入成功，false写入失败
     */
    public static boolean writeFile(File file, InputStream is) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = is.read(data)) != -1) {
                os.write(data, 0, length);
            }
            os.flush();
            return true;
        } catch (Exception e) {
            if (file != null && file.exists()) {
                file.deleteOnExit();
            }
            e.printStackTrace();
        } finally {
            closeStream(os);
            closeStream(is);
        }
        return false;
    }

    /**
     * describe:delete file or folder
     * 删除文件或文件夹
     *
     * @param file
     * File对象
     */
    public static void deleteFile(File file) {
        try {
            if (file == null || !file.exists()) {
                return;
            }

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File f : files) {
                        if (f.exists()) {
                            if (f.isDirectory()) {
                                deleteFile(f);
                            } else {
                                f.delete();
                            }
                        }
                    }
                }
            } else {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * describe:close stream
     * 关闭流
     *
     * @param closeable
     * Closeable对象
     */
    public static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}