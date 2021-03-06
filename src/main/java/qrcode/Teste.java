package qrcode;

import javax.swing.*;
import java.io.*;
import java.sql.*;

class Teste {
    public static void main(String args[]) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        PreparedStatement ps1 = null, ps2 = null, psPedido = null, psLuminaria = null,
                psBridge = null, psCodigo = null, psPedidoUpdate = null, psPedidoSearch = null,
                psQRCode = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String nome, numeroPedido, dataEnvio;
        int qtd, count, pedidoId;
        int rno, ch;
        try { /* * Class.forName("org.postgresql.Driver"); * con=DriverManager.getConnection * ("jdbc:postgresql:5333","root","redhat2"); */
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/qrcode?useTimezone=true&serverTimezone=UTC", "matheushcamilo", "12M04H97c@");
            stmt = con.createStatement();
            psPedido = con.prepareStatement("insert into pedidos values(null,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            psPedidoSearch = con.prepareStatement("select * from pedidos where ");
            psBridge = con.prepareStatement("insert into bridge_table values(?,?)");
            psCodigo = con.prepareStatement("insert into codigos values(null,?,?,?)");
            psQRCode = con.prepareStatement("insert into codigos values(null,?,?,?)");
            psPedidoUpdate = con.prepareStatement("update pedidos set nome=?,qtd=? where numeroPedido=?");
            if (con != null) {
                nome = JOptionPane.showInputDialog("Inserir o responsável");
                do {
                    ch = Integer.parseInt(JOptionPane.showInputDialog("1.Adicionar Pedido\n 2.Modificar\n 3.Apagar \n 4.Buscar QRCode \n 5.Mostrar tudo \n 6.Sair\n"));
                    switch (ch) {
                        case 5:
                            rs = stmt.executeQuery("select * from pedidos");
                            System.out.println("ID \t\t Número do pedido \t Responsável \t Quantidade\n");
                            while (rs.next()) {
                                System.out.println(rs.getInt(1) + "\t\t" + rs.getString(2) + "\t\t\t\t" + rs.getString(3) + "\t\t\t" + rs.getInt(4) + "\n");
                            }
                            break;
                        case 1:
                            numeroPedido = JOptionPane.showInputDialog("Entre com o número do pedido");
                            if (numeroPedido == null) throw new Exception("Entre com um número válido");
                            qtd = Integer.parseInt(JOptionPane.showInputDialog("Inserir a quantidade de itens"));
                            if (qtd < 0) throw new Exception("Entre com um número positivo");
                            psPedido.setString(1, numeroPedido);
                            psPedido.setString(2, nome);
                            psPedido.setInt(3, qtd);
                            psPedido.executeUpdate();
                            rs = psPedido.getGeneratedKeys();
                            if (rs.next()) {
                                pedidoId = rs.getInt(1);
                            } else {
                                System.out.println("Deu errado");
                                break;
                            }
                            rs = stmt.executeQuery("select * from luminarias");
                            while (rs.next()) {
                                System.out.println(rs.getInt(1) + "\t\t" + rs.getString(2) + "\n");
                            }
                            count = 0;
                            while (true) {
                                int numeroLuminaria = Integer.parseInt(JOptionPane.showInputDialog("Inclua uma família" +
                                        " de luminárias"));
                                psBridge.setInt(1, pedidoId);
                                psBridge.setInt(2, numeroLuminaria);
                                psBridge.executeUpdate();
                                int dialogResult = JOptionPane.showConfirmDialog(null, "Deseja inserir outra família?", "Aviso", JOptionPane.YES_NO_OPTION);
                                if (dialogResult == JOptionPane.NO_OPTION || count == qtd) {
                                    count = 0;
                                    break;
                                }
                                count++;
                            }
                            rs = stmt.executeQuery("select * from bridge_table where pedidos_id = " + "'" +
                                    pedidoId + "'");
                            count = 0;
                            while (rs.next()) {
                                while(count < qtd){
                                    try {
                                        String input = JOptionPane.showInputDialog("Insira um qrcode:");
                                        if (input == null) break;
                                        System.out.println(pedidoId);
                                        System.out.println(rs.getInt(2));
                                        psCodigo.setString(1, input);
                                        psCodigo.setInt(2, pedidoId);
                                        psCodigo.setInt(3, rs.getInt(2));
                                        psCodigo.executeUpdate();
                                        count++;
                                    }catch(SQLIntegrityConstraintViolationException e){
                                        final JPanel panel = new JPanel();
                                        JOptionPane.showMessageDialog(panel, "QRCode já existe! Insira outro: ", "Warning",
                                                JOptionPane.WARNING_MESSAGE);

                                    }
                                }

                            }
                            break;
                        case 3:
                            numeroPedido = JOptionPane.showInputDialog("Entre com o número do pedido que deseja apagar");
                            if (numeroPedido == null) throw new Exception("Entre com um número válido");
                            rs = stmt.executeQuery("select * from pedidos where numero_pedido=" + "'" + numeroPedido + "'");
                            if (rs.next()) {
                                stmt.executeUpdate("delete from pedidos where numero_pedido=" + "'" + numeroPedido + "'");
                                System.out.println("Pedido apagado");
                            } else {
                                throw new Exception("Pedido não consta no Banco de Dados");
                            }
                            break;
                        case 2:
                            numeroPedido = JOptionPane.showInputDialog("Entre com o número do pedido que deseja modificar");
                            nome = JOptionPane.showInputDialog("Inserir o responsável");
                            if (numeroPedido == null) throw new Exception("Entre com um número válido");
                            rs = stmt.executeQuery("select * from student where numeroPedido=" + "'" + numeroPedido + "'");
                            if (rs.next()) {
                                qtd = Integer.parseInt(JOptionPane.showInputDialog("Inserir a quantidade de itens"));
                                if (qtd < 0) throw new Exception("Entrar com número positivo");
                                ps2.setString(1, numeroPedido);
                                ps2.setString(3, nome);
                                ps2.setInt(2, qtd);
                                ps2.executeUpdate();
                            } else throw new Exception("Pedido não consta no Banco de Dados");
                            break;
                        case 4:
                            String codigo = JOptionPane.showInputDialog("Entre com o QRCode que deseja buscar");
                            if (codigo == null) throw new Exception("Entre com um número válido");
                            rs = stmt.executeQuery("select * from codigos where qrcode=" + "'" + codigo + "'");
                            if (rs.next()){
                                int pedido_id = rs.getInt(3);
                                System.out.println(pedido_id);
                                rs = stmt.executeQuery("select * from pedidos where ID = " + pedido_id);
                                if (rs.next()) {
                                    System.out.println("Pedido encontrado");
                                    System.out.println(rs.getInt(1) + "\t\t" + rs.getString(2) + "\t\t\t\t" + rs.getString(3) + "\t\t\t" + rs.getInt(4) + "\n");
                                } else System.out.println("Pedido não encontrado!");
                            } else{
                                System.out.println("Ocorreu um erro");
                            }
                        case 6:
                            System.exit(0);
                        default:
                            System.out.println("Escolha inválida!");
                    }
                } while (ch != 6);
                rs.close();
                stmt.close();

                con.close();
            }
        } catch (NumberFormatException ae) {
            System.out.println("Formato inválido");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}    // main }// class

//Read more: http: //mrbool.com/jdbc-basics-how-to-deal-with-databases-in-java/28087#ixzz753ZzAb9R

