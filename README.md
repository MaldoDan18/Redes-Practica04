# Redes-Practica04

Aplicación de servidor HTTP que atiende peticiones GET, POST, PUT y DELETE. El servidor principal funciona con un pool de hilos configurable; cuando las conexiones activas superan la mitad de ese pool, los clientes se redirigen a un servidor secundario en otro puerto para evitar saturación. La carpeta web incluye recursos de prueba para GET (index.html, notas.txt, datos.json) y botones que disparan las demás operaciones mediante fetch.

## Métodos HTTP soportados
- GET: entrega archivos estáticos desde la carpeta web (HTML, TXT, JSON, imágenes). Si se solicita "/" responde con index.html.
- POST y PUT: reciben la petición y devuelven 200 OK con un mensaje de confirmación en texto plano.
- DELETE: misma lógica que POST/PUT, confirmando con 200 OK.

## Cómo compilar y ejecutar
1. Requisitos: JDK 21 y Maven instalados en el sistema.
2. Compilar desde ServidorHTTP: `mvn clean package`.
3. En una terminal, ejecutar el servidor secundario (redirigido): `java -cp target/ServidorHTTP-1.0-SNAPSHOT.jar ServidorSecundario`.
4. En otra terminal, iniciar el servidor principal y definir el tamaño del pool cuando lo solicite: `java -cp target/ServidorHTTP-1.0-SNAPSHOT.jar ServidorHTTP`.
5. Abrir http://localhost:8080 en el navegador. Al rebasar la mitad de hilos disponibles, las nuevas peticiones serán redirigidas automáticamente al puerto 8081.

