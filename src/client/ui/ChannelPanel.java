package client.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;

public class ChannelPanel extends JPanel {
    private DefaultListModel<String> channelListModel;
    private JList<String> channelList;
    private ChatPanel chatPanel; // ChatPanel 참조
    private MemberPanel memberPanel; // MemberPanel 참조
    private InfoPanel infoPanel; // InfoPanel 참조

    public ChannelPanel(PrintWriter out) {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));

        JLabel channelLabel = new JLabel("채널");
        channelLabel.setForeground(new Color(220, 221, 222));
        channelLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(channelLabel, BorderLayout.NORTH);

        channelListModel = new DefaultListModel<>();
        channelList = new JList<>(channelListModel);
        channelList.setBackground(new Color(47, 49, 54));
        channelList.setForeground(new Color(220, 221, 222));
        channelList.setSelectionForeground(Color.WHITE);
        channelList.setCellRenderer(new CircleCellRenderer());

        JScrollPane channelScrollPane = new JScrollPane(channelList);
        channelScrollPane.setBorder(BorderFactory.createEmptyBorder());
        channelScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        channelScrollPane.getVerticalScrollBar().setOpaque(false);
        channelScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        add(channelScrollPane, BorderLayout.CENTER);

        JPanel addChannelPanel = new JPanel(new BorderLayout());
        addChannelPanel.setBackground(new Color(47, 49, 54));

        JTextField newChannelField = new JTextField();
        newChannelField.setBackground(new Color(64, 68, 75));
        newChannelField.setForeground(new Color(220, 221, 222));
        newChannelField.setCaretColor(new Color(220, 221, 222));
        addChannelPanel.add(newChannelField, BorderLayout.CENTER);

        JButton addChannelButton = new JButton("+");
        addChannelButton.setBackground(new Color(88, 101, 242));
        addChannelButton.setForeground(Color.WHITE);
        addChannelButton.setFocusPainted(false);
        addChannelPanel.add(addChannelButton, BorderLayout.EAST);

        add(addChannelPanel, BorderLayout.SOUTH);

        // 새 채널 추가 이벤트
        addChannelButton.addActionListener(e -> {
            String newChannel = newChannelField.getText().trim();
            if (!newChannel.isEmpty() && !channelListModel.contains(newChannel)) {
                out.println("/addchannel " + newChannel);
                newChannelField.setText("");
                addChannel(newChannel);
            }
        });

        // 채널 전환 이벤트
        channelList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedChannel = channelList.getSelectedValue();
                if (selectedChannel != null) {
                    out.println("/join " + selectedChannel);
                    if (chatPanel != null) chatPanel.loadChat(selectedChannel);
                    //if (memberPanel != null) memberPanel.updateMembers(selectedChannel);
                    //if (infoPanel != null) infoPanel.updateInfo(selectedChannel);
                }
            }
        });

        addChannels();
    }

    // ChatPanel, MemberPanel, InfoPanel 참조 설정 메서드
    public void setChatPanel(ChatPanel chatPanel) {
        this.chatPanel = chatPanel;
    }

    public void setMemberPanel(MemberPanel memberPanel) {
        this.memberPanel = memberPanel;
    }

    public void setInfoPanel(InfoPanel infoPanel) {
        this.infoPanel = infoPanel;
    }

    // 채널 추가 메서드
    private void addChannel(String channelName) {
        channelListModel.addElement(channelName);
    }

    // 기존 채널 로드
    private void addChannels() {
        String projectDir = System.getProperty("user.dir");
        String path = projectDir + "/resources/channel";

        File channelFolder = new File(path);
        File[] directories = channelFolder.listFiles(File::isDirectory);

        if (directories != null) {
            for (File dir : directories) {
                channelListModel.addElement(dir.getName());
            }
        } else {
            System.out.println("No directories found or the path is incorrect.");
        }
    }

    // 원형 셀 렌더러
    private class CircleCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(new Color(78, 84, 92));
                    g.fillOval(0, 0, getWidth(), getHeight());

                    if (isSelected) {
                        g.setColor(new Color(88, 101, 242));
                        g.fillOval(0, 0, getWidth(), getHeight());
                    }
                }
            };

            panel.setLayout(new BorderLayout());

            JLabel label = new JLabel(value.toString(), SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("맑은 고딕", Font.BOLD, 16));

            panel.add(label, BorderLayout.CENTER);
            panel.setPreferredSize(new Dimension(100, 100));
            panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            panel.setOpaque(false);

            return panel;
        }
    }
}
