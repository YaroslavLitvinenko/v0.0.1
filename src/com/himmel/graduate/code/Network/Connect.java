package com.himmel.graduate.code.Network;

import com.himmel.graduate.code.DB.DBManagmnet;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by Lyaro on 28.01.2016.
 */
public class Connect {
    //TODO Доделать обработку исключени на TCP соединении
    //TODO перед релизом убрать e.printStackTrace();
    //TODO Допилить если вылезут ошибки при получении mac => нет сетевой карты => все плохо!
    //TODO Допилить синхр. с определенными устройствами, пока что она работает даже если на одном устройстве есть этот мак

    //mac адрес этого ПК
    public static final String MAC;
    //номера портов
    private static final int PORT_UDP = 8033;
    private static final int PORT_TCP = 8034;
    //код для подтверждения части программы
    private static final byte[] MD5_TRUE = "1bb23a583cf7b04cd0774c727465cec9".getBytes();
    private static final byte[] MESSAGE;

    static {
        NetworkInterface network = null;
        String bufMac = "";
        try {
            network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            byte[] macB = network.getHardwareAddress();
            for (int i = 0; i < macB.length; i++) {
                bufMac += (String.format("%02X%s", macB[i], (i < macB.length - 1) ? ":" : ""));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        MAC = bufMac;
        byte[] bufMessage = new byte[MD5_TRUE.length + MAC.length()];
        System.arraycopy(MD5_TRUE, 0, bufMessage, 0, MD5_TRUE.length);
        System.arraycopy(MAC.getBytes(), 0, bufMessage, MD5_TRUE.length, MAC.length());
        MESSAGE = bufMessage;
    }

    private DBManagmnet db;
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


    public Connect(int timeSleep, DBManagmnet db){
        this.timeSleep = timeSleep;
        this.db = db;

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
                            s.send(new DatagramPacket(MESSAGE, MESSAGE.length, addr, PORT_UDP));
                            Thread.sleep(timeSleep);
                        }
                        int a = new Integer(5);
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
                //Пришедшее сообщение
                byte data[] = new byte[MESSAGE.length];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                try {
                    DatagramSocket s = new DatagramSocket(PORT_UDP);
                    Socket bufSocket = new Socket();
                    try {
                        while (flagOfBroadcast){
                            s.receive(packet);
                            InetAddress possibleAddress = packet.getAddress();
                            //Если найден клиент выходим изпоиска
                            if (data.length == MESSAGE.length){
                                //В сообщении пришло уникальный ключ и мак
                                //Разбираем на составляющие
                                //Получаем уникальный ключ
                                byte[] dataMd5 = new byte[MD5_TRUE.length];
                                System.arraycopy(data, 0, dataMd5, 0, MD5_TRUE.length);
                                //Получаем мак
                                byte[] dataMac = new byte[MAC.length()];
                                System.arraycopy(data, MD5_TRUE.length, dataMac, 0, MAC.length());
                                //Проверка на необходимость синхронизации с каким либо устройством осуществляется по mac адресу
                                if (!possibleAddress.equals(InetAddress.getLocalHost()) && connectivity(possibleAddress) &&
                                                Arrays.equals(dataMd5, MD5_TRUE) && contentsMac(new String(dataMac))) {
                                    //Прекращение работы потока ожидающео связь от клиента
                                    bufSocket = new Socket(InetAddress.getByName("127.0.0.1"), PORT_TCP);
                                    bufSocket.close();
                                    bufSocket = new Socket(possibleAddress, PORT_TCP);
                                    //Сохранение информации о клиенте
                                    socket = new MySocket(possibleAddress, true);
                                    flagOfBroadcast = false;
                                }
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

            //Проверка на содержании мака в БД
            private boolean contentsMac (String mac) {
                ObservableList<String> listDevices = db.getDataOfDevice();
                for (String device : listDevices)
                    if (device.equals(mac))
                        return true;
                return false;
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

    //Функция для возврата соединения из класса
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
