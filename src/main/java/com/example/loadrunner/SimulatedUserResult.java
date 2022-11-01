package com.example.loadrunner;

public class SimulatedUserResult {
	public int errors;
	public int count;
	public long totalExecutionTime;
	public long maxExecutionTime;
	public long minExecutionTime;

	public void add(SimulatedUserResult other) {
		count += other.count;
		errors += other.errors;
		totalExecutionTime += other.totalExecutionTime;
		if (other.maxExecutionTime > maxExecutionTime) {
			maxExecutionTime = other.maxExecutionTime;
		}
		if (minExecutionTime == 0 || other.minExecutionTime < minExecutionTime) {
			minExecutionTime = other.minExecutionTime;
		}
	}

	@Override
	public String toString() {
		return super.toString() + " { count: " + count + ", errors: " + errors + ", totalExecutionTime: "
				+ totalExecutionTime + ", maxExecutionTime: " + maxExecutionTime + ", averageExecutionTime: "
				+ String.format("%.2f", getAverageExecutionTime()) + ", minExecutionTime: " + minExecutionTime + "}";
	}

	public double getAverageExecutionTime() {
		return count == 0 ? 0 : (double) totalExecutionTime / (double) count;
	}

	public double getThroughput(long wallclockTime) {
		return count == 0 ? 0 : (double) count / ((double) wallclockTime / 1000D);
	}
}
