package msteamsnotifications.teamcity.payload.util;

import msteamsnotifications.teamcity.payload.util.TemplateMatcher.VariableResolver;

public class VariableMessageBuilder {
	static final String VAR_START = "${";
	static final String VAR_END = "}";
	
	String template;
	VariableResolver resolver;
	TemplateMatcher matcher;
	
	public static VariableMessageBuilder create(final String template, VariableResolver resolver){
		VariableMessageBuilder builder = new VariableMessageBuilder();
		builder.template = template;
		builder.resolver = resolver;
		builder.matcher = new TemplateMatcher(VAR_START, VAR_END);
		return builder;
	}

	public String build(){
		return matcher.replace(template, resolver);
	}
	
}
