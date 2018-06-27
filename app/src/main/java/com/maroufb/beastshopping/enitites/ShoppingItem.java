package com.maroufb.beastshopping.enitites;

public class ShoppingItem {
    private String id;
    private String itemName;
    private String ownerEmail;
    private String boughtBy;
    private boolean bought;

    public ShoppingItem() {
    }

    public ShoppingItem(String id, String itemName, String ownerEmail, String boughtBy, boolean bought) {
        this.id = id;
        this.itemName = itemName;
        this.ownerEmail = ownerEmail;
        this.boughtBy = boughtBy;
        this.bought = bought;
    }

    public String getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getBoughtBy() {
        return boughtBy;
    }

    public boolean isBought() {
        return bought;
    }
}


