package com.luxoft.bankapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.luxoft.bankapp.exceptions.ActiveAccountNotSet;
import com.luxoft.bankapp.model.*;
import com.luxoft.bankapp.service.BankReportService;
import com.luxoft.bankapp.service.BankReportServiceImpl;
import com.luxoft.bankapp.service.Banking;
import com.luxoft.bankapp.service.BankingImpl;
import com.luxoft.bankapp.model.Client.Gender;
import com.luxoft.bankapp.service.feed.BankFeedService;
import com.luxoft.bankapp.service.feed.BankFeedServiceImpl;
import com.luxoft.bankapp.service.storage.ClientStorage;
import com.luxoft.bankapp.service.storage.Storage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@SpringBootApplication
@PropertySource("classpath:clients.properties")
public class BankApplication {
    private static final String[] CLIENT_NAMES =
            {"Jonny Bravo", "Adam Budzinski", "Anna Smith"};

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }

    public static void bankFeedDemo(ApplicationContext context) {
        System.out.println("\n=== Using BankFeedService ===\n");

        String fileName = "test.feed";

        BankFeedService feedService = context.getBean(BankFeedServiceImpl.class);

        feedService.saveFeed(fileName);

        System.out.println("Stored Clients");
        System.out.println("==============");

        try (BufferedReader reader = new BufferedReader(new FileReader("feeds/" + fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("\nLoading clients from file.");
        System.out.println("==========================");

        Banking banking = new BankingImpl();

        Storage<Client> newStorage = new ClientStorage();
        banking.setStorage(newStorage);

        feedService = new BankFeedServiceImpl(banking);

        feedService.loadFeed(new File(fileName));

        banking.getClients().forEach(System.out::println);
    }

    public static void bankReportsDemo(ApplicationContext context) {
        System.out.println("\n=== Using BankReportService ===\n");

        BankReportService reportService = context.getBean(BankReportServiceImpl.class);

        System.out.println("Number of clients: " + reportService.getNumberOfBankClients());


        System.out.println("Number of accounts: " + reportService.getAccountsNumber());


        System.out.println("Bank Credit Sum: " + reportService.getBankCreditSum());
    }

    public static void bankingServiceDemo(Banking banking) {
        System.out.println("\n=== Initialization using Banking implementation ===\n");

        Client anna = new Client(CLIENT_NAMES[2], Gender.FEMALE);
        anna = banking.addClient(anna);

        Account saving = banking.createAccount(anna, SavingAccount.class);
        saving.deposit(1000);

        banking.updateAccount(anna, saving);

        Account checking = banking.createAccount(anna, CheckingAccount.class);
        checking.deposit(3000);

        banking.updateAccount(anna, checking);

        banking.getAllAccounts(anna).stream().forEach(System.out::println);
    }

    public static void workWithExistingClients(Banking banking) {
        System.out.println("\n=======================================");
        System.out.println("\n===== Work with existing clients ======");

        Client jonny = banking.getClient(CLIENT_NAMES[0]);
        try {
            jonny.deposit(5_000);
        } catch (ActiveAccountNotSet e) {
            System.out.println(e.getMessage());

            jonny.setDefaultActiveAccountIfNotSet();
            jonny.deposit(5_000);
        }

        System.out.println(jonny);

        Client adam = banking.getClient(CLIENT_NAMES[1]);
        adam.setDefaultActiveAccountIfNotSet();

        adam.withdraw(1500);

        double balance = adam.getBalance();
        System.out.println("\n" + adam.getName() + ", current balance: " + balance);

        banking.transferMoney(jonny, adam, 1000);

        System.out.println("\n=======================================");
        banking.getClients().forEach(System.out::println);
    }

    /*
     * Method that creates a few clients and initializes them with sample values
     */
    public static Banking initialize(ApplicationContext context) {
        Banking banking = context.getBean(BankingImpl.class);

        Client client_1 = (Client) context.getBean("client1");
        Client client_2 = (Client) context.getBean("client2");

        banking.addClient(client_1);
        banking.addClient(client_2);


        return banking;
    }

    @Bean(name = "client1")
    public Client getDemoClient1() {
        String name = environment.getProperty("client1");

        Client client = new Client(name, Gender.MALE);
        client.setCity("Moscow");

        Account savingAccount = (SavingAccount) applicationContext.getBean("savingAccount1");
        client.addAccount(savingAccount);

        Account checkingAccount = (CheckingAccount) applicationContext.getBean("checkingAccount1");
        client.addAccount(checkingAccount);

        return client;
    }

    @Bean(name = "savingAccount1")
    public SavingAccount getDemoSavingAccount1() {
        return new SavingAccount(1000);
    }

    @Bean(name = "checkingAccount1")
    public CheckingAccount getDemoCheckingAccount1() {
        return new CheckingAccount(1000);
    }

    @Bean(name = "client2")
    public Client getDemoClient2() {
        String name = environment.getProperty("client2");

        Client client = new Client(name, Gender.MALE);
        client.setCity("Kiev");

        Account checking = (CheckingAccount) applicationContext.getBean("checkingAccount2");
        client.addAccount(checking);

        return client;
    }

    @Bean(name = "checkingAccount2")
    public CheckingAccount getDemoCheckingAccount2() {
        return new CheckingAccount(1500);
    }


}
