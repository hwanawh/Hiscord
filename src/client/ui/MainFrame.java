package client.ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class MainFrame extends JFrame {
    public MainFrame(String username) {
        setTitle("Chat - " + username);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
            Socket socket = new Socket("localhost", 12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 채널 패널 생성
            ChannelPanel channelPanel = new ChannelPanel(out);
            add(channelPanel, BorderLayout.WEST);

            // 채팅 패널 생성 및 추가
            ChatPanel chatPanel = new ChatPanel(out);
            add(chatPanel, BorderLayout.CENTER);

            // 접속 중인 멤버 패널 생성 및 추가
            RightPanel rightPanel = new RightPanel();
            add(rightPanel, BorderLayout.EAST);

            // ChannelPanel에 ChatPanel과 RightPanel 설정
            channelPanel.setChatPanel(chatPanel);
            //channelPanel.setMemberPanel(rightPanel);

            // 서버 메시지 수신 처리
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        if (message.startsWith("/members")) {
                            // 멤버 리스트 업데이트 메시지 처리
                            String[] members = message.substring(9).split(",");
                            //rightPanel.updateMembers(Arrays.asList(members));
                        } else {
                            chatPanel.appendMessage(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // 사용자 이름 서버로 전송
            out.println(username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
