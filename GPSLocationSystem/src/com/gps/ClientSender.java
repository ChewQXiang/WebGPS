package com.gps; // 指定此類別屬於 com.gps 套件

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class ClientSender {

    public static void main(String[] args) {
        String serverIP = "127.0.0.1";  // ★本機測試使用 localhost，部署時請改為報告主機的實際 IP
        int serverPort = 5000;          // ★固定與伺服器端一致的通訊埠號

        // 初始模擬位置
        double latitude = 24.0;
        double longitude = 120.0;
        Random random = new Random(); // 用來模擬走動

        while (true) { // 外層無窮迴圈：若連線失敗可重試
            Socket socket = null;
            try {
                // 嘗試建立與伺服器的連線
                socket = new Socket(serverIP, serverPort);
                OutputStream output = socket.getOutputStream(); // 建立輸出串流
                PrintWriter writer = new PrintWriter(output, true); // 建立文字寫入器，自動 flush

                System.out.println("已連接到Server，開始傳送GPS位置資料...");

                while (true) { // 內層無窮迴圈：持續送資料
                    String id = "user001"; // 模擬使用者 ID

                    // 模擬隨機位置變動（±0.005）
                    latitude += (random.nextDouble() - 0.5) * 0.01;
                    longitude += (random.nextDouble() - 0.5) * 0.01;
                    String timestamp = java.time.LocalDateTime.now().toString(); // 取得時間戳記

                    // 建立位置物件
                    Location location = new Location(id, latitude, longitude, timestamp);

                    // 加密位置資料（轉為字串後進行簡單 Caesar 加密）
                    String encryptedData = Encryptor.encrypt(location.toString());

                    // 傳送加密資料到伺服器
                    writer.println(encryptedData);

                    System.out.println("已傳送一筆資料：" + location); // 顯示原始資料（方便觀察）

                    // 每 3 秒傳送一次資料
                    Thread.sleep(3000);
                }

            } catch (Exception e) {
                // 若連線中途中斷或發生例外
                if (socket != null) {
                    try {
                        socket.close(); // 關閉 socket 連線
                    } catch (Exception ex) {
                        ex.printStackTrace(); // 補充印出 socket 關閉時的例外
                    }
                }

                // 印出錯誤原因
                System.out.println("連線失敗：" + e.getMessage());

                // 稍作等待後重試（防止 CPU 過度消耗）
                try {
                    Thread.sleep(5000); // 等待 5 秒再重新嘗試連線
                } catch (InterruptedException ie) {
                    ie.printStackTrace(); // 印出中斷例外
                }
            }
        }
    }
}
