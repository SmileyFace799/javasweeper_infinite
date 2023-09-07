package smiley.javasweeper.filestorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonMap extends MultiTypeMap<String> {
  final ObjectMapper mapper = new ObjectMapper();
  final String filePath;

  public JsonMap(String filePath) throws IOException {
    this.filePath = filePath;
    load();
  }

  public String getFilePath() {
    return filePath;
  }

  //Mutators
  public void load() throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      this.putAll(mapper.readValue(br.readLine(), HashMap.class));
    }
  }

  public void save() throws IOException {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
      bw.write(mapper.writeValueAsString(getAll()));
    }
  }
}
