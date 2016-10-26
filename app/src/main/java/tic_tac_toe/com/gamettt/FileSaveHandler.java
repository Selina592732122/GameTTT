package tic_tac_toe.com.gamettt;

import android.text.TextUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileSaveHandler {

	private FileSaveHandler() {
	}

	public static void saveObject(String filePath, Object obj) {
		if (obj == null) {
			return;
		}
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		DataOutputStream dos = null;
		ObjectOutputStream oos = null;
		try {
			dos = new DataOutputStream(new FileOutputStream(file));
			oos = new ObjectOutputStream(dos);
			oos.writeObject(obj);
		} catch (Exception ignored) {
			file.delete();
		} finally {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static Object readObject(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return null;
		}
		File file = new File(filePath);
		DataInputStream dis = null;
		ObjectInputStream ois = null;
		Object obj = null;
		try {
			dis = new DataInputStream(new FileInputStream(file));
			ois = new ObjectInputStream(dis);
			obj = ois.readObject();
		} catch (Exception ignored) {
		} finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return obj;
	}

	public static void removeObject(String filePath) {
		File file = new File(filePath);
		file.delete();
	}
}
