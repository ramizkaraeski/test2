package com.delivery.nearby.Getset;


public class cartgetset {

    private String resid;
    private String menuid;
    private String foodid;
    private String foodname;
    private String foodprice; //Its quantity
    private String foodpricekg; //Its kilograms
    private String fooddesc;
    private String restcurrency; //Its a final price
    private String key;
    private String kg;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getResid() {
        return resid;
    }

    public void setResid(String resid) {
        this.resid = resid;
    }

    public String getMenuid() {
        return menuid;
    }

    public void setMenuid(String menuid) {
        this.menuid = menuid;
    }

    public String getFoodid() {
        return foodid;
    }

    public void setFoodid(String foodid) {
        this.foodid = foodid;
    }

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public String getFoodprice() {
        return foodprice;
    }

    public void setFoodprice(String foodprice) {
        this.foodprice = foodprice;
    }

    public String getFooddesc() {
        return fooddesc;
    }

    public void setFooddesc(String fooddesc) {
        this.fooddesc = fooddesc;
    }

    public String getRestcurrency() {
        return restcurrency;
    }

    public void setRestcurrency(String restcurrency) {
        this.restcurrency = restcurrency;
    }

    public String getFoodpricekg() {
        return foodpricekg;
    }

    public void setFoodpricekg(String foodpricekg) {
        this.foodpricekg = foodpricekg;
    }

    public String getKg() {
        return kg;
    }

    public void setKg(String kg) {
        this.kg = kg;
    }
}
