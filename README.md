# Repositorio que incluye CBOMkit y el plugin sonar-cryptography
## CBOMkit
Este repositorio utiliza la versión 2.1.7 de CBOMkit.
## sonar-cryptography
Esta versión de CBOMkit utiliza la versión 1.4.5 del plugin sonar-cryptography.
## Ejecución
Para ejecutar el CBOMkit utilizando el sonar-cryptograph y desde local, se deben seguir una serie de pasos:

	1. Crear un directorio desde CBOMkit:
		mkdir -p cbomkit/cbomkit-override/lib
	2. Comentar la línea del docker-compose.yaml de CBOMkit relativo al volumen local del backend:
		volumes:
			- cbomkit-volume:/home/user/.cbomkit
			# - ./cbomkit-override/lib/main/:/deployments/lib/main/
	3. Crear un contenedor temporal del backend de CBOMkit o lanzarlo con make production.
	4. Cuando se tenga el contenedor en ejecución, copiar todos los fichero de la carpeta /deployments/lib/main en la carpeta creada antetiormente.
		docker cp cbomkit_backend_1:/deployments/lib/main ./cbomkit-override/lib
		En el caso de un contenedor temporal, cambiar cbomkit_backend_1 por el nombre del contenedor
	5. Compilar el proyecto de sonar-cryptography. Desde dentro de la carpeta sonar-cryptography:
		mvn clean install -Dmaven.test.skip=true
	6. Dar permisos de ejecución (en caso de que no haya) y ejecutar el script copy_jars.sh. Esto reemplazará los módulos correspondientes al plugin sonar-cryptography con los compilados localmente.
		chmod +x cbomkit/copy_jars.sh
	7. Descomentar la línea del docker-compose.yaml de CBOMkit que se ha comentado en el paso 2.
	8. Eliminar todos los volúmenes.
		make down
	9. Ejecutar la aplicación desde la carpeta cbomkit.
		make production
	10. Acceder a la aplicación desde el navegador.
		http://localhost:8001
