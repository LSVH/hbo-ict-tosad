package edu.hu.tosad;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AttributeInterRule implements RuleInterface {
	private int id;
	private String name;
	private String entity1;
	private String attribute1;
	private String operator;
	private String entity2;
	private String attribute2;
	private String timing;
	private String action;
	private boolean foreach;
	private StringBuilder joins;
	private String MySQLTemplate;
	private String OracleSQLTemplate;

	AttributeInterRule(int id, String name, String entity1, String attribute1, String operator, String entity2, String attribute2, String timing, String action, boolean foreach, String fkeys, String rkeys) {
		this.id = id;
		this.name = name.toLowerCase();
		this.entity1 = entity1;
		this.attribute1 = attribute1;
		this.operator = operator;
		this.entity2 = entity2;
		this.attribute2 = attribute2;
		this.timing = timing.toUpperCase();
		this.action = action.toUpperCase();
		this.foreach = foreach;
		this.joins = new StringBuilder();
		String[] foreignKeys = fkeys.split(",");
		String[] referenceKeys = rkeys.split(",");

		for (int i = 0; i < foreignKeys.length; i++) {
			if (referenceKeys.length <= i && referenceKeys[i] == null) {
				continue;
			}
			if (i > 0) {
				this.joins.append(" AND ");
			}
			this.joins.append("x.").append(foreignKeys[i]).append("=y.").append(referenceKeys[i]);
		}
		this.MySQLTemplate = getClass().getResource("/../templates/mysql/icmp.template").getPath();
		this.OracleSQLTemplate = getClass().getResource("/../templates/oraclesql/icmp.template").getPath();
	}

	public String toMySQL() {
		String content = this.setupTemplate(this.MySQLTemplate);
		ArrayList<String> timing = new ArrayList<String>();
		timing.add("BEFORE");
		timing.add("AFTER");

		content = content.replaceAll("##TIMING##", timing.contains(this.timing) ? this.timing : timing.get(0));
		return content;
	}

	public String toOracleSQL() {
		String content = this.setupTemplate(this.OracleSQLTemplate);
		content = content.replaceAll("##TIMING##", this.timing);
		return content;
	}

	private String setupTemplate(String file) {
		String content = null;
		try {
			Path path = Paths.get(file);
			Charset charset = StandardCharsets.UTF_8;
			content = new String(Files.readAllBytes(path), charset);
			content = content.replaceAll("##ID##", Integer.toString(this.id));
			content = content.replaceAll("##NAME##", this.name);
			content = content.replaceAll("##ENTITY1##", this.entity1);
			content = content.replaceAll("##ATTRIBUTE1##", this.attribute1);
			content = content.replaceAll("##OPERATOR##", this.operator);
			content = content.replaceAll("##ENTITY2##", this.entity2);
			content = content.replaceAll("##ATTRIBUTE2##", this.attribute2);
			content = content.replaceAll("##ACTION##", this.action);
			content = content.replaceAll("##FOREACH##", this.foreach ? "FOR EACH ROW" : "");
			content = content.replaceAll("##JOINS##", this.joins.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
}
