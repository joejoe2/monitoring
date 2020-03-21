import datetime
import serial
import threading
import queue
import time
import socket
import aes

entry1 = "entry.showdata.nctu.me"
port = 6000
key = "aa43235167123456"
valid = "validate code is dmd5464fas4e629DF"


class Worker(threading.Thread):
    def __init__(self, queue, num, lock):
        threading.Thread.__init__(self)
        self.queue = queue
        self.num = num
        self.lock = lock

    def run(self):
        while True:
            if self.queue.qsize() > 0:
                msg = self.queue.get()
                # to send to server accept entry...
                # refer to simulatetransmisterr example
                sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                try:
                    sock.settimeout(5)
                    sock.connect((entry1, port))
                    msg = valid + msg
                    sock.send((aes.encrypt(key, msg)).encode("utf8"))
                    sock.close()
                    print("connect success !")
                except:
                    print("connect fail !")
            else:
                time.sleep(0.5)


my_queue = queue.Queue()  # queue is thread safe !!!

# 建立 lock
lock = threading.Lock()

my_worker1 = Worker(my_queue, 1, lock)
my_worker2 = Worker(my_queue, 2, lock)
my_worker1.start()
my_worker2.start()

# Enable USB Communication
ser = serial.Serial('/dev/ttyUSB0', 9600,timeout=.5)

while True:
    incoming = ser.readline().strip()
    # use a thread to below...
    try:
        incoming=incoming[incoming.find(b';')+1:incoming.rfind(b';')].decode()
        #ex 01&01,co2,0,t;02,tm,29.94,t;03,humid,57.75,t;04,humid,57.75,t
        target="target=devices"+incoming.split("&")[0]
        devicesid="devicesid="++incoming.split("&")[0]
        ar=incoming.split("&")[1].split(";")
        sen_cnt=len(ar)
        time="time="+datetime.datetime.now().isoformat()
        status="status=running"
        obj="obj=["
        for i in range(0,sen_cnt):
            # ... build the json struct
            data=ar[i].split(",")
            sen_id="{\"id\": \"sensor"+data[0]+"\", "
            sen_type="\"type\": \""+data[1]+"\", "
            sen_val="\"value\":"+data[2]+"\" ,"
            sen_status="\"status\": \""+("test" if data[3]=="t" else "unknown")+"\"}"
            # use obj to concat data...
            obj += sen_id + sen_type + sen_val + sen_status
            if i != sen_cnt - 1:
                obj += ", "
        obj = obj+"]"
        send = target+"&"+devicesid+"&"+status+"&"+time+"&"+obj
        print(send)
        my_queue.put(send)

    except:
        print(incoming)
