package smiley.javasweeper.filestorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonMap extends MultiTypeMap<String> {
  final ObjectMapper mapper = new ObjectMapper();
  final String fileName;

  public JsonMap(String fileName) {
    this.fileName = fileName;
    load();
  }

  //Mutators
  public void load(){
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      this.putAll(mapper.readValue(br.readLine(), HashMap.class));
    } catch (IOException ioe) {
      Logger.getLogger(getClass().getName()).log(
              Level.WARNING, String.format("\"%s\" not found", fileName), ioe
      );
    }
  }

  public void save() {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
      bw.write(mapper.writeValueAsString(getAll()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
