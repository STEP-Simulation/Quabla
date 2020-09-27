package quabla.parameter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ParameterManager {

	private ArrayList<Parameter> paramList = new ArrayList<>();

	public void addParameter(Parameter parameter) {
		this.paramList.add(parameter);
	}


	// 実装中
	public LinkedHashMap<String, LinkedHashMap<String, Parameter>> getInputParamMap(boolean inUserParamMap) {
		LinkedHashMap<String, LinkedHashMap<String, Parameter>> parentMap = new LinkedHashMap<>();
		for (Parameter param : paramList) {

		}

		return parentMap;
	}

}
