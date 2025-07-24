# Repositorio que incluye CBOMkit y el plugin sonar-cryptography
## CBOMkit
Este repositorio utiliza la versión 2.1.7 de CBOMkit.
## sonar-cryptography
Esta versión de CBOMkit utiliza la versión 1.4.5 del plugin sonar-cryptography.

## Ramas
Este repositorio constará de un total de 6 ramas:

	- main: contiene los ficheros del repositorio de cbomkit y sonar-cryptography-plugin originales, es decir, sin modificaciones.
 	- develop: contiene la última versión que se ha finalizado. Tras las 4 versiones, debería de estar alineada con feature/v3
  	- Todas las ramas que se encuentran dentro de la carpeta feature/, pertenecen a cada versión realizada.
   	- feature/v0: esta versión incluye la entrada de los activos a excluir por texto en una nueva pestaña del front, en Advanced options. Tras esto, se realiza un filtrado previo a la generación del cbom.json, pero posterior a la detección.
    	- feature/v1: en esta versión se incluye la funcionalidad anterior, a diferencia de que, en este caso, el filtrado se realiza anterior a la detección. La exclusión de los activos se realiza comparando el tipo de objeto, siendo menos exacto que el anterior.
     	- feature/v2: esta versión se centra más en el front. En vez de introducir los activos por texto, se ofrecen de forma más visual, con un desplegable automatizado.
      	- feature/v3: el objetivo de esta versión es aumentar la efectividad del filtrado. Para ello, la exclusión se realiza comparando el nombre del método, además del tipo de objeto como en las versiones anteriores. Esto aumentará la precisión de la detección de código en Python, ya que el nombre del método en Java no contiene los activos de forma explícita.

## Ejecución
En el caso de esta rama, la ejecución es muy sencilla. Hay que situarse en la rama cbomkit, y desde ahí lanzar la herramienta en modo producción:

	cd cbomkit
 	make production
  
