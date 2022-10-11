package com.example.loadrunner;

public class LoadRunnerResult {
	public SimulatedUserResult totalResults = new SimulatedUserResult();
	
	@Override
	public String toString() {
		return super.toString() + " { totalResults: " + totalResults + "}";
	}
}
