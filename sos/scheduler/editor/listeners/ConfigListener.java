package sos.scheduler.editor.listeners;

import org.jdom.Element;

import sos.scheduler.editor.app.DomParser;
import sos.scheduler.editor.app.Utils;

public class ConfigListener {
	private DomParser _dom;

	private Element _config;

	public ConfigListener(DomParser dom) {
		_dom = dom;
		_config = _dom.getRoot().getChild("config");
	}

	public DomParser getDom() {
		return _dom;
	}

	public String getComment() {
		return Utils.getAttributeValue("__comment__", _config);
	}
	
	public void setComment(String comment) {
		Utils.setAttribute("__comment__", comment, _config, _dom);
	}
	
	public String getIncludePath() {
		return Utils.getAttributeValue("include_path", _config);
	}

	public void setIncludePath(String path) {
		Utils.setAttribute("include_path", path, _config, _dom);
	}

	public String getJavaClasspath() {
		return Utils.getAttributeValue("java_class_path", _config);
	}

	public void setJavaClasspath(String classpath) {
		Utils.setAttribute("java_class_path", classpath, _config, _dom);
	}

	public String getJavaOptions() {
		return Utils.getAttributeValue("java_options", _config);
	}

	public void setJavaOptions(String options) {
		Utils.setAttribute("java_options", options, _config, _dom);
	}

	public String getLogDir() {
		return Utils.getAttributeValue("log_dir", _config);
	}

	public void setLogDir(String dir) {
		Utils.setAttribute("log_dir", dir, _config, _dom);
	}

	public String getMailXSLTStylesheet() {
		return Utils.getAttributeValue("mail_xslt_stylesheet", _config);
	}

	public void setMailXSLTStylesheet(String stylesheet) {
		Utils.setAttribute("mail_xslt_stylesheet", stylesheet, _config, _dom);
	}

	public String getMainSchedulerHost() {
		return Utils.getAttributeValue("main_scheduler", _config).split(":")[0];
	}

	public int getMainSchedulerPort() {
		String[] str = Utils.getAttributeValue("main_scheduler", _config)
				.split(":");
		if (str.length > 1) {
			try {
				return new Integer(str[1]).intValue();
			} catch (Exception e) {
				Utils.setAttribute("main_scheduler", str[0] + ":0", _config,
						_dom);
			}
		}
		return 0;
	}

	public void setMainScheduler(String scheduler) {
		if (scheduler.startsWith(":"))
			scheduler = "";
		Utils.setAttribute("main_scheduler", scheduler, _config, _dom);
	}

	public boolean isMainScheduler() {
		return _config.getAttribute("main_scheduler") != null;
	}

	public String getParam() {
		return Utils.getAttributeValue("param", _config);
	}

	public void setParam(String param) {
		Utils.setAttribute("param", param, _config, _dom);
	}

	public int getPriorityMax() {
		if (_config.getAttributeValue("priority_max") == null)
			return 1000;
		else
			return Utils.getIntValue("priority_max", _config);
	}

	public void setPriorityMax(int max) {
		Utils.setAttribute("priority_max", new Integer(max).toString(),
				_config, _dom);
	}

	public String getSpoolerID() {
		return Utils.getAttributeValue("spooler_id", _config);
	}

	public void setSpoolerID(String spoolerid) {
		_config.setAttribute("spooler_id", spoolerid);
		_dom.setChanged(true);
	}

	public int getTcpPort() {
		return Utils.getIntValue("tcp_port", _config);
	}

	public void setTcpPort(int port) {
		_config.setAttribute("tcp_port", new Integer(port).toString());
		_config.removeAttribute("port");
		_dom.setChanged(true);
	}

	public int getUdpPort() {
		return Utils.getIntValue("udp_port", _config);
	}

	public void setUdpPort(int port) {
		_config.setAttribute("udp_port", new Integer(port).toString());
		_config.removeAttribute("port");
		_dom.setChanged(true);
	}

	public int getPort() {
		return Utils.getIntValue("port", _config);
	}

	public void setPort(int port) {
		_config.setAttribute("port", new Integer(port).toString());
		_config.removeAttribute("tcp_port");
		_config.removeAttribute("udp_port");
		_dom.setChanged(true);
	}

	public boolean isPort() {
		return _config.getAttribute("port") != null;
	}

}
