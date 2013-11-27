package com.techery.droid.billings.models;

public class BillableItem {
    private boolean isAvailable;
    private final String sku;
    private Purchase purchase;

    public BillableItem(String sku) {
        this.sku = sku;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getSku() {
        return sku;
    }

    public void update(Inventory inventory) {
        this.purchase = inventory.getPurchase(getSku());
        this.isAvailable = validate(this.purchase);
    }

    protected boolean validate(Purchase p) {
        return p != null;
    }

    public void updateFromPurchase(Purchase p) {
        this.purchase = p;
        this.isAvailable = validate(p);
    }

    public Purchase getPurchase() {
        return purchase;
    }
}
