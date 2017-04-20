package com.cuelogic.firebase.chat.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @version 1.0.0
 * @Date 14/04/2017
 */
@Entity
public class Sample {
    @Id(autoincrement = true)
    private Long id;
    private String userName;
    @Generated(hash = 505417145)
    public Sample(Long id, String userName) {
        this.id = id;
        this.userName = userName;
    }
    @Generated(hash = 976859954)
    public Sample() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
