package qrcode;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

import static view.Painel.buildTableModel;

public class DBController {
    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private PreparedStatement ps1 = null, ps2 = null, psPedido = null, psLuminaria = null,
            psBridge = null, psCodigo = null, psPedidoUpdate = null, psPedidoSearch = null,
            psQRCode = null;
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private String nome, numeroPedido, dataEnvio;
    private int qtd, count, pedidoId;
    public DBController(){
        try{ /* * Class.forName("org.postgresql.Driver"); * con=DriverManager.getConnection * ("jdbc:postgresql:5333","root","redhat2"); */
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/qrcode?useTimezone=true&serverTimezone=UTC", "matheushcamilo", "12M04H97c@");
            stmt = con.createStatement();
            psPedido = con.prepareStatement("insert into pedidos values(null,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            psPedidoSearch = con.prepareStatement("select * from pedidos where ");
            psBridge = con.prepareStatement("insert into bridge_table values(?,?)");
            psCodigo = con.prepareStatement("insert into codigos values(null,?,?,?)");
            psQRCode = con.prepareStatement("insert into codigos values(null,?,?,?)");
            psPedidoUpdate = con.prepareStatement("update pedidos set nome=?,qtd=? where numeroPedido=?");
        }catch (Exception e) {
            e.getMessage();
        }
    }
    public Connection getCon() {
        return con;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void closeConection(){
        try {
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    void showAll(){
        try {
            rs = stmt.executeQuery("select * from pedidos");
            JTable table = new JTable(buildTableModel(rs));
            JOptionPane.showMessageDialog(null, new JScrollPane(table));
        } catch (SQLException e) {
            System.out.println("Formato inv??lido!");
        }
    }
    void addOrder(){
        try {
            numeroPedido = JOptionPane.showInputDialog("Entre com o n??mero do pedido");
            if (numeroPedido == null) throw new Exception("Entre com um n??mero v??lido");
            qtd = Integer.parseInt(JOptionPane.showInputDialog("Inserir a quantidade de itens"));
            if (qtd < 0) throw new Exception("Entre com um n??mero positivo");
            psPedido.setString(1, numeroPedido);
            psPedido.setString(2, nome);
            psPedido.setInt(3, qtd);
            psPedido.executeUpdate();
            rs = psPedido.getGeneratedKeys();
            if (rs.next()) {
                pedidoId = rs.getInt(1);
            } else {
                System.out.println("Deu errado");
            }
            rs = stmt.executeQuery("select * from luminarias");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "\t\t" + rs.getString(2) + "\n");
            }
            count = 0;
            while (true) {
                int numeroLuminaria = Integer.parseInt(JOptionPane.showInputDialog("Inclua uma fam??lia" +
                        " de lumin??rias"));
                psBridge.setInt(1, pedidoId);
                psBridge.setInt(2, numeroLuminaria);
                psBridge.executeUpdate();
                int dialogResult = JOptionPane.showConfirmDialog(null, "Deseja inserir outra fam??lia?", "Aviso", JOptionPane.YES_NO_OPTION);
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
                        JOptionPane.showMessageDialog(panel, "QRCode j?? existe! Insira outro: ", "Warning",
                                JOptionPane.WARNING_MESSAGE);

                    }
                }

            }
        } catch (Exception e) {
            e.getMessage();
        }

    }

    void delOrder(){
        try {
            numeroPedido = JOptionPane.showInputDialog("Entre com o n??mero do pedido que deseja apagar");
            if (numeroPedido == null) throw new Exception("Entre com um n??mero v??lido");
            rs = stmt.executeQuery("select * from pedidos where numero_pedido=" + "'" + numeroPedido + "'");
            if (rs.next()) {
                stmt.executeUpdate("delete from pedidos where numero_pedido=" + "'" + numeroPedido + "'");
                System.out.println("Pedido apagado");
            } else {
                throw new Exception("Pedido n??o consta no Banco de Dados");
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    void modifyOrder(){
        try {
            numeroPedido = JOptionPane.showInputDialog("Entre com o n??mero do pedido que deseja modificar");
            nome = JOptionPane.showInputDialog("Inserir o respons??vel");
            if (numeroPedido == null) throw new Exception("Entre com um n??mero v??lido");
            rs = stmt.executeQuery("select * from student where numeroPedido=" + "'" + numeroPedido + "'");
            if (rs.next()) {
                qtd = Integer.parseInt(JOptionPane.showInputDialog("Inserir a quantidade de itens"));
                if (qtd < 0) throw new Exception("Entrar com n??mero positivo");
                ps2.setString(1, numeroPedido);
                ps2.setString(3, nome);
                ps2.setInt(2, qtd);
                ps2.executeUpdate();
            } else throw new Exception("Pedido n??o consta no Banco de Dados");
        } catch (Exception e) {
            e.getMessage();
        }
    }
    void findQRCode(){
        try {
            String codigo = JOptionPane.showInputDialog("Entre com o QRCode que deseja buscar");
            if (codigo == null) throw new Exception("Entre com um n??mero v??lido");
            rs = stmt.executeQuery("select * from codigos where codigo=" + "'" + codigo + "'");
            if (rs.next()){
                int pedido_id = rs.getInt(3);
                System.out.println(pedido_id);
                rs = stmt.executeQuery("select * from pedidos where ID = " + pedido_id);
                if (rs.next()) {
                    System.out.println("Pedido encontrado");
                    System.out.println(rs.getInt(1) + "\t\t" + rs.getString(2) + "\t\t\t\t" + rs.getString(3) + "\t\t\t" + rs.getInt(4) + "\n");
                } else System.out.println("Pedido n??o encontrado!");
            } else{
                System.out.println("Ocorreu um erro");
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
