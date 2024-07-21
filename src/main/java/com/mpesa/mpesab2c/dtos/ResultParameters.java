package com.mpesa.mpesab2c.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResultParameters{

	@JsonProperty("ResultParameter")
	private List<ResultParameterItem> resultParameter;
}