package edu.hu.tosad;

import java.util.ArrayList;
import java.util.Collection;

public class Rule {
	private String slug;
	private String type;
	private String category;
	private String operator;
	private String table;
	private String column;
	private String template;
	private Database database;
	private ArrayList<Comparison> comparisons;

	public Rule(String slug, String type, String category, String operator, String table, String column, String template, Database database, ArrayList<Comparison> comparisons) {
		this.slug = slug;
		this.type = type;
		this.category = category;
		this.operator = operator;
		this.table = table;
		this.column = column;
		this.template = template;
		this.database = database;
		this.comparisons = comparisons;
	}

	public String getProperty(String name) {
		String output = null;
		try {
			Object field = getClass().getField(name).get(this);
			if (field instanceof Collection) {
				StringBuilder sb = new StringBuilder();
				for (Object f: (ArrayList) field) {
					sb.append(f.toString());
					sb.append(", ");
				}
				output = sb.toString();
			} else {
				output = field.toString();
			}
		} catch (NoSuchFieldException nsfe) {
			nsfe.printStackTrace();
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		}
		return output;
	}

	public String getTemplate() {
		return template;
	}
}
