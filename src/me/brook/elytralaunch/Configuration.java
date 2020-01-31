package me.brook.elytralaunch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration extends BrookConfiguration {

	private String fileName;
	private JavaPlugin plugin;
	private File file;

	public Configuration(JavaPlugin jp, String name) {
		this.plugin = jp;
		this.fileName = name.endsWith(".yml") ? name : name + ".yml";

		loadFile();
		createData();

		try {
			loadConfig();
		}
		catch(IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public boolean injectComment(String previousPath, String comment) {

		List<String> list = comments.getOrDefault(previousPath, new ArrayList<>());

		if(!comment.startsWith("#")) {
			comment = "#" + comment;
		}

		list.add(comment);

		if(!list.contains(comment)) {
			comments.put(previousPath, list);
			return true;
		}

		return false;
	}

	@Override
	public Object get(String path, Object def) {
		Object value = super.get(path, def);

		// Returned the default, so save value to config
		if(!doesPathExist(path) && def != null) {
			this.set(path, def);
			this.saveData();
		}

		return value;
	}

	private void loadConfig() throws FileNotFoundException, IOException, InvalidConfigurationException {
		this.load(file);
	}

	public File loadFile() {
		this.file = new File(this.plugin.getDataFolder(), this.fileName);
		return this.file;
	}

	public void saveData() {
		this.file = new File(this.plugin.getDataFolder(), this.fileName);
		try {
			this.save(this.file);
		}
		catch(IOException e) {
			e.printStackTrace();
			System.out.println("Attempting to fix error...");
			createData();
			saveData();
		}
	}

	@Override
	public void save(File file) throws IOException {
		super.save(file);
	}

	public void createData() {
		if(!file.exists()) {
			if(!this.plugin.getDataFolder().exists()) {
				this.plugin.getDataFolder().mkdirs();
			}

			// If file isn't a resource, create from scratch
			if(this.plugin.getResource(this.fileName) == null) {
				try {
					this.file.createNewFile();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			else {
				this.plugin.saveResource(this.fileName, false);
			}
		}
	}

	public void delete() {
		if(this.file.exists()) {
			this.file.delete();
		}
	}

	public static void main(String[] args) {
		Configuration cfg = new Configuration((JavaPlugin) null, "config.yml");

		System.out.println(cfg.getInt("Test.path.integer"));

		cfg.set("Test.path.integer", 99);
		cfg.saveData();
	}

}
