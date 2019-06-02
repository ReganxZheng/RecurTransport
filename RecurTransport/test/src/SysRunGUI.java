
import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ZhengJwh
 */
public class SysRunGUI extends javax.swing.JFrame {

    private String dbURL = "jdbc:derby://localhost:1527/RecurSysDB;"
            + "create=true;user=recur;password=recur2019";
    String driverURL = "org.apache.derby.jdbc.EmbeddedDriver";
    public Connection conn = null;
    public Statement statement;
    private static int MAX_ROUNT = 6;
    private int prev_day_dispatch = 0;
    private static ArrayList<Order> order_list = new ArrayList<Order>();
    private static ArrayList<Driver> driver_list = new ArrayList<Driver>();
    private static HashMap<Driver, Order> dispatch = new HashMap<Driver, Order>();
    private static ArrayList<Location> locations = new ArrayList() {
        {
            add(new Location("131 Wiri Station Rd", "MCP"));
            add(new Location("21 Oak Road", "UCL"));
            add(new Location("339 Neilson St", "Metrobox"));
            add(new Location("140 Hugo Johnston Drive", "Hugo Jonhston"));
            add(new Location("131 Wiri Station Rd", "WIP"));
            add(new Location("339 Neilson St", "Kiwi Rail"));
            add(new Location("20 Savill Dr", "SCS"));
            add(new Location("Sunderland St", "FED"));
            add(new Location("Solent St", "FCT"));

        }
    };

    public void sortDriverList() {
        Driver temp;
        for (int i = 0; i < driver_list.size(); i++) {
            for (int j = 1; j < driver_list.size(); j++) {
                if ((driver_list.get(j).getPriority()) < (driver_list.get(j - 1).getPriority())) {
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

    public Driver getAvailableDriver(int daysAhead) {
        for (Driver driver : driver_list) {
            if (driver.getRoundNumber() < 6 * daysAhead) {
                return driver;
            }
        }
        return null;
    }

    public int getDispatchDays() {
        String option = this.dispatchDays.getSelectedItem().toString();
        if (option.equals("3 Days")) {
            return 3;
        } else if (option.equals("1 Week")) {
            return 7;
        } else {
            return 1;
        }
    }

    // get drivers and orders from db so we are up to date.
    public void syncDriversOrdersFromDB() {
        this.driver_list.clear();
        this.order_list.clear();

        this.retriveDrvierList();
        this.retriveOrderList();
    }

    public String dispatchingOrders() {
        this.syncDriversOrdersFromDB();

        int dispatchDaysAhead = this.getDispatchDays();
        prev_day_dispatch = dispatchDaysAhead;
        while (true) {
            Driver driver = this.getAvailableDriver(dispatchDaysAhead);
            if (driver == null) {
                return "No driver available.";
            }
            String defaultLocation = driver.getLocation();
            int i = 1;
            // dispatch 1 day or 3 days or 1 week for the driver before getting to next
            while (driver.getRoundNumber() < 6 * dispatchDaysAhead) {
                // for new day, reset location to default
                if (driver.getRoundNumber() % 6 == 0) {
                    driver.setLocation(defaultLocation);
                }
                Round previousRound = null;
                Round round = driver.getRound();
                int capacityLeft = round.getCapacityLeft();
                // find a container for the round
                int orderLeft = 0;
                // check same location first, then same city, then everywhere
                // s is used to substring
                for (int s = 3; s >= 0; s -= 3) {
                    for (Order order : order_list) {
                        if (order.container_list.con20_num > 0 || order.container_list.con40_num > 0) {
                            orderLeft++;
                        }

                        boolean sameFromAndTo = order.getFrom().getLocationID().equals(round.getFrom()) && order.getTo().getLocationID().equals(round.getTo());
                        boolean hasProperContainer = capacityLeft >= 40 && order.container_list.con40_num > 0
                                && (round.isEmpty() || sameFromAndTo)
                                || capacityLeft >= 20 && order.container_list.con20_num > 0
                                && (round.isEmpty() || sameFromAndTo);
                        while (hasProperContainer && driver.getLocation().substring(0, s).equalsIgnoreCase(order.getFrom().getLocationID().substring(0, s))) {
                            if (capacityLeft >= 40 && order.container_list.con40_num > 0
                                    && (round.isEmpty() || sameFromAndTo)) {
                                round.addContainer(order.order_id, order.getFrom().getLocationID(), order.getTo().getLocationID(), 40);
                                order.container_list.con40_num--;
                                capacityLeft = round.getCapacityLeft();
                                if (previousRound != null && previousRound != round) {
                                    driver.setLocation(order.getTo().getLocationID());
                                }
                                previousRound = round;
                            } else if (capacityLeft >= 20 && order.container_list.con20_num > 0
                                    && (round.isEmpty() || sameFromAndTo)) {
                                round.addContainer(order.order_id, order.getFrom().getLocationID(), order.getTo().getLocationID(), 20);
                                order.container_list.con20_num--;
                                capacityLeft = round.getCapacityLeft();
                                if (previousRound != null && previousRound != round) {
                                    driver.setLocation(order.getTo().getLocationID());
                                }
                                previousRound = round;
                            }
                            sameFromAndTo = order.getFrom().getLocationID().equals(round.getFrom()) && order.getTo().getLocationID().equals(round.getTo());
                            hasProperContainer = capacityLeft >= 40 && order.container_list.con40_num > 0
                                    && (round.isEmpty() || sameFromAndTo)
                                    || capacityLeft >= 20 && order.container_list.con20_num > 0
                                    && (round.isEmpty() || sameFromAndTo);
                        }
                    }
                }
                if (orderLeft == 0) {
                    return "No order left.";
                }

                driver.increaseRound();
            }

        }
    }

    public String reDispatchingOrders() {
        this.syncDriversOrdersFromDB();

        int dispatchDaysAhead = prev_day_dispatch;
        while (true) {
            Driver driver = this.getAvailableDriver(dispatchDaysAhead);
            if (driver == null) {
                return "No driver available.";
            }
            String defaultLocation = driver.getLocation();
            int i = 1;
            // dispatch 1 day or 3 days or 1 week for the driver before getting to next
            while (driver.getRoundNumber() < 6 * dispatchDaysAhead) {
                // for new day, reset location to default
                if (driver.getRoundNumber() % 6 == 0) {
                    driver.setLocation(defaultLocation);
                }
                Round previousRound = null;
                Round round = driver.getRound();
                int capacityLeft = round.getCapacityLeft();
                // find a container for the round
                int orderLeft = 0;
                // check same location first, then same city, then everywhere
                // s is used to substring
                for (int s = 3; s >= 0; s -= 3) {
                    for (Order order : order_list) {
                        if (order.container_list.con20_num > 0 || order.container_list.con40_num > 0) {
                            orderLeft++;
                        }

                        boolean sameFromAndTo = order.getFrom().getLocationID().equals(round.getFrom()) && order.getTo().getLocationID().equals(round.getTo());
                        boolean hasProperContainer = capacityLeft >= 40 && order.container_list.con40_num > 0
                                && (round.isEmpty() || sameFromAndTo)
                                || capacityLeft >= 20 && order.container_list.con20_num > 0
                                && (round.isEmpty() || sameFromAndTo);
                        while (hasProperContainer && driver.getLocation().substring(0, s).equalsIgnoreCase(order.getFrom().getLocationID().substring(0, s))) {
                            if (capacityLeft >= 40 && order.container_list.con40_num > 0
                                    && (round.isEmpty() || sameFromAndTo)) {
                                round.addContainer(order.order_id, order.getFrom().getLocationID(), order.getTo().getLocationID(), 40);
                                order.container_list.con40_num--;
                                capacityLeft = round.getCapacityLeft();
                                if (previousRound != null && previousRound != round) {
                                    driver.setLocation(order.getTo().getLocationID());
                                }
                                previousRound = round;
                            } else if (capacityLeft >= 20 && order.container_list.con20_num > 0
                                    && (round.isEmpty() || sameFromAndTo)) {
                                round.addContainer(order.order_id, order.getFrom().getLocationID(), order.getTo().getLocationID(), 20);
                                order.container_list.con20_num--;
                                capacityLeft = round.getCapacityLeft();
                                if (previousRound != null && previousRound != round) {
                                    driver.setLocation(order.getTo().getLocationID());
                                }
                                previousRound = round;
                            }
                            sameFromAndTo = order.getFrom().getLocationID().equals(round.getFrom()) && order.getTo().getLocationID().equals(round.getTo());
                            hasProperContainer = capacityLeft >= 40 && order.container_list.con40_num > 0
                                    && (round.isEmpty() || sameFromAndTo)
                                    || capacityLeft >= 20 && order.container_list.con20_num > 0
                                    && (round.isEmpty() || sameFromAndTo);
                        }
                    }
                }
                if (orderLeft == 0) {
                    return "No order left.";
                }

                driver.increaseRound();
            }

        }
    }

    public ArrayList<String> getDispatchResultString(String result) {
        ArrayList<String> allResult = new ArrayList<String>();
        String all = "";
        for (Driver driver : driver_list) {
            int r = 0;
            for (Round round : driver.getRounds()) {
                String orderId = "";
                for (Map.Entry<String, HashMap<Integer, Integer>> order : round.getOrderDispatch().entrySet()) {
                    String containerInfo = "";
                    if (order.getValue().containsKey(40)) {
                        containerInfo += "40ft:" + order.getValue().get(40);
                    }
                    if (order.getValue().containsKey(20)) {
                        containerInfo += ":20ft:" + order.getValue().get(20);
                    }
                    if (containerInfo != "") {
                        if (orderId.equals(order.getKey())) {
                            all += containerInfo;
                        } else {
                            all = driver.getName() + ",priority:" + driver.getPriority() + ",day:"
                                    + (int) (r / 6 + 1) + ",round:" + (r % 6 + 1) + ",order id:" + order.getKey() + ",from: "
                                    + round.getFrom() + ",to:" + round.getTo() + ",containers:" + containerInfo;
                            String[] containerSplit = containerInfo.split(":");
                            int con20 = 0, con40 = 0;
                            if (containerSplit.length == 4) {
                                con20 = Integer.parseInt(containerSplit[3]);
                                con40 = Integer.parseInt(containerSplit[1]);
                            }
                            if (containerSplit.length == 3) {
                                con20 = Integer.parseInt(containerSplit[2]);
                            }
                            String sqlQuery = "INSERT INTO DISPATCH_RESULT VALUES ('" + driver.getName()
                                    + "'," + driver.getPriority() + "," + (int) (r / 6 + 1) + "," + (r % 6 + 1) + ",'" + order.getKey() + "','"
                                    + round.getFrom() + "','" + round.getTo() + "'," + con40 + "," + con20 + ")";
                            addDispatchToDatabase(sqlQuery);
                        }
                    }
                    allResult.add(all);

                }
                r++;
            }
        }

        all += "\n" + result;
        return allResult;
    }

    public void establishConnection() {
        try {
            Class.forName(driverURL);
            conn = DriverManager.getConnection(dbURL);

            System.out.println("database connected.");
        } catch (SQLException e) {
            Logger.getLogger(SysRun.class.getName()).log(Level.SEVERE, null, e);
        } catch (ClassNotFoundException ex) {

        }
    }

    public void closeDataBaseConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Connection closed");
            } catch (SQLException e) {
                Logger.getLogger(SysRun.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public void addOrderToDataBase(String sqlQuery) {
        try {
            statement.executeUpdate(sqlQuery);
        } catch (SQLException ex) {
            this.labelAlert.setText("Failed to write order to database: " + ex.getMessage());
            Logger.getLogger(SysRun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addDriverToDataBase(String sqlQuery) {
        try {
            statement.executeUpdate(sqlQuery);
        } catch (SQLException ex) {
            this.labelAlert.setText("Failed to write driver to database: " + ex.getMessage());
            Logger.getLogger(SysRun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addDispatchToDatabase(String sqlQuery) {
        try {
            statement.executeUpdate(sqlQuery);
        } catch (SQLException ex) {
            this.labelAlert.setText("Failed to write driver to database: " + ex.getMessage());
            Logger.getLogger(SysRun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void clearDriversInDB() {
        try {
            statement.execute("DELETE FROM DRIVER_LIST");
        } catch (SQLException ex) {
            this.labelAlert.setText("Failed to write driver to database: " + ex.getMessage());
            Logger.getLogger(SysRun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void clearOrdersInDB() {
        try {
            statement.execute("DELETE FROM ORDER_LIST");
        } catch (SQLException ex) {
            this.labelAlert.setText("Failed to write driver to database: " + ex.getMessage());
            Logger.getLogger(SysRun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createTables() {
        try {
            statement = conn.createStatement();
            String sqlQuery = "create table DRIVER_LIST (driver_id VARCHAR(100), driver_name VARCHAR(100), driver_location VARCHAR(100), driver_priority INT, start_time VARCHAR(100), stop_time VARCHAR(100))";
            statement.execute(sqlQuery);
        } catch (SQLException ex) {
        }

        try {
            statement = conn.createStatement();
            String sqlQuery = "create table ORDER_LIST (order_id VARCHAR(100) PRIMARY KEY, date_year INT, date_month int, date_day int, date_hour int, date_min int, con20 int, con40 int, address_id VARCHAR(100), from_name VARCHAR(100), from_id VARCHAR(100), to_name VARCHAR(100), to_id VARCHAR(100))";
            statement.execute(sqlQuery);
        } catch (SQLException ex) {
        }

        try {
            statement = conn.createStatement();
            String sqlQuery = "CREATE TABLE DISPATCH_RESULT (DRIVER_NAME VARCHAR(50), DRIVER_PRIORITY INT, DAY INT, ROUND INT, ORDER_ID VARCHAR(100), FROM_LOCATION VARCHAR(100), TO_LOCATION VARCHAR(100), CON40 INT, CON20 INT)";
            statement.execute(sqlQuery);
        } catch (SQLException ex) {

        }
    }

    public void retriveDrvierList() {
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            String sqlQuery = "select * from DRIVER_LIST ORDER BY DRIVER_PRIORITY";
            rs = statement.executeQuery(sqlQuery);
            int i = 0;
            while (rs.next()) {
                String driver_id = rs.getString("DRIVER_ID");
                String driver_name = rs.getString("DRIVER_NAME");
                String driver_location = rs.getString("DRIVER_LOCATION");
                String start_time = rs.getString("START_TIME");
                String stop_time = rs.getString("STOP_TIME");
                int driver_priority = rs.getInt("DRIVER_PRIORITY");
                driver_list.add(new Driver(driver_name, driver_id, driver_location, driver_priority, start_time, stop_time));
                i++;
            }
            this.labelAlert.setText("Successfully loaded " + i + " drivers from database");
            this.updateDisplayDriverList();
        } catch (SQLException ex) {
            this.labelAlert.setText("Failed to retrieve driver list from database: " + ex.getMessage());
            Logger.getLogger(SysRun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void retriveOrderList() {
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            String sqlQuery = "select * from ORDER_LIST";
            rs = statement.executeQuery(sqlQuery);
            int i = 0;
            while (rs.next()) {
                String order_id = rs.getString("order_id");
                String address_id = rs.getString("address_id");
                String from_name = rs.getString("from_name");
                String from_id = rs.getString("from_id");
                String to_name = rs.getString("to_name");
                String to_id = rs.getString("to_id");
                int year = rs.getInt("date_year");
                int month = rs.getInt("date_month");
                int day = rs.getInt("date_day");
                int hour = rs.getInt("date_hour");
                int min = rs.getInt("date_min");
                int con20 = rs.getInt("con20");
                int con40 = rs.getInt("con40");
                order_list.add(new Order(order_id, new Time(year, month, day, hour, min, 0), new ContainerList(con20, con40), address_id, new Location(from_name, from_id), new Location(to_name, to_id)));
                i++;
            }
            this.labelAlert.setText(this.labelAlert.getText() + ". Successfully loaded " + i + " orders from database");
            this.updateDisplayOrderList();
        } catch (SQLException ex) {
            this.labelAlert.setText("Failed to retrieve order list from database: " + ex.getMessage());
            Logger.getLogger(SysRun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        this.fromLocation.removeAllItems();
        for (Location l : locations) {
            this.fromLocation.addItem(l);
        }
        this.toLocation.removeAllItems();
        for (Location l : locations) {
            this.toLocation.addItem(l);
        }
        this.driverLocation.removeAllItems();
        for (Location l : locations) {
            this.driverLocation.addItem(l);
        }
        String dispatchResult = null;
        this.establishConnection();

        try {
            ImageIcon icon1 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("RecurIcon.png")));
            Image img1 = icon1.getImage();
            Image img2 = img1.getScaledInstance(jLabel24.getWidth(), jLabel24.getHeight(), Image.SCALE_SMOOTH);
            ImageIcon icon2 = new ImageIcon(img2);
            jLabel24.setIcon(icon2);
        } catch (NullPointerException e) {

        }
        this.createTables();
        this.retriveDrvierList();
        this.retriveOrderList();
        String user_choice;

    }

    /**
     * Creates new form SysRunGUI
     */
    public SysRunGUI() {
        initComponents();
        this.run();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        textDate = new javax.swing.JTextField();
        textOrder = new javax.swing.JTextField();
        textAddress = new javax.swing.JTextField();
        text40FtNum = new javax.swing.JTextField();
        text20FtNum = new javax.swing.JTextField();
        btnAddOrder = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        orderTime = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        fromLocation = new javax.swing.JComboBox<>();
        toLocation = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        textDriverName = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        textDriverId = new javax.swing.JTextField();
        textDriverPriority = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        btnAddDriver = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        driverStartTime = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        driverFinishTime = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        driverLocation = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        btnResetOrders = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        OrderTable = new javax.swing.JTable();
        btnDelOrder = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        btnResetDrivers = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        DriverTable = new javax.swing.JTable();
        btnDelDriver = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        btnDispatch = new javax.swing.JButton();
        dispatchDays = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        dispatchTable = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        labelAlert = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Recur Transport Management System");
        setResizable(false);

        jPanel1.setToolTipText("");

        jLabel1.setText("Order ID:");

        jLabel2.setText("CutOff Date:");

        jLabel4.setText("Address:");

        jLabel5.setText("20ft Num:");

        jLabel6.setText("40ft Num:");

        textDate.setToolTipText("year/month/day");

        textOrder.setToolTipText("");

        btnAddOrder.setText("Add Order");
        btnAddOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddOrderActionPerformed(evt);
            }
        });

        jLabel7.setText("Add Order");

        jLabel3.setText("E.g 2019/03/10");

        jLabel12.setText("E.g 3");

        jLabel13.setText("E.g 3");

        jLabel18.setText("CutOff Time:");

        orderTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" }));

        jLabel21.setText("From Location:");

        jLabel22.setText("To Location:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(143, 143, 143)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel18)
                    .addComponent(jLabel4)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fromLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel7))
                    .addComponent(textOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(orderTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(textDate, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addComponent(textAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(text20FtNum, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(text40FtNum, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel12)))
                    .addComponent(btnAddOrder))
                .addContainerGap(255, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel7)
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(textOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(textDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(orderTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(textAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(fromLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(toLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(text20FtNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(text40FtNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addComponent(btnAddOrder)
                .addContainerGap(77, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Add Order", jPanel2);

        jLabel8.setText("Add Driver");

        textDriverName.setToolTipText("");

        jLabel9.setText("Driver Name:");

        jLabel10.setText("Driver ID:");

        textDriverId.setToolTipText("year/month/day");

        jLabel11.setText("Driver Priority:");

        btnAddDriver.setText("Add Driver");
        btnAddDriver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDriverActionPerformed(evt);
            }
        });

        jLabel14.setText("E.g 2");

        jLabel19.setText("Start Time:");

        driverStartTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" }));

        jLabel20.setText("Finish Time:");

        driverFinishTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" }));

        jLabel23.setText("Driver Location:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(136, 136, 136)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel20)
                    .addComponent(jLabel19)
                    .addComponent(jLabel11)
                    .addComponent(jLabel9)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel23)
                        .addComponent(jLabel10)))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(btnAddDriver)
                    .addComponent(textDriverId, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(textDriverPriority, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14))
                    .addComponent(driverStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(driverFinishTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textDriverName, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(driverLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(356, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel8)
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textDriverName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(textDriverId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(driverLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(textDriverPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(driverStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(driverFinishTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addComponent(btnAddDriver)
                .addContainerGap(116, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Add Driver", jPanel3);

        btnResetOrders.setText("Reset Orders");
        btnResetOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetOrdersActionPerformed(evt);
            }
        });

        OrderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order ID", "Start Date", "CutOff Date", "20ft.", "40ft.", "Address", "FROM", "TO"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        OrderTable.setColumnSelectionAllowed(true);
        jScrollPane3.setViewportView(OrderTable);
        OrderTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if (OrderTable.getColumnModel().getColumnCount() > 0) {
            OrderTable.getColumnModel().getColumn(0).setPreferredWidth(30);
            OrderTable.getColumnModel().getColumn(3).setPreferredWidth(10);
            OrderTable.getColumnModel().getColumn(4).setPreferredWidth(10);
            OrderTable.getColumnModel().getColumn(6).setPreferredWidth(50);
            OrderTable.getColumnModel().getColumn(7).setPreferredWidth(50);
        }

        btnDelOrder.setText("Delete");
        btnDelOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelOrderActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 783, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnResetOrders)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnResetOrders)
                    .addComponent(btnDelOrder))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Show Orders", jPanel4);

        btnResetDrivers.setText("Reset Drivers");
        btnResetDrivers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetDriversActionPerformed(evt);
            }
        });

        DriverTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Driver ID", "Priority", "Name", "Location"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        DriverTable.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(DriverTable);
        DriverTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (DriverTable.getColumnModel().getColumnCount() > 0) {
            DriverTable.getColumnModel().getColumn(0).setMinWidth(80);
            DriverTable.getColumnModel().getColumn(0).setMaxWidth(120);
            DriverTable.getColumnModel().getColumn(1).setMinWidth(80);
            DriverTable.getColumnModel().getColumn(1).setMaxWidth(80);
        }

        btnDelDriver.setText("Delete");
        btnDelDriver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelDriverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 783, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnResetDrivers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelDriver, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnResetDrivers)
                    .addComponent(btnDelDriver))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Show Drivers", jPanel5);

        btnDispatch.setText("Run Dispatch");
        btnDispatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDispatchActionPerformed(evt);
            }
        });

        dispatchDays.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1 Day", "3 Days", "1 Week" }));

        jLabel16.setText("Dispatch Days:");

        dispatchTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Priority", "Day", "Round", "Order ID", "FROM", "TO", "40Ft", "20Ft"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(dispatchTable);
        if (dispatchTable.getColumnModel().getColumnCount() > 0) {
            dispatchTable.getColumnModel().getColumn(0).setMinWidth(150);
            dispatchTable.getColumnModel().getColumn(0).setMaxWidth(200);
            dispatchTable.getColumnModel().getColumn(1).setResizable(false);
            dispatchTable.getColumnModel().getColumn(2).setResizable(false);
            dispatchTable.getColumnModel().getColumn(3).setResizable(false);
            dispatchTable.getColumnModel().getColumn(4).setResizable(false);
            dispatchTable.getColumnModel().getColumn(5).setResizable(false);
            dispatchTable.getColumnModel().getColumn(6).setResizable(false);
            dispatchTable.getColumnModel().getColumn(7).setResizable(false);
            dispatchTable.getColumnModel().getColumn(8).setResizable(false);
        }

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(194, 194, 194)
                .addComponent(jLabel16)
                .addGap(18, 18, 18)
                .addComponent(dispatchDays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDispatch)
                .addContainerGap(330, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDispatch)
                    .addComponent(dispatchDays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Dispatch", jPanel6);

        jLabel15.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel15.setText("Recur Transport Auto Dispatch System");

        labelAlert.setFont(new java.awt.Font("Lucida Grande", 2, 13)); // NOI18N
        labelAlert.setText("Add drivers and orders to start!");

        jLabel17.setText("Info:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addGap(18, 18, 18)
                        .addComponent(labelAlert, javax.swing.GroupLayout.PREFERRED_SIZE, 732, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(119, 119, 119)
                        .addComponent(jLabel15)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelAlert)
                    .addComponent(jLabel17))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateDisplayDriverList() {
        DefaultTableModel model = (DefaultTableModel) DriverTable.getModel();
        model.setRowCount(0);
        Object rowData[] = new Object[4];
        for (int i = 0; i < driver_list.size(); i++) {
            rowData[0] = driver_list.get(i).getId().toUpperCase();
            rowData[1] = driver_list.get(i).getPriority();
            rowData[2] = driver_list.get(i).getName();
            rowData[3] = driver_list.get(i).getLocation();
            model.addRow(rowData);
        }
    }

    private void updateDisplayOrderList() {
        DefaultTableModel model = (DefaultTableModel) OrderTable.getModel();
        model.setRowCount(0);
        Object rowData[] = new Object[8];
        for (int i = 0; i < order_list.size(); i++) {
            rowData[0] = order_list.get(i).getId().toUpperCase();
            rowData[1] = order_list.get(i).getCutoffDate().formatStart();
            rowData[2] = order_list.get(i).getTime();
            rowData[3] = order_list.get(i).getContainer().getContainer20();
            rowData[4] = order_list.get(i).getContainer().getContainer40();
            rowData[5] = order_list.get(i).getAddressId();
            rowData[6] = order_list.get(i).getFrom().getLocationID();
            rowData[7] = order_list.get(i).getTo().getLocationID();
            model.addRow(rowData);
        }
    }

    private void updateDispatchTable() {
        if (prev_day_dispatch != 0) {
            ArrayList<String> result = this.getDispatchResultString(this.reDispatchingOrders());
            DefaultTableModel model = (DefaultTableModel) dispatchTable.getModel();
            model.setRowCount(0);
            Object rowData[] = new Object[9];
            for (String s : result) {
                String[] split1 = s.split(",");
                rowData[0] = split1[0];
                String[] prioritySplit = split1[1].split(":");
                rowData[1] = prioritySplit[1];
                String[] daySplit = split1[2].split(":");
                rowData[2] = daySplit[1];
                String[] roundSplit = split1[3].split(":");
                rowData[3] = roundSplit[1];
                String[] orderIdSplit = split1[4].split(":");
                rowData[4] = orderIdSplit[1];
                String[] fromSplit = split1[5].split(":");
                rowData[5] = fromSplit[1];
                String[] toSplit = split1[6].split(":");
                rowData[6] = toSplit[1];
                String[] containerSplit = split1[7].split(":");
                if (containerSplit.length == 4) {
                    rowData[7] = "0";
                    rowData[8] = containerSplit[3];
                } else if (containerSplit.length == 5) {
                    rowData[7] = containerSplit[2];
                    rowData[8] = containerSplit[4];
                }
                model.addRow(rowData);
            }
        }
    }

    private void btnAddOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddOrderActionPerformed
        String[] splitdate = this.textDate.getText().split("/", 3);
        String[] splitTime = this.orderTime.getSelectedItem().toString().split(":", 2);
        if (splitdate.length != 3) {
            this.labelAlert.setText("Invalid order date");
            return;
        }
        int year, month, day, hour, min, con20, con40;
        try {
            year = Integer.parseInt(splitdate[0]);
            month = Integer.parseInt(splitdate[1]);
            day = Integer.parseInt(splitdate[2]);
            hour = Integer.parseInt(splitTime[0]);
            min = Integer.parseInt(splitTime[1]);
        } catch (Exception e) {
            this.labelAlert.setText("Invalid order date");
            return;
        }
        if (this.textOrder.getText().isEmpty()) {
            this.labelAlert.setText("Invalid order ID");
            return;
        }

        try {
            con20 = Integer.parseInt(this.text20FtNum.getText());

        } catch (Exception e) {
            this.labelAlert.setText("Invalid 20 feet container number");
            return;
        }

        try {
            con40 = Integer.parseInt(this.text40FtNum.getText());

        } catch (Exception e) {
            this.labelAlert.setText("Invalid 40 feet container number");
            return;
        }

        if (this.textAddress.getText().isEmpty()) {
            this.labelAlert.setText("Invalid order address");
            return;
        }
        Order o = new Order(textOrder.getText(), new Time(year, month, day, hour, min, 0), new ContainerList(con20, con40), textAddress.getText(), (Location) this.fromLocation.getSelectedItem(), (Location) this.toLocation.getSelectedItem());
        String sqlQuery = "INSERT INTO ORDER_LIST VALUES ('"
                + textOrder.getText().toUpperCase()
                + "'," + o.getCutoffDate().getYear()
                + "," + o.getCutoffDate().getMonth()
                + "," + o.getCutoffDate().getDay()
                + "," + o.getCutoffDate().getHour()
                + "," + o.getCutoffDate().getMin()
                + "," + con20 + "," + con40 + ",'"
                + o.getAddressId()
                + "','" + o.getFrom().getLocationName()
                + "','" + o.getFrom().getLocationID()
                + "','" + o.getTo().getLocationName()
                + "','" + o.getTo().getLocationID() + "')";
        this.addOrderToDataBase(sqlQuery);
        order_list.add(o);
        this.labelAlert.setText("New Order added successfully!");
        this.sortOrderList();
        this.updateDisplayOrderList();
        this.resetOrderTextFields();
    }//GEN-LAST:event_btnAddOrderActionPerformed

    private void resetOrderTextFields() {
        this.textOrder.setText("");
        this.textDate.setText("");
        this.orderTime.setSelectedIndex(0);
        this.textAddress.setText("");
        this.fromLocation.setSelectedIndex(0);
        this.toLocation.setSelectedIndex(0);
        this.text20FtNum.setText("");
        this.text40FtNum.setText("");
    }
    private void btnAddDriverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDriverActionPerformed
        int priority;
        try {
            priority = Integer.parseInt(this.textDriverPriority.getText());

        } catch (Exception e) {
            this.labelAlert.setText("Invalid driver priority number");
            return;
        }

        if (this.textDriverId.getText().isEmpty()) {
            this.labelAlert.setText("Invalid driver ID");
            return;
        }
        if (this.textDriverName.getText().isEmpty()) {
            this.labelAlert.setText("Invalid driver name");
            return;
        }
        String location = ((Location) this.driverLocation.getSelectedItem()).getLocationID();
        Driver d = new Driver(textDriverName.getText(), textDriverId.getText().toUpperCase(), location, priority, this.driverStartTime.getSelectedItem().toString(), this.driverFinishTime.getSelectedItem().toString());
        driver_list.add(d);
        String sqlQuery1 = "INSERT INTO DRIVER_LIST VALUES ('" + textDriverId.getText().toUpperCase() + "', '"
                + textDriverName.getText() + "', '"
                + location + "'," + priority + ", '" + this.driverStartTime.getSelectedItem().toString() + "', '" + this.driverFinishTime.getSelectedItem().toString() + "')";
        this.addDriverToDataBase(sqlQuery1);

        this.labelAlert.setText("New Driver added successfully!");
        this.sortDriverList();
        this.updateDisplayDriverList();
        this.resetDriverTextFields();
    }//GEN-LAST:event_btnAddDriverActionPerformed

    private void resetDriverTextFields() {
        this.textDriverName.setText("");
        this.textDriverId.setText("");
        this.textDriverPriority.setText("");
        this.driverLocation.setSelectedIndex(0);
        this.driverStartTime.setSelectedIndex(0);
        this.driverFinishTime.setSelectedIndex(0);
    }
    private void btnDispatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDispatchActionPerformed
        ArrayList<String> result = this.getDispatchResultString(this.dispatchingOrders());
        DefaultTableModel model = (DefaultTableModel) dispatchTable.getModel();
        model.setRowCount(0);
        Object rowData[] = new Object[9];
        for (String s : result) {
            String[] split1 = s.split(",");
            rowData[0] = split1[0];
            String[] prioritySplit = split1[1].split(":");
            rowData[1] = prioritySplit[1];
            String[] daySplit = split1[2].split(":");
            rowData[2] = daySplit[1];
            String[] roundSplit = split1[3].split(":");
            rowData[3] = roundSplit[1];
            String[] orderIdSplit = split1[4].split(":");
            rowData[4] = orderIdSplit[1];
            String[] fromSplit = split1[5].split(":");
            rowData[5] = fromSplit[1];
            String[] toSplit = split1[6].split(":");
            rowData[6] = toSplit[1];
            String[] containerSplit = split1[7].split(":");
            if (containerSplit.length == 4) {
                rowData[7] = "0";
                rowData[8] = containerSplit[3];
            } else if (containerSplit.length == 5) {
                rowData[7] = containerSplit[2];
                rowData[8] = containerSplit[4];
            }
            model.addRow(rowData);
        }
    }//GEN-LAST:event_btnDispatchActionPerformed

    private void btnResetOrdersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetOrdersActionPerformed
        order_list.clear();
        for (Driver d : driver_list) {
            d.resetRounds();
        }
        this.clearOrdersInDB();
//        this.textDispatchResult.setText("");
        this.updateDisplayOrderList();
    }//GEN-LAST:event_btnResetOrdersActionPerformed

    private void btnResetDriversActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetDriversActionPerformed
        driver_list.clear();
        this.clearDriversInDB();
        this.updateDisplayDriverList();
    }//GEN-LAST:event_btnResetDriversActionPerformed

    private void btnDelOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelOrderActionPerformed
        DefaultTableModel model = (DefaultTableModel) OrderTable.getModel();
        int[] rows = OrderTable.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            Object order_id = model.getValueAt(rows[i], 0);
            this.delOrderFromDB(order_id.toString());
            for (int j = 0; j < order_list.size(); j++) {
                if (order_list.get(j).getId().equals(order_id.toString())) {
                    order_list.remove(j);
                }
            }
            model.removeRow(rows[i] - i);
        }
        this.labelAlert.setText("Order successfully deleted from database");
        this.updateDisplayOrderList();
        this.updateDispatchTable();
    }//GEN-LAST:event_btnDelOrderActionPerformed

    private void btnDelDriverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelDriverActionPerformed
        DefaultTableModel model = (DefaultTableModel) DriverTable.getModel();
        int[] rows = DriverTable.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            Object driver_id = model.getValueAt(rows[i], 0);
            this.delDriverFromDB(driver_id.toString());
            for (int j = 0; j < driver_list.size(); j++) {
                if (driver_list.get(j).getId().equals(driver_id.toString())) {
                    driver_list.remove(j);
                }
            }
            model.removeRow(rows[i] - i);
        }
        this.labelAlert.setText("Driver successfully deleted from database");
        this.updateDisplayDriverList();
        this.updateDispatchTable();
    }//GEN-LAST:event_btnDelDriverActionPerformed

    private void delDriverFromDB(String id) {
        try {
            String sql = "DELETE FROM DRIVER_LIST WHERE DRIVER_ID LIKE'" + id + "'";
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void delOrderFromDB(String id) {
        try {
            String sql = "DELETE FROM ORDER_LIST WHERE ORDER_ID LIKE'" + id + "'";
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SysRunGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SysRunGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SysRunGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SysRunGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SysRunGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable DriverTable;
    private javax.swing.JTable OrderTable;
    private javax.swing.JButton btnAddDriver;
    private javax.swing.JButton btnAddOrder;
    private javax.swing.JButton btnDelDriver;
    private javax.swing.JButton btnDelOrder;
    private javax.swing.JButton btnDispatch;
    private javax.swing.JButton btnResetDrivers;
    private javax.swing.JButton btnResetOrders;
    private javax.swing.JComboBox<String> dispatchDays;
    private javax.swing.JTable dispatchTable;
    private javax.swing.JComboBox<String> driverFinishTime;
    private javax.swing.JComboBox<Location> driverLocation;
    private javax.swing.JComboBox<String> driverStartTime;
    private javax.swing.JComboBox<Location> fromLocation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labelAlert;
    private javax.swing.JComboBox<String> orderTime;
    private javax.swing.JTextField text20FtNum;
    private javax.swing.JTextField text40FtNum;
    private javax.swing.JTextField textAddress;
    private javax.swing.JTextField textDate;
    private javax.swing.JTextField textDriverId;
    private javax.swing.JTextField textDriverName;
    private javax.swing.JTextField textDriverPriority;
    private javax.swing.JTextField textOrder;
    private javax.swing.JComboBox<Location> toLocation;
    // End of variables declaration//GEN-END:variables

}
