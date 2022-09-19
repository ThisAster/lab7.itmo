package com.freiz.common.commands;

import com.freiz.common.data.User;
import com.freiz.common.dto.CommandResultDto;
import com.freiz.common.exception.InvalidRequestException;
import com.freiz.common.exception.NotMaxException;
import com.freiz.common.exception.NotMinException;
import com.freiz.common.network.Request;
import com.freiz.common.util.CollectionManager;
import com.freiz.common.util.HistoryManager;

import static com.freiz.common.db.UserDao.getUser;

public class LoginCommand extends AbstractCommand {

    public LoginCommand() {
        super("login");
    }

    @Override
    public Request packageRequest(Object[] args) throws InvalidRequestException {
        return new Request("login", args);
    }

    @Override
    public CommandResultDto execute(Request request, CollectionManager collectionManager, HistoryManager historyManager) throws NotMaxException, NotMinException {
        User userFromDB = getUser(request.getUser());
        if (userFromDB != null && userFromDB.getPassword().equals(request.getUser().getPassword())) {
            return new CommandResultDto("success login, your id=" + userFromDB.getId());
        } else {
            return new CommandResultDto("unsuccess, invalid login or password");
        }
    }
}
