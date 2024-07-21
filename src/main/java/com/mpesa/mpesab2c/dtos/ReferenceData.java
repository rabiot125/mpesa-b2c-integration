package com.mpesa.mpesab2c.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReferenceData {

	@JsonProperty("ReferenceItem")
	private ReferenceItem referenceItem;
}