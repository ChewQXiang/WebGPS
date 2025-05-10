package com.gps; 


import com.sun.net.httpserver.HttpServer; //引入Java 內建提供的輕量級伺服器
import com.sun.net.httpserver.HttpHandler; //用來處理路徑
import com.sun.net.httpserver.HttpExchange; //用來處理request和post

// 引入 Java 標準函式庫中處理 I/O 與字串的相關類別
import java.io.*; //處理輸入輸出
import java.net.InetSocketAddress; //用來處理IP位置和port
import java.nio.charset.StandardCharsets; //用來處理字元
import java.time.Instant; //用來表示時間
import java.util.*; 
import java.util.concurrent.ConcurrentHashMap; //引入多執行緒的HashMap

public class WebServer {

    // 使用 ConcurrentHashMap 儲存所有裝置的定位資料，支援多執行緒存取
    private static Map<String, Location> allLocations = new ConcurrentHashMap<>();

    // 啟動 WebServer
    public static void start() {
        try {
            // 建立一個 HTTP 伺服器，監聽 8080 埠口
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            // 建立一個 API "/positions"，用於回傳所有裝置位置的 JSON 陣列
            server.createContext("/positions", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    // 設定回應標頭為 JSON 格式，並允許跨來源存取
                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                    // 準備 JSON 格式的回應字串
                    StringBuilder response = new StringBuilder();
                    response.append("["); // 開始 JSON 陣列

                    boolean first = true; // 控制是否加逗號分隔
                    for (Map.Entry<String, Location> entry : allLocations.entrySet()) {
                        if (!first) response.append(","); // 若不是第一筆則加入逗號
                        Location loc = entry.getValue(); // 取得定位資料
                        response.append(String.format(
                                "{\"id\":\"%s\",\"lat\":%.6f,\"lon\":%.6f,\"time\":\"%s\"}",
                                entry.getKey(), loc.latitude, loc.longitude, loc.time
                        )); // 加入一筆裝置位置 JSON
                        first = false;
                    }

                    response.append("]"); // 結束 JSON 陣列

                    // 將字串轉為位元組並送出
                    byte[] bytes = response.toString().getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, bytes.length); // 回傳 HTTP 200 成功
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes); // 寫入回應資料
                    os.close(); // 關閉輸出串流
                }
            });

            // 建立一個 API "/updateLocation"，用於接收 POST 請求並更新裝置位置
            server.createContext("/updateLocation", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    // 設定允許跨來源和允許的 HTTP 方法與標頭
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

                    // 如果是瀏覽器發出的預檢請求（CORS），直接回應 204 No Content
                    if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                        exchange.sendResponseHeaders(204, -1); // 無內容，結束處理
                        return;
                    }

                    // 如果是 POST 請求，開始處理位置上傳
                    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                        // 讀取請求主體（JSON 格式）
                        InputStream is = exchange.getRequestBody();
                        String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                        // 解析 JSON 並取得欄位資料
                        Map<String, String> data = parseJson(json);
                        String id = data.get("id");
                        double lat = Double.parseDouble(data.get("lat"));
                        double lon = Double.parseDouble(data.get("lon"));
                        // 若沒有提供時間，使用當下系統時間
                        String time = data.getOrDefault("time", Instant.now().toString());

                        // 儲存位置資料到 map 中
                        allLocations.put(id, new Location(id, lat, lon, time));

                        // 回傳 HTTP 200，表示成功
                        exchange.sendResponseHeaders(200, -1);
                    } else {
                        // 若使用不支援的方法（非 POST/OPTIONS），回傳 405 Method Not Allowed
                        exchange.sendResponseHeaders(405, -1);
                    }
                }
            });

            // 啟動 HTTP 伺服器
            server.start();
            System.out.println("WebServer running on port 8080"); // 顯示伺服器啟動成功
        } catch (IOException e) {
            e.printStackTrace(); // 印出例外資訊以供除錯
        }
    }

    // 提供 Socket Client 使用的更新位置方法（非 HTTP，用在其他模組）
    public static void updateLocation(Location loc) {
        allLocations.put(loc.id, loc); // 根據 ID 更新位置
    }

    // 簡易版 JSON 解析函數，將格式為 {"key":"value",...} 的字串轉為 Map
    private static Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim().replaceAll("[{}\"]", ""); // 去除大括號與引號
        for (String pair : json.split(",")) { // 每個欄位用逗號分隔
            String[] parts = pair.split(":", 2); // 用冒號分隔 key 和 value
            if (parts.length == 2) {
                map.put(parts[0].trim(), parts[1].trim()); // 加入 map 中
            }
        }
        return map;
    }
}
