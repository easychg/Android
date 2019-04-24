package com.pstreets.nfc;

public class Good {
    private String name;
    private String by_code;
    private boolean bo;
    public String getBy_code() {
        return by_code;
    }
    public void setBy_code(String by_code) {
        this.by_code = by_code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean getBo() {
        return bo;
    }
    public void setBo(boolean bo) {
        this.bo = bo;
    }
    @Override
    public String toString() {
        return "Good [name=" + name + ", bo=" + bo + "，by_code"+by_code+"]";
    }
    public Good(String name, boolean bo,String by_code) {
        super();
        this.name = name;
        this.bo = bo;
        this.by_code=by_code;
    }
    public Good() {
        super();
    }
}
