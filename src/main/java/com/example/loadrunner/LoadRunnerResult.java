package com.example.loadrunner;

public class LoadRunnerResult {
	public String loadIdentifier;
	public SimulatedUserResult totalResults = new SimulatedUserResult();
	public String status;
	
	@Override
	public String toString() {
		return super.toString() + " { totalResults: " + totalResults + "}";
	}
}
