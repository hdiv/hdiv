package org.hdiv.init;

import org.hdiv.config.HDIVConfig;
import org.hdiv.util.HDIVUtil;

public class HdivParameterInitializer {

	/**
	 * HDIV configuration object
	 */
	protected HDIVConfig config;

	public HdivParameterInitializer() {
		// TODO Auto-generated constructor stub
	}

	protected String getHdivParameter() {
		if (config.isRandomName()) {
			return HDIVUtil.createRandomToken(Integer.MAX_VALUE);
		}
		else {
			return config.getStateParameterName();
		}
	}

	protected String getModifyHdivParameter() {
		if (config.isRandomName()) {
			return HDIVUtil.createRandomToken(Integer.MAX_VALUE);
		}
		else {
			return config.getModifyStateParameterName();
		}
	}

}
