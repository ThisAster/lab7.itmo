package com.freiz.common.commands;

import java.util.List;

import com.freiz.common.db.SpaceMarineDao;
import com.freiz.common.dto.CommandResultDto;
import com.freiz.common.exception.NotMaxException;
import com.freiz.common.exception.NotMinException;
import com.freiz.common.network.Request;
import com.freiz.common.util.CollectionManager;
import com.freiz.common.util.HistoryManager;

public class ClearCommand extends AbstractCommand {
    public ClearCommand() {
        super("clear");

    }

    @Override
    public Request packageRequest(Object[] args) {
        Request request = new Request("clear", new Object[] {});
        return request;
    }

    @Override
    public CommandResultDto execute(Request request, CollectionManager collectionManager, HistoryManager historyManager)
            throws NotMaxException, NotMinException {
        historyManager.addNote(this.getName());
        collectionManager.getHashSetId().clear();
        collectionManager.clear();
        List<Long> ids = SpaceMarineDao.getAllUserSpaceMarinesIds(request.getUser().getId());
        ids.stream().forEach(id -> SpaceMarineDao.removeById(id));
        return new CommandResultDto("succes clear");
    }
}
