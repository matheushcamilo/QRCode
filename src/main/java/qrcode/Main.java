package qrcode;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.sql.*;

class Main {
    public static void main(String args[]) {
        DBController controlador = new DBController();
        String nome;
        int ch;

        try {
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
                            controlador.delOrder();
                            break;
                        case 2:
                            controlador.modifyOrder();
                            break;
                        case 4:
                            controlador.findQRCode();
                        case 6:
                            System.exit(0);
                        default:
                            System.out.println("Escolha inválida!");
                    }
                } while (ch != 6);
                controlador.closeConection();
            }
        } catch (HeadlessException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}    // main }// class

//Read more: http: //mrbool.com/jdbc-basics-how-to-deal-with-databases-in-java/28087#ixzz753ZzAb9R

