package com.gps; // 定義此類別所屬的套件為 com.gps

import java.util.*; // 匯入 Java 中集合類別，例如 List、ArrayList、Random 等

public class Sender {

    // 初始緯度（模擬位置起點）
    private double latitude = 24.0;

    // 初始經度（模擬位置起點）
    private double longitude = 120.0;

    // 用來隨機產生變化量的亂數產生器
    private Random random = new Random();

    // 儲存每次傳送過的位置資料（歷史記錄）
    private List<Location> history = new ArrayList<>();

    // 傳送位置的主方法：模擬位置改變、加密後回傳字串
    public String sendLocation() {
        // 模擬位置變動：每次微調 ±0.005 的範圍
        String id = "user001"; // 模擬使用者 ID
        latitude += (random.nextDouble() - 0.5) * 0.01; // 緯度隨機微調
        longitude += (random.nextDouble() - 0.5) * 0.01; // 經度隨機微調

        // 取得目前時間作為時間戳記
        String timestamp = java.time.LocalDateTime.now().toString();

        // 建立一個新的 Location 物件
        Location location = new Location(id, latitude, longitude, timestamp);

        // 將該位置加入歷史記錄中
        history.add(location);

        // 將該位置轉為字串（格式：id: ..., lat: ..., lon: ..., time: ...）
        String plainText = location.toString();

        // 將位置資料加密後回傳
        return Encryptor.encrypt(plainText);
    }

    // 回傳歷史紀錄列表（可供查詢過去發送過的所有位置）
    public List<Location> getHistory() {
        return history;
    }
}
