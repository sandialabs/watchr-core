/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

import java.io.IOException;

import gov.sandia.watchr.graph.chartreuse.generator.PlotGeneratorCommandWrapper;
import gov.sandia.watchr.graph.chartreuse.PlotToken;

public class TokenStringUtil {
	private static final String TOKEN_IDENTIFIER = "$";
	private static final String TOKEN_IDENTIFIER_REGEX = "\\$";

	public static String findAndReplaceToken(
            String origin, PlotToken token, int tokenLevel, PlotGeneratorCommandWrapper command) throws IOException {
                
		String findToken = toTokenString(token, tokenLevel, TOKEN_IDENTIFIER);
		if(origin.contains(findToken)) {
			String generatedString = command.generate();
			String regexToken = toTokenString(token, tokenLevel, TOKEN_IDENTIFIER_REGEX);
			return origin.replaceAll(regexToken, generatedString);
		}
		return origin;
	}
	
	private static String toTokenString(PlotToken token, int level, String tokenIdentifier) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < level; i ++) {
			sb.append(tokenIdentifier);
		}
		sb.append(token.toString());
		return sb.toString();
	}
}
