package com.velvet.rest.common.config;

import java.util.Map;

/**
 * The Class ExternalApplicationConfig.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class ExternalApplicationConfig {

    /** The active profile. */
    private String activeProfile;

    /** The cache config. */
    private CacheConfig cacheConfig;
    
    /** The elasticsearch config */
    private ESConfig es;

	/** The dbserver. */
    private DBServer dbserver;
    
    /** The swagger host. */
    private String swaggerHost;
    
    
    private String swaggerHostUat;
    
    
    /** The app ports. */
    private Map<String, String> appPorts;
    
    private Map<String, String> sqsUrls;

    /**
     * Gets the cache config.
     *
     * @return the cache config
     */
    public CacheConfig getCacheConfig() {
        return cacheConfig;
    }
    

    /**
     * Sets the cache config.
     *
     * @param cacheConfig the new cache config
     */
    public void setCacheConfig(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }

    
    /**
     * Gets the swagger host.
     *
     * @return the swagger host
     */
    public String getSwaggerHost() {
        return swaggerHost;
    }

    
    /**
     * Sets the swagger host.
     *
     * @param swaggerHost the new swagger host
     */
    public void setSwaggerHost(String swaggerHost) {
        this.swaggerHost = swaggerHost;
    }
    public ESConfig getEs() {
		return es;
	}

	public void setEs(ESConfig es) {
		this.es = es;
	}

    /**
     * Gets the dbserver.
     *
     * @return the dbserver
     */
    public DBServer getDbserver() {
        return dbserver;
    }

    /**
     * Sets the dbserver.
     *
     * @param dbserver the new dbserver
     */
    public void setDbserver(DBServer dbserver) {
        this.dbserver = dbserver;
    }
    

    /**
     * Gets the app ports.
     *
     * @return the app ports
     */
    public Map<String, String> getAppPorts() {
		return appPorts;
	}

	/**
	 * Sets the app ports.
	 *
	 * @param appPorts the app ports
	 */
	public void setAppPorts(Map<String, String> appPorts) {
		this.appPorts = appPorts;
	}

	/**
	 * <p>Getter for the field <code>sqsUrls</code>.</p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<String, String> getSqsUrls() {
		return sqsUrls;
	}

	/**
	 * <p>Setter for the field <code>sqsUrls</code>.</p>
	 *
	 * @param sqsUrls a {@link java.util.Map} object.
	 */
	public void setSqsUrls(Map<String, String> sqsUrls) {
		this.sqsUrls = sqsUrls;
	}


	/**
	 * <p>Getter for the field <code>swaggerHostUat</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSwaggerHostUat() {
		return swaggerHostUat;
	}

	/**
	 * <p>Setter for the field <code>swaggerHostUat</code>.</p>
	 *
	 * @param swaggerHostUat a {@link java.lang.String} object.
	 */
	public void setSwaggerHostUat(String swaggerHostUat) {
		this.swaggerHostUat = swaggerHostUat;
	}



	/**
	 * The Class Server.
	 */
    public static class Server {

        /** The port. */
        int port;
    }

    /**
     * Gets the active profile.
     *
     * @return the active profile
     */
    public String getActiveProfile() {
        return activeProfile;
    }

    /**
     * Sets the active profile.
     *
     * @param activeProfile the new active profile
     */
    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }
    /*
     * The class ESConfig
     */
    public static class ESConfig{
    	String protocol;
    	String host;
    	Integer port;
    	Map<String,Object> product;
    	public String getProtocol() {
			return protocol;
		}
		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
		public Integer getPort() {
			return port;
		}
		public void setPort(Integer port) {
			this.port = port;
		}
		public Map<String,Object> getProduct() {
			return product;
		}
		public void setProduct(Map<String,Object> product) {
			this.product = product;
		}
		
    }
   
    /**
     * The Class CacheConfig.
     */
    public static class CacheConfig {

        /** The timeout. */
        Long timeout;

        /** The timeout unit. */
        String timeoutUnit;

        /** The host. */
        String host;

        /** The port. */
        Integer port;

        /** The password. */
        String password;
        
        Long readTimeout = 20L;
        
        Long connectTimeout = 30L;
        
        Integer minIdle = 1;
        
        Boolean usePooling = Boolean.FALSE;

        /**
         * Gets the timeout.
         *
         * @return the timeout
         */
        public Long getTimeout() {
            return timeout;
        }

        /**
         * Sets the timeout.
         *
         * @param timeout the new timeout
         */
        public void setTimeout(Long timeout) {
            this.timeout = timeout;
        }

        /**
         * Gets the timeout unit.
         *
         * @return the timeout unit
         */
        public String getTimeoutUnit() {
            return timeoutUnit;
        }

        /**
         * Sets the timeout unit.
         *
         * @param timeoutUnit the new timeout unit
         */
        public void setTimeoutUnit(String timeoutUnit) {
            this.timeoutUnit = timeoutUnit;
        }

        /**
         * Gets the host.
         *
         * @return the host
         */
        public String getHost() {
            return host;
        }

        /**
         * Sets the host.
         *
         * @param host the new host
         */
        public void setHost(String host) {
            this.host = host;
        }

        /**
         * Gets the port.
         *
         * @return the port
         */
        public Integer getPort() {
            return port;
        }

        /**
         * Sets the port.
         *
         * @param port the new port
         */
        public void setPort(Integer port) {
            this.port = port;
        }

        /**
         * Gets the password.
         *
         * @return the password
         */
        public String getPassword() {
            return password;
        }

        /**
         * Sets the password.
         *
         * @param password the new password
         */
        public void setPassword(String password) {
            this.password = password;
        }

		public Long getReadTimeout() {
			return readTimeout;
		}

		public void setReadTimeout(Long readTimeout) {
			this.readTimeout = readTimeout;
		}

		public Long getConnectTimeout() {
			return connectTimeout;
		}

		public void setConnectTimeout(Long connectTimeout) {
			this.connectTimeout = connectTimeout;
		}

		public Integer getMinIdle() {
			return minIdle;
		}

		public void setMinIdle(Integer minIdle) {
			this.minIdle = minIdle;
		}

		public Boolean getUsePooling() {
			return usePooling;
		}

		public void setUsePooling(Boolean usePooling) {
			this.usePooling = usePooling;
		}
        
    }

    /**
     * The Class DBServer.
     */
    public static class DBServer {

        /** The primary. */
        private DBProperties primary;

        /** The replica. */
        private DBProperties replica;

        /** The showsql. */
        private boolean showsql;

        /**
         * Gets the primary.
         *
         * @return the primary
         */
        public DBProperties getPrimary() {
            return primary;
        }

        /**
         * Sets the primary.
         *
         * @param primary the new primary
         */
        public void setPrimary(DBProperties primary) {
            this.primary = primary;
        }

        /**
         * Gets the replica.
         *
         * @return the replica
         */
        public DBProperties getReplica() {
            return replica;
        }

        /**
         * Sets the replica.
         *
         * @param replica the new replica
         */
        public void setReplica(DBProperties replica) {
            this.replica = replica;
        }

        /**
         * Checks if is showsql.
         *
         * @return true, if is showsql
         */
        public boolean isShowsql() {
            return showsql;
        }

        /**
         * Sets the showsql.
         *
         * @param showsql the new showsql
         */
        public void setShowsql(boolean showsql) {
            this.showsql = showsql;
        }
    }

    /**
     * The Class DBProperties.
     */
    public static class DBProperties {

        /** The url. */
        private String url;

        /** The username. */
        private String username;

        /** The password. */
        private String password;

        /** The driver class. */
        private String driverClass;

        /** The initial size. */
        private int initialSize;

        /** The validation query. */
        private String validationQuery;

        /** The validation interval. */
        private long validationInterval;
        
        private boolean testOnBorrow = true;
        
        private boolean removeAbandoned = true;

        /**
         * Gets the url.
         *
         * @return the url
         */
        public String getUrl() {
            return url;
        }

        /**
         * Sets the url.
         *
         * @param url the new url
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * Gets the username.
         *
         * @return the username
         */
        public String getUsername() {
            return username;
        }

        /**
         * Sets the username.
         *
         * @param username the new username
         */
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         * Gets the password.
         *
         * @return the password
         */
        public String getPassword() {
            return password;
        }

        /**
         * Sets the password.
         *
         * @param password the new password
         */
        public void setPassword(String password) {
            this.password = password;
        }

        /**
         * Gets the driver class.
         *
         * @return the driver class
         */
        public String getDriverClass() {
            return driverClass;
        }

        /**
         * Sets the driver class.
         *
         * @param driverClass the new driver class
         */
        public void setDriverClass(String driverClass) {
            this.driverClass = driverClass;
        }

        /**
         * Gets the initial size.
         *
         * @return the initial size
         */
        public int getInitialSize() {
            return initialSize;
        }

        /**
         * Sets the initial size.
         *
         * @param initialConnectionSize the new initial size
         */
        public void setInitialSize(int initialConnectionSize) {
            this.initialSize = initialConnectionSize;
        }

        /**
         * Gets the validation query.
         *
         * @return the validation query
         */
        public String getValidationQuery() {
            return validationQuery;
        }

        /**
         * Sets the validation query.
         *
         * @param validationQuery the new validation query
         */
        public void setValidationQuery(String validationQuery) {
            this.validationQuery = validationQuery;
        }

        /**
         * Gets the validation interval.
         *
         * @return the validation interval
         */
        public long getValidationInterval() {
            return validationInterval;
        }

        /**
         * Sets the validation interval.
         *
         * @param validationInterval the new validation interval
         */
        public void setValidationInterval(long validationInterval) {
            this.validationInterval = validationInterval;
        }

		public boolean getTestOnBorrow() {
			return testOnBorrow;
		}

		public void setTestOnBorrow(boolean testOnBorrow) {
			this.testOnBorrow = testOnBorrow;
		}

		public boolean getRemoveAbandoned() {
			return removeAbandoned;
		}

		public void setRemoveAbandoned(boolean removeAbandoned) {
			this.removeAbandoned = removeAbandoned;
		}
        
    }
}
