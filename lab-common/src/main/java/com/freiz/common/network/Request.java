package com.freiz.common.network;

import com.freiz.common.data.User;

import java.io.Serializable;

public class Request  implements Serializable {
    private static final long serialVersionUID = 2043095393988838048L;
    private String commandName;
    private Object[] args;
    private User user;

    public Request(String commandName, Object[] args) {
        this.commandName = commandName;
        this.args = args;
    }
    public Object[] getArgs() {
        return args;
    }
    public String getCommandName() {
         return commandName;
     }
    public Object getArg(int i) {
        return args[i];
    }
    public int getArgsLength() {
        return args.length;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
