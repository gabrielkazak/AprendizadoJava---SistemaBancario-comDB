package BancoComDb;

import java.math.BigDecimal;
import java.util.Scanner;

public class Main {

    public static boolean containsOnlyDigits(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static void getUserInfos() {
        Scanner read = new Scanner(System.in);

        System.out.println("Vamos criar sua conta:");

        System.out.println("Insira seu nome: ");
        String name = read.nextLine();

        System.out.println("Insira seu cpf: ");
        String cpf = read.nextLine();
        while (cpf.length() != 11 || containsOnlyDigits(cpf)) {
            System.out.println("CPF Inválido, insira novamente");
            cpf = read.nextLine();
        }

        System.out.println("Insira sua senha: ");
        String password = read.nextLine();
        while (password.length() < 8) {
            System.out.println("Senha muito curta, insira novamente");
            password = read.nextLine();
        }

        DatabaseOperations insert = new DatabaseOperations();
        insert.createUser(name, cpf, password);
    }


    public static void enterAccount() {
        Scanner read = new Scanner(System.in);

        System.out.println("Insira seu cpf: ");
        String cpf = read.nextLine();
        while (cpf.length() != 11 || containsOnlyDigits(cpf)) {
            System.out.println("CPF Inválido, insira novamente");
            cpf = read.nextLine();
        }

        System.out.println("Insira sua senha: ");
        String password = read.nextLine();
        while (password.length() < 8) {
            System.out.println("Senha muito curta, insira novamente");
            password = read.nextLine();
        }

        DatabaseOperations operation = new DatabaseOperations();
        boolean canAccess = operation.loginUser(cpf, password);

        if(!canAccess){
            System.out.println("CPF ou Senha Incorretos");
            return;
        }

        System.out.println("Acesso Liberado");

        while(true){
            System.out.println("Cliente, o que gostaria de realizar?");
            System.out.println("1 - Ver saldo\n2 - Sacar\n3 - Depositar\n4 - Transferir\n0 - Sair");

            int option;
            while (!read.hasNextInt()) {
                System.out.println("Digite uma opção válida (0 a 4): ");
                read.nextLine();
            }

            option = read.nextInt();
            read.nextLine();

            switch (option) {
                case 1:
                    operation.getBalance(cpf);
                    break;

                case 2:
                    System.out.print("Insira o valor do saque: R$");
                    BigDecimal withdrawValue = read.nextBigDecimal();
                    operation.withdraw(cpf, withdrawValue);
                    break;

                case 3:
                    System.out.print("Insira o valor de depósito: R$");
                    BigDecimal depositValue = read.nextBigDecimal();
                    operation.deposit(cpf, depositValue);
                    break;

                case 4:
                    System.out.print("Insira o valor da Transferencia: R$");
                    BigDecimal transferValue = read.nextBigDecimal();

                    read.nextLine();

                    System.out.print("Insira o CPF da conta de destino:");
                    String targetCPF = read.nextLine();
                    while(targetCPF.length() != 11 || containsOnlyDigits(targetCPF)){
                        System.out.println("CPF em formato inválido");
                        targetCPF = read.nextLine();
                    }

                    operation.transfer(cpf, transferValue, targetCPF);

                    break;

                case 0:
                    System.out.println("Obrigado pela visita ao banco");
                    return;

                default:
                    System.out.println("Opção inválida.");
            }
        }
    }


    public static void main(String[] args) {
        Scanner read = new Scanner(System.in);

        while (true) {
            System.out.println("\nOlá Cliente, o que deseja realizar?");
            System.out.println("1 - Criar Conta\n2 - Acessar Conta\n0 - Sair");

            int option;
            while (!read.hasNextInt()) {
                System.out.println("Digite uma opção válida (0 a 2): ");
                read.nextLine();
            }

            option = read.nextInt();
            read.nextLine();

            switch (option) {
                case 1:
                    getUserInfos();
                    break;

                case 2:
                    enterAccount();
                    break;

                case 0:
                    System.out.println("Obrigado pela visita ao banco");
                    read.close();
                    return;

                default:
                    System.out.println("Opção inválida.");
            }
        }
    }
}