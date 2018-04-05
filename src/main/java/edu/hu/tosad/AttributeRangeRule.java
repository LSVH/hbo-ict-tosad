package edu.hu.tosad;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AttributeRangeRule implements RuleInterface {
	private int id;
	private String name;
	private String entity;
	private String attribute;
	private boolean negate;
	private String comparer1;
	private String comparer2;
	private String MySQLTemplate;
	private String OracleSQLTemplate;

	AttributeRangeRule(int id, String name, String entity, String attribute, boolean negate, String comparer1, String comparer2) {
		this.id = id;
		this.name = name.toLowerCase();
		this.entity = entity;
		this.attribute = attribute;
		this.negate = negate;
		this.comparer1 = comparer1;
		this.comparer2 = comparer2;
		this.MySQLTemplate = getClass().getResource("/../templates/mysql/arng.template").getPath();
		this.OracleSQLTemplate = getClass().getResource("/../templates/oraclesql/arng.template").getPath();
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
			content = content.replaceAll("##NEGATE##", this.negate ? "NOT " : "");
			content = content.replaceAll("##COMPARER1##", this.comparer1);
			content = content.replaceAll("##COMPARER2##", this.comparer2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
}
