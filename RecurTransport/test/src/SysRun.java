
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ZhengJwh
 */
public class SysRun {

    private String url = "jdbc:derby://localhost:1527/RecirOrderDB; create=true";
    private String username = "Recir";
    private String password = "recir";
    public Connection conn = null;
    public Statement statement;
    private static int MAX_ROUNT = 6;
    private static ArrayList<Order> order_list = new ArrayList<Order>();
    private static ArrayList<Driver> driver_list = new ArrayList<Driver>();
    private static HashMap<Driver, Order> dispatch = new HashMap<Driver, Order>();

    public void printOrderList() {
        if (order_list.isEmpty()) {
            System.out.println("The order list is currently empty, please add orders!");
        }
        int num = 1;
        for (Order o : order_list) {
            System.out.println("Order priority: " + num + ", " + o.toString());
            num++;
        }
    }

    public void printDriverList() {
        if (driver_list.isEmpty()) {
            System.out.println("No data store in driver list yet!");
        }
        for (Driver d : driver_list) {
            System.out.println(d.toString());
        }
    }

    public void sortDriverList() {
        Driver temp;
        for (int i = 0; i < driver_list.size(); i++) {
            for (int j = 1; j < driver_list.size(); j++) {
                if ((driver_list.get(j).getPriority()) < (driver_list.get(j - 1).getPriority())) {
                    //two different type of swap. both works.
//                    temp = driver_list.get(j-1);
//                    driver_list.set(j-1, driver_list.get(j));
//                    driver_list.set(j, temp);
                    Collections.swap(driver_list, j - 1, j);
                }
            }
        }
    }

    public void sortOrderList() {
        for (Order o : order_list) {
            if (o.getContainer().isEmpty()) {
                order_list.remove(o);
            }
        }
        Order temp;
        for (int i = 0; i < order_list.size(); i++) {
            for (int j = 1; j < order_list.size(); j++) {
                if ((order_list.get(j).cutoff_date.cutoff_date.before(order_list.get(j - 1).cutoff_date.cutoff_date))) {

                    Collections.swap(order_list, j - 1, j);
                }
            }
        }
    }

    public Driver getAvailableDriver() {
        for (Driver driver : driver_list) {
            if (driver.getRoundNumber() < 6) {
                return driver;
            }
        }
        return null;
    }

    public String dispatchingOrders() {

        while (true) {
            Driver driver = this.getAvailableDriver();
            if (driver == null) {
//                System.out.println("No driver available.");
                return "No driver available.";
            }

            while (driver.getRoundNumber() < 6) {
                Round round = driver.getRound();
                int capacityLeft = round.getCapacityLeft();
                // find a container for the round
                int orderLeft = 0;
                for (Order order : order_list) {
                    if (order.container_list.con20_num > 0 || order.container_list.con40_num > 0) {
                        orderLeft++;
                    }
                    
                    boolean sameFromAndTo = order.getFrom().getLocationID().equals(round.getFrom()) && order.getTo().getLocationID().equals(round.getTo());
                    boolean hasProperContainer = capacityLeft >= 40 && order.container_list.con40_num > 0
                            && (round.isEmpty() || sameFromAndTo)
                            || capacityLeft >= 20 && order.container_list.con20_num > 0
                            && (round.isEmpty() || sameFromAndTo);
                    while (hasProperContainer) {
                        if (capacityLeft >= 40 && order.container_list.con40_num > 0
                                && (round.isEmpty() || sameFromAndTo)) {
                            round.addContainer(order.order_id, order.getFrom().getLocationID(), order.getTo().getLocationID(), 40);
                            order.container_list.con40_num--;
                            capacityLeft = round.getCapacityLeft();
                        } else if (capacityLeft >= 20 && order.container_list.con20_num > 0
                                && (round.isEmpty() || sameFromAndTo)) {
                            round.addContainer(order.order_id, order.getFrom().getLocationID(), order.getTo().getLocationID(), 20);
                            order.container_list.con20_num--;
                            capacityLeft = round.getCapacityLeft();
                        }
                        hasProperContainer = capacityLeft >= 40 && order.container_list.con40_num > 0
                                && (round.isEmpty() || sameFromAndTo)
                                || capacityLeft >= 20 && order.container_list.con20_num > 0
                                && (round.isEmpty() || sameFromAndTo);
                    }
                }

                if (orderLeft == 0) {
//                    System.out.println("No order left.");
                    return "No order left.";
                }

                driver.increaseRound();
            }

            // look for container less/equal capacityAvailable
        }
        //Before dispathing. The system should require sorting on both
        //order_list and driver_list in order to follow the algorithm.

        //In this method. Use two while loop to justify each rounds, algorithm
        //will add datas into dispatch, such as, when algorithm runs, it will evaluate
        //the first Driver and Order object in both list.
        //In plain code: if first Order subject contains (10x40ft, 5x20ft) initial round = 1;
        //<Driver> - <Order object (2x40ft, 1x20ft)> <-- change happens in Order object (10-2 x40ft, 5-1 x20ft) round++;
        //dispatch.put(key, value)
        //if the round reach MAX_ROUND, change into second Driver object in the list.
        //if the Order object isEmpty, change into second Order object in the list.
        //Google online for Hashmap External Chaining to finish this part.
    }

    public void printDispatchResult(String result) {
        for (Driver driver : driver_list) {
            int r = 0;
            for (Round round : driver.getRounds()) {
                r++;
                String orderId = "";
                for (Map.Entry<String, HashMap<Integer, Integer>> order : round.getOrderDispatch().entrySet()) {
                    String containerInfo = "";
                    if (order.getValue().containsKey(40)) {
                        containerInfo += " 40ft:" + order.getValue().get(40);
                    }
                    if (order.getValue().containsKey(20)) {
                        containerInfo += " 20ft:" + order.getValue().get(20);
                    }
                    if (containerInfo != "") {
                        if (orderId.equals(order.getKey())) {
                            System.out.print(containerInfo);
                        } else {
                            System.out.print("\n" + driver.getName() + ", priority:" + driver.getPriority() + ", round:" + r + ", order id:" + order.getKey() + ", from: " + round.getFrom() + ", to: " + round.getTo() + ", containers:" + containerInfo);
                        }
                    }
                }
            }
        }

        System.out.println("\n" + result);
    }

    public void printUI() {
        System.out.println("Welcome to the Recur Transportataion User Interface: ");
        System.out.println("Please select below options: ");
        System.out.println("1----------------Add orders");
        System.out.println("2----------------Add driver details");
        System.out.println("3----------------Check orders:");
        System.out.println("4----------------Check drivers detail");
        System.out.println("5----------------Dispatch existing orders to drivers");
        System.out.println("6----------------Print out dispatching result");
        System.out.println("q----------------Exit the system");
        System.out.print("Your input: >>>  ");
    }

    public void establishConnection() {
        try {
            conn = DriverManager.getConnection(url, username, password);

            System.out.println(url + " database connected.");
        } catch (SQLException e) {
            Logger.getLogger(SysRun.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void closeDataBaseConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Connection close at " + url);
            } catch (SQLException e) {
                Logger.getLogger(SysRun.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public void formDatabaseTable() {
        //Use DMBC create your own local database. Form a TABLE for ORDER, DRIVER
        //In ORDER table, the variables are ORDER_ID VARCHAR(20), START_DATE VARCHAR(30),
        //CUTOFF_DATE VARCHAR(30), 20_FT_CONTAINER INT, 40_FT_CONTAINER INT.

        //In DRIVER table, the varibles are DRIVER_NAME VARCHAR(30), DRIVER_ID VARCHAR(20),
        //DRIVER_PRIORITY INT.
    }

    public void addOrderToDataBase(String sqlQuery) {
        try {
            statement.executeUpdate(sqlQuery);
        } catch (SQLException ex) {
            Logger.getLogger(SysRun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addDriverToDataBase(String sqlQuery) {
        try {
            statement.executeUpdate(sqlQuery);
        }catch (SQLException ex) {
            Logger.getLogger(SysRun.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
    public void retriveDrvierList() {
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            String sqlQuery = "select driver_id, driver_name, driver_priority from DRIVER_LIST ORDER BY DRIVER_PRIORITY";
            rs = statement.executeQuery(sqlQuery);
            while (rs.next()) {
                String driver_id = rs.getString("DRIVER_ID");
                String driver_name = rs.getString("DRIVER_NAME");
                int driver_priority = rs.getInt("DRIVER_PRIORITY");
                driver_list.add(new Driver(driver_name, driver_id, driver_priority));
            }

        } catch (SQLException ex) {
            Logger.getLogger(SysRun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run(SysRun run) {
        String dispatchResult = null;
        run.establishConnection();
//        driver_list.add(new Driver("Frankie Zheng", "REC001", 1));
//        driver_list.add(new Driver("Antony Li", "REC004", 3));             //this info only for testing purpose.
//        driver_list.add(new Driver("Webber Jiang", "REC002", 2));
//        driver_list.add(new Driver("Charlie Ye", "REC003", 2));
        run.retriveDrvierList();
        boolean terminate = false;
        Scanner scan;
        Scanner scan1 = new Scanner(System.in);
        //Before the System runs, must 
        while (!terminate) {
            run.printUI();
            String user_choice;
            user_choice = scan1.nextLine();
            scan = new Scanner(System.in);
            switch (user_choice) {
                case "1":
                    String id;
                    String date;
                    String[] splitdate;
                    int year;
                    int month;
                    int day;
                    int con20;
                    int con40;
                    String address;
                    System.out.println("Enter id num: ");
                    id = scan.nextLine();
                    System.out.println("Enter date by (year/month/day)");
                    date = scan.nextLine();
                    splitdate = date.split("/", 3);
                    year = Integer.parseInt(splitdate[0]) - 1900;
                    month = Integer.parseInt(splitdate[1]) - 1;
                    day = Integer.parseInt(splitdate[2]);
                    System.out.println("Enter address: ");
                    address = scan.nextLine();
                    System.out.println("Enter 20 ft container num: ");
                    con20 = scan.nextInt();
                    System.out.println("Enter 40 ft container num: ");
                    con40 = scan.nextInt();
                    Order o = new Order(id, new Time(year, month, day, 0, 0, 0), new ContainerList(con20, con40), address, null, null);
                    String sqlQuery = "INSERT INTO ORDER_LIST VALUES ('"
                            + id + "','" + o.cutoff_date.formatStart() + "','" + o.cutoff_date.formatCutOff()
                            + "'," + con20 + "," + con40 + ",'" + o.getAddressId() + "',TRUE)";
                    run.addOrderToDataBase(sqlQuery);
//                    System.out.println(o.cutoff_date.formatStart());
//                    System.out.println(o.cutoff_date.formatCutOff());
                    order_list.add(o);
                    System.out.println();
                    break;
                case "2":
                    String driver_id;
                    String driver_name;
                    int priority;
                    System.out.println("Enter driver name");
                    driver_name = scan.nextLine();
                    System.out.println("Enter driver_id");
                    driver_id = scan.nextLine();
                    System.out.println("Enter driver priority");
                    priority = scan.nextInt();
                    Driver d = new Driver(driver_name, driver_id, priority);
                    driver_list.add(d);
                    String sqlQuery1 = "INSERT INTO DRIVER_LIST VALUES ('"+driver_id+"', '"
                            +driver_name+"',"+priority+")";
                    run.addDriverToDataBase(sqlQuery1);
                    System.out.println();
                    break;
                case "3":
                    run.sortOrderList();
                    run.printOrderList();
                    System.out.println();
                    break;
                case "4":
                    run.sortDriverList();
                    run.printDriverList();
                    System.out.println();
                    break;
                case "5":
                    run.sortDriverList();
                    run.sortOrderList();
                    dispatchResult = run.dispatchingOrders();
                    System.out.println();
                    break;
                //check if the order_list been sorted.
                //check if the driver_list been sorted.
                //run.dispatchingOrders();
                case "6":
                    run.printDispatchResult(dispatchResult);
                    System.out.println();
                    break;
                case "q":
                    terminate = true;
                    run.closeDataBaseConnection();
                    System.out.println("Farewell!");
                    break;
                default:
                    System.out.println("please enter a vaild key to proceed!!!");
                    System.out.println();
                    break;
            }
        }
    }

    public static void main(String[] args) {
//        test();
//        return;

        SysRun run = new SysRun();
        run.run(run);
    }
}
