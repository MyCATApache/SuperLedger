package io.bestcloud.supersyn;

public class DemoSynProcessor extends DataSynProcess{

	@Override
	public boolean processSynData(byte[] syncData) {
		System.out.println("synchronize data ,length "+syncData);
		return true;
	}

}
