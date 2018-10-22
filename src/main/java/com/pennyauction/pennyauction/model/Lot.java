package com.pennyauction.pennyauction.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "lots")
@JsonPropertyOrder({"id", "user_uid", "state", "start_price", "final_price", "start_date", "product"})
public class Lot {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @JsonProperty("user_uid")
    private String userUid;

    private String state;

    // TODO: 18/10/2018 Should be of type Money
    @JsonProperty("start_price")
    private float startPrice;

    // TODO: 18/10/2018 Should be of type Money
    @JsonProperty("final_price")
    private float finalPrice;

    @JsonProperty("start_date")
    private Date startDate;

    @Transient
    private Product product;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public float getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(float startPrice) {
        this.startPrice = startPrice;
    }

    public float getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(float finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
