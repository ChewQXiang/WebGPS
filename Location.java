package com.gps; // 定義此類別所屬的套件為 com.gps

// 定義一個代表 GPS 位置資訊的資料結構
public class Location {

    // 使用者或裝置的識別代號
    public String id;

    // 緯度（latitude）
    public double latitude;

    // 經度（longitude）
    public double longitude;

    // 傳送位置資料的時間戳記（使用 ISO 8601 格式，例如 "2025-05-09T21:00:00"）
    public String time;

    // 建構子，用來建立一個新的位置物件，並初始化所有屬性
    public Location(String id, double latitude, double longitude, String time) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    // 若未來需要，也可以在這邊加上 toString() 方法覆寫，輸出自訂格式
}
