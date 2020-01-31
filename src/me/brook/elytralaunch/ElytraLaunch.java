package me.brook.elytralaunch;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class ElytraLaunch extends JavaPlugin implements Listener {

	private double directionMultiplier;
	private Vector additionDirection;

	private String message;

	@Override
	public void onEnable() {
		loadConfig();
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("elytrareload").setExecutor(this);
	}

	private void loadConfig() {
		Configuration config = new Configuration(this, "config.yml");

		ConfigurationSection section = config.getConfigurationSection("Settings.launch");

		message = section.getString("launchMessage", "&bwoosh!");

		directionMultiplier = section.getDouble("directionalMultiplier", 2.0);

		section = section.getConfigurationSection("additionalVector");
		double x = section.getDouble("x", 0.0);
		double y = section.getDouble("y", 1.0);
		double z = section.getDouble("z", 0.0);
		additionDirection = new Vector(x, y, z);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(sender.hasPermission("ElytraLaunch.reload")) {
			loadConfig();
			sender.sendMessage(color("&2Reloaded!"));
		}

		return true;
	}

	@EventHandler
	public void onFallDamage(EntityToggleGlideEvent event) {
		if(event.isGliding()) {
			Entity entity = event.getEntity();
			if(entity.hasPermission("ElytraLaunch.launch")) {
				Vector velocity = entity.getVelocity();
				velocity.add(entity.getLocation().getDirection().multiply(directionMultiplier));
				velocity.add(additionDirection);
				entity.setVelocity(velocity);
				if(!message.trim().isEmpty()) {
					entity.sendMessage(color(message));
				}
			}
		}
	}

	private String color(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

}
