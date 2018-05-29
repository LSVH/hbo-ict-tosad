package edu.hu.tosad;

public class Comparison {
	private String value;
	private String column;

	public Comparison(String value, String column) {
		this.column = column;
	}

	@Override
	public String toString() {
		return this.value != null || !this.value.trim().isEmpty() ? "\'"+this.value+"\'" : this.column;
	}
}
