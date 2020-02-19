import socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

address=('sever_ip',9600)


s.connect(address)


while True:
    data = s.recv(1024)#資料接收
    print (data.decode('utf-8'))
    s.send("message has received by client".encode('utf-8'))
    
    
