package edu.hu.tosad;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AttributeCompareRule implements RuleInterface {
	private int id;
	private String name;
	private String entity;
	private String attribute;
	private String operator;
	private String comparer;
	private String MySQLTemplate;
	private String OracleSQLTemplate;

	AttributeCompareRule(int id, String name, String entity, String attribute, String operator, String comparer) {
		this.id = id;
		this.name = name.toLowerCase();
		this.entity = entity;
		this.attribute = attribute;
		this.operator = operator;
		this.comparer = comparer;
		this.MySQLTemplate = getClass().getResource("/../templates/mysql/acmp.template").getPath();
		this.OracleSQLTemplate = getClass().getResource("/../templates/oraclesql/acmp.template").getPath();
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
			content = content.replaceAll("##ATTRIBUTE##", this.attribute);
			content = content.replaceAll("##OPERATOR##", this.operator);
			content = content.replaceAll("##COMPARER##", this.comparer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
}
