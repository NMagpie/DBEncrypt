package main;

import com.mongodb.*;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    
    private static MongoClient mongoClient = new MongoClient("localhost", 27017);

    private static DB database = mongoClient.getDB("UsersDB");

    private static DBCollection collection = database.getCollection("UsersData");

    private static Scanner sc = new Scanner(System.in);

    private static BasicDBObject selectedObj = null;

    public static void main(String[] args) throws InterruptedException {

        TimeUnit.MILLISECONDS.sleep(500);

        String input;

        while (true) {
            if (selectedObj != null) System.out.print("[" + selectedObj.get("username") + "] ");
            System.out.print(">>> ");
            input = sc.nextLine();
            parseCommand(input);
        }

    }

    public static void setSelectedObj(BasicDBObject selectedObj) {
        Main.selectedObj = selectedObj;
    }

    private static void parseCommand(String str) {

        String[] args = str.split(" ");

        switch (args[0]) {
            case "print":
                Commands.print(args);
                break;
            case "sel":
                Commands.select(args);
                break;
            case "desel":
                Commands.deselect();
                break;
            case "exit":
                Commands.exit();
                break;
            case "ch":
                Commands.change(args);
                break;
            case "del":
                Commands.delete(args);
                break;
            case "enc":
                Commands.crypto(args, true);
                break;
            case "dec":
                Commands.crypto(args, false);
                break;
            case "help":
                Commands.help();
                break;
            case "reset":
                Commands.resetKeys();
                break;
            default:
                System.out.println("Error: no such command");
        }

    }

    public static DBCollection getCollection() {
        return collection;
    }
}
