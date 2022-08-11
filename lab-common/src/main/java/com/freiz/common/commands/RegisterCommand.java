package com.freiz.common.commands;

import com.freiz.common.data.User;
import com.freiz.common.dto.CommandResultDto;
import com.freiz.common.exception.InvalidRequestException;
import com.freiz.common.exception.NotMaxException;
import com.freiz.common.exception.NotMinException;
import com.freiz.common.network.Request;
import com.freiz.common.util.CollectionManager;
import com.freiz.common.util.HistoryManager;

import static com.freiz.common.db.UserDao.createUser;
import static com.freiz.common.db.UserDao.getUser;

public class RegisterCommand extends AbstractCommand {
    public RegisterCommand() {
        super("register");
    }

    @Override
    public Request packageRequest(Object[] args) throws InvalidRequestException {
        return new Request("register", args);
    }

    @Override
    public CommandResultDto execute(Request request, CollectionManager collectionManager, HistoryManager historyManager) throws NotMaxException, NotMinException {
        if (createUser(request.getUser()).contains("successfully")) {
            User userFromDB = getUser(request.getUser());
            return new CommandResultDto("success login, your id=" + userFromDB.getId());
        } else {
            return new CommandResultDto("user with this name already exists!");
        }
    }
}
