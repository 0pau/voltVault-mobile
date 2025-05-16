package hu.opau.voltvault.models;

import java.util.Date;
import java.util.Map;

public class Product {

    private String id;
    private String description;
    private String image; //Base64
    private String manufacturer;
    private String name;
    private String category;
    private Map<String, Map<String, String>> properties;
    private int price;
    private int discount = 0;

    private Date addDate;

    public int getPrice() {
        return price;
    }

    public Product() {}

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getName() {
        return name;
    }

    public Map<String, Map<String, String>> getProperties() {
        return properties;
    }

    public String getId() {
        return id;
    }

    public int getDiscount() {
        return discount;
    }

    public int getDiscountedPrice() {
        return (int)Math.floor(getPrice()-getPrice()*((double)getDiscount()/100));
    }

    public void setName(String name) {
        this.name = name;
    }
}
