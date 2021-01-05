package co.amscraft.quests;

import co.amscraft.quests.objectives.TalkNPC;
import co.amscraft.quests.requirements.QuestRequirement;
import co.amscraft.ultralib.editor.EditorCheck;
import co.amscraft.ultralib.editor.EditorData;
import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultralib.utils.ObjectUtils;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;

public class QuestsModule extends Module {
    @Override
    public String[] getModuleDependancies() {
        return new String[0];
    }

    @Override
    public void onEnable() {
        try {
            EditorData.registerParse(Quest.class, Quest.class.getMethod("getQuest", String.class));
            EditorCheck.register(Quest.class.getDeclaredField("name"), new EditorCheck("The quest name must be origonal") {
                @Override
                public boolean check(Object object, CommandSender sender) {
                    return Quest.getQuest(object + "") == null;
                }
            });
            EditorCheck.register(Quest.class.getDeclaredField("NPC"), new EditorCheck("You must enter a valid NPC!") {
                @Override
                public boolean check(Object object, CommandSender sender) {
                    int i = Integer.parseInt(object + "");
                    return i == -1 || CitizensAPI.getNPCRegistry().getById(i) != null;
                }
            });
            EditorCheck.register(TalkNPC.class.getField("NPC"), EditorCheck.getCheck(Quest.class.getDeclaredField("NPC")));
            //EditorCheck.register(KillNPC.class.getField("NPC"), EditorCheck.getCheck(Quest.class.getField("NPC")));
            //  for (Class<?> type : this.getClasses()) {
            //    if (!Modifier.isAbstract(type.getModifiers())) {
                    /*if (Objective.class.isAssignableFrom(type)) {
                        EditorData.registerConstructors(Objective.class, type.getConstructors());
                    } else if (Reward.class.isAssignableFrom(type)) {
                        EditorData.registerConstructors(Reward.class, type.getConstructors());
                    } else if (Requirement.class.isAssignableFrom(type)) {
                        EditorData.registerConstructors(Requirement.class, type.getConstructors());
                    }*/
            //  }
            ///}
            EditorCheck.register(QuestLocation.class.getField("world"), new EditorCheck("You must enter a valid world!") {
                @Override
                public boolean check(Object object, CommandSender sender) {
                    return Bukkit.getWorld(object + "") != null;
                }
            });
            EditorCheck.register(QuestRequirement.class.getField("quest"), new EditorCheck("You must enter a valid quest") {
                @Override
                public boolean check(Object object, CommandSender sender) {
                    try {
                        return ObjectUtils.parse(Quest.class, object) != null;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }
}
