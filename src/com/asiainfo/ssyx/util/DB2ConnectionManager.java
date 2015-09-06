package com.asiainfo.ssyx.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

//import com.asiainfo.uap.util.EncryptInterface;

/**
 * This class is a Singleton that provides access to one or many connection
 * pools defined in a Property file. A client gets access to the single instance
 * through the static getInstance() method and can then check-out and check-in
 * connections from a pool. When the client shuts down it should call the
 * release() method to close all open connections and do other clean up.
 */
public class DB2ConnectionManager {
	static private DB2ConnectionManager instance; // The single instance
	static private int clients;
	private Vector<Driver> drivers = new Vector<Driver>();
	private Hashtable<String, DBConnectionPool> pools = new Hashtable<String, DBConnectionPool>();

	private static Logger logger = Logger.getLogger(DB2ConnectionManager.class);

	/**
	 * Returns the single instance, creating one if it's the first time this
	 * method is called.
	 * 
	 * @return DBConnectionManager The single instance.
	 */
	static synchronized public DB2ConnectionManager getInstance() {
		if (instance == null) {
			instance = new DB2ConnectionManager();
		}
		clients++;
		// System.out.println(clients);
		return instance;
	}

	public int getAccessNum() {
		return clients;
	}

	public int getUsedConNum(String poolName) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(poolName);
		int size = 0;
		if (pool != null) {
			size = pool.getUsedConNum();
		}
		pool = null;
		return size;
	}

	public int getFreeConNum(String poolName) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(poolName);
		int size = 0;
		if (pool != null) {
			size = pool.getFreeConNum();
		}
		pool = null;
		return size;
	}

	public String[] getPoolName() {
		Enumeration<String> names = pools.keys();
		String[] nameList = new String[pools.size()];
		int i = 0;
		while (names.hasMoreElements()) {
			nameList[i++] = (String) names.nextElement();
		}
		return nameList;
	}

	/**
	 * A private constructor since this is a Singleton
	 */
	private DB2ConnectionManager() {
		init();
	}

	/**
	 * Returns a connection to the named pool.
	 * 
	 * @param name
	 *            The pool name as defined in the properties file
	 * @param con
	 *            The Connection
	 */
	public void freeConnection(String name, Connection con) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			pool.freeConnection(con);
		}
		pool = null;
	}

	/**
	 * Returns an open connection. If no one is available, and the max number of
	 * connections has not been reached, a new connection is created.
	 * 
	 * @param name
	 *            The pool name as defined in the properties file
	 * @return Connection The connection or null
	 */
	public java.sql.Connection getConnection(String name) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			return pool.getConnection();
		}
		pool = null;
		return null;
	}

	/**
	 * Returns an open connection. If no one is available, and the max number of
	 * connections has not been reached, a new connection is created. If the max
	 * number has been reached, waits until one is available or the specified
	 * time has elapsed.
	 * 
	 * @param name
	 *            The pool name as defined in the properties file
	 * @param time
	 *            The number of milliseconds to wait
	 * @return Connection The connection or null
	 */
	public java.sql.Connection getConnection(String name, long time) {

		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			return pool.getConnection(time);
		}
		pool = null;
		return null;
	}

	/**
	 * Closes all open connections and deregisters all drivers.
	 */
	public synchronized void release() {
		// Wait until called by the last client
		if (--clients != 0) {
			return;
		}
		Enumeration<DBConnectionPool> allPools = pools.elements();
		while (allPools.hasMoreElements()) {
			DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
			pool.release();
		}
		Enumeration<Driver> allDrivers = drivers.elements();
		while (allDrivers.hasMoreElements()) {
			Driver driver = (Driver) allDrivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				logger.info("Deregistered JDBC driver "
						+ driver.getClass().getName());
			} catch (SQLException e) {
				logger.info("Can't deregister JDBC driver: "
						+ driver.getClass().getName() + " Exception:"
						+ e.getMessage());
			}
		}
	}

	/**
	 * Creates instances of DBConnectionPool based on the properties. A
	 * DBConnectionPool can be defined with the following properties:
	 * 
	 * <PRE>
	 * &lt;poolname&gt;.url The JDBC URL for the database
	 * &lt;poolname&gt;.user  A database user (optional)
	 * &lt;poolname&gt;.passwordA database user password (if user specified)
	 * &lt;poolname&gt;.maxconn The maximal number of connections (optional)
	 * </PRE>
	 * 
	 * @param props
	 *            The connection pool properties
	 */
	private void createPools(Properties props) {
		Enumeration<?> propNames = props.propertyNames();
		while (propNames.hasMoreElements()) {
			String name = (String) propNames.nextElement();
			if (name.endsWith(".url")) {
				String poolName = name.substring(0, name.lastIndexOf("."));
				String url = props.getProperty(poolName + ".url");
				if (url == null) {
					logger.info("No URL specified for " + poolName);
					continue;
				}
				String user = props.getProperty(poolName + ".user");
				String password = props.getProperty(poolName + ".password");
				// password = EncryptInterface.desUnEncryptData(password);
				String maxconn = props.getProperty(poolName + ".maxconn", "3");
				String minconn = props.getProperty(poolName + ".minconn", "1");
				String defconn = props.getProperty(poolName + ".defconn", "2");
				int max, min, def;
				try {
					max = Integer.valueOf(maxconn.trim()).intValue();
					min = Integer.valueOf(minconn.trim()).intValue();
					def = Integer.valueOf(defconn.trim()).intValue();
				} catch (Exception e) {
					logger.info("Invalid maxconn value " + maxconn + " for "
							+ poolName);
					logger.info("Invalid minconn value " + minconn + " for "
							+ poolName);
					logger.info("Invalid minconn value " + defconn + " for "
							+ poolName);
					max = 3;
					min = 1;
					def = 2;
				}
				DBConnectionPool pool = new DBConnectionPool(poolName, url
						.trim(), user, password, max, min, def);
				pools.put(poolName, pool);
				logger.info("Initialized pool " + poolName);
			}
		}
	}

	/**
	 * Loads properties and initializes the instance with its values.
	 */
	private void init() {
		FileInputStream is = null;
		try {
			System.out.println("File path:"+EmSalesConstant.DB2CONFIG);
			is = new FileInputStream(EmSalesConstant.DB2CONFIG);

		} catch (FileNotFoundException e) {
			System.out.println("File db2.properties not found");
		}

		Properties dbProps = new Properties();
		try {
			dbProps.load(is);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Can't read the properties file. "
					+ "Make sure db.properties is in the CLASSPATH");
			return;
		}
		loadDrivers(dbProps);
		createPools(dbProps);
	}

	/**
	 * Loads and registers all JDBC drivers. This is done by the
	 * DBConnectionManager, as opposed to the DBConnectionPool, since many pools
	 * may share the same driver.
	 * 
	 * @param props
	 *            The connection pool properties
	 */
	private void loadDrivers(Properties props) {
		String driverClasses = props.getProperty("drivers");
		StringTokenizer st = new StringTokenizer(driverClasses);
		while (st.hasMoreElements()) {
			String driverClassName = st.nextToken().trim();
			try {
				Driver driver = (Driver) Class.forName(driverClassName)
						.newInstance();
				DriverManager.registerDriver(driver);
				drivers.addElement(driver);
				logger.info("Registered JDBC driver " + driverClassName);
			} catch (Exception e) {
				logger.info("Can't register JDBC driver: " + driverClassName
						+ ", Exception: " + e);
			}
		}
	}

	/**
	 * This inner class represents a connection pool. It creates new connections
	 * on demand, up to a max number if specified. It also makes sure a
	 * connection is still open before it is returned to a client.
	 */
	class DBConnectionPool {
		private int checkedOut; // 已用连接数
		private Vector<Connection> freeConnections = new Vector<Connection>(); // 可用连接数
		private int maxConn; // 最大连接数
		private int minConn; // 最小连接数
		private int defaultConn; // 默认连接数
		private String name; // 数据库名称
		private String password; // 登陆密码
		private String URL; // 数据库路径
		private String user; // 用户名称

		public int getUsedConNum() {
			return checkedOut;
		}

		public int getFreeConNum() {
			return freeConnections.size();
		}

		/**
		 * Creates new connection pool.
		 * 
		 * @param name
		 *            The pool name
		 * @param URL
		 *            The JDBC URL for the database
		 * @param user
		 *            The database user, or null
		 * @param password
		 *            The database user password, or null
		 * @param maxConn
		 *            The maximal number of connections, or 0 for no limit
		 */
		public DBConnectionPool(String name, String URL, String user,
				String password, int maxConn, int minConn, int defaultConn) {
			this.name = name;
			this.URL = URL;
			this.user = user;
			this.password = password;
			this.maxConn = maxConn;
			this.minConn = minConn;
			this.defaultConn = defaultConn;
			initConnections();
		}

		/**
		 * Checks in a connection to the pool. Notify other Threads that may be
		 * waiting for a connection.
		 * 
		 * @param con
		 *            The connection to check in
		 */
		public synchronized void freeConnection(Connection con) {
			// Put the connection at the end of the Vector
			int free = freeConnections.size();
			if (con == null
					|| ((checkedOut + free > maxConn) && (free >= minConn))) {
				try {
					if (con != null) {
						con.close();
						logger.info("Closed connection for pool " + name);
					} else
						logger.info("Thread Name :"
								+ Thread.currentThread().getName()
								+ " Closed Connecion is null " + name);
				} catch (SQLException e) {
					logger.info("Can't close connection for pool " + name
							+ " Exception :" + e);
				}
			} else {
				Statement stmt = null;
				try {
					if (!con.getAutoCommit())
						con.setAutoCommit(true);
					stmt = con.createStatement();
					stmt.close();
					freeConnections.addElement(con);
				} catch (Exception e) {
					if (stmt != null) {
						try {
							stmt.close();
							con.close();
						} catch (SQLException ex) {
						}
					}
				}
			}
			checkedOut--;
			notifyAll();
		}

		/**
		 * Checks out a connection from the pool. If no free connection is
		 * available, a new connection is created unless the max number of
		 * connections has been reached. If a free connection has been closed by
		 * the database, it's removed from the pool and this method is called
		 * again recursively.
		 */
		public synchronized java.sql.Connection getConnection() {
			java.sql.Connection con = null;
			if (freeConnections.size() > 0) {
				con = (java.sql.Connection) freeConnections.firstElement();
				freeConnections.removeElementAt(0);
				Statement stmt = null;
				try {

					if (con == null || con.isClosed()) {
						logger.info("Removed bad connection from 1 in:"
								+ Thread.currentThread().getName());

						con = getConnection();
					} else {
						stmt = con.createStatement();
						stmt.close();
					}
				} catch (SQLException e) {
					logger.info("Removed bad connection from 2 in:"
							+ Thread.currentThread().getName());
					if (stmt != null) {
						try {
							stmt.close();
							con.close();
						} catch (SQLException ex) {
						}
					}
					con = getConnection();
				}
			} else if (maxConn == 0 || checkedOut < maxConn) { // ���maxConΪ������������

				con = newConnection();
			}
			if (con != null) {
				checkedOut++;
			}
			return con;
		}

		/**
		 * Checks out a connection from the pool. If no free connection is
		 * available, a new connection is created unless the max number of
		 * connections has been reached. If a free connection has been closed by
		 * the database, it's removed from the pool and this method is called
		 * again recursively.
		 * <P>
		 * If no connection is available and the max number has been reached,
		 * this method waits the specified time for one to be checked in.
		 * 
		 * @param timeout
		 *            The timeout value in milliseconds
		 */
		public synchronized java.sql.Connection getConnection(long timeout) {
			long startTime = new Date().getTime();
			java.sql.Connection con;
			while ((con = getConnection()) == null) {
				try {
					wait(timeout); // ��notifyAll()���ѻ�ʱ�䵽�Զ�����
				} catch (InterruptedException e) {
				}
				if ((new Date().getTime() - startTime) >= timeout) {
					// Timeout has expired
					return null;
				}
			}
			return con;
		}

		/**
		 * Closes all available connections.
		 */
		public synchronized void release() {
			Enumeration<Connection> allConnections = freeConnections.elements();
			while (allConnections.hasMoreElements()) {
				java.sql.Connection con = (java.sql.Connection) allConnections
						.nextElement();
				try {
					con.close();
					logger.info("Closed connection for pool " + name);
				} catch (SQLException e) {
					logger.info("Can't close connection for pool " + name
							+ " Exception :" + e);
				}
			}
			freeConnections.removeAllElements();
		}

		/**
		 * Creates a new connection, using a userid and password if specified.
		 */
		private java.sql.Connection newConnection() {
			java.sql.Connection con = null;
			try {
				if (user == null) {
					con = DriverManager.getConnection(URL);
				} else {
					con = DriverManager.getConnection(URL, user, password);
				}
				logger.info("Created a new connection in pool " + name);
			} catch (SQLException e) {
				logger.info("Can't create a new connection for " + URL
						+ " Exception :" + e);
				return null;
			}
			return con;
		}

		private void initConnections() {
			java.sql.Connection con = null;
			for (int i = 0; i < defaultConn; i++) {
				con = newConnection();
				freeConnections.addElement(con);
			}
		}
	}
}
