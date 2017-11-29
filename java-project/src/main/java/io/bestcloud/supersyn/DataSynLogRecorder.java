package io.bestcloud.supersyn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class DataSynLogRecorder {

	private DataSyLogRec lastSyLogRec;
	private Path logFile;

	public DataSynLogRecorder(String txLogFile) {
		this.logFile = Paths.get(txLogFile);
	}

	public void loadSynLog() throws IOException {
		lastSyLogRec = new DataSyLogRec();
		lastSyLogRec.blockID = -1;

		if (!Files.exists(logFile)) {
			Files.createFile(logFile);
		} else {
			Files.lines(logFile).forEach(line -> {
				String[] txLogItems = line.split(",");
				long curBlockId = Long.valueOf(txLogItems[0]);
				if (curBlockId > lastSyLogRec.blockID) {
					lastSyLogRec.blockID = curBlockId;
					lastSyLogRec.txSeqs.clear();
				}
				lastSyLogRec.txSeqs.add(txLogItems[1]);
			});

		}
	}

	public void addTXSeq(long curBlockID, String txSeq) throws IOException {
		if (curBlockID != lastSyLogRec.blockID) {
			lastSyLogRec.blockID = curBlockID;
			lastSyLogRec.txSeqs.clear();
		}
		lastSyLogRec.txSeqs.add(txSeq);
		byte[] line = (curBlockID + "," + txSeq + "," + System.currentTimeMillis()+"\r\n").getBytes("iso-8859-1");
		Files.write(logFile, line, StandardOpenOption.APPEND);

	}

	public DataSyLogRec getLastDataSyLogRec() {
		 
		return lastSyLogRec;
	}

}
