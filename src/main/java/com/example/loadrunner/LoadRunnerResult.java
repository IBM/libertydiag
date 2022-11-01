package com.example.loadrunner;

public class LoadRunnerResult {
	public String loadIdentifier;
	public SimulatedUserResult totalResults = new SimulatedUserResult();
	public String status;

	@Override
	public String toString() {
		return super.toString() + " { totalResults: " + totalResults + "}";
	}

	public String getStatus(long time) {
		return "requests: " + totalResults.count + ", errors: " + totalResults.errors + ", average execution: "
				+ String.format("%.2f", totalResults.getAverageExecutionTime()) + " ms, max execution: "
				+ totalResults.maxExecutionTime + " ms, min execution: " + totalResults.minExecutionTime
				+ " ms, throughput: " + String.format("%.2f", totalResults.getThroughput(time)) + " tps";
	}
}
