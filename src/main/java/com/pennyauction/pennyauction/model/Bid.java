package com.pennyauction.pennyauction.model;

import javax.persistence.*;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private int lotId;

    private int depositorId;

    private String state;

    private String txid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getLotId() {
        return lotId;
    }

    public void setLotId(int lotId) {
        this.lotId = lotId;
    }

    public int getDepositorId() {
        return depositorId;
    }

    public void setDepositorId(int depositorId) {
        this.depositorId = depositorId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }
}
