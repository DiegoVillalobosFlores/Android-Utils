package installred.installred.installred.installred;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by deimi on 19/12/2016.
 */

public class ObjectsIO{
    public static void SaveObject(String filename, Context context, Object object){
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(filename,Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            fileOutputStream.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static Object LoadObject(String filename, Context context){
        try {
            FileInputStream fileInputStream = context.openFileInput(filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Object object = (Object) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return object;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
