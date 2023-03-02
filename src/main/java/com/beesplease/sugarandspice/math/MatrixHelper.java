package com.beesplease.sugarandspice.math;

import com.beesplease.sugarandspice.BuildConfig;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixPreservingVisitor;
import org.apache.commons.math3.linear.RealVector;

import java.util.Arrays;

public class MatrixHelper {
    private MatrixHelper() {
        // util class, no instances
    }

    public static RealMatrix vstack(RealMatrix... matrices) {
        int dimPrimary = 0;
        int dimOther = 0;
        for (RealMatrix m : matrices) {
            if (matrixEmpty(m))
                continue;
            dimPrimary += m.getRowDimension();
            dimOther = Math.max(dimOther, m.getColumnDimension());
        }

        RealMatrix stackedMatrix = new OpenMapRealMatrix(dimPrimary, dimOther);

        int offset = 0;
        for (RealMatrix m : matrices) {
            if (matrixEmpty(m))
                continue;
            setSparseSubMatrix(stackedMatrix, m, offset, 0);
            offset += m.getRowDimension();
        }
        return stackedMatrix;
    }

    public static RealMatrix hstack(RealMatrix... matrices) {
        int dimPrimary = 0;
        int dimOther = 0;
        for (RealMatrix m : matrices) {
            if (matrixEmpty(m))
                continue;
            dimPrimary += m.getColumnDimension();
            dimOther = Math.max(dimOther, m.getRowDimension());
        }

        RealMatrix stackedMatrix = new OpenMapRealMatrix(dimOther, dimPrimary);

        int offset = 0;
        for (RealMatrix m : matrices) {
            if (matrixEmpty(m))
                continue;
            setSparseSubMatrix(stackedMatrix, m, 0, offset);
            offset += m.getColumnDimension();
        }
        return stackedMatrix;
    }

    public static boolean matrixEmpty(RealMatrix m) {
        return m == null || m.getColumnDimension() == 0 || m.getRowDimension() == 0;
    }

    public static void setSparseSubMatrix(RealMatrix base, RealMatrix stamp, int rowOffset, int columnOffset) {
        if (matrixEmpty(stamp) || matrixEmpty(base))
            return;
        if (stamp instanceof OpenMapRealMatrix sparseStamp) {
            sparseStamp.walkInOptimizedOrder(new RealMatrixPreservingVisitor() {
                @Override
                public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
                }

                @Override
                public void visit(int row, int column, double value) {
                    base.setEntry(row + rowOffset, column + columnOffset, value);
                }

                @Override
                public double end() {
                    return 0;
                }
            });
        } else {
            base.setSubMatrix(stamp.getData(), rowOffset, columnOffset);
        }
    }

    public static void printMatrix(RealMatrix matrix) {
        int numRows = matrix.getRowDimension();
        int numCols = matrix.getColumnDimension();
        for (int i = 0; i < numRows; i++) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < numCols; j++) {
                builder.append(String.format("%8.2f", matrix.getEntry(i, j)));
            }
            BuildConfig.LOGGER.info(builder.toString());
        }
    }

    public static void printVector(RealVector vector) {
        BuildConfig.LOGGER.info(Arrays.toString(vector.toArray()));
    }
}
