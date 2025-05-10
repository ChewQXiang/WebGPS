package com.gps; 

// 匯入 Java 網路與輸入相關的類別
import java.io.BufferedReader; 
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerReceiver {

    public static void main(String[] args) {
        WebServer.start();  // 啟動 WebServer，讓 HTTP API 可供前端查詢位置

        int port = 5000; // 設定接收 Socket 傳輸資料的通訊埠為 5000

        while (true) {  // 外層無窮迴圈：一直等待新的 Client 連線
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server啟動，等待Client連線...");

                // 等待 Client 端連線進來，程式會在此行停住直到有連線進入
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client已連線成功！");

                // 用 BufferedReader 來讀取來自 Client 的資料（加密的字串）
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                while (true) {  // 內層無窮迴圈：持續從已連接的 Client 讀取資料
                    String encryptedData = reader.readLine(); // 一次讀取一行（加密的定位資料）

                    if (encryptedData == null) { // 若讀到 null 表示 Client 中斷連線
                        System.out.println("Client連線中斷，準備重新等待...");
                        break; // 跳出內層迴圈，等待下一個 Client
                    }

                    // 解密傳來的資料
                    String decryptedData = Encryptor.decrypt(encryptedData);
                    System.out.println("接收到位置資料：" + decryptedData);

                    try {
                        // 假設解密後格式為：id: user001, lat: 23.4, lon: 121.5, time: 2025-05-09T21:00:00Z
                        String[] parts = decryptedData.split(", "); // 先以逗號分割成 4 個欄位
                        String id = parts[0].split(": ")[1]; // 拿到 id 部分的值
                        double lat = Double.parseDouble(parts[1].split(": ")[1]); // 拿到緯度值並轉為 double
                        double lon = Double.parseDouble(parts[2].split(": ")[1]); // 拿到經度值並轉為 double
                        String time = parts[3].split(": ")[1]; // 拿到時間字串

                        // 建立一個 Location 物件
                        Location location = new Location(id, lat, lon, time);

                        // 更新位置資料到 WebServer 的共享儲存區（ConcurrentHashMap）
                        WebServer.updateLocation(location);
                    } catch (Exception e) {
                        // 若解析錯誤，印出錯誤訊息但不中斷程式
                        System.out.println("解析位置資料失敗：" + e.getMessage());
                    }
                }

                // 關閉與 Client 的連線
                clientSocket.close();
            } catch (Exception e) {
                // 若遇到例外，例如埠口被占用、Client 傳輸異常等，印出錯誤並休息 5 秒
                System.out.println("Server錯誤：" + e.getMessage());
                e.printStackTrace();

                try {
                    Thread.sleep(5000); // 暫停 5 秒後再重新啟動 ServerSocket
                } catch (InterruptedException ie) {
                    ie.printStackTrace(); // 處理 sleep 被中斷的狀況
                }
            }
        }
    }
}
