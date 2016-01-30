package com.himmel.graduate.code.Management;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by Lyaro on 28.01.2016.
 */
class Connect {
    //����� �����
    private static final int port = 8033;
    //��� ��� ������������� ����� ���������
    private static final byte []md5Trur = "1bb23a583cf7b04cd0774c727465cec9".getBytes();
    //����� ��� ����� ��������� ��������� ����������
    private int timeSleep;
    //������ ���������� ���������� � ������� ���������
    private Thread outSignall;
    private Thread inSignal;

    //��������� ����� IP �������� �������
    private int countIP;

    //���� ��� ���������� �������������
    private boolean flagOfBroadcast = true;

    //����� ������� ��� ������������ ����������
    private InetAddress addressInput;

    public Connect (int timeSleep){
        this.timeSleep = timeSleep;

        outSignall = new Thread(new Runnable() {
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
                        while (flagOfBroadcast){
                            s.send(new DatagramPacket(md5Trur, md5Trur.length, addr, port));
                            //JOptionPane.showMessageDialog(new Frame(),"packet isotput");
                            System.out.println("packet isotput");
                            Thread.sleep(timeSleep);
                        }
                        flagOfBroadcast = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        // �������� ����� ����������
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        s.close();
                    }
                } catch (SocketException e) {
                    // �������� ������ ��� �������� ������
                    e.printStackTrace();
                }
            }
        });

        inSignal = new Thread(new Runnable() {
            @Override
            public void run() {
                byte data[] = new byte[md5Trur.length];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                try {
                    DatagramSocket s = new DatagramSocket(port);
                    try {
                        while (flagOfBroadcast){
                            s.receive(packet);
                            InetAddress possibleAddress = packet.getAddress();
                            //���� ������ ������ ������� ��������
                            if (!possibleAddress.equals(InetAddress.getLocalHost()) && connectivity(possibleAddress) && Arrays.equals(data, md5Trur)){
                                addressInput = possibleAddress;
                                flagOfBroadcast = false;
                            }
                        }
                        //JOptionPane.showMessageDialog(new Frame(),"packet input");
                        System.out.println("Packet input");
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } finally {
                        s.close();
                    }
                }  catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //�������� �� �� ��� IP ��������� ������ IP ������ �������
            private boolean connectivity (InetAddress addr){
                String IPaddr = addr.toString();
                int i = IPaddr.lastIndexOf(".");
                if (Integer.valueOf(IPaddr.substring(i + 1, IPaddr.length())) < countIP)
                    return true;
                else return false;
            }
        });

        outSignall.start();
        inSignal.start();
    }

    public InetAddress getAddress (){
        try {
            outSignall.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return addressInput;
    }
}
