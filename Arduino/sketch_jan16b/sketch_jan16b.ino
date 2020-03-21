#include <SPI.h>
#include <Wire.h>
#include <SoftwareSerial.h>
#include "Adafruit_SHT31.h"
#include <XBee.h>


Adafruit_SHT31 sht31 = Adafruit_SHT31();


#define pwmPin 10  //接D10
#define sleeppin 8
int preheatSec = 10; //預熱時間
SoftwareSerial mySerial(2, 3); // RX, TX
//SoftwareSerial xbee3Serial(12, 13);

byte cmd[9] = {0xFF, 0x01, 0x86, 0x00, 0x00, 0x00, 0x00, 0x00, 0x79};
//XBeeAddress64 addr64 = XBeeAddress64(0x0013a200, 0x4191cc92);
unsigned char response[9];
unsigned long th, tl, ppm, ppm2, ppm3 = 0;
float t, h;
XBee xbee = XBee();
//bool tosend=false;

void setup() {
  Serial.begin(9600);
  mySerial.begin(9600);
  //xbee3Serial.begin(9600);

  // pinMode(pwmPin, INPUT);
  pinMode(sleeppin, INPUT);
  digitalWrite(sleeppin, HIGH);
  if (! sht31.begin(0x44))
  {
    Serial.println("Couldn't find SHT31");
    while (1) delay(1);
  }
}

void loop() {

  mySerial.write(cmd, 9);
  mySerial.readBytes(response, 9);
  unsigned int responseHigh = (unsigned int) response[2];
  unsigned int responseLow = (unsigned int) response[3];
  ppm = (256 * responseHigh) + responseLow;

  //CO2 via pwm

  if (preheatSec > 0) {


    Serial.print("Preheating");
    Serial.println(preheatSec);
    preheatSec--;
    delay(1000);
  }
  else {
    pinMode(sleeppin, OUTPUT);
    digitalWrite(sleeppin, LOW);
    delay(200);
    xbee.setSerial(Serial);
    th = pulseIn(pwmPin, HIGH, 1004000) / 1000;
    tl = 1004 - th;
    //ppm2 = 2000 * (th-2)/(th+tl-4);
    //ppm3 = 5000 * (th-2)/(th+tl-4);
    t = sht31.readTemperature();
    h = sht31.readHumidity();
    String sen = "";
    sen = sen + ";01&";

    sen = sen + "01,co2," + ppm + ",t;";

    if (! isnan(t))
    {
      sen = sen + "02,tm," + t + ",t;";
    }
    else {
      t = -1;
      sen = sen + "02,tm," + t + ",t;";
    }

    if (! isnan(h))
    {
      sen = sen + "03,humid," + h + ",t;";
    }
    else {
      h = -1;
      sen = sen + "03,humid," + h + ",t;";
    }
    sen = sen + "04,humid," + h + ",t;\n";
    //Serial.println(sen);

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



}
