package lib.core.heartspy;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
//ToDo: Create a dedicated package ... Keep only record in main code.
public class FileManager {
    private ArrayList<File> Collection = null;
    private File Directory;
    private File InUseDB = null;
    private String NameDB;
    private int LastFileIndex = 0;

    public FileManager(Context context) {
        // Check access to External storage
        Boolean hasExternal = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());

        if (hasExternal) {
            Directory  = Environment.getExternalStoragePublicDirectory(Constants.FilesWorkingSpace);
            Directory.mkdir();
        }
        else Directory = context.getFilesDir();
        Log.d("FileManager", "Selecting workspace : "+ Directory.getAbsolutePath() );

        NameDB = getNowDB();

        // Create InUse database
        InUseDB = new File(Directory.getPath(),NameDB);
    }

    public File getDirectory() { return Directory; }

    public void resetStreams() {LastFileIndex = 0;}

    public FileOutputStream getWriteStream() {
        FileOutputStream WriteStream = null;
        try { WriteStream = new FileOutputStream(InUseDB, true); }
        catch (Exception StreamError) { Log.d("FileManager","Can't open stream "+ InUseDB.getPath()+" for writing..."); }
        return WriteStream;
    }

    public FileInputStream getNextStream() {
        if (LastFileIndex == Collection.size()) return null;
        FileInputStream ReadStream;
        Log.d("FileManager","Selecting data from " + Collection.get(LastFileIndex).getPath());
        try { ReadStream = new FileInputStream(Collection.get(LastFileIndex)); }
        catch (Exception StreamError) {
            Log.d("FileManager","Can't open stream "+ InUseDB.getPath()+" for reading...");
            ReadStream = null;
        }
        LastFileIndex++;
        return ReadStream;
    }

    private String getNowDB(){

        // Calculate today database file
        Calendar Today = Calendar.getInstance();
        int Day = Today.get(Calendar.DAY_OF_MONTH);
        int Month = Today.get(Calendar.MONTH)+1; // Month is from 0 to 11
        int Year = Today.get(Calendar.YEAR);
        int Hour = Today.get(Calendar.HOUR_OF_DAY);
        int Minute = Today.get(Calendar.MINUTE);

        return (String.valueOf(Year) +
                String.format("%2s", String.valueOf(Month)).replace(' ', '0') +
                String.format("%2s", String.valueOf(Day)).replace(' ', '0') +
                "-"+
                String.format("%2s", String.valueOf(Hour)).replace(' ', '0') +
                String.format("%2s", String.valueOf(Minute)).replace(' ', '0') +
                Constants.FilesSignature);
    }
}