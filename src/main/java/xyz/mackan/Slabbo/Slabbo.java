package xyz.mackan.Slabbo;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.mackan.Slabbo.listeners.*;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.io.File;
import java.util.logging.Logger;

public class Slabbo extends JavaPlugin {
	static {
		ConfigurationSerialization.registerClass(Shop.class, "Shop");
	}

	private static final Logger log = Logger.getLogger("Minecraft");

	private static String dataPath = null;

	private static Economy econ = null;
	private static Permission perms = null;
	private static Chat chat = null;

	private static Slabbo instance;

	public static ShopUtil shopUtil = new ShopUtil();

	@Override
	public void onEnable () {
		dataPath = this.getDataFolder().getPath();
		if (!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		setupPermissions();
		setupCommands();
		setupListeners();
		setupChat();

		new File(getDataPath()).mkdirs();

		instance = this;

		getLogger().info("Slabbo enabled.");

		shopUtil.loadShops();
	}

	@Override
	public void onDisable () { log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion())); }

	private void setupListeners () {
		getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
		getServer().getPluginManager().registerEvents(new EntityPickupItemListener(), this);
		getServer().getPluginManager().registerEvents(new ItemDespawnListener(), this);
		getServer().getPluginManager().registerEvents(new ItemMergeListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
	}

	private void setupCommands () {

	}

	private boolean setupEconomy () {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		chat = rsp.getProvider();
		return chat != null;
	}

	public static String getDataPath () {
		return dataPath;
	}

	public static Economy getEconomy() {
		return econ;
	}

	public static Permission getPermissions() {
		return perms;
	}

	public static Chat getChat () { return chat; }

	public static Slabbo getInstance() { return instance; }
}
