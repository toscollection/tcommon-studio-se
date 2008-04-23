package org.talend.dataprofiler.persistence;

// Generated Apr 23, 2008 1:33:52 PM by Hibernate Tools 3.2.0.CR1

import java.util.HashSet;
import java.util.Set;

/**
 * TdqValues generated by hbm2java
 */
public class TdqValues implements java.io.Serializable {

	private Integer valPk;
	private String valString;
	private Set<TdqIndicatorValue> tdqIndicatorValues = new HashSet<TdqIndicatorValue>(
			0);

	public TdqValues() {
	}

	public TdqValues(String valString) {
		this.valString = valString;
	}

	public TdqValues(String valString, Set<TdqIndicatorValue> tdqIndicatorValues) {
		this.valString = valString;
		this.tdqIndicatorValues = tdqIndicatorValues;
	}

	public Integer getValPk() {
		return this.valPk;
	}

	public void setValPk(Integer valPk) {
		this.valPk = valPk;
	}

	public String getValString() {
		return this.valString;
	}

	public void setValString(String valString) {
		this.valString = valString;
	}

	public Set<TdqIndicatorValue> getTdqIndicatorValues() {
		return this.tdqIndicatorValues;
	}

	public void setTdqIndicatorValues(Set<TdqIndicatorValue> tdqIndicatorValues) {
		this.tdqIndicatorValues = tdqIndicatorValues;
	}

}
