import socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

address=('sever_ip',9600)


s.connect(address)


while True:
    message = "test"#傳輸資料
    s.send(message.encode('utf-8'))
    
    data = s.recv(1024)
    print(data.decode('utf-8'))

    
