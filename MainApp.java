package com.gps; // 指定本類別屬於 com.gps 套件

import java.util.*; // 匯入 Java 中的公用類別，例如 Scanner、List 等

public class MainApp {

    public static void main(String[] args) {
        Sender sender = new Sender();       // 建立發送端模擬器
        Receiver receiver = new Receiver(); // 建立接收端模擬器
        Scanner scanner = new Scanner(System.in); // 建立輸入掃描器供使用者輸入指令

        // 系統啟動提示訊息與指令列表
        System.out.println("==GPS位置分享系統啟動==");
        System.out.println("指令: ");
        System.out.println("s - 傳送一筆GPS位置");
        System.out.println("h - 查看曆史傳送記錄");
        System.out.println("q - 離開系統");

        // 進入主迴圈，等待使用者輸入指令
        while(true) {
            System.out.print("\n請輸入指令: ");
            String input = scanner.nextLine(); // 讀取使用者輸入

            // 傳送一筆位置資料
            if(input.equalsIgnoreCase("s")) {
                String locationData = sender.sendLocation();   // 產生並加密位置資料
                receiver.reveiveLocation(locationData);        // 傳送至接收端模擬顯示與解密
            }
            // 顯示歷史記錄
            else if(input.equalsIgnoreCase("h")) {
                List<Location> history = sender.getHistory();  // 取得已傳送的所有位置資料
                if(history.isEmpty()) {
                    System.out.println("目前沒有傳送過GPS資料。");
                } else {
                    System.out.println("歷史傳送記錄: ");
                    for(Location loc : history) {
                        System.out.println(loc); // 每筆資料直接列印
                    }
                }
            }
            // 離開程式
            else if(input.equalsIgnoreCase("q")) {
                System.out.println("系統結束，謝謝使用！");
                break; // 結束 while 迴圈
            }
            // 其他無效指令
            else {
                System.out.println("無效指令，請重新輸入。");
            }
        }

        scanner.close(); // 關閉輸入掃描器
    }
}
