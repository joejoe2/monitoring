#include <SPI.h>
#include <Wire.h>
#include <SoftwareSerial.h>
#include <XBee.h>
#include <MQUnifiedsensor.h>

#define         Board                   ("Arduino UNO")
#define         Pin                     (A0)  //Analog input 3 of your arduino
/***********************Software Related Macros************************************/
#define         Type                    ("MQ-7") //MQ2
#define         Voltage_Resolution      (5)
#define         ADC_Bit_Resolution      (10) // For arduino UNO/MEGA/NANO
#define RatioMQ7CleanAir 27.5 //RS / R0 = 27.5 ppm  //RS / R0 = 9.83 ppm 
#define sleeppin 8
unsigned long oldTime = 0;
float CO;


MQUnifiedsensor MQ7(Board, Voltage_Resolution, ADC_Bit_Resolution, Pin, Type);

XBee xbee = XBee();

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  MQ7.setRegressionMethod(1); //_PPM =  a*ratio^b
  MQ7.setA(99.042); MQ7.setB(-1.518); // Configurate the ecuation values to get CO concentration
   MQ7.init(); 
   float calcR0 = 0;
  for(int i = 1; i<=10; i ++)
  {
    MQ7.update(); // Update data, the arduino will be read the voltage on the analog pin
    calcR0 += MQ7.calibrate(RatioMQ7CleanAir);
    
  }
  MQ7.setR0(calcR0/10);
  pinMode(sleeppin, INPUT);
  digitalWrite(sleeppin, HIGH);
}

void loop() {
  // put your main code here, to run repeatedly:
    pinMode(sleeppin, OUTPUT);
    digitalWrite(sleeppin, LOW);
    delay(200);
    xbee.setSerial(Serial);

    
    MQ7.update(); // Update data, the arduino will be read the voltage on the analog pin
    CO =  MQ7.readSensor(); // Sensor will read PPM concentration using the model and a and b values setted before or in the setup

    //MQ7.serialDebug(); 
    String sen = "";
    sen = sen + ";02&";
    sen = sen + "01,co," + CO + ",t;";
    //int sensorValue = analogRead(A1);
   
    sen = sen+"\n";
  
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
