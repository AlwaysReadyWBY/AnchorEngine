package top.alwaysready.anchorengine.spigot.support.betonquest;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

public class BQPortraitEvent implements Event {

    public static BQPortraitEvent parse(Instruction instruction) throws InstructionParseException {
        BQPortraitEvent ev = new BQPortraitEvent(instruction.next());
        instruction.getOptionalArgument("slot").ifPresent(ev::setSlot);
        return ev;
    }

    public BQPortraitEvent(String path) {
        this.path = path;
    }
    private String slot = "left";
    private final String path;

    @Override
    public void execute(Profile profile) throws QuestRuntimeException {
        AnchorDefaultIO.get(profile.getPlayer().getPlayer())
                .ifPresent(io -> io.setPortrait(path.equalsIgnoreCase("clear")?
                        null:path,
                        slot));
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }
}
