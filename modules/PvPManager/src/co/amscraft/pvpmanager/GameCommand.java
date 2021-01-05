package co.amscraft.pvpmanager;

import co.amscraft.pvpmanager.exceptions.ArenaInUseException;
import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"game", "pvp"};
    }

    @Override
    public String getHelp() {
        return "The game command!";
    }

    @Override
    public Component[] getComponents() {

        return new Component[]{new Component() {

            @Override
            public String[] getAliases() {
                return new String[]{"join"};
            }

            @Override
            public String getHelp() {
                return "<arena> {player}";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                Arena arena = Arena.getArena(args[i]);
                EditorSettings s = EditorSettings.getSettings(sender);
                if (Game.getGame(Bukkit.getPlayer(sender.getName())) == null) {
                    if (arena != null) {
                        Game game = arena.getGame();
                        if (game == null) {
                            try {
                                game = new Game(arena);
                            } catch (ArenaInUseException e) {
                                e.printStackTrace();
                            }
                        }
                        if (game != null && !game.hasStarted()) {
                            if (args.length > i + 1) {
                                Player player = Bukkit.getPlayer(args[i + 1]);
                                if (player != null) {
                                    Team team = game.getTeam(player);
                                    if (team != null) {
                                        if (team.getPlayers().size() < game.getArena().getMaxPlayersOnTeam()) {
                                            team.addPlayer(Bukkit.getPlayer(sender.getName()));
                                            sender.sendMessage(s.getSuccess() + "You have successfully join player " + args[i + 1] + "'s team!");
                                        } else {
                                            sender.sendMessage(s.getError() + "This team is already at max capacity");
                                        }
                                    } else {
                                        sender.sendMessage(s.getError() + "Player " + args[i + 1] + " is not in this game!");
                                    }
                                } else {
                                    sender.sendMessage(s.getError() + "Player " + args[i + 1] + " is not online!");
                                }
                            } else {
                                if (game.getTeams().size() < arena.getMaxTeams()) {
                                    new Team(Bukkit.getPlayer(sender.getName()), game);
                                    sender.sendMessage(s.getSuccess() + "You have successfully joined the game!");
                                    // team.addPlayer(Bukkit.getPlayer(sender.getName()));
                                } else {
                                    sender.sendMessage(s.getError() + "This arena is already at max capacity");
                                }
                            }
                        } else {
                            sender.sendMessage(s.getError() + "The game has already started!");
                        }
                    } else {
                        sender.sendMessage(s.getError() + "Arena " + args[i] + " does not exist!");
                    }
                } else {
                    sender.sendMessage(s.getError() + "You are already in a game!");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"start"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                Game game = Game.getGame(Bukkit.getPlayer(sender.getName()));
                EditorSettings s = EditorSettings.getSettings(sender);
                if (game != null) {
                    if (!game.hasStarted()) {
                        game.start();
                        sender.sendMessage(s.getSuccess() + "Game has successfully started!");
                    } else {
                        sender.sendMessage(s.getError() + "The game has already started!");
                    }
                } else {
                    sender.sendMessage(s.getError() + "You are not in a game!");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"stats"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                Game game = null;
                String fail;
                EditorSettings s = EditorSettings.getSettings(sender);
                if (args[i] != null) {
                    Arena arena = Arena.getArena(args[i]);
                    if (arena != null) {
                        game = arena.getGame();
                        fail = "Arena " + args[i] + " does not have a game right now!";
                    } else {
                        fail = "Arena " + args[i] + " does not exist";
                    }
                } else {
                    game = Game.getGame((Player) sender);
                    fail = "You are not in a game!";
                }
                if (game != null) {
                    game.sendStats(sender);
                } else {
                    sender.sendMessage(s.getError() + fail);
                }
            }

            @Override
            public String getHelp() {
                return "{arena}";
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"quit"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                Game game = null;
                EditorSettings s = EditorSettings.getSettings(sender);
                game = Game.getGame((Player) sender);
                if (game != null) {
                    Team team = game.getTeam((Player) sender);
                    team.removePlayer((Player) sender);
                    sender.sendMessage(s.getSuccess() + "Successfully quit the game");
                } else {
                    sender.sendMessage(s.getError() + "You are not in a game!");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"rules"};
            }

            @Override
            public Component[] getComponents() {
                return new Component[]{new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"lives"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        EditorSettings s = EditorSettings.getSettings(sender);
                        Game game = Game.getGame((Player) sender);
                        if (game != null) {
                            if (!game.hasStarted()) {
                                try {
                                    int lives = Integer.parseInt(args[i]);
                                    if (lives > 0 && lives < 50) {
                                        game.getArena().setLives(lives);
                                        sender.sendMessage(s.getSuccess() + "Successfully changed the lives to: " + lives);
                                    } else {
                                        sender.sendMessage(s.getError() + "You must enter a reasonable integer >= 0");
                                    }
                                } catch (NumberFormatException e) {
                                    sender.sendMessage(s.getError() + "You must enter an integer value");
                                }
                            } else {
                                sender.sendMessage(s.getError() + "The game has started");
                            }
                        } else {
                            sender.sendMessage(s.getError() + "You are not in a game");
                        }
                    }

                    @Override
                    public String getHelp() {
                        return "<lives>";
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"spells"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        Game game = null;
                        EditorSettings s = EditorSettings.getSettings(sender);
                        game = Game.getGame((Player) sender);
                        if (game != null) {
                            if (!game.hasStarted()) {
                                try {
                                    game.getArena().setAllowSpells(Boolean.parseBoolean(args[i]));
                                    sender.sendMessage(s.getSuccess() + "Successfully set spells to: " + (game.getArena().isAllowingSpells() ? "enabled" : "disabled"));
                                } catch (Exception e) {
                                    sender.sendMessage(s.getError() + "You must enter a boolean value of true/false");
                                }
                            } else {
                                sender.sendMessage(s.getError() + "The game has started");
                            }
                        } else {
                            sender.sendMessage(s.getError() + "You are not in a game");
                        }
                    }

                    @Override
                    public String getHelp() {
                        return "<true/false>";
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"protectTeam"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        Game game = null;
                        EditorSettings s = EditorSettings.getSettings(sender);
                        game = Game.getGame((Player) sender);
                        if (game != null) {
                            if (!game.hasStarted()) {
                                try {
                                    game.getArena().setProtectTeam(Boolean.parseBoolean(args[i]));
                                    sender.sendMessage(s.getSuccess() + "Successfully set protect team to: " + (game.getArena().isProtectingTeam() ? "enabled" : "disabled"));
                                } catch (Exception e) {
                                    sender.sendMessage(s.getError() + "You must enter a boolean value of true/false");
                                }
                            } else {
                                sender.sendMessage(s.getError() + "The game has started");
                            }
                        } else {
                            sender.sendMessage(s.getError() + "You are not in a game");
                        }
                    }

                    @Override
                    public String getHelp() {
                        return "<true/false>";
                    }
                }};
            }
        }};
    }
}
