package org.example;

import org.jgroups.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GroupChatExample implements Receiver {

    private JChannel channel;
    private String coordenador;
    private String membros;
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

    public String getClientes(){
        return membros;
    }

    @Override
    public void receive(Message message) {
        String line = message.getSrc()+" disse: "+message.getObject()+"\n";
        System.out.println(line);
        mensagens.add(line);

    }

    @Override
    public void viewAccepted(View view_atual) {

        // Simple fix for duplicate clients.
        membros = "";
        System.out.println("Limpada lista de clientes");

        System.out.println("---VISÃO DO GRUPO ATUALIZADA---");
        System.out.println("ID da view: "+view_atual.getViewId().getId());
        System.out.println("Coordenador: "+view_atual.getCreator());
        coordenador = view_atual.getCreator().toString();
        System.out.print("Membros: ");
        List<Address> processos = view_atual.getMembers();
        for(Address proc: processos){
            membros = membros + ", " + proc;
            System.out.print(proc+", ");
        }
        System.out.println();
    }

    @Override
    public void suspect(Address suspected_mbr) {
        System.out.println("PROCESSO SUSPEITO DE FALHA: " + suspected_mbr);
        try {
            channel.send(new Message(null, "PROCESSO SUSPEITO DE FALHA: "+ suspected_mbr));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void iniciaChat(){
        // Inicializa a classe e implementa cluster
        try {
            System.setProperty("java.net.preferIPv4Stack", "true");
            channel.setReceiver(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
