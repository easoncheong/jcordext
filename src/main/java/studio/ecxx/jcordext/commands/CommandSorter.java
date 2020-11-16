package studio.ecxx.jcordext.commands;

import java.util.Comparator;

/**
 * A sorter class for Commands. Prioritises the default extensions that are automatically installed
 */
public class CommandSorter implements Comparator<JCommand> {

    /**
     * The method sorts commands by Extension name, then by invocator name.
     * This is used only for the generation of the help command.
     */
    @Override
    public int compare(JCommand o1, JCommand o2) {

        if (o1.getExtensionName().equals("&help") && (!o2.getExtensionName().equals("&help"))) {
            return -1;
        }
        if (o2.getExtensionName().equals("&help") && (!o1.getExtensionName().equals("&help"))) {
            return 1;
        }

        if (o1.getExtensionName().equals("&default") && (!o2.getExtensionName().equals("&default"))) {
            return -1;
        }

        if (o2.getExtensionName().equals("&default") && (!o1.getExtensionName().equals("&default"))) {
            return 1;
        }

        if (o1.getExtensionName().compareTo(o2.getExtensionName())!=0)
            return o1.getExtensionName().compareTo(o2.getExtensionName());
        return o1.getInvocator().compareTo(o2.getInvocator());
    }
}
