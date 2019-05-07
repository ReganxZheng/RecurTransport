/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;

/**
 *
 * @author ZhengJwh
 */
public class Order {

    protected String order_id;
    protected Time cutoff_date;
    protected ContainerList container_list;
    private String addressId;
    private Location from;
    private Location to;

    public Order(String id, Time date, ContainerList size, String addressId, Location from, Location to) {
        this.order_id = id;
        this.cutoff_date = date;
        this.container_list = size;
        this.addressId = addressId;
        this.from = from;
        this.to = to;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public void setId(String id) {
        this.order_id = id;
    }

    public void setTime(Time time) {
        this.cutoff_date = time;
    }

    public void setSize(ContainerList size) {
        this.container_list = size;
    }

    public String getId() {
        return order_id;
    }

    public ContainerList getContainer() {
        return container_list;
    }

    public String getTime() {
        return cutoff_date.formatCutOff();
    }

    public Time getCutoffDate() {
        return this.cutoff_date;
    }

    public String getContainerSize() {
        return container_list.toString();
    }

    public void setAddress(String address) {
        this.addressId = address;
    }

    public String getAddressId() {
        return this.addressId;
    }

    @Override
    public String toString() {
        return "Order id: " + order_id + ". " + cutoff_date.toString()
                + ". " + container_list.toString() + ". Address: " + addressId
                + ". From: " + this.from + ". To: " + this.to;
    }

//    public static void main(String[] args) {
//        ArrayList<Order> order_list = new ArrayList<Order>();
//        while (true) {
//            Scanner scan = new Scanner(System.in);
//            String id;
//            String date;
//            String[] splitdate;
//            int year;
//            int month;
//            int day;
//            int con20;
//            int con40;
//            String address;
//            System.out.println("Enter id num: ");
//            id = scan.nextLine();
//            System.out.println("Enter date by (year/month/day)");
//            date = scan.nextLine();
//            splitdate = date.split("/", 3);
//            year = Integer.parseInt(splitdate[0]) - 1900;
//            month = Integer.parseInt(splitdate[1]) - 1;
//            day = Integer.parseInt(splitdate[2]);
//            System.out.println("Enter 20 ft container num: ");
//            con20 = scan.nextInt();
//            System.out.println("Enter 40 ft container num: ");
//            con40 = scan.nextInt();
//            System.out.println("Enter address: ");
//            address = scan.nextLine();
//            Order o = new Order(id, new Time(year, month, day), new ContainerList(con20, con40), address);
//            order_list.add(o);
//            System.out.println(o.toString());
//            System.out.println(o.getTime());
//            System.out.println(o.getId());
//            System.out.println(o.getContainerSize());
//            for (Order e : order_list) {
//                System.out.println(e.toString());
//            }
//            System.out.println(order_list.size());
//        }
//        Order o = new Order("Akl100", new Time(119, 2, 5, 0, 0, 0), new ContainerList(20, 20), "AKL", null, null);
//        o.setSize(new ContainerList(0, 0));
//        System.out.println(o.getContainer().isEmpty());
//
//    }
}
