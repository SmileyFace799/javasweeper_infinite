package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonMap<V> extends HashMap<String, V> {
  final ObjectMapper mapper = new ObjectMapper();
  final String fileName;

  public JsonMap(String fileName) {
    this.fileName = fileName;
    load();
  }

  //Mutators
  public void load() {
    try {
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      this.putAll(mapper.readValue(br.readLine(), HashMap.class));
      br.close();
    } catch (Exception e) {
      System.out.println(
          "Could not load data from file: \"" + fileName + "\". The file might not exist"
      );
    }
  }

  public void save() {
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
      bw.write(mapper.writeValueAsString(this));
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
