package au.com.dius.pact.provider.junit.sysprops;

import org.apache.commons.lang3.StringUtils;

public class SystemPropertyResolver implements ValueResolver {

  @Override
  public String resolveValue(final String property) {
    PropertyValueTuple tuple = new PropertyValueTuple(property).invoke();
    String propertyValue = property;

    if (StringUtils.isNotEmpty(propertyValue)) {
      propertyValue = System.getProperty(tuple.getPropertyName());
      if (propertyValue == null) {
        propertyValue = System.getenv(tuple.getPropertyName());
      }
      if (propertyValue == null) {
        propertyValue = tuple.getDefaultValue();
      }
      if (propertyValue == null) {
        throw new RuntimeException("Could not resolve property \"" + tuple.getPropertyName()
          + "\" in the system properties or environment variables and no default value is supplied");
      }
    }

    return propertyValue;
  }

  @Override
  public boolean propertyDefined(String property) {
    String propertyValue = System.getProperty(property);
    if (propertyValue == null) {
      propertyValue = System.getenv(property);
    }
    return propertyValue != null;
  }

  public static class PropertyValueTuple {
    private String propertyName;
    private String defaultValue;

    public PropertyValueTuple(String property) {
      this.propertyName = property;
      this.defaultValue = null;
    }

    public String getPropertyName() {
      return propertyName;
    }

    public String getDefaultValue() {
      return defaultValue;
    }

    public PropertyValueTuple invoke() {
      if (StringUtils.contains(propertyName, ":")) {
        String[] kv = StringUtils.splitPreserveAllTokens(propertyName, ':');
        propertyName = kv[0];
        if (kv.length > 1) {
          defaultValue = kv[1];
        }
      }
      return this;
    }
  }
}
