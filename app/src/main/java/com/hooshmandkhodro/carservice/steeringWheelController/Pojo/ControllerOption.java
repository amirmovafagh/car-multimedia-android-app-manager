package com.hooshmandkhodro.carservice.steeringWheelController.Pojo;

import com.j256.ormlite.field.DatabaseField;

public class ControllerOption {

    @DatabaseField(id = true)
    Options id;
    @DatabaseField(unique = true)
    Integer value;


    public ControllerOption() {
    }

    public Options getId() {
        return id;
    }

    public void setId(Options id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public ControllerOption(Options id, Integer value) {
        this.id = id;
        this.value = value;
    }
}
