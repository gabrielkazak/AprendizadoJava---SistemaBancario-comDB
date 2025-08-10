package BancoComDb;

import java.math.BigDecimal;
import java.sql.*;

public class DatabaseOperations {

    String url = "sua-url-do-banco";
    String userDB = "seu-usuario-no-banco";
    String passwordDB = "sua-senha-no-banco";
    
    public void createUser(String name, String cpf, String password){
        String sql = "INSERT INTO users (name, cpf, password) VALUES (?,?,?)";

        try(Connection connection = DriverManager.getConnection(url, userDB, passwordDB); PreparedStatement statement = connection.prepareStatement(sql)){

            String hashedPassword = PasswordOperations.hashPassword(password);
            statement.setString(1, name);
            statement.setString(2, cpf);
            statement.setString(3, hashedPassword);

            int dbRows = statement.executeUpdate();

            if(dbRows>0){
                System.out.println("Usuario criado com sucesso");
            }else{
                System.out.println("Nenhuma Linha inserida");
            }

        }catch (SQLException e){
            System.out.println("Erro ao inserir usuario " + e.getMessage() );
        }
    }

    public boolean loginUser(String cpf, String passwordInput){
        String sql = "SELECT password, name FROM users WHERE cpf = ?";

        try(Connection connection = DriverManager.getConnection(url, userDB, passwordDB); PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, cpf);

            ResultSet dbRows = statement.executeQuery();

            if (dbRows.next()) {
                String passwordDB = dbRows.getString("password");
                if(PasswordOperations.checkPassword(passwordInput,passwordDB)){
                    String name = dbRows.getString("name");
                    System.out.println("Bem-vindo, " + name + "!");
                    return true;
                }else{
                    return false;
                }
            } else{
                return false;
            }

        } catch (SQLException e){
            System.out.println("Erro ao buscar por usuarios " + e.getMessage());
        }
        return false;
    }

    public void getBalance(String cpf){
        String sql = "SELECT balance FROM users WHERE cpf = ?";

        try(Connection connection = DriverManager.getConnection(url, userDB, passwordDB); PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, cpf);
            ResultSet dbRows = statement.executeQuery();

            if(dbRows.next()){
                BigDecimal balance = dbRows.getBigDecimal("balance");
                System.out.println("Seu saldo atual é de: R$ " + balance.setScale(2));
            } else{
                System.out.println("Saldo não encontrado");
            }
        } catch (SQLException e){
            System.out.println("Erro ao buscar o saldo "+ e.getMessage());
        }
    }

    public void withdraw(String cpf, BigDecimal value ){
        String sqlUpdate = "UPDATE users SET balance = balance - ? WHERE cpf = ? AND balance >= ?";

        try(Connection connection = DriverManager.getConnection(url, userDB, passwordDB);
            PreparedStatement updateStatement = connection.prepareStatement(sqlUpdate)){

            updateStatement.setBigDecimal(1, value);
            updateStatement.setString(2, cpf);
            updateStatement.setBigDecimal(3, value);

            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Saque de R$" + value + " realizado com sucesso.");
            } else {
                System.out.println("Saldo insuficiente ou conta não encontrada.");
            }

        }catch (SQLException e){
            System.out.println("Erro ao atualizar saldo " + e.getMessage());
        }
    }

    public void deposit(String cpf, BigDecimal value){
        String sqlUpdate = "UPDATE users SET balance = balance + ? WHERE cpf = ?";

        try(Connection connection = DriverManager.getConnection(url, userDB, passwordDB);
            PreparedStatement updateStatement = connection.prepareStatement(sqlUpdate);){

            updateStatement.setBigDecimal(1, value);
            updateStatement.setString(2, cpf);

            int dbRows = updateStatement.executeUpdate();

            if(dbRows == 0){
                System.out.println("Falha ao encontrar a conta");
            } else{
                System.out.println("Deposito de R$" + value + " realizado com sucesso.");
            }

        }catch (SQLException e){
            System.out.println("Erro ao realizar deposito: " + e.getMessage());
        }
    }

    public void transfer(String userCpf, BigDecimal value, String targetCpf ){

        if(userCpf.equals(targetCpf)){
            System.out.println("CPF de destino igual ao do CPF de origem");
            return;
        }

        String sqlWithdraw = "UPDATE users SET balance = balance - ? WHERE cpf = ? AND balance >= ?";
        String sqlDeposit = "UPDATE users SET balance = balance + ? WHERE cpf = ?";

        try(Connection connection = DriverManager.getConnection(url, userDB, passwordDB);
            PreparedStatement withdrawStatement = connection.prepareStatement(sqlWithdraw);
            PreparedStatement depositStatement = connection.prepareStatement(sqlDeposit);){

            connection.setAutoCommit(false);

            withdrawStatement.setBigDecimal(1, value);
            withdrawStatement.setString(2, userCpf);
            withdrawStatement.setBigDecimal(3, value);

            int dbRows = withdrawStatement.executeUpdate();

            if(dbRows == 0){
                System.out.println("Saldo insuficiente ou conta não encontrada");
                connection.rollback();
            } else{
                depositStatement.setBigDecimal(1, value);
                depositStatement.setString(2, targetCpf);

                int targetDbRows = depositStatement.executeUpdate();

                if(targetDbRows == 0){
                    System.out.println("Conta de destino nao encontrada");
                    connection.rollback();
                } else{
                    connection.commit();
                    System.out.println("Transação efetuada com sucesso");
                    System.out.println("Transferência de R$"+value+" efetuada para conta de CPF:"+targetCpf);
                }
            }
        }catch (SQLException e){
            System.out.println("Falha ao executar deposito: "+ e.getMessage());
        }
    }
}
