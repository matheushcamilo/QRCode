package qrcode;

import javax.swing.*;
import java.io.*;
import java.sql.*;

class Main {
    public static void main(String args[]) {
        DBController controlador = new DBController();
        String nome;
        int ch;
        if (controlador.getCon() != null) {
            nome = JOptionPane.showInputDialog("Inserir o responsável");
            controlador.setNome(nome);
            do {
                ch = Integer.parseInt(JOptionPane.showInputDialog("1.Adicionar Pedido\n 2.Modificar\n 3.Apagar \n 4.Buscar QRCode \n 5.Mostrar tudo \n 6.Sair\n"));
                switch (ch) {
                    case 5:
                       controlador.showAll();
                        break;
                    case 1:
                        controlador.addOrder();
                        break;
                    case 3:
                        int escolha = Integer.parseInt(JOptionPane.showInputDialog("1. Deletar pedido e seus códigos;\n2.Deletar um código específico"));
                        switch (escolha){
                            case 1:
                                controlador.delOrder();
                                break;
                            case 2:
                                controlador.delQRCode();
                                break;

                        }
                        break;
                    case 2:
                        controlador.modifyOrder();
                        break;
                    case 4:
                        controlador.findQRCode();
                        break;
                    case 6:
                        System.exit(0);
                    default:
                        System.out.println("Escolha inválida!");
                }
            } while (ch != 6);
            controlador.closeConection();
        }
    }
}    // main }// class

//Read more: http: //mrbool.com/jdbc-basics-how-to-deal-with-databases-in-java/28087#ixzz753ZzAb9R

