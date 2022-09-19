package com.freiz.server;

import com.freiz.common.commands.AbstractCommand;
import com.freiz.common.commands.AddCommand;
import com.freiz.common.commands.AddIfMaxCommand;
import com.freiz.common.commands.AddIfMinCommand;
import com.freiz.common.commands.ClearCommand;
import com.freiz.common.commands.CountGreaterThanHeartCountCommand;
import com.freiz.common.commands.CountLessThanMeleeWeaponMeleeWeaponCommand;
import com.freiz.common.commands.FilterByWeaponTypeCommand;
import com.freiz.common.commands.HelpCommand;
import com.freiz.common.commands.HistoryCommand;
import com.freiz.common.commands.InfoCommand;
import com.freiz.common.commands.LoginCommand;
import com.freiz.common.commands.RegisterCommand;
import com.freiz.common.commands.RemoveByIdCommand;
import com.freiz.common.commands.ShowCommand;
import com.freiz.common.commands.UpdateCommand;
import com.freiz.common.util.InputManager;
import com.freiz.common.util.OutputManager;

import java.util.HashMap;
import java.util.Map;

public final class CommandResolver {
    private static CommandResolver instance;
    private final Map<String, AbstractCommand> commands = new HashMap<>();
    private final OutputManager outputManager = new OutputManager();
    private final InputManager inputManager = new InputManager(System.in);




    private CommandResolver() {
        commands.put("add", new AddCommand(outputManager, inputManager));
        commands.put("add_if_max", new AddIfMaxCommand(inputManager, outputManager));
        commands.put("add_if_min", new AddIfMinCommand(inputManager, outputManager));
        commands.put("clear", new ClearCommand());
        commands.put("count_greater_than_heart_count", new CountGreaterThanHeartCountCommand());
        commands.put("help", new HelpCommand());
        commands.put("history", new HistoryCommand());
        commands.put("info", new InfoCommand());
        commands.put("show", new ShowCommand());
        commands.put("update", new UpdateCommand(inputManager, outputManager));
        commands.put("count_less_than_melee_weapon", new CountLessThanMeleeWeaponMeleeWeaponCommand());
        commands.put("filter_by_weapon_type", new FilterByWeaponTypeCommand());
        commands.put("remove_by_id", new RemoveByIdCommand());
        commands.put("login", new LoginCommand());
        commands.put("register", new RegisterCommand());
    }
    public static synchronized CommandResolver getInstance() {
        if (instance == null) {
            instance = new CommandResolver();
        }
        return instance;
    }

    public AbstractCommand resolveCommand(String commandMessage) {
        if (commands.containsKey(commandMessage)) {
            return commands.get(commandMessage);
        }
        throw new RuntimeException("no such command!");
    }
}
