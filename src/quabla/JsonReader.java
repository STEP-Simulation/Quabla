package quabla;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReader {

	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(new File("rocket_config.json"));
			Object obj = node.get("Structure");
			System.out.println(node.get("Aero").get("Pitch Dumping Moment Coefficient Cmq"));
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
