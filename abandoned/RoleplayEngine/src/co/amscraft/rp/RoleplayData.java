package co.amscraft.rp;

import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.PlayerData;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.utils.NMSUtils;
import co.amscraft.ultralib.utils.ObjectUtils;
import co.amscraft.ultralib.utils.savevar.SaveVar;
import com.earth2me.essentials.Essentials;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.permissions.Permission;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RoleplayData extends PlayerData {
    private static final int BOOK_PAGE_SIZE = 256;
    private long birthday;
    private String link = null;
    private String race = null;
    private Map<RoleplayPower, RoleplayPower.PowerLevel> powers = new HashMap<>();
    private Map<UUID, Relation> relationships = new HashMap<>();
    private List<String> roleplaysPastebinKeys = new ArrayList<>();
    private String backstoryPastebinKey;
    @FieldDescription(save = false)
    private ItemStack book;
    @FieldDescription(save = false)
    private EditorSettings bookSettings;

    public enum Relation {
        FRIEND("Friends"), NEUTRAL(null), ENEMY("Enemies");
        private Relation(String plural) {
            this.plural = plural;
        }
        private String plural;
        public String getPlural() {
            return plural;
        }
        public static Relation getRelation(String relation) {
            for (Relation r: values()) {
                if (r.toString().equalsIgnoreCase(relation)) {
                    return r;
                }
            }
            return null;
        }
    }

    public void setRelationship(UUID target, Relation relation) {
        if (relation == Relation.NEUTRAL) {
            relationships.remove(target);
        } else {
            relationships.put(target, relation);
        }
        book = null;
    }

    public Relation getRelation(UUID player) {
        return relationships.getOrDefault(player, Relation.NEUTRAL);
    }

    public static String getName(String player) {
        return ChatColor.stripColor(Essentials.getPlugin(Essentials.class).getOfflineUser(player).getNickname());
    }
    public static String getName(UUID player) {
        return getName(Bukkit.getOfflinePlayer(player).getName());
    }
    public String getName() {
        return getName(this.getPlayer().getBukkit().getName());
    }

    public String getRace() {
        if (this.race != null) {
            return this.race;
        } else {
            RoleplayRank rank = this.getRank();
            if (rank != null) {
                return rank.getRace();
            }
        }
        return null;
    }

    private RoleplayRank getRank() {
        return RoleplayRank.getRank(PermissionsEx.getUser(this.getPlayer().getBukkit().getName()).getGroupNames()[0]);
    }
    private Set<RoleplayRank> getRanks() {
        Set<RoleplayRank> ranks = new HashSet<>();
        for (String group: PermissionsEx.getUser(this.getPlayer().getBukkit().getName()).getGroupNames()) {
            RoleplayRank rank = RoleplayRank.getRank(group);
            if (rank != null) {
                ranks.add(rank);
            }
        }
        return ranks;
    }

    public Map<RoleplayPower, RoleplayPower.PowerLevel> getPowers() {
        //System.out.println(this.powers);
        Map<RoleplayPower, RoleplayPower.PowerLevel> powers = new HashMap<>(this.powers);
        for (RoleplayRank rank: getRanks()) {
            for (RoleplayPower power: rank.getPowers().keySet()) {
                if (!powers.containsKey(power) || powers.get(power).compareTo(rank.getPowers().get(power)) > 0) {
                    powers.put(power, rank.getPowers().get(power));
                }
            }
        }
        return powers;
    }

    public void sendCard(CommandSender p, int page) {
        EditorSettings s = EditorSettings.getSettings(p);
        ItemStack card = getCard(s);
        List<String> pages = ((BookMeta)card.getItemMeta()).getPages();
        if (page-1 < pages.size()) {
            String text = pages.get(page-1);
            p.sendMessage(s.getVariable() + this.getPlayer().getBukkit().getDisplayName() + s.getVariable() + ChatColor.BOLD + "'s Card");
            while (text.length() < BOOK_PAGE_SIZE) {
                text += " ";
            }
            p.sendMessage(text);
            TextComponent comp = new TextComponent("         ");

                TextComponent previous = new TextComponent(s.getColon() + ChatColor.BOLD + "<<");
                previous.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(s.getHelp() + "Go back")}));
                previous.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rp info " + this.getPlayer().getBukkit().getName() + " " + (page-1)));
                comp.addExtra(previous);
            comp.addExtra(" " + s.getValue() + page + "/" + pages.size() + " ");
                TextComponent next = new TextComponent(s.getColon() + ChatColor.BOLD + ">>");
                next.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(s.getHelp() + "Go forward")}));
                next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rp info " + this.getPlayer().getBukkit().getName() + " " + (page+1)));
                comp.addExtra(next);
            p.spigot().sendMessage(comp);
        } else{
            p.sendMessage(s.getError() + "Page not found!");
        }
    }

    public ItemStack getCard(EditorSettings s) {
        if (book == null || (bookSettings != null && !bookSettings.equals(s))) {
            bookSettings = s;
            List<String> pages = new ArrayList<>();
            pages.add(s.getVariable() + ChatColor.BOLD + "Character Card\n\n" +
                    s.getVariable() + "Name" + s.getColon() + ": " + s.getValue() + this.getName() + "\n\n" +
                    s.getVariable() + "Age" + s.getColon() + ": " + s.getValue() + this.getAge() + "\n\n" +
                    s.getVariable() + "Race" + s.getColon() + ": " + s.getValue() + this.getRace() + "\n\n" +
                    s.getVariable() + "Rank" + s.getColon() + ": " + s.getValue() + PermissionsEx.getUser(this.getPlayer().getBukkit()).getGroups()[0].getName() + "\n\n" +
                    s.getVariable() + "Link" + s.getColon() + ": " + s.getValue() + this.getLink() + "\n\n"
            );
            {
                String string = s.getVariable() + ChatColor.BOLD + "Powers & Skills\n\n";
                Map<RoleplayPower, RoleplayPower.PowerLevel> powers = getPowers();
                for (RoleplayPower power: powers.keySet()) {
                    string += s.getVariable() + power.getName() + s.getColon() + ": " + s.getValue() + powers.get(power).name() + "\n";
                    string += s.getValue() + power.getDescription() + "\n\n";
                }
                pages.addAll(split(string, BOOK_PAGE_SIZE));
            }

            if (!relationships.isEmpty()){
                String string = s.getVariable() + ChatColor.BOLD + "Relationships\n\n";
                for (Relation r: Relation.values()) {
                    Set<UUID> relations = getRelations(r);
                    if (!relations.isEmpty()) {
                        string+= s.getVariable() + r.getPlural() + s.getColon() + ": ";
                        for (UUID player : relations) {
                            String name = getName(player);
                            string += s.getValue() + name + s.getColon() + ", ";
                        }
                        string = string.substring(string.length()-3);
                    }
                }
                pages.addAll(split(string, BOOK_PAGE_SIZE));
            }
            String backstory = ObjectUtils.getUrlContents("https://pastebin.com/raw/" + backstoryPastebinKey);
            if (backstory != null && !backstory.equals("")) {
                backstory = ChatColor.translateAlternateColorCodes('&', backstory);
                pages.addAll(split(s.getVariable() + ChatColor.BOLD + "Backstory\n    " + s.getValue() + backstory, BOOK_PAGE_SIZE));
            } else {
                pages.add(s.getError()+"Error 404: Backstory Not Found!");
            }
            {
                List<String> roleplays = new ArrayList<>();
                for (String key : roleplaysPastebinKeys) {
                    roleplays.add(s.getVariable() + "Roleplay: " + s.getValue() + ChatColor.translateAlternateColorCodes('&',ObjectUtils.getUrlContents("https://pastebin.com/raw/" + key)));
                }
                if (!roleplays.isEmpty()) {
                    roleplays.set(0, s.getVariable() + ChatColor.BOLD + "Roleplays" + roleplays.get(0));
                }
                for (String roleplay : roleplays) {
                    pages.addAll(split(roleplay, BOOK_PAGE_SIZE));
                }
            }
            book = getBook(pages, s.getVariable() + this.getPlayer().getBukkit().getDisplayName(), this.getName()  + s.getHelp()+ "'s Character Card");
        }
        return book;
    }

    public static long getDate(int day, String month, int year) {
        String string_date = day + "-" + month + "-" + year;
        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date d = f.parse(string_date);
            long milliseconds = d.getTime();
            return milliseconds;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setAge(int day, String month, int year) {
        this.book = null;
        this.birthday = getDate(day, month, year);
    }

    public static int getAge(long birthday) {
        long diff = System.currentTimeMillis() - birthday;
        int years = (int) (diff / ((long)86400 * 1000 * 365));
        return years;
    }

    public int getAge() {
        return getAge(birthday);
    }

    private static List<String> split(String text, int length) {
        if (text == null) {
            return null;
        }
        List<String> pages = new ArrayList<>();
        pages.add("");
        String[] words = text.split(" ");
        String lastColor = "";
        for (String word: words) {
            if (pages.get(pages.size()-1).length() + word.length() < length) {
                for (int i = word.length()-2; i >= 0; i--) {
                    if (word.charAt(i) == ChatColor.COLOR_CHAR) {
                        lastColor = ChatColor.COLOR_CHAR + "" + word.charAt(i+1);
                        break;
                    }
                }
                pages.set(pages.size()-1, pages.get(pages.size()-1) + " " + word);
            } else {
                pages.add(lastColor + word);
            }
        }
        return pages;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        book = null;
        this.link = link;
    }

    @SaveVar
    private static int BOOK_DURATION = 3600;
    private ItemStack getBook(List<String> pages, String title, String lore) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta)book.getItemMeta();
        //meta.setAuthor(this.getName());
        meta.setTitle(title);
        meta.setDisplayName(title);
        meta.setPages(pages);
        meta.setLore(split(lore, 20));
        book.setItemMeta(meta);
        try {
            book = NMSUtils.write(book, "expires", (System.currentTimeMillis() + Math.round((BOOK_DURATION * 1000))) + "");
            book = NMSUtils.write(book, "RPCard", this.getPlayer().getBukkit().getUniqueId() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return book;
    }

    private static Set<String> getNames(Set<UUID> uuids) {
        Set<String> set = new HashSet<>();
        for (UUID uuid: uuids) {
            set.add(getName(uuid));
        }
        return set;
    }

    public Set<UUID> getRelations(Relation relation) {
        Set<UUID> set = new HashSet<>();
        for (UUID uuid: this.relationships.keySet()) {
            if (getRelation(uuid) == relation) {
                set.add(uuid);
            }
        }
        return set;
    }

    public void setBackstory(String key) {
        book = null;
        this.backstoryPastebinKey = key;
    }


    public void setPower(RoleplayPower power, RoleplayPower.PowerLevel level) {
        book = null;
        this.powers.put(power, level);
    }

    public void removePower(RoleplayPower power) {
        this.powers.remove(power);
    }
}
