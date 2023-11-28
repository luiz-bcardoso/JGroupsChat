package org.example;

import org.jgroups.*;

import javax.swing.*;
import java.util.List;


public class JFormChat implements Receiver{
    private JPanel panelMain;
    private JButton conectaButton;
    private JButton desconectaButton;
    public JTextArea textAreaChat;
    private JTextField MensagemtextField;
    private JButton enviarMensagemButton;
    public JTextField textFieldCoordenador;

    public static JChannel channel;

    public JFormChat(){
        conectaButton.addActionListener(e -> conectar());
        desconectaButton.addActionListener(e -> desconectar());
        enviarMensagemButton.addActionListener(e -> enviarMensagem());
    }
    private void conectar(){

        try {
            channel.connect(JOptionPane.showInputDialog("Digite o nome do cluster: "));
            JOptionPane.showMessageDialog(null, "Conectado ao cluster!");

            // Altera estado dos botoes
            desconectaButton.setEnabled(true);
            conectaButton.setEnabled(false);
            enviarMensagemButton.setEnabled(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void desconectar(){
        channel.disconnect();
        JOptionPane.showMessageDialog(null, "Desconectado do cluster!");

        // Altera estado dos botoes
        conectaButton.setEnabled(true);
        desconectaButton.setEnabled(false);
        enviarMensagemButton.setEnabled(false);
    }
    private void enviarMensagem(){
        String line = MensagemtextField.getText();
        if(!line.isBlank()){
            Message message = new Message(null, line);
            try {
                channel.send(message);
            } catch (Exception e) {
                System.err.println("Erro ao enviar mensagem, talvez não esteja conectado?");
                JOptionPane.showMessageDialog(null, "Erro ao enviar mensagem, talvez não esteja conectado?");
                throw new RuntimeException(e);
            }
            MensagemtextField.setText("");
        }
    }

    public void iniciarCluster(){
        try {
            System.setProperty("java.net.preferIPv4Stack", "true");
            channel = new JChannel("src/main/resources/udp.xml");
            channel.setReceiver(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
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

        // Inicializa a classe e implementa cluster
        JFormChat chat = new JFormChat();
        chat.iniciarCluster();
    }

    @Override
    public void receive(Message message) {
        String line = message.getSrc()+" disse: "+message.getObject()+"\n";
        System.out.println(line);
        textAreaChat.append(line);
    }

    @Override
    public void viewAccepted(View view_atual) {
        System.out.println("---VISÃO DO GRUPO ATUALIZADA---");
        System.out.println("ID da view: "+view_atual.getViewId().getId());
        System.out.println("Coordenador: "+view_atual.getCreator());
        textFieldCoordenador.setText(view_atual.getCreator().toString());
        System.out.print("Membros: ");
        List<Address> processos = view_atual.getMembers();
        for(Address proc: processos){
            System.out.print(proc+", ");
        }
        System.out.println();
    }

    @Override
    public void suspect(Address suspected_mbr) {
        System.out.println("PROCESSO SUSPEITO DE FALHA: " + suspected_mbr);
    }
}
