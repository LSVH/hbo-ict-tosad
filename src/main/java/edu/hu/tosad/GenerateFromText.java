package edu.hu.tosad;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateFromText implements Generator {
	private Rule rule;
	private static final Pattern placeholders = Pattern.compile(":[A-Za-z][A-Za-z0-9_]+[A-Za-z0-9]");

	public GenerateFromText(Rule r) {
		this.rule = r;
	}

	public String generate() {
		String template = rule.getTemplate();
		Matcher m = placeholders.matcher(template);
		while (m.find()) {
			String replacement = rule.getProperty(m.group().substring(1));
			template = template.replaceAll(m.group(), replacement);
		}
		return template;
	}
}
