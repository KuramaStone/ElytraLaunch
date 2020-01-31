package me.brook.elytralaunch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/*
 * Heyo! This little class was made by me, Brook! Or just KuramaStone if ya wanna find me. Use this however ya want, fam.
 * I'm just writing this because I'm proud that I finally made this after a long time of wanting to.
 */
public class BrookConfiguration extends YamlConfiguration {

	/*
	 * comments.put("path.prior.to.comment", "comment")
	 */
	protected final Map<String, List<String>> comments;
	protected final List<String> paths;

	public BrookConfiguration() {
		comments = new HashMap<>();
		paths = new ArrayList<>();
	}

	protected boolean doesPathExist(String path) {
		return paths.contains(path);
	}

	@Override
	public String saveToString() {
		String contents = super.saveToString();

		List<String> list = new ArrayList<>();
		Collections.addAll(list, contents.split("\n"));


		StringBuilder sb = new StringBuilder();

		for(Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String line = iterator.next();
			sb.append(line);
			sb.append('\n');

			if(!line.isEmpty()) {
				if(line.contains(":")) {

					String path = getKeyFromLine(line);
					if(comments.containsKey(path)) {
						comments.get(path).forEach(string -> {
							sb.append(string);
							sb.append('\n');
						});
					}
				}
			}
		}

		return sb.toString();
	}

	@Override
	public void loadFromString(String contents) throws InvalidConfigurationException {
		super.loadFromString(contents);
		addPaths();

		List<String> list = new ArrayList<>();
		Collections.addAll(list, contents.split("\n"));

		String currentPath = "";

		for(Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String line = iterator.next();

			String trimmed = line.trim();
			if(trimmed.startsWith("##") || trimmed.isEmpty()) {
				addCommentLine(currentPath.toString(), line);
				continue;
			}

			if(!line.isEmpty()) {
				if(line.contains(":")) {
					String segmentPath = getKeyFromLine(line);
					currentPath = segmentPath;
				}
			}
		}
	}

	private void addPaths() {
		paths.clear();
		getKeys(true).forEach(path -> paths.add(path));
	}

	private void addCommentLine(String currentPath, String line) {

		List<String> list = comments.get(currentPath);
		if(list == null) {
			list = new ArrayList<>();
		}
		list.add(line);

		comments.put(currentPath, list);
	}

	private String getKeyFromLine(String line) {
		String key = null;

		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == ':') {
				key = line.substring(0, i);
				break;
			}
		}

		return key == null ? null : key.trim();
	}

}
