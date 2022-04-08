package com.hairlesscat.app.algorithm;

public class UnequalBipartitePartitionException extends Exception {
    public UnequalBipartitePartitionException(int numOfLeftVertices, int numOfRightVertices) {
        super(String.format("Bipartite graph has uneven sides: num of left vertices = %d, num of right vertices = %d", numOfLeftVertices, numOfRightVertices));
    }
}
