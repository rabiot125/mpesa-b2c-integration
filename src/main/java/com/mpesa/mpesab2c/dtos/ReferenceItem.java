package com.mpesa.mpesab2c.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReferenceItem {

	@JsonProperty("Value")
	private String value;

	@JsonProperty("Key")
	private String key;
}