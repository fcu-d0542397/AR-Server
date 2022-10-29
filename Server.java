import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class Server {
    private static int serverport = 5050; // 自訂的 Port
    private static ServerSocket serverSocket; // 伺服端的Socket
    private static int count = 0; // 計算有幾個 Client 端連線
    private static ArrayList<String> userId = new ArrayList();
    private static ArrayList<String> userIp = new ArrayList();
    private static ArrayList<String> userImage = new ArrayList();
    private static ArrayList<String> hostID = new ArrayList();
    private static ArrayList<String> dataPath = new ArrayList();
    // private static ArrayList<String> textSpeed = new ArrayList();
    static ArrayList<Integer> clientIndex = new ArrayList<>();

    static String[] runningText = new String[16];
    static String[] dirNumber = new String[16];
    static String[] textSpeed = new String[16];
    static double[] physicalX = new double[16];
    static double[] physicalY = new double[16];
    static String phyXString = "";
    static String phyYString = "";
    public static Thread t2;
    public static String mode;

    public static String cropType;

    private static int order = 0;
    public static String location;
    public static int[] imageSize = new int[16];
    public static int[][] imageSended = new int[16][16];

    public static String[] virtualCommand = new String[16];
    public static String[] virtualIp = new String[16];
    public static int virtualCount = 0;

    public static int palyFlag = 0;
    public static String dirFlag = "-1";
    // public static String dataPath;
    // public static String lng;

    // 用 ArrayList 來儲存每個 Client 端連線
    private static ArrayList<Socket> clients = new ArrayList();
    static int countReadData = 0;

    public static void main(String[] args) {

        for (int i = 0; i < virtualIp.length; i++) {
            virtualIp[i] = "-1";
        }
        t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("successfully");
            }
        });
        t2.start();
        for (int i = 1; i < 30; i++) {
            File file = new File(".\\image" + i + ".jpg");
            if (file.exists()) {
                file.delete();
                System.out.println(file.getName() + "File deleted successfully");
            }
            file = new File(".\\image" + i + ".gif");
            if (file.exists()) {
                file.delete();
                System.out.println(file.getName() + "File deleted successfully");
            }
            file = new File(".\\image" + i + ".mp4");
            if (file.exists()) {
                file.delete();
                System.out.println(file.getName() + "File deleted successfully");
            }
            file = new File(".\\run" + i + ".jpg");
            if (file.exists()) {
                file.delete();
                System.out.println(file.getName() + "File deleted successfully");
            }
            file = new File(".\\run" + i + ".gif");
            if (file.exists()) {
                file.delete();
                System.out.println(file.getName() + "File deleted successfully");
            }
            // if (file.delete()) {

            // } else {
            // System.out.println("Failed to delete the file");
            // }
            // file = new File("C:\\Users\\ak479\\Desktop\\server\\AR Server\\image" + i +
            // ".gif");
            // if (file.delete()) {
            // System.out.println("File deleted successfully");
            // } else {
            // System.out.println("Failed to delete the file");
            // }

        }

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

    // 等待接受 Client 端連接
    public static void waitNewClient() {
        try {
            countReadData = 0;
            Socket socket = serverSocket.accept();
            ++count;
            System.out.println("現在使用者個數：" + count);

            // 呼叫加入新的 Client 端
            addNewClient(socket);

        } catch (IOException e) {
        }
    }

    // 加入新的 Client 端
    public static void addNewClient(final Socket socket) throws IOException {
        // 以新的執行緒來執行
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 增加新的 Client 端
                    clients.add(socket);
                    // System.out.println(clients);
                    // System.out.println(clients.get(0));
                    System.out.println(socket.getInetAddress());
                    // 取得網路串流

                    // BufferedReader br = new BufferedReader(new
                    // InputStreamReader(socket.getInputStream()));
                    // // 當Socket已連接時連續執行

                    // BufferedWriter bw;
                    // bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    // // 寫入訊息到串流
                    // bw.write("hello" + "\n");
                    // // // 立即發送
                    // bw.flush();

                    // FileInputStream fis = new FileInputStream("./marker/" + (count - 1) +".jpg");
                    // byte[] buffer = new byte[fis.available()];
                    // fis.read(buffer);
                    // ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    // oos.writeObject(buffer);
                    // System.out.println("send1");

                    // FileInputStream fis2 = new FileInputStream("./pichu.jpg");
                    // buffer = null;
                    // buffer = new byte[fis2.available()];
                    // fis2.read(buffer);
                    // oos.writeObject(buffer);
                    // System.out.println("send2");
                    // oos.close();

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
                        // Thread.sleep(500);
                        bw.write(order + "\n");
                        bw.flush();
                        Thread.sleep(100);
                        bw.write(socket.getInetAddress().toString() + "\n");
                        bw.flush();
                        bw.write(dataPath.get(dataPath.size() - 1) + "\n");
                        bw.flush();
                        System.out.println("dirFlag: " + dirFlag);
                        bw.write(dirFlag + "\n");
                        bw.flush();
                        dirFlag = "-1";
                        // if(!runningText[hostID.size()].equals("")){
                        // bw.write("run" + "\n");
                        // bw.flush();
                        // }
                        // else{
                        // bw.write("other" + "\n");
                        // bw.flush();
                        // }
                        System.out.println(order);
                        // if (buf.readLine().equals("mode")) {
                        // System.out.println("dasdsadasdddddd");
                        // bw.write(cropType + "\n");
                        // bw.flush();
                        // }

                    } else if (getIDString.equals("image")) {
                        hostID.add(buf.readLine());
                        System.out.println("hostID : " + hostID);
                        String tmp = buf.readLine();
                        System.out.println(tmp);
                        dataPath.add(tmp);
                        // dataPath = buf.readLine();
                        // cropType = buf.readLine();
                        // System.out.println("cropType : " + cropType);
                        String imageType = buf.readLine();
                        Thread.sleep(300);
                        dirNumber[hostID.size()] = "none";
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
                    } else if (getIDString.equals("text")) {
                        hostID.add(buf.readLine());
                        System.out.println("hostID : " + hostID);
                        String runText = buf.readLine();
                        runningText[hostID.size()] = runText;
                        System.out.println("str: " + runText);
                        String tmp = buf.readLine();
                        dataPath.add(tmp);
                        String tmppath = tmp;
                        tmp = buf.readLine();
                        System.out.println("dirFlag: " + tmp);
                        dirFlag = tmp;
                        dirNumber[hostID.size()] = dirFlag;
                        tmp = buf.readLine();
                        String speed = tmp;
                        textSpeed[hostID.size()] = speed;
                        Thread.sleep(300);
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        try {
                            byte[] buffer = (byte[]) ois.readObject();
                            if (tmppath.indexOf("gif") > -1) {
                                FileOutputStream fos = new FileOutputStream("./run" + hostID.size() + ".gif");
                                imageSize[hostID.size() - 1] = buffer.length;
                                System.out.println("buffer len: " + String.valueOf(buffer.length));
                                fos.write(buffer);
                                // Thread.sleep(200);
                                fos.close();
                                clients.remove(socket);
                                --count;
                            }

                            else {
                                FileOutputStream fos = new FileOutputStream("./run" + hostID.size() + ".jpg");
                                imageSize[hostID.size() - 1] = buffer.length;
                                System.out.println("buffer len: " + String.valueOf(buffer.length));
                                fos.write(buffer);
                                // Thread.sleep(200);
                                fos.close();
                                clients.remove(socket);
                                --count;
                            }
                            // gifToImages("image1.gif");
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    else if (getIDString.indexOf(",") < 0) {
                        userId.add(getIDString);
                        userIp.add(socket.getInetAddress().toString());
                        userImage.add("-1");
                        System.out.println(userId);
                        System.out.println(userIp);
                        Thread.sleep(200);
                        bw.write(userId.size() - 1 + "\n");
                        bw.flush();
                        String physicalLength = buf.readLine();
                        for (int i = 0; i < virtualCommand.length; i++) {
                            System.out.println(virtualIp[i]);
                            System.out.println(socket.getInetAddress().toString());
                            if (virtualIp[i].equals(socket.getInetAddress().toString())) {
                                System.out.println("7594546");
                                String[] tmp = virtualCommand[i].split(",");
                                int imageNumber = Integer.valueOf(tmp[tmp.length - 2]);
                                String type = ".jpg";
                                mode = "normal";
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
                                file = new File(".\\run" + imageNumber + ".jpg");
                                if (file.exists()) {
                                    type = "jpg";
                                    mode = "run";
                                    mode = mode + "#" + textSpeed[imageNumber];
                                    // mode = mode + "#" + runningText[imageNumber];
                                }
                                file = new File(".\\run" + imageNumber + ".gif");
                                if (file.exists()) {
                                    type = "gif";
                                    mode = "run";
                                    mode = mode + "#" + textSpeed[imageNumber];
                                    // mode = mode + "#" + runningText[imageNumber];
                                }
                                System.out.println("dirNumber :" + dirNumber[imageNumber]);
                                if (dirNumber[imageNumber].equals("1")) {
                                    mode = "r" + mode;
                                } else if (dirNumber[imageNumber].equals("0")) {
                                    mode = "l" + mode;
                                } else if (dirNumber[imageNumber].equals("2")) {
                                    mode = "u" + mode;
                                } else if (dirNumber[imageNumber].equals("3")) {
                                    mode = "d" + mode;
                                }
                                bw.write(mode + "," + virtualCommand[i] + "\n");
                                bw.flush();
                                // if(mode.indexOf("run")>-1){
                                // FileInputStream fis = new FileInputStream("./run" + imageNumber + "." +
                                // type);
                                // byte[] buffer = new byte[fis.available()];
                                // fis.read(buffer);
                                // ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                // Thread.sleep(100);
                                // oos.writeObject(buffer);
                                // // Thread.sleep(200);
                                // System.out.println(545646546);
                                // }
                                // else{
                                // FileInputStream fis = new FileInputStream("./image" + imageNumber + "." +
                                // type);
                                // byte[] buffer = new byte[fis.available()];
                                // fis.read(buffer);
                                // ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                // Thread.sleep(100);
                                // oos.writeObject(buffer);
                                // // Thread.sleep(200);
                                // System.out.println(545646546);
                                // }

                                break;
                            } else if (!virtualIp[i].equals(socket.getInetAddress().toString())
                                    && i == virtualCommand.length - 1) {
                                bw.write("None" + "\n");
                                bw.flush();
                            }
                        }

                        String[] tmpArray = physicalLength.split(",");
                        physicalX[userId.size() - 1] = Double.parseDouble(tmpArray[0]);
                        physicalY[userId.size() - 1] = Double.parseDouble(tmpArray[1]);
                        System.out.println(physicalX[userId.size() - 1] + "xxxxx");
                        System.out.println(physicalY[userId.size() - 1] + "yyyyy");
                        System.out.println("ssssadsadasdsadsadsad");
                        phyXString = "";
                        phyYString = "";
                        for (int i = 0; i < userId.size(); i++) {
                            // if (i == userId.size() - 1) {
                            // phyXString = phyXString + physicalX[i];
                            // phyYString = phyYString + physicalY[i];
                            // } else {
                            // phyXString = phyXString + physicalX[i] + ",";
                            // phyYString = phyYString + physicalY[i] + ",";
                            // }
                            if (i == userId.size() - 1) {
                                phyYString = phyYString + physicalX[i];
                                phyXString = phyXString + physicalY[i];
                            } else {
                                phyYString = phyYString + physicalX[i] + ",";
                                phyXString = phyXString + physicalY[i] + ",";
                            }
                        }
                        System.out.println("send1");
                    }
                    // else {
                    // castMsg2(getIDString);
                    // Thread.sleep(500);
                    // String[] tmpArray = getIDString.split(",");
                    // int imageNumber = Integer.valueOf(tmpArray[tmpArray.length - 1]);
                    // System.out.println("imageNumber: " + imageNumber);
                    // FileInputStream fis = new FileInputStream("./image" + imageNumber + ".jpg");
                    // byte[] buffer = new byte[fis.available()];
                    // fis.read(buffer);
                    // ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    // oos.writeObject(buffer);
                    // }
                    String msg;
                    // int i = 0;
                    while ((msg = buf.readLine()) != null) {
                        // if (msg.indexOf("play") < 0 && palyFlag == 1) {
                        // t2.stop();
                        // }
                        System.out.println(msg);
                        if (msg.indexOf("clear") >= 0) {
                            castMsg2(msg);
                        } else if (msg.indexOf("play") >= 0) {
                            Thread.sleep(300);
                            castMsg2(msg);
                            palyFlag = 1;
                            // t2.stop();
                            // t2 = new Thread(new Runnable() {
                            // @Override
                            // public void run() {
                            // while(true){
                            // try {
                            // Thread.sleep(7500);
                            // castMsg2("play");
                            // } catch (InterruptedException e) {
                            // // TODO Auto-generated catch block
                            // e.printStackTrace();
                            // }
                            // }
                            // }
                            // });
                            // t2.start();

                        } else if (msg.indexOf("physical") >= 0 && countReadData <= 20) {
                            // Thread.sleep(500);
                            Thread.sleep(300);
                            bw.write(phyXString + "?" + phyYString + "\n");
                            bw.flush();
                            countReadData = countReadData + 1;
                            bw.write(String.valueOf(countReadData)+ "\n");
                            bw.flush();
                        } else if (msg.indexOf("image2") >= 0) {
                            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                            try {
                                byte[] buffer = (byte[]) ois.readObject();
                                FileOutputStream fos = new FileOutputStream("./image" + hostID.size() + "." + "jpg");
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
                        } else if (msg.indexOf("virtual") >= 0) {
                            String[] tmp = msg.split(",");
                            virtualIp[virtualCount] = tmp[tmp.length - 4];
                            int imageNumber = Integer.valueOf(tmp[tmp.length - 3]);
                            String type = ".jpg";
                            mode = "normal";
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
                            file = new File(".\\run" + imageNumber + ".jpg");
                            if (file.exists()) {
                                type = "jpg";
                                mode = "run";
                                // mode = mode + "#" + runningText[imageNumber];
                                mode = mode + "#" + textSpeed[imageNumber];
                            }
                            file = new File(".\\run" + imageNumber + ".gif");
                            if (file.exists()) {
                                type = "gif";
                                mode = "run";
                                // mode = mode + "#" + runningText[imageNumber];
                                mode = mode + "#" + textSpeed[imageNumber];
                            }
                            System.out.println("virtual");
                            virtualCommand[virtualCount] = type + "," + msg;
                            virtualCount++;
                        } else {
                            String[] tmpArray = msg.split(",");
                            int imageNumber = Integer.valueOf(tmpArray[tmpArray.length - 2]);
                            System.out.println("imageNumber: " + imageNumber);
                            String type = ".jpg";
                            mode = "normal";
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
                            file = new File(".\\run" + imageNumber + ".jpg");
                            if (file.exists()) {
                                type = "jpg";
                                mode = "run";
                                // mode = mode + "#" + runningText[imageNumber];
                                mode = mode + "#" + textSpeed[imageNumber];
                            }
                            file = new File(".\\run" + imageNumber + ".gif");
                            if (file.exists()) {
                                type = "gif";
                                mode = "run";
                                // mode = mode + "#" + runningText[imageNumber];
                                mode = mode + "#" + textSpeed[imageNumber];
                            }
                            if (dirNumber[imageNumber].equals("1")) {
                                mode = "r" + mode;
                            } else if (dirNumber[imageNumber].equals("0")) {
                                mode = "l" + mode;
                            } else if (dirNumber[imageNumber].equals("2")) {
                                mode = "u" + mode;
                            } else if (dirNumber[imageNumber].equals("3")) {
                                mode = "d" + mode;
                            }
                            System.out.println("dirNumber :" + dirNumber[imageNumber]);
                            System.out.println("mode :" + mode);
                            castMsg2(mode + "," + imageSize[imageNumber - 1] + "," + type + "," + msg);
                            // Thread.sleep(100);
                            System.out.println("接收到的資料是：" + msg);
                            String unSplite = tmpArray[tmpArray.length - 1];
                            System.out.println("unSplite: " + unSplite);
                            if (unSplite.indexOf("&") >= 0) {
                                String[] spliteIP = unSplite.split("&");
                                System.out.println("spliteIP: " + spliteIP.length);
                                int[] spliteIPInt = new int[spliteIP.length - 1];
                                for (int j = 0; j < spliteIP.length - 1; j++) {
                                    spliteIPInt[j] = Integer.valueOf(spliteIP[j]);
                                    System.out.println("spliteIP: " + spliteIPInt[j]);
                                }
                                Thread.sleep(250);
                                castImage(imageNumber, spliteIPInt, tmpArray[tmpArray.length - 2], type);
                            } else {
                                int[] spliteIPInt = new int[1];
                                spliteIPInt[0] = Integer.valueOf(unSplite);
                                castImage(imageNumber, spliteIPInt, tmpArray[tmpArray.length - 2], type);
                                // if(userImage.get(Integer.valueOf(unSplite)).equals("-1")){
                                // castImage(imageNumber, spliteIPInt);
                                // }
                                // else
                                // if(userImage.get(Integer.valueOf(unSplite)).equals(tmpArray[tmpArray.length -
                                // 2])){

                                // }
                                // else{
                                // castImage(imageNumber, spliteIPInt);
                                // userImage.set(Integer.valueOf(unSplite), tmpArray[tmpArray.length - 2]);
                                // }

                            }
                        }

                        // Thread.sleep(1000);
                        // FileInputStream fis = new FileInputStream("./image" + imageNumber + ".jpg");
                        // byte[] buffer = new byte[fis.available()];
                        // fis.read(buffer);
                        // ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        // oos.writeObject(buffer);
                        // w.println("OK 哈哈哈 " + i++);
                    }
                    // String msg;
                    // int i = 0;
                    // while ((msg = buf.readLine()) != null) {

                    // System.out.println("接收到的資料是：" + msg);
                    // castMsg2(msg);
                    // // w.println("OK 哈哈哈 " + i++);
                    // }

                    // while (socket.isConnected()) {
                    // }
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

    // 廣播訊息給其它的客戶端
    public static void castMsg(String Msg) {

        if (clients.size() > 1) {

            int sendTo = 0;
            for (int i = 0; i < clients.size(); i++) {
                if (clients.get(i).getInetAddress().toString().equals("/192.168.43.37")) {
                    sendTo = i;
                    break;
                }
            }
            // 創造socket陣列
            Socket[] clientArrays = new Socket[clients.size()];
            // 將 clients 轉換成陣列存入 clientArrays
            clients.toArray(clientArrays);
            // System.out.println(clientArrays);

            // 走訪 clientArrays 中的每一個元素
            // for (Socket socket : clientArrays) {
            try {
                // 創造網路輸出串流
                BufferedWriter bw;
                // System.out.println(client);
                bw = new BufferedWriter(new OutputStreamWriter(clients.get(sendTo).getOutputStream()));
                // 寫入訊息到串流
                bw.write(Msg + "\n");
                // 立即發送
                bw.flush();
            } catch (IOException e) {
            }
            // }

        }

    }

    public static void castImage(int fileName, int[] tmp, String marker, String type) {

        // int sendTo = 0;
        ArrayList<Integer> values = new ArrayList<>();
        int clientIp[] = new int[userIp.size()];
        for (int i = 0; i < clientIp.length; i++) {
            clientIp[i] = -1;
        }
        int k = 0;
        int index = -1;
        for (int i = 0; i < tmp.length; i++) {
            for (int j = 0; j < clients.size(); j++) {
                if (userIp.get(tmp[i]).equals(clients.get(j).getInetAddress().toString())) {
                    clientIp[k] = j;
                    k++;
                }
            }
        }

        for (int i = 0; i < tmp.length; i++) {
            System.out.println(tmp[i]);
        }

        for (int i = 0; i < tmp.length; i++) {
            if (userImage.get(tmp[i]).equals("-1")) {
                System.out.println(tmp[i] + "-1");
                userImage.set(tmp[i], marker);

            } else if (userImage.get(i).equals(marker)) {
                System.out.println(tmp[i] + "same");
                for (int j = 0; j < clientIp.length; j++) {
                    if (clientIp[j] == tmp[i]) {
                        clientIp[i] = -1;
                    }
                }
            } else {
                System.out.println(tmp[i] + "other");
                userImage.set(tmp[i], marker);
            }
        }
        for (int i = 0; i < tmp.length; i++) {

            System.out.println(tmp[i]);
        }

        // for (int i = 0; i < clientIp.length; i++) {
        // if (clientIp[i] != -1) {
        // if (userImage.get(i).equals("-1")) {
        // System.out.println(i + "-1");
        // userImage.set(i, marker);

        // } else if (userImage.get(i).equals(marker)) {
        // System.out.println(i + "same");
        // clientIp[i] = -1;
        // } else {
        // System.out.println(i + "other");
        // userImage.set(i, marker);
        // }
        // }

        // }

        for (int i = 0; i < clientIp.length; i++) {
            System.out.println(clientIp[i]);
        }
        // 創造socket陣列
        Socket[] clientArrays = new Socket[clients.size()];
        // 將 clients 轉換成陣列存入 clientArrays
        clients.toArray(clientArrays);
        // System.out.println(clientArrays);

        // 走訪 clientArrays 中的每一個元素
        long time1, time2, time3;

        for (int i = 0; i < clientIp.length; i++) {
            try {
                // BufferedReader readBuf = new BufferedReader(new
                // InputStreamReader(clients.get(clientIp[i]).getInputStream()));
                // if(readBuf.readLine().equals("exist")){

                // }
                // System.out.println(client);
                // 創造網路輸出串流
                if (imageSended[Integer.valueOf(fileName) - 1][i] == 0) {
                    imageSended[Integer.valueOf(fileName) - 1][i] = 1;
                    if (mode.indexOf("run") > -1) {
                        FileInputStream fis = new FileInputStream("./run" + fileName + "." + type);
                        byte[] buffer = new byte[fis.available()];
                        fis.read(buffer);
                        if (clientIp[i] != -1) {
                            ObjectOutputStream oos = new ObjectOutputStream(clients.get(clientIp[i]).getOutputStream());
                            Thread.sleep(100);
                            time1 = System.currentTimeMillis();
                            oos.writeObject(buffer);
                            // Thread.sleep(200);
                            time2 = System.currentTimeMillis();
                            System.out.println("doSomething()花了：" + (time2 - time1) / 1000 + "秒");
                            System.out.println(545646546);
                        }
                    } else {
                        FileInputStream fis = new FileInputStream("./image" + fileName + "." + type);
                        byte[] buffer = new byte[fis.available()];
                        fis.read(buffer);
                        if (clientIp[i] != -1) {
                            ObjectOutputStream oos = new ObjectOutputStream(clients.get(clientIp[i]).getOutputStream());
                            Thread.sleep(100);
                            time1 = System.currentTimeMillis();
                            oos.writeObject(buffer);
                            // Thread.sleep(200);
                            time2 = System.currentTimeMillis();
                            System.out.println("doSomething()花了：" + (time2 - time1) / 1000 + "秒");
                            System.out.println(545646546);
                        }

                    }

                } else {
                    System.out.println("sendED");
                }
                // BufferedReader buf = new BufferedReader(new
                // InputStreamReader(socket.getInputStream()));
                // PrintWriter w = new PrintWriter(new
                // OutputStreamWriter(socket.getOutputStream()), true);
                // w.println(Msg);

            } catch (IOException | InterruptedException e) {
            }
            //

        }
        for (Socket socket : clientArrays) {

        }

    }

    public static void castMsg3(String msg, int[] tmp) {

        // int sendTo = 0;
        ArrayList<Integer> values = new ArrayList<>();
        int clientIp[] = new int[userIp.size()];
        for (int i = 0; i < clientIp.length; i++) {
            clientIp[i] = -1;
        }
        int k = 0;
        int index = -1;

        for (int i = 0; i < tmp.length; i++) {
            for (int j = 0; j < clients.size(); j++) {
                if (userIp.get(tmp[i]).equals(clients.get(j).getInetAddress().toString())) {
                    clientIp[k] = j;
                    k++;
                }
            }
        }
        // 創造socket陣列
        Socket[] clientArrays = new Socket[clients.size()];
        // 將 clients 轉換成陣列存入 clientArrays
        clients.toArray(clientArrays);
        // System.out.println(clientArrays);

        // 走訪 clientArrays 中的每一個元素

        for (int i = 0; i < clientIp.length; i++) {
            try {
                // 創造網路輸出串流

                if (clientIp[i] != -1) {
                    BufferedWriter bw;
                    // System.out.println(client);
                    bw = new BufferedWriter(new OutputStreamWriter(clients.get(clientIp[i]).getOutputStream()));
                    // 寫入訊息到串流
                    bw.write(msg + "\n");
                    // 立即發送
                    bw.flush();
                }

            } catch (IOException e) {

            }

        }
        for (Socket socket : clientArrays) {

        }

    }

    public static void castMsg2(String Msg) {

        // int sendTo = 0;
        // for (int i = 0; i < clients.size(); i++) {
        // if (clients.get(i).getInetAddress().toString().equals("/192.168.43.37")) {
        // sendTo = i;
        // break;
        // }
        // }
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

                // BufferedReader buf = new BufferedReader(new
                // InputStreamReader(socket.getInputStream()));
                // PrintWriter w = new PrintWriter(new
                // OutputStreamWriter(socket.getOutputStream()), true);
                // w.println(Msg);

            } catch (IOException e) {
            }
        }

    }

    public static void gifToImages(String imagePath) throws IOException {
        GifDecoder decoder = new GifDecoder();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setDelay(300); // ???? 500/ms
        encoder.setRepeat(0); // 0:????? -1:??????
        encoder.start(bos); // git???生成先??bos?設定

        int status = decoder.read(imagePath);
        if (status != GifDecoder.STATUS_OK) {
            throw new IOException("read image " + imagePath + " error!");
        }
        for (int i = 0; i < decoder.getFrameCount(); i++) {
            if (decoder.getFrameCount() < 50) {
                BufferedImage bufferedImage = decoder.getFrame(i);// ?取每?BufferedImage流
                encoder.addFrame(bufferedImage);
            } else {
                BufferedImage bufferedImage = decoder.getFrame(i);// ?取每?BufferedImage流
                encoder.addFrame(bufferedImage);
            }
            // else if (i % 2 == 0) {
            // BufferedImage bufferedImage = decoder.getFrame(i);// ?取每?BufferedImage流
            // encoder.addFrame(bufferedImage);
            // }
        }
        encoder.finish();
        String path3 = "./imageccccc.gif";
        File filePath = new File(path3);
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(imagePath);
            // bos?生成???gif?????????吐?出?
            outputStream.write(bos.toByteArray());
            outputStream.close();
        } catch (IOException e) {
        }

    }
}
