import socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

address=('ip',9600)
s.bind(address)

s.listen(2)

while True:
    conn, addr = s.accept()#連接確認
    #conn1, addr1 = s.accept()
    print ('Connected by ', addr)
    #print ('Connected by ', addr1)
    while True:
        data = conn.recv(1024)#資料接收
        print (data.decode('utf-8'))
        conn.send("message has received by server".encode('utf-8'))

        
        #conn1.send(data)#傳輸資料
        #feedback = conn1.recv(1024)
        #print (feedback.decode('utf-8'))
        

        


