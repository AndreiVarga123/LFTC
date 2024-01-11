package org.example.Scanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class PIFReader {
    private ArrayList<Pair> pifList;

    public PIFReader() {
        this.pifList = new ArrayList<>();
    }

    public void addToPifList(Pair p) {
        this.pifList.add(p);
    }

    public int length() {
        return this.pifList.size();
    }

    public Pair get(int position) {
        return this.pifList.get(position);
    }

    public static List<String> readFromPifOutput(String filename) {
        try{
            List<String> tokens = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            while(line!=null){
                tokens.add(line.split("->")[0].strip());
                line = reader.readLine();
            }
            reader.close();
            return tokens;
        }
        catch (Exception exception) {
            return new ArrayList<>();
        }
    }
}
