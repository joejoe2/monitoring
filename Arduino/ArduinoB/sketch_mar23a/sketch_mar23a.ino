#include <SPI.h>
#include <Wire.h>
#include <SoftwareSerial.h>
#include <XBee.h>
#include <MQUnifiedsensor.h>

#define pin A0 //Analog input 0 of your arduino
#define type 7 //MQ7
#define sleeppin 8
float CO;


MQUnifiedsensor MQ7(pin, type);
XBee xbee = XBee();

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  MQ7.inicializar();
  pinMode(sleeppin, INPUT);
  digitalWrite(sleeppin, HIGH);
}

void loop() {
  // put your main code here, to run repeatedly:
    pinMode(sleeppin, OUTPUT);
    digitalWrite(sleeppin, LOW);
    delay(200);
    xbee.setSerial(Serial);
    
    MQ7.update();
    CO =  MQ7.readSensor("CO");

    String sen = "";
    sen = sen + ";02&";
    sen = sen + "01,co," + CO + ",t;";
    int sensorValue = analogRead(A1);
   
   sen = sen + "02,vibration," + sensorValue + ",t;\n";
  
    Serial.println(sen);

    uint8_t buf[sen.length() + 1];
    sen.toCharArray(buf, sen.length(), 0);
    delay(500);
    XBeeAddress64 addr64 = XBeeAddress64(0, 0);
    ZBTxRequest zbTx = ZBTxRequest(addr64, buf, sizeof(buf));
    xbee.send(zbTx);

    pinMode(sleeppin, INPUT);
    digitalWrite(sleeppin, HIGH);
    delay(1000);
}
