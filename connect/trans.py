import socket
import threading
import time
import serial




def job():
    ser=serial.Serial('/dev/ttyAMA0',9600,timeout=0.5)

    while True:
        allow=0
        trans=0
        text = ser.read(1024)
        text=text[48:]

        x=len(s)/2-1
        x=int(x)
        result=''

        for i in range(x):
            tempt=s[:2]
            s=s[2:]
            deci=int(tempt,16)
            result=result+chr(deci)
            allow=1
            time.sleep(3)



t = threading.Thread(target = job)
t.start()
    
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

address=('ip',9600)
s.bind(address)

s.listen(1)


while True:
    conn1, addr1 = s.accept()
    print ('Connected by ', addr1)
    while True:
        
        current_time=time.asctime( time.localtime(time.time()) )
        data=result+'\n'+current_time
        if allow==1 and trans==0:
            conn1.send(data)#傳輸資料
            feedback = conn1.recv(1024)
            print (feedback.decode('utf-8'))
            trans=1
            

        


