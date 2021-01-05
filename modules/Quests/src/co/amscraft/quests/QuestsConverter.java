package co.amscraft.quests;

import co.amscraft.quests.objectives.*;
import co.amscraft.quests.requirements.AllRequirements;
import co.amscraft.quests.requirements.Cooldown;
import co.amscraft.quests.requirements.PermissionRequirement;
import co.amscraft.quests.requirements.QuestRequirement;
import co.amscraft.quests.rewards.CommandReward;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

public class QuestsConverter {

    public static void convertQuests(File file) {
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String id : config.getConfigurationSection("quests").getKeys(false)) {
                String path = "quests." + id;
                String name = config.getString(path + ".name");
                try {
                    if (Quest.getObject(Quest.class, "name", name) == null) {
                        Quest quest = new Quest();
                        quest.setName(name);
                        quest.setNPC(config.getInt(path + ".npc-giver-id"));
                        quest.setRequirement(new AllRequirements());
                        Cooldown cooldown = new Cooldown();
                        cooldown.cooldown = config.getLong(path + ".redo-delay");
                        quest.setFinishMessage(config.getString(path + ".finish-message"));
                        if (cooldown.cooldown == 0) {
                            cooldown.cooldown = -1;
                        }
                        ((AllRequirements) quest.getRequirement()).requirements.add(cooldown);
                        if (config.get(path + ".requirements.permission") != null) {
                            for (String perm : config.getStringList(path + ".requirements.permission")) {
                                PermissionRequirement permission = new PermissionRequirement();
                                permission.permission = perm;
                                ((AllRequirements) quest.getRequirement()).requirements.add(permission);
                            }
                        }
                        if (config.get(path + ".requirements.quests") != null) {
                            for (String q : config.getStringList(path + ".requirements.quests")) {
                                QuestRequirement requirement = new QuestRequirement();
                                requirement.quest = q;
                                ((AllRequirements) quest.getRequirement()).requirements.add(requirement);
                            }
                        }
                        if (config.get(path + ".rewards.commands") != null) {
                            for (String command : config.getStringList(path + ".rewards.commands")) {
                                CommandReward reward = new CommandReward();
                                reward.command = command;
                                quest.getRewards().add(reward);
                            }
                        }
                        if (config.get(path + ".rewards.items") != null) {
                            for (String item : config.getStringList(path + ".rewards.items")) {
                                CommandReward reward = new CommandReward();
                                Material material = null;
                                int amount = 0;
                                String display = "";
                                String lore = "";
                                for (String split : item.split(":")) {
                                    String key = split.split("-")[0];
                                    String value = split.replaceFirst(key + "-", "");
                                    switch (key) {
                                        case "amount":
                                            amount = Integer.parseInt(value);
                                            break;
                                        case "name":
                                            material = Material.valueOf(value);
                                            break;
                                        case "displayname":
                                            display = value;
                                            break;
                                        case "lore":
                                            lore = value;
                                            break;
                                        case "enchantment":

                                            break;
                                        default:
                                            break;
                                    }
                                }
                                reward.command = "essentials:give <player> " + material + " " + amount + " " + (!display.equals("") ? "name:" + display.replace(" ", "_") : "") + (!lore.equals("") ? " lore:" + lore.replace(" ", "_") : "");
                                quest.getRewards().add(reward);
                            }
                        }
                        quest.setQuestion(config.getString(path + ".ask-message"));
                        for (String stageNumber : config.getConfigurationSection(path + ".stages.ordered").getKeys(false)) {
                            try {
                                String stagepath = path + ".stages.ordered." + stageNumber;
                                Stage stage = new Stage();
                                quest.getStages().add(stage);
                                stage.startMessage = config.getString(stagepath + ".start-message");

                                if (stage.startMessage == null) {
                                    stage.startMessage = "";
                                }

                                stage.finishMessage = config.getString(stagepath + ".complete-message");
                                if (stage.finishMessage == null) {
                                    stage.finishMessage = "";
                                }

                                if (config.get(stagepath + ".npc-ids-to-talk-to") != null) {
                                    for (int npc : (List<Integer>) config.getList(stagepath + ".npc-ids-to-talk-to")) {
                                        TalkNPC objective = new TalkNPC();
                                        objective.NPC = npc;
                                        stage.objectives.add(objective);
                                    }
                                }
                                if (config.get(stagepath + ".locations-to-reach") != null) {
                                    List<String> locations = config.getStringList(stagepath + ".locations-to-reach");
                                    List<Integer> radius = (List<Integer>) config.getList(stagepath + ".reach-location-radii");
                                    List<String> names = config.getStringList(stagepath + ".reach-location-names");
                                    for (int i = 0; i < locations.size(); i++) {
                                        ReachLocation objective = new ReachLocation();
                                        String[] split = locations.get(i).split(" ");
                                        objective.location = new QuestLocation(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                                        objective.radius = radius.get(i);
                                        objective.displayOverride = "Reach location " + names.get(i) + " at " + objective.location;
                                        stage.objectives.add(objective);
                                    }
                                }
                                if (config.get(stagepath + ".npc-ids-to-kill") != null) {
                                    List<Integer> npcs = (List<Integer>) config.getList(stagepath + ".npc-ids-to-kill");
                                    List<Integer> kills = (List<Integer>) config.getList(stagepath + ".npc-kill-amounts");
                                    for (int i = 0; i < npcs.size(); i++) {
                                        KillNPC objective = new KillNPC();
                                        objective.npcs.add(npcs.get(i));
                                        objective.kills = kills.get(i);
                                        stage.objectives.add(objective);
                                    }
                                }
                                if (config.get(stagepath + ".mobs-to-kill") != null) {
                                    List<String> mobs = config.getStringList(stagepath + ".mobs-to-kill");
                                    List<Integer> kills = (List<Integer>) config.getList(stagepath + ".mob-amounts");
                                    for (int i = 0; i < mobs.size(); i++) {
                                        KillMobs objective = new KillMobs();
                                        objective.monsters.add(EntityType.valueOf(mobs.get(i).toUpperCase().replace("PIGZOMBIE", "PIG_ZOMBIE").replace("POLARBEAR", "POLAR_BEAR")));
                                        objective.amount = kills.get(i);
                                        stage.objectives.add(objective);
                                    }
                                }
                                if (config.get(stagepath + ".custom-objectives") != null) {
                                    for (String custom : config.getConfigurationSection(stagepath + ".custom-objectives").getKeys(false)) {
                                        String custompath = stagepath + ".custom-objectives." + custom;
                                        switch (config.getString(custompath + ".name")) {
                                            case "Delay":
                                                Delay delay = new Delay();
                                                delay.delay = Long.parseLong(config.getString(custompath + ".data.Time"));
                                                stage.objectives.add(delay);
                                                break;
                                            case "Kill Mob Army":
                                                KillMobs mobs = new KillMobs();
                                                for (String mob : config.getString(custompath + ".data.Mobs").split(" ")) {
                                                    mobs.monsters.add(EntityType.valueOf(mob.toUpperCase()));
                                                }
                                                mobs.amount = config.getInt(custompath + ".count");
                                                stage.objectives.add(mobs);
                                                break;
                                            case "Kil NPC Army":
                                                KillNPC npcs = new KillNPC();
                                                npcs.kills = config.getInt(custompath + ".count");
                                                for (String npc : config.getString(custompath + ".data.NPC IDs").split(" ")) {
                                                    npcs.npcs.add(Integer.parseInt(npc));
                                                }
                                                npcs.displayOverride = "Kill {COUNT}/{MAX_COUNT} " + config.getString(custompath + ".Display");
                                                stage.objectives.add(npcs);
                                                break;
                                        }
                                    }
                                }
                                if (config.get(stagepath + ".objective-override") != null) {
                                    for (Objective objective : stage.objectives) {
                                        objective.displayOverride = config.getString(stagepath + ".objective-override");
                                    }
                                }
                            } catch (Exception e) {
                                ObjectUtils.debug(Level.WARNING, "Failed to convert stage " + stageNumber + " of quest " + name);
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    ObjectUtils.debug(Level.WARNING, "Error while converting quest: " + name);
                    e.printStackTrace();
                }

            }
            Quest.save(Quest.class);
        }
    }
}
