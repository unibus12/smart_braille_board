#-*-coding: utf-8-*-
import os
import sys
import RPi.GPIO as GPIO
import socket
import threading
import pymysql

# temp
import time
from smbus2 import SMBus
from mlx90614 import MLX90614
bus=SMBus(1)
sensor=MLX90614(bus, address=0x5A)
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)

# mlx90614 : GPIO.BOARD SDA : 3, SCL : 5
# Connect check : sudo i2cdetect -y 1
temp_pin = 18 # GPIO.BOARD = 12
GPIO.setup(temp_pin, GPIO.OUT)
GPIO.output(temp_pin, False)

# hx711
EMULATE_HX711=False

if not EMULATE_HX711:
	import RPi.GPIO as GPIO
	from hx711 import HX711
else:
	from emulated_hx711 import HX711

def cleanAndExit():
	print("Cleaning...")
	if not EMULATE_HX711:
		GPIO.cleanup()
		print("Bye!")
		sys.exit()

hx1 = HX711(24,9) # 5kg
#hx2 = HX711(25,11) # 5kg
#hx3 = HX711(5,6) # 10kg
hx4 = HX711(20,21) # 10kg

hx1.set_reading_format("MSB", "MSB")
#hx2.set_reading_format("MSB", "MSB")
#hx3.set_reading_format("MSB", "MSB")
hx4.set_reading_format("MSB", "MSB")

hx1.set_reference_unit(466) # 5kg 466
#hx2.set_reference_unit(1) # 5kg 466
#hx3.set_reference_unit(248) # 10kg 248
hx4.set_reference_unit(248) # 10kg 248

hx1.reset()
#hx2.reset()
#hx3.reset()
hx4.reset()

hx1.tare()
#hx2.tare()
#hx3.tare()
hx4.tare()

print("Tare done! Add weight now...")

def hx711get():
	val1 = hx1.get_weight(5)
	#val2 = hx2.get_weight(5)
	#val3 = hx3.get_weight(5)
	val4 = hx4.get_weight(5)

	print('val1 = ', val1)
	#print('val2 = ', val2)
	#print('val3 = ', val3)
	print('val4 = ', val4)

	hx1.power_down()
	hx1.power_up()
	#hx2.power_down()
	#hx2.power_up()
	#hx3.power_down()
	#hx3.power_up()
	hx4.power_down()
	hx4.power_up()

	time.sleep(0.1)

# 통신 정보 설정
IP = ''
PORT = 35000
SIZE = 1024
ADDR = (IP, PORT)
msg = ''

conn=pymysql.connect(host='localhost', user='root', password='1234', db='catdb', charset='utf8')
cur=conn.cursor()

activity = ""

signup = []
idname = ""
pwd = ""
catName = ""
age = ""
catKind = ""

enableStepAuto = 0
Step1 = 0
Step2 = 3

tempAuto = "on"
tempState = ""
temp1 = ""
temp2 = ""

weight = "1-2-3-4-5-6-7"
calAutoState = "on"
cal = "high"
feed = "autofeed"
feednum = "0"

playcount = "3-2-1-1-3-2-1"
snack = "on"

health = "2-3-1-3-3-2-2"
healthStep = "위험"
healthState = "병원 방문이 필요합니다."

def server():
	global activity
	global idname, pwd, catName, age, catKind
	global enableStepAuto, Step1, Step2
	global tempAuto, tempState, temp1, temp2
	global weight, cal, feed, feednum, calAutoState
	global playcount, snack
	global health, healthStep, healthState

	print("server start")
	# 서버 소켓 설정
	with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
		server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		server_socket.bind(ADDR)  # 주소 바인딩
		server_socket.listen(3)  # 클라이언트의 요청을 받을 준비
		client_socket, client_addr = server_socket.accept()  # 수신대기, 접속한 클라이언트 정보 (소켓, 주소) 반환
		print("connected")
		activity = "login"

	print("thread start")

	# 무한루프 진입
	while (True):
		msg = client_socket.recv(1024).decode()
		if msg!="" :
			print("[{}] massage : {}".format(client_addr, msg))

			if(msg[:6]=="return"):
				activity = {'l':'login','m':'menu'}.get(msg[7],'end')
				print('return {}!'.format(activity))
				if(activity=="login"):
					idname = ""
					pwd = ""
					catName = ""
					age = ""
					catKind = ""

			if(msg[:5]=="signup,"):
				activity = "login"
				signup = msg.split(",",5)
				print("signup : ",signup)

				cur.execute("select id from catinfo;")
				rows = cur.fetchall()
				checkid = []
				for row in rows:
					checkid.append(row[0])
				if(idname in checkid):
					print("아이디 존재, 다른 아이디 생성 해야됨!")
				else:
					cur.execute("INSERT INTO catinfo(id,pwd,name,age,breed) VALUES('"+signup[1]+"','"+signup[2]+"','"+signup[3]+"',"+signup[4]+",'"+signup[5]+"');")
					cur.execute("create table "+idname+" (select * from catdata);")
					conn.commit()
					print("아이디 생성됨!")
			elif(msg[:4]=="login,"):
				idpwd = msg.split(",",2)
				cur.execute("select id,pwd,name,age,breed from catinfo;")
				rows = cur.fetchall()
				checkid = []
				checkpwd = []
				for row in rows:
					checkid.append(row[0])
					checkpwd.append(row[1])
				print("checkid = ",checkid, ", checkpwd = ",checkpwd)
				if(idpwd[1] in checkid):
					index = checkid.index(idpwd[1])
					if(checkpwd[index]==idpwd[2]):
						activity = "menu"
						client_socket.sendall("login,success\r\n".encode())
						print("message back to client : login,success")
						idname = idpwd[1]
						pwd = idpwd[2]
						catName = rows[0][2]
						age = rows[0][3]
						catKind = rows[0][4]
					else:
						client_socket.sendall("login,fail\r\n".encode())
						print("login fail : 해당하는 패스워드가 아님")
				else:
					client_socket.sendall("login,fail\r\n".encode())
					print("login fail : 해당하는 아이디 없음")
			elif(msg[:2]=="step"):
				activity = "step"
				if(msg=="step"):
					if(enableStepAuto==0):
						client_socket.sendall("step,{},{}\r\n".format(Step1,Step2).encode())
						print("message back to client : step,{},{}".format(Step1,Step2))
					else:
						client_socket.sendall("step,{},{},auto\r\n".format(Step1,Step2).encode())
						print("message back to client : step,{},{},auto".format(Step1,Step2))
				elif(msg[0:3]=="step,"):
					if(msg=="step,auto"):
						enableStepAuto = 1
					else:
						enableStepAuto = 0

					if(enableStepAuto==0):
						Step1 = msg[3]
						Step2 = msg[5]
						client_socket.sendall("step,{},{}\r\n".format(Step1,Step2).encode())
						print("message back to client : step,{},{}".format(Step1,Step2))
					else:
						client_socket.sendall("step,{},{},auto\r\n".format(Step1,Step2).encode())
						print("message back to client : step,{},{},auto".format(Step1,Step2))
			elif(msg[:2]=="temp"):
				activity = "temp"
				if(msg=="temp,on"):
					tempAuto = "off"
					tempState = "ON"
				elif(msg=="temp,off"):
					tempAuto = "off"
					tempState = "OFF"
				elif(msg=="temp,auto"):
					tempAuto = "on"
				if(tempAuto=="off"):
					client_socket.sendall("temp,{},{},{}\r\n".format(temp1,temp2,tempState).encode())
					print("message back to client : temp,{},{}".format(temp1,temp2))
					print("tempState : {}".format(tempState))
				else:
					client_socket.sendall("temp,{},{},auto/{}\r\n".format(temp1,temp2,tempState).encode())
					print("message back to client : temp,{},{}".format(temp1,temp2))
					print("tempState : auto/{}".format(tempState))
			elif(msg[:2]=="weight"):
				activity = "weight"
				if(msg=="weight"):
					if(calAutoState=="off"):
						client_socket.sendall("weight,{},{},{},{}\r\n".format(weight,cal,feed,feednum).encode())
						print("message back to client : weight,{},{},{},{}".format(weight,cal,feed,feednum))
					else:
						client_socket.sendall("weight,{},auto,{},{},{}\r\n".format(weight,cal,feed,feednum).encode())
						print("message back to client : weight,{},auto,{},{},{}".format(weight,cal,feed,feednum))
				elif(msg=="weight,low"):
					calAutoState="off"
					cal = "low"
					client_socket.sendall("weight,{}\r\n".format(cal).encode())
					print("message back to client : weight,{}".format(cal))
				elif(msg=="weight,high"):
					calAutoState="off"
					cal = "high"
					client_socket.sendall("weight,{}\r\n".format(cal).encode())
					print("message back to client : weight,{}".format(cal))
				elif(msg=="weight,autocal"):
					calAutoState="on"
					client_socket.sendall("weight,autocal,{}\r\n".format(cal).encode())
					print("message back to client : weight,autocal,{}".format(cal))
				elif(msg=="weight,nonautofeed"):
					feed = "nonautofeed"
					client_socket.sendall("weight,{},{}\r\n".format(feed,feednum).encode())
					print("message back to client : weight,{},{}".format(feed,feednum))
				elif(msg=="weight,autofeed"):
					feed = "autofeed"
					client_socket.sendall("weight,{},{}\r\n".format(feed,feednum).encode())
					print("message back to client : weight,{},{}".format(feed,feednum))
				elif(msg[3]>="0" or msg[3]<="3"):
					feed = "nonautofeed"
					feednum = msg[3]
					client_socket.sendall("weight,{},{}\r\n".format(feed,feednum).encode())
					print("message back to client : weight,{},{}".format(feed,feednum))
			elif(msg[:2]=="play"):
				activity = "play"
				if(msg=="play"):
					client_socket.sendall("play,{},{}\r\n".format(playcount,snack).encode())
					print("message back to client : play,{},{}".format(playcount,snack))
				elif(msg=="play,on"):
					snack = "on"
					client_socket.sendall("play,{}\r\n".format(snack).encode())
					print("message back to client : play,{}".format(snack))
				elif(msg=="play,off"):
					snack = "off"
					client_socket.sendall("play,{}\r\n".format(snack).encode())
					print("message back to client : play,{}".format(snack))
				elif(msg=="play,auto"):
					snack = "auto/"+snack
					client_socket.sendall("play,{}\r\n".format(snack).encode())
					print("message back to client : play,{}".format(snack))
			elif(msg=="health"):
				activity = "health"
				client_socket.sendall("health,{},{},{}\r\n".format(health,healthStep,healthState).encode())
				print("message back to client : health,{},{},{}".format(health,healthStep,healthState))
			elif(msg[:2]=="info"):
				activity = "info"
				if(msg=="info"):
					client_socket.sendall("info,{},{},{}\r\n".format(catName,age,catKind).encode())
					print("message back to client : info,{},{},{}".format(catName,age,catKind))
				elif(msg=="info,cancel"):
					activity = "menu"
				else:
					signup = msg.split(",",3)
					print("정보 변경 : ",signup)
					cur.execute("update catinfo set name='"+signup[1]+"', age="+signup[2]+", breed='"+signup[3]+"' where id='"+idname+"';")
					conn.commit()

					catName = signup[1]
					age = signup[2]
					catKind = signup[3]
					client_socket.sendall("info,{},{},{}\r\n".format(catName,age,catKind).encode())
					print("message back to client : info,{},{},{}".format(catName,age,catKind))
	client_socket.close()  # 클라이언트 소켓 종료

t=threading.Thread(target=server)
t.start()

def temp_mode():
	temp_amb = sensor.get_ambient()
	temp_obj = sensor.get_object_1()
	print("Ambient Temperature :",temp_amb)
	print("Object Temperature :" ,temp_obj)

	temp1 = temp_obj

	if(temp_obj>35):
		temp2 = "high"
	elif (temp_obj>25) and (temp_obj<=35):
		temp2 = "good"
	else:
		temp2 = "low"

	if(tempAuto == "on"):
		if(temp2=="high" or temp2=="good"):
			tempState = "OFF"
		else:
			tempState = "ON"
		if(activity=="temp"):
			client_socket.sendall("temp,{},{},auto/{}\r\n".format(temp1,temp2,tempState).encode())
			print("message back to client : temp,{},{}".format(temp1,temp2))
			print("tempState : auto/{}".format(tempState))
		print("temp : auto mode 실행중!!")
	else:
		if(activity=="temp"):
			client_socket.sendall("temp,{},{},{}\r\n".format(temp1,temp2,tempState).encode())
			print("message back to client : temp,{},{}".format(temp1,temp2))
			print("tempState : {}".format(tempState))
		print("temp : nonauto mode 실행중!!")

	if(tempState == "OFF"):
		GPIO.output(temp_pin, True) # 변경 필요 - 발열 패드 설정 off
	else:
		GPIO.output(temp_pin, True) # 변경 필요 - 발열 패드 설정 on

	print("test {}!".format(activity))
	time.sleep(5)

# main
while True:
	try:
		#temp_mode()
		hx711get()
		#pass
	except (KeyboardInterrupt, SystemExit):
		# Ctrl + c
		cleanAndExit()
'''
	except:
		print("problem!!")
'''
bus.close()
