import serial
 
# Enable USB Communication
ser = serial.Serial('/dev/ttyUSB0', 9600,timeout=.5)
 
while True:
    incoming = ser.readline().strip()
    # please use a thread to process below...
    try:
        incoming=incoming.decode('utf-8')
        print(incoming)
    except:
        print(incoming)