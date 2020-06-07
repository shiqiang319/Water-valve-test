package com.example.mymqqttuse;

import org.json.JSONArray;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class Data extends LitePalSupport {
    private  int Id;
    private  int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    private int Cmd;
    private String P1;
    private String P2;
    private String P3;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getCmd() {
        return Cmd;
    }

    public void setCmd(int cmd) {
        Cmd = cmd;
    }

    public String getP1() {
        return P1;
    }

    public void setP1(String p1) {
        P1 = p1;
    }

    public String getP2() {
        return P2;
    }

    public void setP2(String p2) {
        P2 = p2;
    }

    public String getP3() {
        return P3;
    }

    public void setP3(String p3) {
        P3 = p3;
    }
}
