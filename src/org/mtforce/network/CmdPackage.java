package org.mtforce.network;

import java.io.Serializable;

public class CmdPackage  implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -184098546949471233L;
	private boolean requestUpdate;
	private int toggleFreq;

	public boolean isRequestUpdate() {
		return requestUpdate;
	}

	public void setRequestUpdate(boolean requestUpdate) {
		this.requestUpdate = requestUpdate;
	}

	public int getToggleFreq() {
		return toggleFreq;
	}

	public void setToggleFreq(int toggleFreq) {
		this.toggleFreq = toggleFreq;
	}
	
	
}
