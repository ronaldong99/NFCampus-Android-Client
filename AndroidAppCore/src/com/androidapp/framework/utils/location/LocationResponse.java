/*
    Android Client Core, LocationResponse
    Copyright (c) 2015 NF
     
 */

package com.androidapp.framework.utils.location;

import java.util.List;


/**
 * [A brief description]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2014-4-11
 * 
 **/
public class LocationResponse {

	private String status;
	private List<LocationResults> results;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<LocationResults> getResults() {
		return results;
	}

	public void setResults(List<LocationResults> results) {
		this.results = results;
	}

}
