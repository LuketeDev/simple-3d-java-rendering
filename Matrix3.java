
public class Matrix3 {
    double[] values;

    Matrix3(double[] values) {
        this.values = values;
    }

    /**
     * Multiplica essa matriz 3x3 por outra
     * 
     * @param otherMatrix3 - Matrix3 que será multiplicada por esta matriz
     * @return Matrix3 - multiplicação das matrizes this * otherMatrix3
     */
    Matrix3 multiply(Matrix3 otherMatrix3) {
        double[] result = new double[9];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                for (int i = 0; i < 3; i++) {
                    result[row * 3 + col] += this.values[row * 3 + i] * otherMatrix3.values[i * 3 + col];
                }
            }
        }
        return new Matrix3(result);
    }

    /**
     * Aplica a transformação desta matriz 3x3 a um vértice 3D.
     *
     * A multiplicação é feita como: result = this * in, considerando o vértice in
     * como vetor coluna.
     *
     * @param in - o vértice 3D a ser transformado
     * @return Vertex - um novo Vertex resultante da transformação aplicada
     */
    Vertex transform(Vertex in) {
        return new Vertex(
                in.x * values[0] + in.y * values[3] + in.z * values[6],
                in.x * values[1] + in.y * values[4] + in.z * values[7],
                in.x * values[2] + in.y * values[5] + in.z * values[8]);
    }
}
