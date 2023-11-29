package org.example;

import org.jgroups.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GroupChatExample implements Receiver {

    private JChannel channel;
    private String coordenador;
    private List<String> mensagens = new ArrayList<>();

    public GroupChatExample(JChannel channel) {
        this.channel = channel;
    }

    public String getCoordenador() {
        return coordenador;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    @Override
    public void receive(Message message) {
        String line = message.getSrc()+" disse: "+message.getObject()+"\n";
        System.out.println(line);
        mensagens.add(line);

    }

    @Override
    public void viewAccepted(View view_atual) {
        System.out.println("---VISÃO DO GRUPO ATUALIZADA---");
        System.out.println("ID da view: "+view_atual.getViewId().getId());
        System.out.println("Coordenador: "+view_atual.getCreator());
        coordenador = view_atual.getCreator().toString();
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

    public void iniciaChat(){
        // Inicializa a classe e implementa cluster
        try {
            System.setProperty("java.net.preferIPv4Stack", "true");
            channel.setReceiver(this); // BUG NÃO USA O MESMO JFRAME E DA PAU!!!
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
