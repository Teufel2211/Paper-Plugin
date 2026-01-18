package de.paperserver.plugin.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * VORGEFERTIGTE COMMAND-TYPEN - Copy & Paste für neue Commands!
 * 
 * Hier sind verschiedene Template-Strukturen für Commands.
 * Wähle das Template, das zu deinem Command passt, und passe die Logik an.
 */
public class CommandTemplates {

    /**
     * TEMPLATE 1: EINFACHER COMMAND (kein Subcommands)
     * 
     * Beispiel: /sell
     * 
     * Verwendung:
     * 1. Neue Datei erstellen: SellCommand.java
     * 2. Diesen Code kopieren
     * 3. "MyCommand" durch "SellCommand" ersetzen
     * 4. Deine Logik in onCommand() einfügen
     * 5. In plugin.yml registrieren:
     *    commands:
     *      sell:
     *        description: "Verkaufe ein Item"
     *        usage: "/sell <preis>"
     */
    public static class SimpleCommandTemplate implements CommandExecutor {
        // private final Manager manager;
        
        // public MyCommand(Manager manager) {
        //     this.manager = manager;
        // }

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cDieser Befehl kann nur von Spielern genutzt werden!");
                return true;
            }
            Player player = (Player) sender;

            // DEINE LOGIK HIER
            player.sendMessage("§a✓ Command ausgeführt!");
            return true;
        }
    }

    /**
     * TEMPLATE 2: COMMAND MIT SUBCOMMANDS
     * 
     * Beispiel: /shop create, /shop add, /shop list
     * 
     * Verwendung:
     * 1. Neue Datei erstellen: ShopCommand.java
     * 2. Diesen Code kopieren
     * 3. switch/case anpassen für deine Subcommands
     * 4. In plugin.yml registrieren:
     *    commands:
     *      shop:
     *        description: "Shop Verwaltung"
     *        usage: "/shop <create|add|list>"
     */
    public static class SubcommandTemplate implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cDieser Befehl kann nur von Spielern genutzt werden!");
                return true;
            }
            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage("§c✗ Verwendung: /mycommand <subcommand>");
                return true;
            }

            String subcommand = args[0].toLowerCase();

            switch (subcommand) {
                case "create":
                    // Logik für /mycommand create
                    player.sendMessage("§a✓ Create ausgeführt!");
                    break;

                case "add":
                    // Logik für /mycommand add
                    if (args.length < 2) {
                        player.sendMessage("§c✗ Verwendung: /mycommand add <name>");
                        return true;
                    }
                    String name = args[1];
                    player.sendMessage("§a✓ Hinzugefügt: " + name);
                    break;

                case "list":
                    // Logik für /mycommand list
                    player.sendMessage("§a✓ Liste:");
                    player.sendMessage("  - Item 1");
                    player.sendMessage("  - Item 2");
                    break;

                default:
                    player.sendMessage("§c✗ Unbekannter Subbefehl!");
                    break;
            }
            return true;
        }
    }

    /**
     * TEMPLATE 3: COMMAND MIT ARGUMENTEN UND FEHLERBEHANDLUNG
     * 
     * Beispiel: /home set <name>
     * 
     * Verwendung:
     * 1. Neue Datei erstellen: HomeCommand.java
     * 2. Diesen Code kopieren
     * 3. Argumente und Validierung anpassen
     */
    public static class ArgumentTemplate implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cDieser Befehl kann nur von Spielern genutzt werden!");
                return true;
            }
            Player player = (Player) sender;

            // Argument-Validierung
            if (args.length < 1) {
                player.sendMessage("§c✗ Verwendung: /mycommand <argument>");
                return true;
            }

            String argument = args[0];

            // Argumente parsen
            try {
                int value = Integer.parseInt(args[1]);
                player.sendMessage("§a✓ Wert: " + value);
            } catch (NumberFormatException e) {
                player.sendMessage("§c✗ Das muss eine Zahl sein!");
                return true;
            } catch (ArrayIndexOutOfBoundsException e) {
                player.sendMessage("§c✗ Zu wenige Argumente!");
                return true;
            }

            return true;
        }
    }

    /**
     * TEMPLATE 4: COMMAND MIT PERMISSION CHECK
     * 
     * Beispiel: /spawn set (nur mit plugin.spawn.set)
     * 
     * Verwendung:
     * 1. Neue Datei erstellen: SpawnCommand.java
     * 2. Diesen Code kopieren
     * 3. Permission-Node anpassen
     */
    public static class PermissionTemplate implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cDieser Befehl kann nur von Spielern genutzt werden!");
                return true;
            }
            Player player = (Player) sender;

            // Permission Check
            if (!player.hasPermission("plugin.mycommand.use")) {
                player.sendMessage("§c✗ Du darfst diesen Befehl nicht benutzen!");
                return true;
            }

            // Nur Ops oder mit spezifischer Permission
            if (args.length > 0 && args[0].equals("admin")) {
                if (!player.hasPermission("plugin.mycommand.admin")) {
                    player.sendMessage("§c✗ Du darfst das nicht!");
                    return true;
                }
                player.sendMessage("§a✓ Admin-Aktion ausgeführt!");
            } else {
                player.sendMessage("§a✓ Aktion ausgeführt!");
            }

            return true;
        }
    }

    /**
     * ANLEITUNG: Wie erstelle ich einen neuen Command?
     * 
     * SCHRITT 1: Java-Datei erstellen
     * --------------------------------
     * - Neue Datei in src/main/java/de/paperserver/plugin/commands/
     * - Name: MeinCommand.java
     * 
     * SCHRITT 2: Code von Template kopieren
     * ------————————————————————————————
     * - Wähle das passende Template oben aus (Simple, Subcommand, Argument, Permission)
     * - Kopiere den Code in deine neue Datei
     * - Ersetze "MyCommand" durch deinen Command-Namen
     * 
     * SCHRITT 3: In plugin.yml registrieren
     * —————————————————————————————————————
     * Öffne src/main/resources/plugin.yml und füge am Ende ein:
     * 
     * commands:
     *   meincommand:
     *     description: "Beschreibung deines Commands"
     *     usage: "/meincommand <argumente>"
     *     aliases: [mc]  # Optionale Kurz-Befehle
     *     permission: plugin.meincommand.use  # Permission
     * 
     * SCHRITT 4: In Main-Klasse registrieren
     * ———————————————————————————————————————
     * Öffne PaperPluginSuite.java und füge in onEnable() ein:
     * 
     * MeinCommand meinCommand = new MeinCommand(manager);
     * Objects.requireNonNull(this.getCommand("meincommand")).setExecutor(meinCommand);
     * 
     * SCHRITT 5: Kompilieren und testen
     * —————————————————————————————————
     * mvn clean package -U
     * Dann starte den Server und teste: /meincommand
     * 
     * 
     * BEISPIEL: /tip command erstellen
     * =================================
     * 
     * 1. Datei: TipCommand.java
     * 
     * public class TipCommand implements CommandExecutor {
     *     private final PaperPluginSuite plugin;
     *     
     *     public TipCommand(PaperPluginSuite plugin) {
     *         this.plugin = plugin;
     *     }
     *     
     *     @Override
     *     public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
     *         if (!(sender instanceof Player)) return true;
     *         Player player = (Player) sender;
     *         
     *         if (args.length < 1) {
     *             player.sendMessage("§c✗ Verwendung: /tip <betrag>");
     *             return true;
     *         }
     *         
     *         try {
     *             double amount = Double.parseDouble(args[0]);
     *             plugin.getEconomy().depositPlayer(player, amount);
     *             player.sendMessage("§a✓ Trinkgeld erhalten: " + amount);
     *         } catch (NumberFormatException e) {
     *             player.sendMessage("§c✗ Das muss eine Zahl sein!");
     *         }
     *         return true;
     *     }
     * }
     * 
     * 2. plugin.yml:
     * 
     * commands:
     *   tip:
     *     description: "Gib jemandem Trinkgeld"
     *     usage: "/tip <betrag>"
     *     permission: plugin.tip.use
     * 
     * 3. PaperPluginSuite.java (onEnable):
     * 
     * TipCommand tipCommand = new TipCommand(this);
     * Objects.requireNonNull(this.getCommand("tip")).setExecutor(tipCommand);
     */

}
