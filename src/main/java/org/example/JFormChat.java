package org.example;

import org.jgroups.*;

import javax.swing.*;
import java.util.List;


public class JFormChat{

    private JPanel panelMain;
    private JButton conectaButton;
    private JButton desconectaButton;
    private JButton enviarMensagemButton;
    private JTextField MensagemtextField;
    private JTextArea textAreaChat;
    private JTextField textFieldCoordenador;
    private JButton atualizarButton;
    private JTextField textFieldMembros;

    private static JChannel channel;
    private static GroupChatExample gce;

    public JFormChat(){
        conectaButton.addActionListener(e -> conectar());
        desconectaButton.addActionListener(e -> desconectar());
        enviarMensagemButton.addActionListener(e -> enviarMensagem());
        atualizarButton.addActionListener(e -> atualizaChat());
    }


    private void atualizaChat() {
        // This is horrible, but it's the only one that worked for the time i had...
        textAreaChat.setText("");
        List<String> mensagens = gce.getMensagens();

        for(int i = 0; i < mensagens.size(); i++){
            textAreaChat.append(mensagens.get(i));
        }
        textFieldCoordenador.setText(gce.getCoordenador());
        textFieldMembros.setText(gce.getClientes());
    }

    private void conectar(){

        try {
            channel.connect(JOptionPane.showInputDialog("Digite o nome do cluster: "));
            JOptionPane.showMessageDialog(null, "Conectado ao cluster!");

            // Altera estado dos botoes
            conectaButton.setEnabled(false);
            desconectaButton.setEnabled(true);
            atualizarButton.setEnabled(true);
            enviarMensagemButton.setEnabled(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void desconectar(){
        int option = JOptionPane.showConfirmDialog(null, "Deseja também finalizar o cluster ao sair?");
        if(option == 0 || option == 1){
            System.out.println("Desconectando do chat.");
            try {
                channel.send(new Message(null, "<< SAIU DO CHAT >>"));
                channel.disconnect();
                JOptionPane.showMessageDialog(null, "Desconectado do cluster.");
            } catch (Exception e) {
                System.err.println("Erro ao enviar mensagem de saida.");
                throw new RuntimeException(e);
            }

            // Altera estado dos botoes
            conectaButton.setEnabled(true);
            atualizarButton.setEnabled(false);
            desconectaButton.setEnabled(false);
            enviarMensagemButton.setEnabled(false);
        }

        if(option == 0){
            channel.close();
            JOptionPane.showMessageDialog(null, "Cluster foi fechado com sucesso!");
        }

    }
    private void enviarMensagem(){
        String line = MensagemtextField.getText();
        if(!line.isBlank()){
            Message message = new Message(null, line);
            try {
                channel.send(message);
                atualizaChat();
            } catch (Exception e) {
                System.err.println("Erro ao enviar mensagem, talvez não esteja conectado?");
                JOptionPane.showMessageDialog(null, "Erro ao enviar mensagem, talvez não esteja conectado?");
                throw new RuntimeException(e);
            }
            MensagemtextField.setText("");
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("JFormChat");
        frame.setContentPane(new JFormChat().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        // Center GUI location and sets visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Instancia a classe GCE que tem o listner implementado para mensagens
        try {
            channel = new JChannel("src/main/resources/udp.xml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        gce = new GroupChatExample(channel);
        gce.iniciaChat();
    }
}
