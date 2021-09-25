package com.rwn.rwnstudy.utilities;

public class StoresInfoGetterSetter {

    private String storeName ;
    private String storeDesc;
    private String storeAdd ;
    private String storeImage;


    public StoresInfoGetterSetter() {
    }

    public StoresInfoGetterSetter(String storeName, String storeDesc, String storeAdd, String storeImage) {
        this.storeName = storeName;
        this.storeDesc = storeDesc;
        this.storeAdd = storeAdd;
        this.storeImage = storeImage;
    }

    public String getStoreName() {
        return storeName;
    }

    public StoresInfoGetterSetter setStoreName(String storeName) {
        this.storeName = storeName;
        return this;
    }

    public String getStoreDesc() {
        return storeDesc;
    }

    public StoresInfoGetterSetter setStoreDesc(String storeDesc) {
        storeDesc = storeDesc;
        return this;
    }

    public String getStoreAdd() {
        return storeAdd;
    }

    public StoresInfoGetterSetter setStoreAdd(String storeAdd) {
        this.storeAdd = storeAdd;
        return this;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public StoresInfoGetterSetter setStoreImage(String storeImage) {
        this.storeImage = storeImage;
        return this;
    }


    @Override
    public String toString() {
        return "StoresInfoGetterSetter{" +
                "storeName='" + storeName + '\'' +
                ", StoreDesc='" + storeDesc + '\'' +
                ", storeAdd='" + storeAdd + '\'' +
                ", storeImage='" + storeImage + '\'' +
                '}';
    }
}
