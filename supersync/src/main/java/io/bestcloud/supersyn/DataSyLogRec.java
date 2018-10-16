package io.bestcloud.supersyn;

import java.util.LinkedList;

/**
 * 数据同步日志记录
 * 
 * @author wuzhihui
 *
 */
public class DataSyLogRec {
	public long blockID;
	public LinkedList<String> txSeqs = new LinkedList<>();

	public boolean containT(String txSeq) {
		return txSeqs.contains(txSeq);
	}

}
