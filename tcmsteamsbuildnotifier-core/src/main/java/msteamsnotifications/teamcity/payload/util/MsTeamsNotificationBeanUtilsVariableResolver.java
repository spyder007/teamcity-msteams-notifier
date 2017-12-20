package msteamsnotifications.teamcity.payload.util;

import org.apache.commons.beanutils.PropertyUtils;
import msteamsnotifications.teamcity.Loggers;
import msteamsnotifications.teamcity.payload.util.TemplateMatcher.VariableResolver;

import java.lang.reflect.InvocationTargetException;

/**
 * This is a VariableResolver for the TemplateMatcher
 * 
 * It resolves the values of variables from javaBean objects using 
 * org.apache.commons.beanutils.PropertyUtils 
 * 
 * @author NetWolfUK
 *
 */

public class MsTeamsNotificationBeanUtilsVariableResolver implements VariableResolver {
	
	private static final String EXCEPTION_MESSAGE = " thrown when trying to resolve value for ";
	
	Object bean;
	
	public MsTeamsNotificationBeanUtilsVariableResolver(Object javaBean) {
		this.bean = javaBean;
	}
	
	@Override
	public String resolve(String variableName) {
		String value = "UNRESOLVED";
		try {
			value = (String) PropertyUtils.getProperty(bean, variableName);
		} catch (IllegalAccessException e) {
			Loggers.SERVER.warn(this.getClass().getSimpleName() + " :: " + e.getClass() + EXCEPTION_MESSAGE + variableName); 
			Loggers.SERVER.debug(e);
		} catch (InvocationTargetException e) {
			Loggers.SERVER.warn(this.getClass().getSimpleName() + " :: " + e.getClass() + EXCEPTION_MESSAGE + variableName); 
			Loggers.SERVER.debug(e);
		} catch (NoSuchMethodException e) {
			Loggers.SERVER.warn(this.getClass().getSimpleName() + " :: " + e.getClass() + EXCEPTION_MESSAGE + variableName); 
			Loggers.SERVER.debug(e);
		}
		return value;
		
	}

}
