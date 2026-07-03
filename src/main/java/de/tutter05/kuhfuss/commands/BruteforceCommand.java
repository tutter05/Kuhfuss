package de.tutter05.kuhfuss.commands;

import de.tutter05.kuhfuss.ChestCracker;
import de.tutter05.kuhfuss.utils.BlockPosition;
import de.tutter05.kuhfuss.utils.Command;

import java.util.Arrays;
import java.util.stream.Collectors;

import static de.tutter05.kuhfuss.utils.TextUtils.requireNumericString;
import static de.tutter05.kuhfuss.utils.ReflectionHelper.*;

public class BruteforceCommand extends Command {

    public BruteforceCommand() {
        super("bruteforce", "bruteforce x y z (start_passcode) (target_title)",
                "Starts brute-forcing the chest at the specified location. Optionally you can specify the" +
                        " starting code and the title of the target chest");
    }

    @Override
    public void executeCommand(String[] args) {

        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);

            BlockPosition targetPosition = new BlockPosition(x,y,z);

            switch (args.length) {
                case 3 -> ChestCracker.startBruteforcer(targetPosition);
                case 4 -> ChestCracker.startBruteforcer(targetPosition, requireNumericString(args[3]));
            }
            if(args.length >= 5)  {
                String title = Arrays.stream(args).skip(3).collect(Collectors.joining(" "));
                ChestCracker.startBruteforcer(targetPosition, requireNumericString(args[3]), title);
            }

        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            sendChatMessage("§cInvalid arguments! Type .help for help");
        }

    }
}
