package hu.opau.voltvault.models;

import java.util.Date;
import java.util.List;

public class Order {
    private String user;
    private int price;
    private List<UserBasketItem> items;
    private int status = 0;
    private BillingAddress billingAddress;
    private Date date = new Date();

    public Order(String user, int price, List<UserBasketItem> items, BillingAddress billingAddress) {
        this.user = user;
        this.price = price;
        this.items = items;
        this.billingAddress = billingAddress;
    }

    public String getUser() {
        return user;
    }

    public int getPrice() {
        return price;
    }

    public List<UserBasketItem> getItems() {
        return items;
    }

    public int getStatus() {
        return status;
    }

    public BillingAddress getBillingAddress() {
        return billingAddress;
    }

    public Date getDate() {
        return date;
    }
}
