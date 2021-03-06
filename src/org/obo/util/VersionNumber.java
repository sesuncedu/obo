package org.obo.util;

import java.text.ParseException;
import java.util.Scanner;
import java.util.regex.MatchResult;

import org.bbop.util.StringUtil;

import org.apache.log4j.*;

public class VersionNumber implements Comparable<VersionNumber> {

	//initialize logger
	protected final static Logger logger = Logger.getLogger(VersionNumber.class);

	protected int majorVersion;

	protected int minorVersion;

	// In case the version number has another place, e.g. 2.1.3
	protected int subminorVersion = -999;

	protected int betaVersion;
	
	//release candidate version
	protected int rcVersion;

	public VersionNumber(String str) throws ParseException {
		Scanner s = new Scanner(str);
		try {
			s.findInLine("(\\d+)");
			MatchResult result = s.match();
			majorVersion = Integer.parseInt(result.group(1));
		} catch (IllegalStateException ex) {
			throw new ParseException(str, -1);
		}
		try {
 			s.findInLine(".(\\d+)");
			MatchResult result = s.match();
			minorVersion = Integer.parseInt(result.group(1));
		} catch (IllegalStateException ex) {
			throw new ParseException(str, -1);
		}
 		try {
		    // Need to leave s alone in case there's a beta or rc suffix
		    Scanner s2 = s;
 			s2.findInLine("\\.(\\d+)");
 			MatchResult result = s2.match();
 			subminorVersion = Integer.parseInt(result.group(1));
 		} catch (Exception ex) {
 		    // It's ok if there's not a subminor version; just be quiet
 		}
		try {
			s.findInLine("-beta(\\d+)");
			MatchResult result = s.match();
			betaVersion = Integer.parseInt(result.group(1));
		} catch (IllegalStateException ex) {
		}
		try {
			s.findInLine("-b(\\d+)");
			MatchResult result = s.match();
			betaVersion = Integer.parseInt(result.group(1));
		} catch (IllegalStateException ex) {
		}
		try {
			s.findInLine("-rc(\\d+)");
			MatchResult result = s.match();
			rcVersion = Integer.parseInt(result.group(1));
		} catch (IllegalStateException ex) {
		}

		s.close();
	}

	public String toString() {
		return majorVersion + "."
		    + StringUtil.pad(minorVersion + "", '0', 1, true)
		    + ((subminorVersion == -999) ? "" : "." + subminorVersion)
		    + (isRC() ? "-rc" +rcVersion : "")
		    + (isBeta() ? "-beta" + betaVersion : "");
	}

	public boolean isBeta() {
		return betaVersion > 0;
	}

	public int getBetaVersion() {
		return betaVersion;
	}
	
	public boolean isRC() {
		return rcVersion > 0;
	}
	
	public int getRCVersion() {
		return rcVersion;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public int hashCode() {
		return getMajorVersion() * 100000 + getMinorVersion() * 100
		+ getRCVersion();
//		return getMajorVersion() * 100000 + getMinorVersion() * 100
//				+ getBetaVersion();
	}

	public boolean equals(Object o) {
		if (o instanceof VersionNumber) {
			VersionNumber vn = (VersionNumber) o;
			return vn.getRCVersion() == getRCVersion()
			&& vn.getMajorVersion() == getMajorVersion()
			&& vn.getMinorVersion() == getMinorVersion();
//			return vn.getBetaVersion() == getBetaVersion()
//					&& vn.getMajorVersion() == getMajorVersion()
//					&& vn.getMinorVersion() == getMinorVersion();
		} else
			return false;
	}

	public int compareTo(VersionNumber o) {
		int majorDiff = o.getMajorVersion() - getMajorVersion();
		int minorDiff = o.getMinorVersion() - getMinorVersion();
		int rcDiff = o.getRCVersion() - getRCVersion();
//		int betaDiff = o.getBetaVersion() - getBetaVersion();
		return majorDiff * 100000 + minorDiff * 100 + rcDiff;
	}
}
