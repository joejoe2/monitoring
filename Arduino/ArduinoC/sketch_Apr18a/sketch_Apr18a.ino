#include <SPI.h>
#include <Wire.h>
#include <SoftwareSerial.h>
#include <XBee.h>
#include <MQUnifiedsensor.h>
/************************Hardware Related Macros************************************/
#define         Board                   ("Arduino UNO")
#define         Pin                     (A2)  //Analog input 3 of your arduino
/***********************Software Related Macros************************************/
#define         Type                    ("MQ-2") //MQ2
#define         Voltage_Resolution      (5)
#define         ADC_Bit_Resolution      (10) // For arduino UNO/MEGA/NANO
#define         RatioMQ2CleanAir        (9.83) //RS / R0 = 9.83 ppm 
#define sleeppin 8
float CO;


MQUnifiedsensor MQ2(Board, Voltage_Resolution, ADC_Bit_Resolution, Pin, Type);
XBee xbee = XBee();

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  MQ2.setRegressionMethod(1); //_PPM =  a*ratio^b
  MQ2.setA(574.25); 
  MQ2.setB(-2.222); // Configurate the ecuation values to get LPG concentration
  MQ2.init(); 

  
  float calcR0 = 0;
  for(int i = 1; i<=10; i ++)
  {
    MQ2.update(); // Update data, the arduino will be read the voltage on the analog pin
    calcR0 += MQ2.calibrate(RatioMQ2CleanAir);
    Serial.print(".");
  }
  MQ2.setR0(calcR0/10);
  
  pinMode(sleeppin, INPUT);
  digitalWrite(sleeppin, HIGH);
}

void loop() {
  // put your main code here, to run repeatedly:
    pinMode(sleeppin, OUTPUT);
    digitalWrite(sleeppin, LOW);
    delay(200);
    xbee.setSerial(Serial);
    
    MQ2.update(); // Update data, the arduino will be read the voltage on the analog pin
    MQ2.setA(574.25); 
    MQ2.setB(-2.222); 
    float lpg=MQ2.readSensor();
    MQ2.setA(658.71); 
    MQ2.setB(-2.168); 
    float propane=MQ2.readSensor();
    
    String sen = "";
    sen = sen + ";03&";
    sen = sen + "01,lpg," + lpg + ",t;";
    int sensorValue = analogRead(A1);
   
   sen = sen + "02,propane," + propane+ ",t;\n";
  
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
