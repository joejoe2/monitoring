#include <SPI.h>
#include <Wire.h>
#include <SoftwareSerial.h>
#include "Adafruit_SHT31.h"
#include <XBee.h>


Adafruit_SHT31 sht31 = Adafruit_SHT31();


//#define pwmPin 10  //接D10
#define sleeppin 8
int preheatSec = 10; //預熱時間
SoftwareSerial xbee2Serial(10, 11); // RX, TX
SoftwareSerial xbee3Serial(12, 13);

byte cmd[9] = {0xFF,0x01,0x86,0x00,0x00,0x00,0x00,0x00,0x79};
//XBeeAddress64 addr64 = XBeeAddress64(0x0013a200, 0x4191cc92);
unsigned char response[9]; 
unsigned long th, tl,ppm, ppm2, ppm3 = 0;
float t,h;
XBee xbee = XBee();
bool tosend=false;

void setup() {
  Serial.begin(9600); 
  xbee2Serial.begin(9600); 
  xbee3Serial.begin(9600); 
  
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
  tosend=false;
  //mySerial.write(cmd,9);
 // mySerial.readBytes(response, 9);
  //unsigned int responseHigh = (unsigned int) response[2];
 // unsigned int responseLow = (unsigned int) response[3];
  //ppm = (256*responseHigh)+responseLow;
  
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
   // th = pulseIn(pwmPin, HIGH, 1004000) / 1000;
    //tl = 1004 - th;
   // ppm2 = 2000 * (th-2)/(th+tl-4);
    //ppm3 = 5000 * (th-2)/(th+tl-4);
     t = sht31.readTemperature();
     h = sht31.readHumidity();
       
     Serial.println();
     Serial.println(ppm);
     
  if (! isnan(t))
  {
    //Serial.print("Temp *C = "); 
    Serial.println(t);  //println = 改行を含めたprint
  } 
  
 
  if (! isnan(h)) 
  {
    //Serial.print("Hum. % = "); 
    Serial.println(h);
  } 
  delay(500);
  xbee.setSerial(xbee3Serial);
  
  xbee3Serial.listen();
  
  if(xbee3Serial.available()>0&&tosend==false){
    
    char recv[xbee3Serial.available()];
    uint8_t payload[xbee3Serial.available()];
    //uint8_t from[]={'F','r','o','m',' ','x','b','e','e','3','\n'};
    xbee3Serial.readBytes(payload,xbee3Serial.available());
    //xbee3Serial.println("From xbee3");
    
    XBeeAddress64 addr64 = XBeeAddress64(0,0x0000ffff);
    ZBTxRequest zbTx = ZBTxRequest(addr64, payload, sizeof(payload));
    xbee.send(zbTx);
    tosend=true;
  }
  delay(500);
  /*xbee.setSerial(xbee2Serial);
  
  xbee2Serial.listen();
  
  if(xbee2Serial.available()>0&&tosend==false){
     
    uint8_t payload[xbee2Serial.available()];
    //uint8_t from[]={'F','r','o','m',' ','x','b','e','e','2','\n'};
    xbee2Serial.readBytes(payload,xbee2Serial.available());
    XBeeAddress64 addr64 = XBeeAddress64(0,0x0000ffff);
    ZBTxRequest zbTx = ZBTxRequest(addr64,payload, sizeof(payload));
    xbee.send(zbTx);
    tosend=true;
  }*/
  

  
  pinMode(sleeppin, INPUT);
  digitalWrite(sleeppin, HIGH);
  delay(500);
    }
  
  

}




   
