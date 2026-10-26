package com.velvet.rest.common.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * The Class FreemarkerUtils.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class FreemarkerUtils {

	@Autowired
	private static Configuration cnfg;

	private FreemarkerUtils() {

	}

	/** The Constant _LOGGER. */
	private static final AppLogger _LOGGER = LoggingManager.getLogger(FreemarkerUtils.class);

	/**
	 * Gets the resolved text.
	 *
	 * @param template    the template
	 * @param dataContext the data context
	 * @return the resolved text
	 */
	public static String getResolvedText(String template, Map<String, Object> dataContext) {
		try {
			if (dataContext == null) {
				dataContext = new HashMap<>();
			}
			final Template fTemplate = new Template("name", new StringReader(template), cnfg);
			final StringWriter writer = new StringWriter();
			fTemplate.process(dataContext, writer);
			final String value = writer.toString();
			writer.close();
			return value;
		} catch (final Exception e) {
			_LOGGER.error("Error Processing Template : Returning : " + template);
			return template;
		}
	}

	/**
	 * <p>
	 * getResolvedText.
	 * </p>
	 *
	 * @param templateReference a {@link java.lang.Class} object.
	 * @param templateName      a {@link java.lang.String} object.
	 * @param dataContext       a {@link java.util.Map} object.
	 * @return a {@link java.lang.String} object.
	 */
	@SuppressWarnings("rawtypes")
	public static String getResolvedText(Class templateReference, String templateName, Map<String, Object> dataContext) {

		final StringWriter out = new StringWriter();
		try {
			if (dataContext == null) {
				dataContext = new HashMap<>();
			}
			cnfg.setClassForTemplateLoading(templateReference, "/");
			cnfg.setDefaultEncoding("UTF-8");
			final Template template = cnfg.getTemplate(templateName);
			template.process(dataContext, out);

		} catch (final Exception e) {
			_LOGGER.error("Error Processing Template :: " + templateName);
		}
		return out.getBuffer().toString();
	}

}
