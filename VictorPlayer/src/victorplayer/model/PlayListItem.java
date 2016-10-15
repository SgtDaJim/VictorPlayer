/**
 * @author Jim
 * @Time 2015年11月16日 下午10:59:40
 */
package victorplayer.model;

import java.io.Serializable;

/**
 * @author Jim
 * @Time 2015年11月16日 下午10:59:40
 *
 */
public class PlayListItem implements Serializable {
	
	private String fileName = "";
	private String fileDir = "";
	private String wholeName = "";

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * @return the fileDir
	 */
	public String getFileDir() {
		return fileDir;
	}

	/**
	 * @param fileDir the fileDir to set
	 */
	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}

	/**
	 * @return the wholeName
	 */
	public String getWholeName() {
		return fileDir + fileName;
	}

	/**
	 * @param wholeName the wholeName to set
	 */
	public void setWholeName(String wholeName) {
		this.wholeName = wholeName;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	
}
