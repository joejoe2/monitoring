import serial

# Enable USB Communication
ser = serial.Serial('/dev/ttyUSB0', 9600,timeout=.5)
 
while True:
    incoming = ser.readline().strip()
    # use a thread to below...
    try:
        incoming=incoming[incoming.find(b';')+1:incoming.rfind(b';')].decode()
        #ex 01&01,0,t;02,29.94,t;03,57.75,t;04,57.75,t
        target="target=devices"+incoming.split("&")[0]
        devicesid="devicesid="++incoming.split("&")[0]
        ar=incoming.split("&")[1].split(";")
        sen_cnt=len(ar)
        # time="time="+... refer to java localdatetime default format
        status="status=runing"
        obj="obj=["
        for i in (0,sen_cnt):
            # ... build the json struct
            data=ar[i].split(",")
            sen_id="sensor"+data[0]
            sen_type=data[1]
            sen_val=data[2]
            sen_status=test if data[3]=="t" else "unknown"
            # use obj to concat data...
            
            pass
        obj=obj+"]"
        
        send=target+"&"+devicesid+"&"+status+"&"+time+"&"+obj
        # to send to server accept entry...
        # refer to simulatetransmisterr example
    except:
        print(incoming)

