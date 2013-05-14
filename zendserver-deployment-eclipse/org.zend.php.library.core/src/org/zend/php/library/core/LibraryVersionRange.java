/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.core;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class LibraryVersionRange {

	private enum Relation {
		EQUAL("="), //$NON-NLS-1$

		GREATER(">"), //$NON-NLS-1$

		LESS("<"), //$NON-NLS-1$

		GREATER_EQUAL(">="), //$NON-NLS-1$

		LESS_EQUAL("<="), //$NON-NLS-1$

		APPROX("~"), //$NON-NLS-1$

		UNKNOWN(null);

		private String symbol;

		private Relation(String symbol) {
			this.symbol = symbol;
		}

		public String getSymbol() {
			return symbol;
		}

		public static Relation bySymbol(String symbol) {
			if (symbol == null) {
				return UNKNOWN;
			}
			Relation[] values = values();
			for (Relation relation : values) {
				if (symbol.equals(relation.getSymbol())) {
					return relation;
				}
			}
			return UNKNOWN;
		}
	}

	private LibraryVersion downVersion;
	private LibraryVersion upVersion;
	private Relation downRelation;
	private Relation upRelation;

	public static LibraryVersionRange getRange(String input) {
		LibraryVersionRange range = new LibraryVersionRange();
		String[] segments = input.split(","); //$NON-NLS-1$
		if (segments.length > 0) {
			String down = null;
			String up = null;
			down = segments[0];
			if (segments.length == 2) {
				up = segments[1];
			}
			range.setDownVersion(parseVersion(down));
			range.setDownRelation(parseRelation(down));
			if (up != null) {
				range.setUpVersion(parseVersion(up));
				range.setUpRelation(parseRelation(up));
			}
		}
		return range;
	}

	public boolean isInRange(LibraryVersion version) {
		int downResult = version.compareTo(downVersion);

		// TODO handle approx

		// e.g. <2.0.0
		if (downRelation == Relation.LESS && downResult < 0) {
			return true;
		}
		// e.g. <=2.0.0
		if (downRelation == Relation.LESS_EQUAL && downResult <= 0) {
			return true;
		}
		// e.g. =2.0.0
		if (downRelation == Relation.EQUAL && downResult == 0) {
			return true;
		}
		if ((downRelation == Relation.GREATER && downResult > 0)
				|| (downRelation == Relation.GREATER_EQUAL && downResult >= 0)) {
			if (upVersion == null) {
				return true;
			}
			int upResult = version.compareTo(upVersion);
			// e.g. <2.0.0
			if (upRelation == Relation.LESS && upResult < 0) {
				return true;
			}
			// e.g. <=2.0.0
			if (upRelation == Relation.LESS_EQUAL && upResult <= 0) {
				return true;
			}
		}
		return false;
	}

	private static LibraryVersion parseVersion(String down) {
		if (down.startsWith(Relation.GREATER_EQUAL.getSymbol())
				|| down.startsWith(Relation.LESS_EQUAL.getSymbol())) {
			return LibraryVersion.byName(down.substring(2));
		}
		if (down.startsWith(Relation.GREATER.getSymbol())
				|| down.startsWith(Relation.LESS.getSymbol())
				|| down.startsWith(Relation.APPROX.getSymbol())) {
			return LibraryVersion.byName(down.substring(1));
		}
		return LibraryVersion.byName(down);
	}

	private static Relation parseRelation(String down) {
		if (down.startsWith(Relation.GREATER_EQUAL.getSymbol())
				|| down.startsWith(Relation.LESS_EQUAL.getSymbol())) {
			return Relation.bySymbol(down.substring(0, 2));
		}
		if (down.startsWith(Relation.GREATER.getSymbol())
				|| down.startsWith(Relation.LESS.getSymbol())
				|| down.startsWith(Relation.APPROX.getSymbol())) {
			return Relation.bySymbol(down.substring(0, 1));
		}
		return Relation.EQUAL;
	}

	protected void setDownVersion(LibraryVersion downVersion) {
		this.downVersion = downVersion;
	}

	protected void setUpVersion(LibraryVersion upVersion) {
		this.upVersion = upVersion;
	}

	protected void setDownRelation(Relation downRelation) {
		this.downRelation = downRelation;
	}

	protected void setUpRelation(Relation upRelation) {
		this.upRelation = upRelation;
	}

}
