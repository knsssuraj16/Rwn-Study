package com.rwn.rwnstudy.utilities;

public class ProductGetterSetter {

    private  String MRP,discount,other,price,productDesc,productImage,productLink,productName;

    public ProductGetterSetter(String MRP, String discount, String other, String price, String productDesc, String productImage, String productLink, String productName) {
        this.MRP = MRP;
        this.discount = discount;
        this.other = other;
        this.price = price;
        this.productDesc = productDesc;
        this.productImage = productImage;
        this.productLink = productLink;
        this.productName = productName;
    }

    public ProductGetterSetter() {
    }

    public String getMRP() {
        return MRP;
    }

    public ProductGetterSetter setMRP(String MRP) {
        this.MRP = MRP;
        return this;
    }

    public String getDiscount() {
        return discount;
    }

    public ProductGetterSetter setDiscount(String discount) {
        this.discount = discount;
        return this;
    }

    public String getOther() {
        return other;
    }

    public ProductGetterSetter setOther(String other) {
        this.other = other;
        return this;
    }

    public String getPrice() {
        return price;
    }

    public ProductGetterSetter setPrice(String price) {
        this.price = price;
        return this;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public ProductGetterSetter setProductDesc(String productDesc) {
        this.productDesc = productDesc;
        return this;
    }

    public String getProductImage() {
        return productImage;
    }

    public ProductGetterSetter setProductImage(String productImage) {
        this.productImage = productImage;
        return this;
    }

    public String getProductLink() {
        return productLink;
    }

    public ProductGetterSetter setProductLink(String productLink) {
        this.productLink = productLink;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public ProductGetterSetter setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    @Override
    public String toString() {
        return "ProductGetterSetter{" +
                "MRP='" + MRP + '\'' +
                ", discount='" + discount + '\'' +
                ", other='" + other + '\'' +
                ", price='" + price + '\'' +
                ", productDesc='" + productDesc + '\'' +
                ", productImage='" + productImage + '\'' +
                ", productLink='" + productLink + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}
