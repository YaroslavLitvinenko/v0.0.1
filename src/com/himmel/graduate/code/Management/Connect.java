package com.himmel.graduate.code.Management;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by Lyaro on 28.01.2016.
 */
class Connect {
    //номера портов
    private static final int PORT_UDP = 8033;
    private static final int PORT_TCP = 8034;
    //код для подтверждения части программы
    private static final byte []md5Trur = "1bb23a583cf7b04cd0774c727465cec9".getBytes();
    //время сна между попытками установиь соединение
    private int timeSleep;
    //Потоки выполнения поиска соединения с другими клиентами
    private Thread outSignalUDP;
    private Thread inSignalUDP;
    //Поток оидания соединения от друго-го клиента
    private Thread inSignalTCP;

    //последняя цифра IP текущего клиента
    private int countIP;

    //Флаги для отключения широковещания
    private boolean flagOfBroadcast = true;
    private boolean flagOfShipping = true;

    //Адрес клиента для установления соединения
    private InetAddress addressInput;

    //Сокет служащий для связи с клиентом
    private MySocket socket;

    public Connect(int timeSleep){
        this.timeSleep = timeSleep;

        outSignalUDP = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket s = new DatagramSocket();
                    try{
                        InetAddress addr = InetAddress.getLocalHost();
                        StringTokenizer st = new StringTokenizer(addr.getHostAddress(), ".");
                        String myMask = st.nextToken() + "." + st.nextToken() + "." + st.nextToken() + ".";
                        countIP = Integer.valueOf(st.nextToken());
                        addr = InetAddress.getByName(myMask + "255");
                        while (flagOfShipping){
                            s.send(new DatagramPacket(md5Trur, md5Trur.length, addr, PORT_UDP));
                            Thread.sleep(timeSleep);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        // неверный адрес получателя
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        s.close();
                    }
                } catch (SocketException e) {
                    // возникли ошибки при передаче данных
                    e.printStackTrace();
                }
            }
        });

        inSignalUDP = new Thread(new Runnable() {
            @Override
            public void run() {
                byte data[] = new byte[md5Trur.length];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                try {
                    DatagramSocket s = new DatagramSocket(PORT_UDP);
                    Socket bufSocket = new Socket();
                    try {
                        while (flagOfBroadcast){
                            s.receive(packet);
                            InetAddress possibleAddress = packet.getAddress();
                            //Если найден клиент выходим изпоиска
                            if (!possibleAddress.equals(InetAddress.getLocalHost()) && connectivity(possibleAddress) && Arrays.equals(data, md5Trur)){
                                bufSocket = new Socket(InetAddress.getByName("127.0.0.1"), PORT_TCP);
                                bufSocket.close();
                                bufSocket = new Socket(possibleAddress, PORT_TCP);
                                socket = new MySocket(possibleAddress, true);
                                flagOfBroadcast = false;
                            }
                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } finally {
                        flagOfShipping = flagOfBroadcast;
                        s.close();
                        bufSocket.close();
                    }
                }  catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //Проверка на то что IP сообщения меньше IP адреса клиетаы
            private boolean connectivity (InetAddress addr){
                String IPaddr = addr.toString();
                int i = IPaddr.lastIndexOf(".");
                if (Integer.valueOf(IPaddr.substring(i + 1, IPaddr.length())) < countIP)
                    return true;
                else return false;
            }
        });

        inSignalTCP = new Thread(new Runnable() {
            @Override
            public void run() {
                InetAddress inetAddress;
                ServerSocket serverSocket;
                try {
                    serverSocket = new ServerSocket(PORT_TCP, 1);
                    try{
                        inetAddress = serverSocket.accept().getInetAddress();
                        if (!inetAddress.getHostName().equals(InetAddress.getByName("127.0.0.1").getHostAddress())) {
                            socket = new MySocket(inetAddress, false);
                        }
                        flagOfBroadcast = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        serverSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Запуск потоков на поиск клиентов
    public void search (){
        flagOfBroadcast = true;
        flagOfShipping = true;
        outSignalUDP.start();
        inSignalTCP.start();
        inSignalUDP.start();
    }

    public MySocket getConnection (){
        try {
            inSignalUDP.join();
            inSignalTCP.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return socket;
    }
}
