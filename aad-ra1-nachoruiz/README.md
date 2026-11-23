## AD-1.b · Valorar las formas de acceso (criterio b)

### 1. Comparación de las formas de acceso

| Forma de acceso | Ventajas | Inconvenientes | Cuándo utilizarla |
|---|---|---|---|
| Texto secuencial | Es fácil de leer y modificar. | Para buscar un dato concreto hay que recorrer el fichero. | Para archivos CSV o ficheros de texto sencillos. |
| Binario secuencial | Los datos ocupan poco espacio y se pueden leer de forma rápida. | No se puede leer directamente con un editor de texto. | Para guardar muchos registros que se suelen leer completos. |
| Binario aleatorio | Permite acceder y modificar directamente un registro concreto. | Necesita que todos los registros tengan un tamaño fijo. | Para consultar o modificar registros individuales. |
| XML con DOM | Permite acceder y modificar fácilmente los datos del XML. | Carga todo el documento en memoria. | Para trabajar con archivos XML pequeños o medianos. |

### 2. Forma de acceso elegida para mi proyecto

Para mi proyecto del taller mecánico elegiría el acceso binario aleatorio.  
Lo elegiría porque permite consultar directamente los registros de los vehículos.  
También puedo modificar un dato concreto, como el año, sin tener que volver a escribir todo el fichero.  
Aunque requiere que todos los registros tengan el mismo tamaño, me parece útil para gestionar los datos de los vehículos.

### 3. Resultados del BenchmarkLectura

He ejecutado `BenchmarkLectura` en mi ordenador con un fichero de 200.000 líneas y un tamaño aproximado de 10,2 MB.

Estos son los resultados obtenidos:

| Método de lectura | Tiempo (mediana) |
|---|---|
| `BufferedReader` | 39 ms |
| `Files.readAllLines()` | 21 ms |
| `Files.lines()` | 28 ms |
| `Scanner.nextLine()` | 131 ms |

En esta prueba, el método más rápido ha sido `Files.readAllLines()` y el más lento ha sido `Scanner.nextLine()`.

### 4. ¿Por qué esta medición no es definitiva?

Esta medición no es definitiva porque los resultados dependen del ordenador donde se ejecuta.  
También pueden variar según la memoria disponible, la velocidad del disco y los programas que estén abiertos.  
Además, el tamaño del fichero puede influir en los tiempos de lectura.  
Por eso, estos resultados sirven para comparar los métodos, pero no significa que uno sea siempre el más rápido.

### 5. Evidencias

- `evidencias/AD-1.b_aleatorio.png`: captura del vehículo antes y después de modificar su año mediante acceso aleatorio.
- `evidencias/AD-1.b_benchmark.png`: captura de los resultados del benchmark.


## AD-1.c · Leer XML (criterio c)

### ¿Qué técnica elegiría para leer un XML de 2 GB?

Para leer un fichero XML de 2 GB elegiría SAX.

Elegiría esta técnica porque permite leer el fichero poco a poco, sin necesidad de cargarlo entero en memoria.

En cambio, DOM carga todo el documento XML en memoria, por lo que un fichero tan grande podría consumir muchos recursos.

Por eso, creo que SAX sería una mejor opción para trabajar con archivos XML de gran tamaño.