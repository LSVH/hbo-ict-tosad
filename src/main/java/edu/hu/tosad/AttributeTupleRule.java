package edu.hu.tosad;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AttributeTupleRule implements RuleInterface {
	private int id;
	private String name;
	private String entity;
	private String attribute1;
	private String operator;
	private String attribute2;
	private String MySQLTemplate;
	private String OracleSQLTemplate;

	AttributeTupleRule(int id, String name, String entity, String attribute1, String operator, String attribute2) {
		this.id = id;
		this.name = name.toLowerCase();
		this.entity = entity;
		this.attribute1 = attribute1;
		this.operator = operator;
		this.attribute2 = attribute2;
		this.MySQLTemplate = getClass().getResource("/../templates/mysql/tcmp.template").getPath();
		this.OracleSQLTemplate = getClass().getResource("/../templates/oraclesql/tcmp.template").getPath();
	}

	public String toMySQL() {
		return this.setupTemplate(this.MySQLTemplate);
	}

	public String toOracleSQL() {
		return this.setupTemplate(this.OracleSQLTemplate);
	}

	private String setupTemplate(String file) {
		String content = null;
		try {
			StringBuilder output = new StringBuilder();
			Path path = Paths.get(file);
			Charset charset = StandardCharsets.UTF_8;
			content = new String(Files.readAllBytes(path), charset);
			content = content.replaceAll("##ID##", Integer.toString(this.id));
			content = content.replaceAll("##NAME##", this.name);
			content = content.replaceAll("##ENTITY##", this.entity);
			content = content.replaceAll("##ATTRIBUTE1##", this.attribute1);
			content = content.replaceAll("##OPERATOR##", this.operator);
			content = content.replaceAll("##ATTRIBUTE2##", this.attribute2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
}
