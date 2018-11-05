package com.axeac.android.client.service;


import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.axeac.android.client.R;
/**
 * vpn服务
 * @author axeac
 * @version 2.3.0.0001
 * */
public class ToyVpnService extends VpnService implements Handler.Callback, Runnable {
    private static final String TAG = "ToyVpnService";

    private String mServerAddress;
    private String mServerPort;
    private byte[] mSharedSecret;
    private PendingIntent mConfigureIntent;

    private Handler mHandler;
    private Thread mThread;

    private ParcelFileDescriptor mInterface;
    private String mParameters;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The handler is only used to show messages.
        // 这个handler只用于显示消息
        if (mHandler == null) {
            mHandler = new Handler(this);
        }

        // Stop the previous session by interrupting the thread.
        // 通过中断线程来停止会话
        if (mThread != null) {
            mThread.interrupt();
        }

        // Extract information from the intent.
        // 从intent中提取信息。
        String prefix = getPackageName();
        mServerAddress = intent.getStringExtra(prefix + ".ADDRESS");
        mServerPort = intent.getStringExtra(prefix + ".PORT");
        mSharedSecret = intent.getStringExtra(prefix + ".SECRET").getBytes();

        // Start a new session by creating a new thread.
        // 通过创建一个新线程开始一个新的会话
        mThread = new Thread(this, "ToyVpnThread");
        mThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mThread != null) {
            mThread.interrupt();
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message != null) {
            Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public synchronized void run() {
        try {
            Log.i(TAG, "Starting");

            InetSocketAddress server = new InetSocketAddress(
                    mServerAddress, Integer.parseInt(mServerPort));

            for (int attempt = 0; attempt < 10; ++attempt) {
                mHandler.sendEmptyMessage(R.string.connecting);

                // Reset the counter if we were connected.
                // 如果我们已经连接，重置计数器
                if (run(server)) {
                    attempt = 0;
                }

                // Sleep for a while. This also checks if we got interrupted.
                // 睡一会  可以检查是否中断了
                Thread.sleep(3000);
            }
            Log.i(TAG, "Giving up");
        } catch (Exception e) {
            Log.e(TAG, "Got " + e.toString());
        } finally {
            try {
                mInterface.close();
            } catch (Exception e) {
                // ignore
            }
            mInterface = null;
            mParameters = null;

            mHandler.sendEmptyMessage(R.string.disconnected);
            Log.i(TAG, "Exiting");
        }
    }

    private boolean run(InetSocketAddress server) throws Exception {
        DatagramChannel tunnel = null;
        boolean connected = false;
        try {
            // Create a DatagramChannel as the VPN tunnel.
            // 创建一个DatagramChannel作为VPN通道。
            tunnel = DatagramChannel.open();

            // Protect the tunnel before connecting to avoid loopback.
            // 在连接之前保护通道，以避免环回
            if (!protect(tunnel.socket())) {
                throw new IllegalStateException("Cannot protect the tunnel");
            }

            // Connect to the server.
            // 连接到服务
            tunnel.connect(server);

            /*
              For simplicity, we use the same thread for both reading and
              writing. Here we put the tunnel into non-blocking mode.

              为了简单起见，我们使用相同的线程进行阅读和写入。
              这里我们将通道置于非阻塞模式。
             */

            tunnel.configureBlocking(false);

            // Authenticate and configure the virtual network interface.
            // 认证和配置虚拟网络接口
            handshake(tunnel);

            // Now we are connected. Set the flag and show the message.
            // 连接后 设置标志并显示消息。
            connected = true;
            mHandler.sendEmptyMessage(R.string.connected);

            // Packets to be sent are queued in this input stream.
            // 要发送的数据包将在此输入流中排队。
            FileInputStream in = new FileInputStream(mInterface.getFileDescriptor());

            // Packets received need to be written to this output stream.
            // 收到的数据包需要写入此输出流。
            FileOutputStream out = new FileOutputStream(mInterface.getFileDescriptor());

            // Allocate the buffer for a single packet.
            // 为单个数据包分配缓冲区。
            ByteBuffer packet = ByteBuffer.allocate(32767);

            /*
               We use a timer to determine the status of the tunnel. It
               works on both sides. A positive value means sending, and
               any other means receiving. We axeac_start with receiving.

              我们使用一个定时器来确定通道的状态。工作在通道两边。
              正值意味着发送，其他的意味着接收。 我们用axeac_start接收。
             */

            int timer = 0;

            // We keep forwarding packets till something goes wrong.
            // 我们不断转发数据包，直到出现问题
            while (true) {
                // Assume that we did not make any progress in this iteration.
                // 假设我们在这个迭代中没有取得任何进展。
                boolean idle = true;

                // Read the outgoing packet from the input stream.
                // 从输入流读取传出数据包
                int length = in.read(packet.array());
                if (length > 0) {
                    // Write the outgoing packet to the tunnel.
                    // 将输出的数据包写入隧道。
                    packet.limit(length);
                    tunnel.write(packet);
                    packet.clear();

                    // There might be more outgoing packets.
                    // 可能会有更多的传出数据包。
                    idle = false;

                    // If we were receiving, switch to sending.
                    // 如果收到，那么切换到发送
                    if (timer < 1) {
                        timer = 1;
                    }
                }

                // Read the incoming packet from the tunnel.
                // 从隧道读取传入的数据包。
                length = tunnel.read(packet);
                if (length > 0) {
                    // Ignore control messages, which axeac_start with zero.
                    // 忽略控制消息，其中axeac_start为零。
                    if (packet.get(0) != 0) {
                        // Write the incoming packet to the output stream.
                        // 将输入的数据包写入输出流。
                        out.write(packet.array(), 0, length);
                    }
                    packet.clear();

                    // There might be more incoming packets.
                    // 可能会有更多的传入数据包
                    idle = false;

                    // If we were sending, switch to receiving.
                    // 如果已经发送，那么切换到接收
                    if (timer > 0) {
                        timer = 0;
                    }
                }

                // If we are idle or waiting for the network, sleep for a fraction of time to avoid busy looping.
                // 如果空闲或在等待网络，睡眠一段时间以避免繁忙的循环。
                if (idle) {
                    Thread.sleep(100);

                    // Increase the timer. This is inaccurate but good enough,since everything is operated in non-blocking mode.
                    // 增加定时器 不准确但足够好，因为一切都是在非阻塞模式下运行的。
                    timer += (timer > 0) ? 100 : -100;

                    // We are receiving for a long time but not sending.
                    // 收到了很长时间但没有发送
                    if (timer < -15000) {
                        // Send empty control messages.
                        // 发送空控制消息
                        packet.put((byte) 0).limit(1);
                        for (int i = 0; i < 3; ++i) {
                            packet.position(0);
                            tunnel.write(packet);
                        }
                        packet.clear();

                        // Switch to sending.
                        // 切换到发送
                        timer = 1;
                    }

                    // We are sending for a long time but not receiving.
                    // 我们发送了很长时间，但没有接收
                    if (timer > 20000) {
                        throw new IllegalStateException("Timed out");
                    }
                }
            }
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "Got " + e.toString());
        } finally {
            try {
                tunnel.close();
            } catch (Exception e) {
                // ignore
            }
        }
        return connected;
    }

    private void handshake(DatagramChannel tunnel) throws Exception {
        /*
          To build a secured tunnel, we should perform mutual authentication
          and exchange session keys for encryption. To keep things simple in
          this demo, we just send the shared secret in plaintext and wait
          for the server to send the parameters.

          Allocate the buffer for handshaking.

          要建立一个安全的隧道，我们应该执行相互认证 并交换会话密钥进行加密。
          为了在这个演示中保持简单，我们只是用明文发送共享的secret，等待为服
          务器发送参数。
          分配缓冲区进行握手。
         */

        ByteBuffer packet = ByteBuffer.allocate(1024);

        // Control messages always axeac_start with zero.
        // 控制消息始终是axeac_start以0开始
        packet.put((byte) 0).put(mSharedSecret).flip();

        // Send the secret several times in case of packet loss.
        // 丢包的时候发几个secret
        for (int i = 0; i < 3; ++i) {
            packet.position(0);
            tunnel.write(packet);
        }
        packet.clear();

        // Wait for the parameters within a limited time.
        // 在有限的时间内等待参数
        for (int i = 0; i < 50; ++i) {
            Thread.sleep(100);

            // Normally we should not receive random packets.
            // 通常情况下，不应该收到随机数据包
            int length = tunnel.read(packet);
            if (length > 0 && packet.get(0) == 0) {
                configure(new String(packet.array(), 1, length - 1).trim());
                return;
            }
        }
        throw new IllegalStateException("Timed out");
    }

    private void configure(String parameters) throws Exception {
        // If the old interface has exactly the same parameters, use it!
        // 如果旧界面有完全相同的参数，复用它。
        if (mInterface != null && parameters.equals(mParameters)) {
            Log.i(TAG, "Using the previous interface");
            return;
        }

        // Configure a builder while parsing the parameters.
        // 解析参数时构建builder
        Builder builder = new Builder();
        for (String parameter : parameters.split(" ")) {
            String[] fields = parameter.split(",");
            try {
                switch (fields[0].charAt(0)) {
                    case 'm':
                        builder.setMtu(Short.parseShort(fields[1]));
                        break;
                    case 'a':
                        builder.addAddress(fields[1], Integer.parseInt(fields[2]));
                        break;
                    case 'r':
                        builder.addRoute(fields[1], Integer.parseInt(fields[2]));
                        break;
                    case 'd':
                        builder.addDnsServer(fields[1]);
                        break;
                    case 's':
                        builder.addSearchDomain(fields[1]);
                        break;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Bad parameter: " + parameter);
            }
        }

        // Close the old interface since the parameters have been changed.
        // 参数更改后，关闭旧界面
        try {
            mInterface.close();
        } catch (Exception e) {
            // ignore
        }

        // Create a new interface using the builder and save the parameters.
        // 使用builder创建新界面并保存参数
        mInterface = builder.setSession(mServerAddress)
                .setConfigureIntent(mConfigureIntent)
                .establish();
        mParameters = parameters;
        Log.i(TAG, "New interface: " + parameters);
    }
}