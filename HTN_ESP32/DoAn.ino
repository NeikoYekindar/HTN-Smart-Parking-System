#include <ESP32Servo.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <ArduinoJson.h>
#include <WiFiClientSecure.h>


const char* ssid= "Neikos";
const char* password = "08102004.";
bool islock1 = false;
bool islock2 = false;
bool islock3 = false;
bool islock4 = false;
SemaphoreHandle_t xDisplaySemaphore;
SemaphoreHandle_t xParkingSemaphore;
bool full = false;
int countp = 0;

String serverUrl = "https://f734-2402-800-b7f7-4122-2836-a5f6-8fb0-f7eb.ngrok-free.app/api/parking/update";

int currentState1 = HIGH;
int currentState2 = HIGH;
int currentState3 = HIGH;
int currentState4 = HIGH;

#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 32
#define OLED_ADDRESS 0x3C // Địa chỉ I2C của OLED

Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, -1);


int IR_GATE_IN = 16;   // Cảm biến hồng ngoại 1 (Vị trí đỗ xe 1)
int IR_GATE_OUT = 13;     // Cảm biến cổng vào
int SERVO_IN = 2;
int SERVO_OUT = 4;     // Chân GPIO nối với servo motor
int IR_PARK1 = 17;
int LED_PARK1 = 25;
int IR_PARK2 = 18;
int LED_PARK2 = 26;
int IR_PARK3 = 14;
int LED_PARK3 = 27;
int IR_PARK4 = 32;
int LED_PARK4 = 33;

Servo gateServoIn;
Servo gateServoOut;
void TaskIRSensor(void *pvParameters);
void TaskGateControl(void *pvParameters);
void checkParkingTask(void *pvParameters);


void setup() {

   Wire.begin(21, 22);
   Serial.begin(115200); // Khởi tạo giao tiếp Serial



for (byte address = 1; address < 127; address++) {
    Wire.beginTransmission(address);
    if (Wire.endTransmission() == 0) {
      Serial.print("Found I2C device at address 0x");
      Serial.println(address, HEX);
    }
    delay(50);
  }
  Serial.println("Scan complete.");  



  // Khởi tạo OLED
 if (!display.begin(OLED_ADDRESS)) {
    Serial.println("SSD1306 allocation failed");
    for (;;); // Dừng chương trình nếu không khởi tạo được
  }
  
  //Xóa màn hình OLED
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0, 0);
  display.println("Parking System");
  display.display();
  delay(2000);





   WiFi.begin(ssid, password);
   while (WiFi.status() != WL_CONNECTED){
   delay(1000);
   Serial.println("Connecting to WiFi...");
   }
   Serial.println("Connected to WiFi.");



  pinMode(IR_GATE_IN, INPUT);
  pinMode(IR_GATE_OUT,INPUT);
  pinMode(SERVO_IN, OUTPUT);
  pinMode(SERVO_OUT, OUTPUT);
  pinMode(IR_PARK1, INPUT);
  pinMode(LED_PARK1,OUTPUT);
  pinMode(IR_PARK2, INPUT);
  pinMode(LED_PARK2, OUTPUT);
  pinMode(IR_PARK3, INPUT);
  pinMode(LED_PARK3, OUTPUT);
  pinMode(IR_PARK4, INPUT);
  pinMode(LED_PARK4, OUTPUT);
  gateServoIn.attach(SERVO_IN);
  gateServoIn.write(0);  
  gateServoOut.attach(SERVO_OUT);
  gateServoOut.write(0);
  xDisplaySemaphore = xSemaphoreCreateMutex();
  if (xDisplaySemaphore == NULL) {
        Serial.println("Failed to create semaphore for display.");
  }
  xParkingSemaphore = xSemaphoreCreateMutex();
    if (xParkingSemaphore == NULL) {
        Serial.println("Failed to create semaphore for parking state.");
  }
  updateDisplay("spot1", false, "spot2", false, "spot3", false, "spot4", false);


  //Run TaskIRSensor on Core 0
   xTaskCreatePinnedToCore(
    TaskIRSensor,
    "Task IR Sensor",
    8192,
    NULL,
    1, 
    NULL,
    0 
  );

 // Run TaskGateControl on Core 1
   xTaskCreatePinnedToCore(
    TaskGateControl,
    "Task Gate Control",
    8192,
    NULL,
    1, 
    NULL,
    1 
  );
  //xTaskCreate
   xTaskCreatePinnedToCore(
    checkParkingTask,
    "CheckParkingTask",
    8192,
    NULL,
    2,
    NULL,
    1
  );


}

void loop() {

}

void TaskIRSensor(void *pvParameters) {
  //int lastState1 = HIGH, lastState2 = HIGH, lastState3 = HIGH;
  while (1) {
   if(xSemaphoreTake(xParkingSemaphore, pdMS_TO_TICKS(100))){

   checkWiFiConnection();
    int ir_park_1 = digitalRead(IR_PARK1);
    int ir_park_2 = digitalRead(IR_PARK2);
    int ir_park_3 = digitalRead(IR_PARK3);
    int ir_park_4 = digitalRead(IR_PARK4);

    // Kiểm tra và cập nhật LED và trạng thái cảm biến
    if (ir_park_1 != currentState1  || ir_park_2 != currentState2 || ir_park_3 != currentState3 || ir_park_4 != currentState4 ) {
      
       if (ir_park_1 != currentState1) {
        digitalWrite(LED_PARK1, ir_park_1 == LOW ? HIGH : LOW);
        sendStatusToServer("spot1", ir_park_1 == LOW);
        
        currentState1 = ir_park_1; // Cập nhật trạng thái
        
      }
       if (ir_park_2 != currentState2) {
         digitalWrite(LED_PARK2, ir_park_2 == LOW ? HIGH : LOW);
        sendStatusToServer("spot2", ir_park_2 == LOW);
       
        currentState2 = ir_park_2; // Cập nhật trạng thái
      }
       if (ir_park_3 != currentState3) {
        digitalWrite(LED_PARK3, ir_park_3 == LOW ? HIGH : LOW);
        sendStatusToServer("spot3", ir_park_3 == LOW);
        
        currentState3 = ir_park_3; // Cập nhật trạng thái
      }
      if (ir_park_4 != currentState4) {
        digitalWrite(LED_PARK4, ir_park_4 == LOW ? HIGH : LOW);
        sendStatusToServer("spot4", ir_park_4 == LOW);
        
        currentState4 = ir_park_4; // Cập nhật trạng thái
      }
      updateDisplay("spot1", ir_park_1 == LOW, "spot2", ir_park_2 == LOW, "spot3", ir_park_3 == LOW, "spot4", ir_park_4 == LOW);

    }
     xSemaphoreGive(xParkingSemaphore);

   }

    vTaskDelay(100 / portTICK_PERIOD_MS);  // Delay 100 ms
  }
}

void sendStatusToServer(String spotID, bool isOccupied) {
  if (WiFi.status() == WL_CONNECTED) { // Kiểm tra kết nối WiFi
    HTTPClient http;

    // Đặt URL của server (thay đổi nếu cần)
    
    http.begin(serverUrl);

    // Thêm header Content-Type
    http.addHeader("Content-Type", "application/json");

    // Tạo JSON body
    String json = "{\"spotID\": \"" + spotID + "\", \"isOccupied\": " + (isOccupied ? "true" : "false") + "}";

    // Gửi POST request
    int httpResponseCode = http.POST(json);

    // Kiểm tra kết quả
    if (httpResponseCode > 0) {
      String response = http.getString(); // Nhận phản hồi từ server
      Serial.println("Response from server: " + response);
    } else {
      Serial.print("Error sending POST: ");
      Serial.println(httpResponseCode);
    }

    http.end(); // Kết thúc kết nối HTTP
  } else {
    Serial.println("WiFi not connected");
  }
}


void TaskGateControl(void *pvParameters) {
  // Biến ghi nhận thời điểm mở cổng
  unsigned long gateInLastOpenTime = 0;
  unsigned long gateOutLastOpenTime = 0;

  while (1) {
   int gate_ir_in = digitalRead(IR_GATE_IN);
   int gate_ir_out = digitalRead(IR_GATE_OUT);
   int led_park1 = digitalRead(LED_PARK1);
   int led_park2 = digitalRead(LED_PARK2);
   int led_park3 = digitalRead(LED_PARK3);
   int led_park4 = digitalRead(LED_PARK4);
    unsigned long currentTime = millis();

    if(led_park1 != HIGH || led_park2 != HIGH || led_park3 != HIGH || led_park4 != HIGH){
        if (gate_ir_in == LOW ) {
          Serial.println("Gate in");
          gateServoIn.write(90);  // Mở cổng vào
          gateInLastOpenTime = currentTime; // Lưu thời điểm mở
        } else if (currentTime - gateInLastOpenTime >= 1000) {
          // Đóng cổng vào sau 2 giây
          gateServoIn.write(0);
        }
    }
    

    // Điều khiển cổng ra
    if (gate_ir_out == LOW) {
      Serial.println("Gate out");
      gateServoOut.write(180); // Mở cổng ra
      gateOutLastOpenTime = currentTime; // Lưu thời điểm mở
    } else if (currentTime - gateOutLastOpenTime >= 1000) {
      // Đóng cổng ra sau 2 giây
      gateServoOut.write(90);
    }

    // Độ trễ nhỏ để tránh quá tải CPU
    vTaskDelay(100 / portTICK_PERIOD_MS);
  }
}

void updateDisplay(String spot1, bool occupied1, String spot2, bool occupied2, String spot3, bool occupied3, String spot4, bool occupied4) {
  if(xSemaphoreTake(xDisplaySemaphore, pdMS_TO_TICKS(100))){

  
  display.clearDisplay(); // Xóa màn hình

  // Hiển thị số thứ tự vị trí (1, 2, 3, 4)
  display.setTextSize(2); // Tăng kích thước chữ
  display.setCursor(3, 0);   // Số 1
  display.print("1");
  display.setCursor(33, 0); // Số 2 (cách 40px)
  display.print("2");
  display.setCursor(63, 0); // Số 3 (cách 80px)
  display.print("3");
  display.setCursor(93, 0); // Số 4 (cách 120px)
  display.print("4");

  // Hiển thị trạng thái bên dưới từng vị trí
  display.setTextSize(1); // Trở về kích thước chữ mặc định
  display.setCursor(0, 20);  // Dòng trạng thái dưới số 1
  display.print(occupied1 ? "(x)" : "( )");

  display.setCursor(30, 20); // Dòng trạng thái dưới số 2
  display.print(occupied2 ? "(x)" : "( )");

  display.setCursor(60, 20); // Dòng trạng thái dưới số 3
  display.print(occupied3 ? "(x)" : "( )");

  display.setCursor(90, 20); // Dòng trạng thái dưới số 4
  display.print(occupied4 ? "(x)" : "( )");

  // Hiển thị nội dung lên màn hình
  display.display();
  
  xSemaphoreGive(xDisplaySemaphore);
  }else{
    Serial.println("Failed to acquire semaphore for display.");

  }
}

void checkParkingTask(void *pvParameters) {

  while (1) {
    if(xSemaphoreTake(xParkingSemaphore, pdMS_TO_TICKS(100))){
      checkWiFiConnection();
      performGetRequest();
       xSemaphoreGive(xParkingSemaphore);
    }
   vTaskDelay(5000 / portTICK_PERIOD_MS); // Delay for 5 seconds
  }
}
void performGetRequest(){
    
      int led_park1 = digitalRead(LED_PARK1);
      int led_park2 = digitalRead(LED_PARK2);
      int led_park3 = digitalRead(LED_PARK3);
      int led_park4 = digitalRead(LED_PARK4);

    if (WiFi.status() == WL_CONNECTED) {
    WiFiClientSecure client;
    client.setInsecure(); // Bỏ qua kiểm tra chứng chỉ, dùng cho thử nghiệm
    HTTPClient http;
    

    // Update with your API endpoint
    String apiURL = "https://f734-2402-800-b7f7-4122-2836-a5f6-8fb0-f7eb.ngrok-free.app/api/user/parking-status";
    Serial.println("Starting HTTPS GET request...");

    http.begin(client, apiURL); // Sử dụng HTTPS với WiFiClientSecure

    int httpResponseCode = http.GET();

    if (httpResponseCode > 0) {
      String response = http.getString();
      Serial.println("Response: " + response);

      // Parse JSON response
      StaticJsonDocument<2048> doc; // Increased size for larger JSON
      DeserializationError error = deserializeJson(doc, response);

      if (!error) {
        // Iterate through the parking spots
        for (JsonObject spot : doc.as<JsonArray>()) {
          const char* spotID = spot["spotID"];
          bool isLocked = spot["isLocked"];

          if (strcmp(spotID, "spot1") == 0) {
            // Cảm biến xác định có xe đậu không
            bool isOccupied = digitalRead(IR_PARK1) == LOW;

            // Đèn sáng nếu có xe đậu hoặc server trả về isLocked == true
            if (isOccupied || isLocked) {
              digitalWrite(LED_PARK1, HIGH);
              Serial.printf("Spot: %s, Locked: %s, Occupied: %s\n", spotID, isLocked ? "Yes" : "No", isOccupied ? "Yes" : "No");
              
            } else {
              digitalWrite(LED_PARK1, LOW);
              Serial.printf("Spot: %s, Locked: %s, Occupied: %s\n", spotID, isLocked ? "Yes" : "No", isOccupied ? "No" : "Yes");
            }
           
          }else if(strcmp(spotID, "spot2") == 0){
              // Cảm biến xác định có xe đậu không
            bool isOccupied = digitalRead(IR_PARK2) == LOW;

            // Đèn sáng nếu có xe đậu hoặc server trả về isLocked == true
            if (isOccupied || isLocked) {
              digitalWrite(LED_PARK2, HIGH);
              Serial.printf("Spot: %s, Locked: %s, Occupied: %s\n", spotID, isLocked ? "Yes" : "No", isOccupied ? "Yes" : "No");
               
            } else {
              digitalWrite(LED_PARK2, LOW);
              Serial.printf("Spot: %s, Locked: %s, Occupied: %s\n", spotID, isLocked ? "Yes" : "No", isOccupied ? "No" : "Yes");
          }
          
          }else if(strcmp(spotID, "spot3") == 0){
              // Cảm biến xác định có xe đậu không
            bool isOccupied = digitalRead(IR_PARK3) == LOW;

            // Đèn sáng nếu có xe đậu hoặc server trả về isLocked == true
            if (isOccupied || isLocked) {
              digitalWrite(LED_PARK3, HIGH);
              Serial.printf("Spot: %s, Locked: %s, Occupied: %s\n", spotID, isLocked ? "Yes" : "No", isOccupied ? "Yes" : "No");
            } else {
              digitalWrite(LED_PARK3, LOW);
              Serial.printf("Spot: %s, Locked: %s, Occupied: %s\n", spotID, isLocked ? "Yes" : "No", isOccupied ? "No" : "Yes");
          }
           
          }else if(strcmp(spotID, "spot4") == 0){
              // Cảm biến xác định có xe đậu không
            bool isOccupied = digitalRead(IR_PARK4) == LOW;

            // Đèn sáng nếu có xe đậu hoặc server trả về isLocked == true
            if (isOccupied || isLocked) {
              digitalWrite(LED_PARK4, HIGH);
              Serial.printf("Spot: %s, Locked: %s, Occupied: %s\n", spotID, isLocked ? "Yes" : "No", isOccupied ? "Yes" : "No");
            } else {
              digitalWrite(LED_PARK4, LOW);
              Serial.printf("Spot: %s, Locked: %s, Occupied: %s\n", spotID, isLocked ? "Yes" : "No", isOccupied ? "No" : "Yes");
          }
            
        }
        }
      } else {
        Serial.println("Failed to parse JSON: ");
        Serial.println(error.c_str());
      }
    } else {
      Serial.print("HTTP request failed. Code: ");
      Serial.println(httpResponseCode);
    }
    http.end();
  } else {
    Serial.println("WiFi not connected");
  }
  
  updateDisplay("spot1", led_park1 == HIGH, "spot2", led_park2 == HIGH, "spot3", led_park3 == HIGH, "spot4",  led_park4 == HIGH);
}
void checkWiFiConnection() {
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("WiFi disconnected! Attempting to reconnect...");
    WiFi.disconnect();
    WiFi.begin(ssid, password);
    int retryCount = 0;
    while (WiFi.status() != WL_CONNECTED && retryCount < 10) {
      delay(1000);
      Serial.print(".");
      retryCount++;
    }
    if (WiFi.status() == WL_CONNECTED) {
      Serial.println("WiFi reconnected!");
    } else {
      Serial.println("Failed to reconnect to WiFi.");
    }
  }
}

