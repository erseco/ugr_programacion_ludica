package es.ernesto.minicars;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import android.content.Context;
import android.widget.Toast;

public class RecordsTable {

	private static final int MAX_RECORDS = 10;
	final String FILENAME = "records";
	String LOG_TAG = "fileLogs";
	boolean SDmounted = true;
	File recFile;
	int linesCount = 0;
	public ArrayList<RecordUnit> units;
	RecordUnit unit;
	Context context;

	public RecordsTable(Context mContext) {
		this.context = mContext;
		/*if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			SDmounted = false;
		} else {
			SDmounted = true;
		}
		String path;
		if (!SDmounted) {
			path = Environment.getDataDirectory() + "/records.txt";
		} else {
			path = Environment.getExternalStorageDirectory()
					+ "/data/records.txt";
		}
		this.recFile = new File(path);
		if (!recFile.exists()) {
			{
				try {
					recFile.createNewFile();
					try {
						FileWriter fWr = new FileWriter(recFile);
						fWr.flush();
						fWr.close();
					} catch (Throwable t) {
						Toast.makeText(context, "Exception: " + t.toString(),
								Toast.LENGTH_LONG).show();
					}
				} catch (Exception e1) {

				}
			}
		}*/
		File myDir = context.getFilesDir();
		System.out.println(context.getFilesDir());
		String filename = "records.txt";
		recFile = new File(myDir, filename);
		if (!recFile.exists()) {
			try {
				recFile.createNewFile();
				try {
					FileWriter fWr = new FileWriter(recFile);
					fWr.flush();
					fWr.close();
				} catch (Throwable t) {
					Toast.makeText(context, "Exception: " + t.toString(),
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e1) {

			}
		}
		units = getPreviousRecords();
	}

	public RecordsTable() {
		units = getPreviousRecords();
	}

	public ArrayList<RecordUnit> getPreviousRecords() {
		ArrayList<RecordUnit> list = new ArrayList<RecordUnit>();
		try {
			FileReader fRd = new FileReader(recFile);
			BufferedReader reader = new BufferedReader(fRd);
			String str;
			RecordUnit unit = new RecordUnit();
			int k = 0;
			while ((str = reader.readLine()) != null) {
				linesCount++;
				if (linesCount % 2 != 0) {
					unit = new RecordUnit();
					unit.name = str;
				} else {
					unit.score = Integer.parseInt(str);
					list.add(k, unit);
					k++;
				}
			}
			fRd.close();
		} catch (Throwable t) {
			Toast.makeText(context, "Exception: " + t.toString(),
					Toast.LENGTH_LONG).show();
		}
		return list;
	}

	private void saveCurrentRecords(ArrayList<RecordUnit> list) {
		try {
			FileWriter fWr = new FileWriter(recFile);
			for (int i = 0; i < list.size(); i++) {
				fWr.write(list.get(i).name + "\n");
				fWr.write(list.get(i).score + "\n");
			}
			fWr.flush();
			fWr.close();
		} catch (Throwable t) {
			Toast.makeText(context, "Exception: " + t.toString(),
					Toast.LENGTH_LONG).show();
		}

	}

	public boolean isRecord(int score) {
		if (units.size() < MAX_RECORDS)
			return true;
		for (int i = 0; i < units.size(); i++) {
			if (score > units.get(i).score) {
				return true;
			}
		}
		return false;
	}

	public void addNewRecord(String name, int score) {

		unit = new RecordUnit();
		unit.name = name;
		unit.score = score;
		if (units.size() < MAX_RECORDS) {
			units.add(units.size(), unit);
			Sort(units);
			saveCurrentRecords(units);
		} else {
			units.set(0, unit);
			Sort(units);
			saveCurrentRecords(units);
		}
	}

	private ArrayList<RecordUnit> Sort(ArrayList<RecordUnit> list) {
		int i = 0;
		int n = list.size() - 1;
		RecordUnit buf = new RecordUnit();
		while (i < n) {
			if (list.get(i + 1).score < list.get(i).score) {
				buf = list.get(i + 1);
				list.set(i + 1, list.get(i));
				list.set(i, buf);
				i = 0;
			} else {
				i++;
			}
		}
		return list;
	}
}
