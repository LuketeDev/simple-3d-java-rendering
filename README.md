# Simple 3D Java Rendering
Um renderizador 3D simples feito em Java puro, com rasterização manual, z-buffer, shading básico e possibilidade de inflar poliedros.

## 🔍 Funcionalidades

- Renderiza um objeto 3D (tetraedro) usando triângulos.
- Permite rotacionar em dois eixos (horizontal e vertical) através de sliders.
- Shading plano baseado em luz direcional atrás do visualizador.
- Z-buffer para profundidade.
- Subdivisão de triângulos para refinar a geometria.

## 🛠️ Como usar
1. Clonar o repositório:
```bash
   git clone https://github.com/LuketeDev/simple-3d-java-rendering.git
````

2. Abrir no seu IDE preferido (IntelliJ, Eclipse, VSCode, etc.).

3. Compilar os arquivos Java:

   ```bash
   javac DemoViewer.java Matrix3.java Triangle.java Vertex.java
   ```

4. Rodar:

   ```bash
   java DemoViewer
   ```

   Ou configurar sua IDE pra rodar `DemoViewer` como classe principal.

## 🎛️ Controles

Quando o programa estiver rodando, há sliders para:

* Slider lateral: Rotação vertical
* Slider de cima: Rotação horizontal
* Slider de baixo: Inflar poliedro

Você pode mexer nos sliders para ver como o objeto reage visualmente.

## 🚧 Possíveis melhorias
* **Controle de sombra**: Adicionar slider ou seletor rádio para controlar direção da sombra
* **Shading mais avançado**: por exemplo sombra suave, interpolação de normais, luz ambiente, múltiplas fontes de luz.
* **Texturização** ou cores variáveis por vértice.
* **Otimizações de desempenho**, especialmente para subdivisões altas ou centenas de triângulos: acelerar o cálculo baricêntrico, evitar muitos objetos alocados dentro do loop, etc.
* **Anti-aliasing** para bordas mais suaves.
* **Câmera móvel**: permitir mover não só rotação, mas também a câmera/objeto.


## 💡 Como funciona por dentro

1. Definimos os vértices e cores de cada triângulo do objeto.
2. Aplicamos transformações (rotação em horizontal e vertical) via matrizes 3×3.
3. Calculamos o vetor normal de cada face para determinar quanto ela "olha pra luz”.
4. Normalizamos normal e vetor de luz, fazendo produto escalar pra calcular intensidade de iluminação.
5. Rasterizamos cada pixel do triângulo dentro do retângulo delimitador, usando coordenadas baricêntricas para saber se o pixel está dentro do triângulo.
6. Usamos z-buffer pra garantir visibilidade correta: só pintamos pixel se ele for mais próximo do visualizador do que o valor atual armazenado no buffer.
7. Desenhamos o resultado numa `BufferedImage` e depois mostramos na tela com `Graphics2D`.

## 📋 Estrutura de arquivos

| Arquivo           | Descrição                                                |
| ----------------- | -------------------------------------------------------- |
| `DemoViewer.java` | Classe principal. Cria janela, sliders, render loop.     |
| `Matrix3.java`    | Matriz 3×3 para transformações (rotação etc.).           |
| `Triangle.java`   | Classe que guarda os três vértices e a cor do triângulo. |
| `Vertex.java`     | Classe que representa ponto no espaço 3D.                    |

## 🚀 Licença

Este projeto está licenciado sob a licença **MIT**.  
Veja o arquivo [LICENSE](LICENSE) para mais detalhes.