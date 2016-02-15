package com.b3.search;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Nishanth on 08/02/2016.
 */
public class Serializer {

    /**
     * Save object to file
     * @param filename name to be saved under
     * @param gg WorldGraph object to be saved
     */
    public void serializeAddress(String filename, WorldGraph gg){
        try {
            FileOutputStream fout = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(gg);
            oos.close();
            System.out.println("Done");

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Load object from file
     * @param filename name of file to be loaded
     * @return WorldGraph successfully loaded. NULL if cannot load.
     */
    public WorldGraph deserialzeAddress(String filename){

        WorldGraph address;

        try{

            FileInputStream fin = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fin);
            address = (WorldGraph) ois.readObject();
            ois.close();

            return address;

        } catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

}
