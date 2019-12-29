import socket
import aes

entry = "entry.showdata.nctu.me"
port = 6000
key = "aa43235167123456"
valid = "validate code is dmd5464fas4e629DF"

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print("socket create success! ")
try:
    sock.settimeout(5)
    sock.connect((entry, port))
    print("socket connect success! ")
    msg = valid + 'target=devices01&devicesid=01&status=unavailable&time=2019-10-24T10:20:45.732&obj=[{"id": "sensor01", "type": "tm", "value":0, "status": "test"}, {"id": "sensor02", "type": "tm", "value":0, "status": "test"}]'
    s = (aes.encrypt(key, msg)).encode("utf8")
    print(s)
    sock.send(s)
    sock.close()
except:
    print("connect fail !")
