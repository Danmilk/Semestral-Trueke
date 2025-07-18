# TruekeApp

**TruekeApp** es una aplicación Android construida con **Jetpack Compose** que permite a los usuarios intercambiar artículos. Este proyecto demuestra una configuración moderna en Android con Kotlin, Retrofit para llamadas a red y `DataStore` para almacenar tokens de forma segura.

## Funcionalidades
- **Autenticación de usuarios** – permite registrarse, iniciar sesión y almacenar el token JWT de forma segura. 🔐  
- **Gestión de ítems** – crea ítems con imágenes, edítalos y administra tu inventario. 🛠️  
- **Sistema de ofertas** – envía y recibe ofertas de trueque entre usuarios. 🤝  
- **Navegación** – navegación con una sola actividad usando Compose y una gráfica de navegación que conecta pantallas como login, inicio, inventario y configuración. 🧭  
- **Modo oscuro** – cambia el tema para intercambiar de día o de noche. 🌙  

## Estructura del proyecto
app/
└─ src/main/java/com/tuempresa/truekeapp/

├─ data/ # Definiciones de API, modelos y repositorio

├─ datastore/ # TokenDataStore para persistencia segura de JWT

├─ session/ # SessionManager para manejar expiración de sesión

├─ ui/ # Pantallas, componentes y navegación con Compose

└─ MainActivity.kt


## Cómo ejecutar la app
1. Instala **Android Studio Hedgehog** o una versión más reciente.
2. Clona este repositorio y ábrelo en Android Studio.
3. Asegúrate de tener un emulador o dispositivo con API 26 o superior.
4. Haz clic en **Run** para compilar y desplegar la app.

El proyecto incluye Gradle wrapper. La primera vez que lo compiles necesitarás conexión a internet para descargar dependencias.  

## Configuración de la API
Puedes apuntar a un servidor local durante el desarrollo o a un host remoto para producción.  
La app espera un backend que exponga los endpoints definidos en `TruekeApi`. Ajusta la URL base en `RetrofitClient.kt` si es necesario.

## Posibles mejoras futuras
- Implementar notificaciones push para ofertas nuevas.
- Agregar sistema de valoraciones entre usuarios.
- Permitir marcar ítems como favoritos o seguidos.
- Mejorar el sistema de búsqueda y filtros por categoría o ubicación.
- Soporte multilenguaje (español, inglés, etc.).
- Validación de imagen antes de subir (peso, formato, dimensiones).
- Modo offline con caché local de ítems.
- chat entre usuarios

## Licencia
Este proyecto se proporciona con fines educativos y no incluye una licencia específica.  
¡A truekear! 
