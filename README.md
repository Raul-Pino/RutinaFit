# RutinaFit

Esta aplicación web es una plataforma integral diseñada para la gestión y el seguimiento personalizado de entrenamientos dentro de un ecosistema de gimnasio. El sistema permite que tanto los usuarios de forma autónoma como sus entrenadores asignados creen y supervisen rutinas diarias, garantizando un control detallado del progreso físico y fomentando la interacción profesional entre alumno y entrenador.

## Tecnologías

- **Frontend**: Angular + Nginx
- **Backend**: Spring Boot (Java 25)
- **Base de datos**: PostgreSQL 16
- **Infraestructura**: Docker + Docker Compose

---

## Requisitos previos

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/)
- Git

## Para comprobar si están instalados

- [Docker](https://docs.docker.com/get-docker/): docker --version
- [Docker Compose](https://docs.docker.com/compose/): docker compose version

---

## Puesta en marcha rápida

### 1. Clona el repositorio

```bash
git clone https://github.com/Raul-Pino/RutinaFit.git
cd RutinaFit
```

### 2. Crea el archivo de variables de entorno

Copia el archivo de ejemplo y edítalo con tus valores:

```bash
cp .env.example .env
```

Abre `.env` y ajusta:

```env
DB_NAME=nombre_de_tu_base_de_datos
DB_USER=tu_usuario
DB_PASSWORD=tu_contraseña_segura
```

### 3. Levanta el proyecto

```bash
docker compose up --build
```

La primera vez descarga las imágenes de Docker y compila el proyecto, puede tardar unos minutos.

### 4. Accede a la aplicación

| Servicio | URL |
|---|---|
| Frontend | http://localhost:4200 |
| Backend | http://localhost:8080 |
| Base de datos | localhost:5432 |

---

## Comandos útiles

```bash
# Levantar el proyecto
docker compose up --build

# Levantar en segundo plano
docker compose up --build -d

# Ver logs en tiempo real
docker compose logs -f

# Ver logs de un servicio concreto
docker compose logs -f back

# Parar los contenedores
docker compose down

# Parar y borrar la base de datos
docker compose down -v

# Reconstruir solo un servicio
docker compose up --build back
```

