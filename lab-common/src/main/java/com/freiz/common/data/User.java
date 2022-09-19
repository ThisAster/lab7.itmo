package com.freiz.common.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class User implements Serializable {

    static final long serialVersionUID = 55L;

    private String name;
    private String password;
    private int id;

    public User(String login, String encryptPassword) {
        this.name = login;
        this.password = encryptPassword;
    }
}
