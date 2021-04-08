import java.io.*;
import java.net.*; 

public class testServer {
 
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(5050);
            Socket s = ss.accept();
 
            BufferedReader buf = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter w = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
            String msg;
            int i = 0;
 
            while ((msg = buf.readLine()) != null) {
                System.out.println("接收到的資料是：" + msg);
                w.println("OK 哈哈哈 " + i++);
            }
 
            s.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}