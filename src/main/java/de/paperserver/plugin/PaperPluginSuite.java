package de.paperserver.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import de.paperserver.plugin.commands.*;
import de.paperserver.plugin.listeners.*;
import de.paperserver.plugin.managers.*;
import de.paperserver.plugin.utils.Logger;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;

public class PaperPluginSuite extends JavaPlugin {

    private static PaperPluginSuite instance;
    private Economy economy;
    private LuckPerms luckPerms;

    private RTPManager rtpManager;
    private NPCManager npcManager;
    private AuctionManager auctionManager;
    private ShopManager shopManager;
    private SpawnManager spawnManager;
    private HomeManager homeManager;
    private TPAManager tpaManager;
    private CrateManager crateManager;
    private MoneyScoreboardManager moneyScoreboardManager;

    @Override
    public void onEnable() {
        instance = this;
        Logger.info("§a========== PaperPluginSuite wird aktiviert ==========");

        // Konfigurationen laden
        saveDefaultConfig();
        loadConfigurations();

        // Economy und Permissions Setup
        if (!setupEconomy()) {
            Logger.warn("§eEconomy-Features werden deaktiviert.");
        }

        if (!setupLuckPerms()) {
            Logger.warn("§eLuckPerms nicht gefunden! Permission-System wird auf Standard zurückfallen.");
        }

        // Manager initialisieren
        initializeManagers();

        // Commands registrieren
        registerCommands();

        // Listener registrieren
        registerListeners();

        Logger.info("§a========== PaperPluginSuite erfolgreich aktiviert ==========");
    }

    @Override
    public void onDisable() {
        Logger.info("§c========== PaperPluginSuite wird deaktiviert ==========");
        
        // Manager speichern
        if (homeManager != null) homeManager.saveHomes();
        if (auctionManager != null) auctionManager.saveAuctions();
        if (shopManager != null) shopManager.saveShops();
        
        Logger.info("§cPaperPluginSuite wurde deaktiviert.");
    }

    private void loadConfigurations() {
        // Alle Konfigurationen laden
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null && getServer().getPluginManager().getPlugin("VaultUnlocked") == null) {
            Logger.error("§cVault (oder VaultUnlocked) nicht gefunden! Bitte installiere es.");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager()
                .getRegistration(Economy.class);
        if (rsp == null) {
            Logger.error("§cVault gefunden, aber kein Economy-Plugin (z.B. EssentialsX) erkannt!");
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    private boolean setupLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit
                .getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            return true;
        }
        return false;
    }

    private void initializeManagers() {
        try {
            rtpManager = new RTPManager(this);
            npcManager = new NPCManager(this);
            auctionManager = new AuctionManager(this);
            shopManager = new ShopManager(this);
            spawnManager = new SpawnManager(this);
            homeManager = new HomeManager(this);
            tpaManager = new TPAManager(this);
            crateManager = new CrateManager(this);
            moneyScoreboardManager = new MoneyScoreboardManager(this);
            Logger.info("§a✓ Alle Manager initialisiert.");
        } catch (Exception e) {
            Logger.error("§cKritischer Fehler beim Initialisieren der Manager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void registerCommands() {
        if (rtpManager != null) getCommand("rtp").setExecutor(new RTPCommand(rtpManager));
        if (rtpManager != null) getCommand("rtp").setTabCompleter(new de.paperserver.plugin.commands.CommandSuggestions(this));
        if (npcManager != null) getCommand("npc").setExecutor(new NPCCommand(npcManager));
        if (npcManager != null) getCommand("npc").setTabCompleter(new de.paperserver.plugin.commands.CommandSuggestions(this));
        if (auctionManager != null) getCommand("auction").setExecutor(new AuctionCommand(auctionManager));
        if (auctionManager != null) getCommand("auction").setTabCompleter(new de.paperserver.plugin.commands.CommandSuggestions(this));
        if (shopManager != null) getCommand("shop").setExecutor(new ShopCommand(shopManager));
        if (shopManager != null) getCommand("shop").setTabCompleter(new de.paperserver.plugin.commands.CommandSuggestions(this));
        if (spawnManager != null) getCommand("spawn").setExecutor(new SpawnCommand(spawnManager));
        if (spawnManager != null) getCommand("spawn").setTabCompleter(new de.paperserver.plugin.commands.CommandSuggestions(this));
        
        if (homeManager != null) {
            getCommand("homes").setExecutor(new HomeCommand(homeManager));
            getCommand("sethome").setExecutor(new SetHomeCommand(homeManager));
            getCommand("delhome").setExecutor(new DelHomeCommand(homeManager));
        }
        
        if (tpaManager != null) {
            getCommand("tpa").setExecutor(new TPACommand(tpaManager));
            getCommand("tpa").setTabCompleter(new de.paperserver.plugin.commands.CommandSuggestions(this));
            getCommand("tpahere").setExecutor(new TPAHereCommand(tpaManager));
            getCommand("tpahere").setTabCompleter(new de.paperserver.plugin.commands.CommandSuggestions(this));
            getCommand("tpaccept").setExecutor(new TPAAcceptCommand(tpaManager));
            getCommand("tpaccept").setTabCompleter(new de.paperserver.plugin.commands.CommandSuggestions(this));
            getCommand("tpdeny").setExecutor(new TPADenyCommand(tpaManager));
            getCommand("tpdeny").setTabCompleter(new de.paperserver.plugin.commands.CommandSuggestions(this));
        }
        
        if (crateManager != null) getCommand("crate").setExecutor(new CrateCommand(crateManager));
        if (crateManager != null) getCommand("crate").setTabCompleter(new de.paperserver.plugin.commands.CommandSuggestions(this));
        
        if (economy != null) getCommand("sell").setExecutor(new SellCommand(this));

        Logger.info("§a✓ Verfügbare Commands registriert.");
    }

    private void registerListeners() {
        // RTP Listener
        if (rtpManager != null) getServer().getPluginManager().registerEvents(new RTPListener(rtpManager), this);

        // Home Listener
        if (homeManager != null) getServer().getPluginManager().registerEvents(new HomeListener(homeManager), this);

        // TPA Listener
        if (tpaManager != null) getServer().getPluginManager().registerEvents(new TPAListener(tpaManager), this);

        // Crate Listener
        if (crateManager != null) getServer().getPluginManager().registerEvents(new CrateListener(crateManager), this);

        // Auction Listener
        if (auctionManager != null) getServer().getPluginManager().registerEvents(new AuctionListener(this), this);

        // Shop Listener
        if (shopManager != null) getServer().getPluginManager().registerEvents(new ShopListener(this), this);

        // NPC Listener
        getServer().getPluginManager().registerEvents(new NPCListener(npcManager), this);
        
        // Join Listener: show commands on join
        getServer().getPluginManager().registerEvents(new de.paperserver.plugin.listeners.JoinListener(this), this);

        Logger.info("§a✓ Alle Listener registriert.");
    }

    public static PaperPluginSuite getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return economy;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public RTPManager getRTPManager() {
        return rtpManager;
    }

    public NPCManager getNPCManager() {
        return npcManager;
    }

    public AuctionManager getAuctionManager() {
        return auctionManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public TPAManager getTPAManager() {
        return tpaManager;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public MoneyScoreboardManager getMoneyScoreboardManager() {
        return moneyScoreboardManager;
    }
}