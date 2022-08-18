# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""
# echo_server.py
import socket

# 통신 정보 설정
IP = ''
PORT = 35000
SIZE = 1024
ADDR = (IP, PORT)
msg = ''

signup = []
idname = ""
pwd = ""
catName = ""
age = ""
catKind = ""

enableStepAuto = 0
Step1 = 0
Step2 = 3

tempState = "on"
temp1 = "0"
temp2 = "온도"

weight = "1-2-3-4-5-6-7"
cal = "저열랑"
feed = "안주기"
feednum = "0"

playcount = "3-2-1-1-3-2-1"

health = "2-3-1-3-3-2-2"
healthStep = "위험"
healthState = "병원 방문이 필요합니다."
# 서버 소켓 설정

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_socket.bind(ADDR)  # 주소 바인딩
    server_socket.listen(3)  # 클라이언트의 요청을 받을 준비
    client_socket, client_addr = server_socket.accept()  # 수신대기, 접속한 클라이언트 정보 (소켓, 주소) 반환
    print("connected")
    
        # 무한루프 진입
    while (True):
        msg = client_socket.recv(1024).decode()
        #print("massage from client : ", msg.decode('utf-8'))
        if msg!="" :
            print("[{}] massage : {}".format(client_addr, msg))
            if(msg[:5]=="회원가입,"):
                signup = msg.split(",",5)
                print("회원가입 : ",signup)
                idname = signup[1]
                pwd = signup[2]
                catName = signup[3]
                age = signup[4]
                catKind = signup[5]
            elif(msg=="로그인,,"):
                client_socket.sendall("로그인,성공\r\n".encode())
                print("message back to client : 로그인,성공")
            elif(msg[:2]=="계단"):
                if(msg=="계단"):
                    Step1 = 0
                    Step2 = 0
                    client_socket.sendall("계단,{},{}\r\n".format(Step1,Step2).encode()) 
                    print("message back to client : 계단,{},{}".format(Step1,Step2))
                elif(msg[0:3]=="계단,"):
                    if(msg=="계단,auto"):
                        enableStepAuto = 1
                    else:
                        enableStepAuto = 0
                    
                    if(enableStepAuto==0):
                        Step1 = msg[3]
                        Step2 = msg[5]
                        client_socket.sendall("계단,{},{}\r\n".format(Step1,Step2).encode()) 
                        print("message back to client : 계단,{},{}".format(Step1,Step2))
                    else:
                        Step1 = 2
                        Step2 = 2
                        client_socket.sendall("계단,{},{},auto\r\n".format(Step1,Step2).encode()) 
                        print("message back to client : 계단,{},{},auto".format(Step1,Step2))
            elif(msg[:2]=="온도"):
                if(msg=="온도"):
                    tempState = "None"
                    temp1 = "20"
                    temp2 = "온도"
                elif(msg=="온도,on"):
                    tempState = "on"
                    temp1 = "26"
                    temp2 = "낮은온도"
                elif(msg=="온도,off"):
                    tempState = "off"
                    temp1 = "28"
                    temp2 = "높은온도"
                elif(msg=="온도,auto"):
                    tempState = "auto"
                    temp1 = "27"
                    temp2 = "적정온도"
                client_socket.sendall("온도,{},{}\r\n".format(temp1,temp2).encode()) 
                print("message back to client : 온도,{},{}".format(temp1,temp2))
                print("tempState : {}".format(tempState))
            elif(msg[:2]=="체중"):
                if(msg=="체중"):
                    weight = "1-2-3-4-5-6-7"
                    cal = "저열랑"
                    feed = "안주기"
                    feednum = 0
                    client_socket.sendall("체중,{},{},{},{}\r\n".format(weight,cal,feed,feednum).encode()) 
                    print("message back to client : 체중,{},{},{},{}".format(weight,cal,feed,feednum))
                elif(msg=="체중,저열량"):
                    cal = "저열량"
                    client_socket.sendall("체중,{}\r\n".format(cal).encode()) 
                    print("message back to client : 체중,{}".format(cal))
                elif(msg=="체중,고열량"):
                    cal = "고열량"
                    client_socket.sendall("체중,{}\r\n".format(cal).encode()) 
                    print("message back to client : 체중,{}".format(cal))
                elif(msg=="체중,주기"):
                    feed = "주기"
                    client_socket.sendall("체중,{}\r\n".format(feed).encode()) 
                    print("message back to client : 체중,{}".format(feed))
                elif(msg=="체중,안주기"):
                    feed = "안주기"
                    client_socket.sendall("체중,{}\r\n".format(feed).encode()) 
                    print("message back to client : 체중,{}".format(feed))
                elif(msg[3]>="0" or msg[3]<="3"):
                    feednum = msg[3]
                    client_socket.sendall("체중,{}\r\n".format(feednum).encode()) 
                    print("message back to client : 체중,{}".format(feednum))
            elif(msg=="운동"):
                playcount = "3-2-1-1-3-2-1"
                client_socket.sendall("운동,{}\r\n".format(playcount).encode()) 
                print("message back to client : 운동,{}".format(playcount))
            elif(msg=="건강"):
                health = "2-3-1-3-3-2-2"
                healthStep = "위험"
                healthState = "병원 방문이 필요합니다."
                client_socket.sendall("건강,{},{},{}\r\n".format(health,healthStep,healthState).encode()) 
                print("message back to client : 건강,{},{},{}".format(health,healthStep,healthState))
            elif(msg[:2]=="정보"):
                if(msg=="정보"):
                    client_socket.sendall("정보,{},{},{}\r\n".format(catName,age,catKind).encode()) 
                    print("message back to client : 정보,{},{},{}".format(catName,age,catKind))
                elif(msg=="정보,cancel"):
                    pass
                else:
                    signup = msg.split(",",3)
                    print("정보 변경 : ",signup)
                    catName = signup[1]
                    age = signup[2]
                    catKind = signup[3]
                    client_socket.sendall("정보,{},{},{}\r\n".format(catName,age,catKind).encode())
                    print("message back to client : 정보,{},{},{}".format(catName,age,catKind))
    client_socket.close()  # 클라이언트 소켓 종료