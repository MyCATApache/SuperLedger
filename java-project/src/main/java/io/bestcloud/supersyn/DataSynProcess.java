package io.bestcloud.supersyn;

public abstract class DataSynProcess {

	/**
	 * 执行同步逻辑，如果成功或者已经成功同步这笔数据，返回TRUE，否则返回FALSE，未来继续同步
	 * @param syncData
	 * @return
	 */
	public abstract boolean processSynData(byte[] syncData);
	
}
	 
