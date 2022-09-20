package com.freiz.common.commands;

import com.freiz.common.commands.subcommand.AddElem;
import com.freiz.common.data.SpaceMarine;
import com.freiz.common.db.SpaceMarineDao;
import com.freiz.common.dto.CommandResultDto;
import com.freiz.common.exception.InvalidRequestException;
import com.freiz.common.exception.NotMaxException;
import com.freiz.common.exception.NotMinException;
import com.freiz.common.exception.NotSuccessfulCommand;
import com.freiz.common.network.Request;
import com.freiz.common.util.CollectionManager;
import com.freiz.common.util.HistoryManager;
import com.freiz.common.util.InputManager;
import com.freiz.common.util.OutputManager;
import lombok.SneakyThrows;

public class UpdateCommand extends AbstractCommand {
    private final OutputManager outputManager;
    private final InputManager inputManager;


    public UpdateCommand(InputManager inputManager, OutputManager outputManager) {
        super("update");
        this.inputManager = inputManager;
        this.outputManager = outputManager;
    }
    @Override
    public Request packageRequest(Object[] args) throws InvalidRequestException {
        if (args.length > 2) {
            SpaceMarine spaceMarine = AddElem.add(inputManager, outputManager);
            Request request = new Request("update", new Object[] {args[0], args[1], spaceMarine});
            return request;
        }
        throw new InvalidRequestException();
    }

    @SneakyThrows
    @Override
    public CommandResultDto execute(Request request, CollectionManager collectionManager, HistoryManager historyManager)
            throws NotMaxException, NotMinException {
        historyManager.addNote(this.getName());
        String castArg = (String) request.getArg(1);
        Long id;
        try {
            id = Long.parseLong(castArg);
        } catch (NumberFormatException e) {
            return new CommandResultDto("Your argument was incorrect. The command was not executed.");
        }
        if (!collectionManager.isAny(id)) {
            return new CommandResultDto("have not this id");
        }
        if (request.getUser().getId() != SpaceMarineDao.getSpaceMarineById(id).getUser().getId()) {
            return new CommandResultDto("Its not your object, you cannot update it. The command was not executed.");
        }
        SpaceMarine spaceMarine = (SpaceMarine) request.getArg(2);
        spaceMarine.setId(id);
        if (collectionManager.isAny(id)) {
            SpaceMarine thisSpaceMarine = collectionManager.findSpaceMarineById(id);
            collectionManager.getHashSetId().remove(thisSpaceMarine.getId());
            collectionManager.remove(thisSpaceMarine);
            collectionManager.getHashSetId().remove(thisSpaceMarine.getId());
            SpaceMarineDao.update(spaceMarine, id);
        } else {
            throw new NotSuccessfulCommand();
        }
        collectionManager.add(spaceMarine);
        return new CommandResultDto("success added");
    }
}
