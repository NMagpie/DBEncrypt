package main;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import java.util.Scanner;

public class Commands {

    private static final DBCollection collection = Main.getCollection();

    private static BasicDBObject selectedObj = null;

    public static void crypto(String[] args, boolean encrypt) {
        if (selectedObj == null) {
            noSelected();
            return;
        }
        if (args.length != 2) {
            argsError();
            return;
        }

        String[] newArgs = new String[args.length + 1];

        newArgs[1] = args[1];

        if (encrypt)
            newArgs[2] = Crypto.encrypt(selectedObj.getString(args[1]));
        else
            newArgs[2] = Crypto.decrypt(selectedObj.getString(args[1]));

        updateData(newArgs);
    }

    public static void resetKeys() {
        System.out.print("Are you sure you want to reset crypto keys? [y/n]: ");

        Scanner sc = new Scanner(System.in);

        String input = sc.nextLine();

        if (input.equals("y"))
            Crypto.resetIv();
    }

    public static void select(String[] args) {
        if (args.length != 2) {
            argsError();
            return;
        }

        BasicDBObject object = new BasicDBObject();

        object.put("username", args[1]);

        DBCursor cursor = collection.find(object);

        if (cursor.hasNext())
            selectedObj = (BasicDBObject) cursor.next();
        else {
            selectedObj = null;
            System.out.println("Error: no such value");
        }

        Main.setSelectedObj(selectedObj);
    }

    public static void deselect() {
        selectedObj = null;
        Main.setSelectedObj(null);
    }

    public static void change(String[] args) {
        if (selectedObj == null) {
            noSelected();
            return;
        }
        if (args.length == 3) {
            updateData(args);
            return;
        }
        if (args.length == 4 && args[3].equals("-e")) {
            args[2] = Crypto.encrypt(args[2]);
            updateData(args);
            return;
        }
        argsError();
    }

    private static void updateData(String[] args) {
        BasicDBObject changes = new BasicDBObject();
        changes.put(args[1], args[2]);
        BasicDBObject updatedData = new BasicDBObject();
        updatedData.put("$set", changes);
        updatedData.putIfAbsent("$set", changes);
        collection.update(selectedObj, updatedData);
        selectedObj.put(args[1], args[2]);
    }

    public static void delete(String[] args) {
        if (args.length != 2) {
            argsError();
            return;
        }
        BasicDBObject updatedData = new BasicDBObject();
        updatedData.put("$unset", new BasicDBObject(args[1], ""));
        collection.update(selectedObj, updatedData);
        selectedObj.remove(args[1]);
    }

    public static void print(String[] args) {
        if (args.length == 1) {
            searchPrint(null);
            return;
        }
        if (args.length == 2) {
            if (!args[1].equals("-s")) {
                DBCursor cursor = collection.find(new BasicDBObject(args[1], new BasicDBObject("$exists", true)));
                while (cursor.hasNext())
                    System.out.println(cursor.next());
                return;
            }
            if (selectedObj != null)
                System.out.println(selectedObj);
            else noSelected();
            return;
        }
        if (args.length == 3) {
            BasicDBObject object = new BasicDBObject();
            object.put(args[1], args[2]);
            searchPrint(object);
        } else argsError();
    }

    public static void exit() {
        System.out.println("Exiting program...");
        System.exit(0);
    }

    public static void help() {
        System.out.println("Commands:\nprint\nsel\ndesel\nexit\nch\ndel\nenc\ndec\nhelp\nreset\n");
    }

    public static void searchPrint(BasicDBObject object) {

        DBCursor cursor = collection.find(object);

        while (cursor.hasNext())
            System.out.println(cursor.next());

    }

    private static void argsError() {
        System.out.println("Error: wrong number of arguments");
    }

    private static void noSelected() {
        System.out.println("Error: no selected document");
    }

}
