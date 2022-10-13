import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class simpleSever {
    private static int serverport = 5051; // 自訂的 Port
    private static ServerSocket serverSocket; // 伺服端的Socket
    private static int count = 0; // 計算有幾個 Client 端連線
    private static int order = 0;    
    // 用 ArrayList 來儲存每個 Client 端連線
    private static ArrayList<Socket> clients = new ArrayList();
    private static ArrayList<String> hostID = new ArrayList();
    private static ArrayList<String> userId = new ArrayList();
    private static ArrayList<String> userIp = new ArrayList();
    private static ArrayList<String> userImage = new ArrayList();
    public static int[] imageSize = new int[16];

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(serverport);
            System.out.println("Server is start.");
            // 顯示等待客戶端連接
            System.out.println("Waiting for client connect");
            // 當Server運作中時
            while (!serverSocket.isClosed()) {
                // 呼叫等待接受客戶端連接
                waitNewClient();
            }
        } catch (IOException e) {
            System.out.println("Server Socket ERROR");
        }
    }

    public static void waitNewClient() {
        try {
            Socket socket = serverSocket.accept();
            ++count;
            System.out.println("現在使用者個數：" + count);

            // 呼叫加入新的 Client 端
            addNewClient(socket);

        } catch (IOException e) {
        }
    }

    public static void addNewClient(final Socket socket) throws IOException {
        // 以新的執行緒來執行
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 增加新的 Client 端
                    clients.add(socket);
                    BufferedWriter bw;
                    bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader buf = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    bw.write("hello" + "\n");
                    // // 立即發送
                    bw.flush();
                    String getIDString = buf.readLine();

                    if (getIDString.equals("clear")) {
                        castMsg2(getIDString);
                    } else if (getIDString.equals("camera")) {
                        order++;
                        bw.write(order + "\n");
                        bw.flush();
                        System.out.println(order);

                    } else if (getIDString.equals("image")) {
                        hostID.add(buf.readLine());
                        System.out.println("hostID : " + hostID);
                        String imageType = buf.readLine();
                        Thread.sleep(300);
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        try {
                            byte[] buffer = (byte[]) ois.readObject();
                            FileOutputStream fos = new FileOutputStream("./image" + hostID.size() + "." + imageType);
                            imageSize[hostID.size() - 1] = buffer.length;
                            System.out.println("buffer len: " + String.valueOf(buffer.length));
                            fos.write(buffer);
                            // Thread.sleep(200);
                            fos.close();
                            clients.remove(socket);
                            --count;
                            // gifToImages("image1.gif");
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        // castMsg2(getIDString);
                    } else if (getIDString.indexOf(",") < 0) {
                        userId.add(getIDString);
                        userIp.add(socket.getInetAddress().toString());
                        userImage.add("-1");
                        System.out.println(userId);
                        System.out.println(userIp);
                        Thread.sleep(200);
                        bw.write(userId.size() - 1 + "\n");
                        bw.flush();

                        String physicalLength = buf.readLine();
                        String[] tmpArray = physicalLength.split(",");
                        System.out.println("send1");
                    }
                    String msg;
                    // int i = 0;
                    while ((msg = buf.readLine()) != null) {
                        System.out.println(msg);
                        if (msg.indexOf("clear") >= 0) {
                            castMsg2(msg);
                        } else if (msg.indexOf("play") >= 0) {
                            castMsg2(msg);

                        } else {
                            String[] tmpArray = msg.split(",");
                            int imageNumber = Integer.valueOf(tmpArray[tmpArray.length - 2]);
                            System.out.println("imageNumber: " + imageNumber);
                            String type = ".jpg";
                            File file = new File(".\\image" + imageNumber + ".jpg");
                            if (file.exists()) {
                                type = "jpg";
                            }
                            file = new File(".\\image" + imageNumber + ".gif");
                            if (file.exists()) {
                                type = "gif";
                            }
                            file = new File(".\\image" + imageNumber + ".mp4");
                            if (file.exists()) {
                                type = "mp4";
                            }
                            castMsg2(imageSize[imageNumber - 1] + "," + type + "," + msg);
                            // Thread.sleep(100);
                            System.out.println("接收到的資料是：" + msg);
                            String unSplite = tmpArray[tmpArray.length - 1];
                            System.out.println("unSplite: " + unSplite);
                            if (unSplite.indexOf(".") >= 0) {
                                String[] spliteIP = unSplite.split("\\.");
                                System.out.println("spliteIP: " + spliteIP.length);
                                int[] spliteIPInt = new int[spliteIP.length];
                                for (int j = 0; j < spliteIP.length; j++) {
                                    spliteIPInt[j] = Integer.valueOf(spliteIP[j]);
                                    System.out.println("spliteIP: " + spliteIPInt[j]);
                                }
                                Thread.sleep(250);
                                // castImage(imageNumber, spliteIPInt, tmpArray[tmpArray.length - 2], type);
                            } else {
                                int[] spliteIPInt = new int[1];
                                spliteIPInt[0] = Integer.valueOf(unSplite);
                                // castImage(imageNumber, spliteIPInt, tmpArray[tmpArray.length - 2], type);
                            }
                        }

                    }
                } catch (IOException e) {
                    e.getStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    // 移除客戶端
                    // clients.remove(socket);
                    // --count;
                    // System.out.println("現在使用者個數：" + count);
                }
            }
        });

        // 啟動執行緒
        t.start();
    }

    public static void castMsg2(String Msg) {
        // 創造socket陣列
        Socket[] clientArrays = new Socket[clients.size()];
        // 將 clients 轉換成陣列存入 clientArrays
        clients.toArray(clientArrays);
        // System.out.println(clientArrays);

        // 走訪 clientArrays 中的每一個元素
        for (Socket socket : clientArrays) {
            try {
                // 創造網路輸出串流
                BufferedWriter bw;
                // System.out.println(client);
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                // 寫入訊息到串流
                bw.write(Msg + "\n");
                // 立即發送
                bw.flush();
            } catch (IOException e) {
            }
        }

    }

    

}
