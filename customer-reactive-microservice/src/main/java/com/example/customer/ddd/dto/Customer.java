package com.example.customer.ddd.dto;

public class Customer {

    private Integer id;
    private String name;

    public Customer() {
    }

    public Integer getId() {
        return id;
    }

    public Customer setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Customer setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "{"
                + "\"id\":\"" + id + "\""
                + ",\"name\":\"" + name + "\""
                + "}";
    }
}
