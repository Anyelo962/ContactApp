# ContactApp

Aplicación Android de gestión de contactos construida con **Jetpack Compose** y arquitectura **MVVM + Clean Architecture**.

---

## Requisitos previos

| Herramienta | Versión mínima |
|---|---|
| Android Studio | Meerkat (2025.1) o superior |
| JDK | 11 |
| Gradle | 9.4.1 (incluido vía Gradle Wrapper) |
| Android SDK — compileSdk | 36 |
| Android SDK — minSdk | 27 (Android 8.1) |

> No se necesita ninguna API key ni configuración externa. Las imágenes de avatar se obtienen automáticamente desde [picsum.photos](https://picsum.photos) (servicio público, sin autenticación).

---

## Clonar y abrir el proyecto

```bash
git clone <url-del-repositorio>
cd ContactApp
```

Luego abre la carpeta raíz `ContactApp/` desde **Android Studio → File → Open**.

Al abrir el proyecto por primera vez Android Studio descargará automáticamente las dependencias y el Gradle Wrapper. Asegúrate de tener conexión a internet en esa primera sincronización.

---

## Configuración del SDK

Android Studio pedirá instalar el SDK 36 si no lo tienes. Puedes hacerlo desde:

**Settings → Languages & Frameworks → Android SDK → SDK Platforms → Android 36**

---

## Ejecutar la aplicación

### En un emulador
1. **Tools → Device Manager → Create Virtual Device**
2. Selecciona cualquier dispositivo con API **27 o superior**
3. Presiona ▶ **Run** (`Shift+F10`)

### En un dispositivo físico
1. Activa **Opciones de desarrollador** en el teléfono
2. Habilita **Depuración USB**
3. Conecta el dispositivo y presiona ▶ **Run**

---

## Correr los tests

```bash
# Unit tests
./gradlew test

# Tests instrumentados (requiere emulador o dispositivo conectado)
./gradlew connectedAndroidTest
```

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| UI | Jetpack Compose · Material 3 |
| Navegación | Navigation Compose (rutas tipadas con `@Serializable`) |
| ViewModel | `androidx.lifecycle` ViewModel + `StateFlow` |
| Inyección de dependencias | Hilt 2.59 + KSP |
| Base de datos local | Room 2.8 |
| Red | Retrofit 2 + OkHttp 4 + Gson |
| Imágenes | Coil 3 |
| Lenguaje | Kotlin 2.2 |

---

## Estructura del proyecto

```
app/src/main/java/com/example/contactapp/
├── data/               # Repositorios, Room, Retrofit, DTOs
├── di/                 # Módulos Hilt (DB, Network, Repository)
├── domain/             # Modelos y casos de uso
├── view/ui/            # Pantallas Compose, navegación y tema
│   ├── navigation/
│   ├── screens/
│   │   ├── contactlist/
│   │   ├── contactdetail/
│   │   └── createcontact/
│   └── theme/
└── viewmodel/          # ViewModels compartidos
```

---

## Notas importantes

- **`local.properties`** es generado automáticamente por Android Studio y **no debe subirse al repositorio**. Solo contiene la ruta local al SDK (`sdk.dir`).
- La base de datos Room se crea automáticamente en el primer arranque. No requiere migraciones manuales.
- El app requiere acceso a internet **únicamente** para cargar las fotos de avatar aleatorias al crear/editar un contacto. Toda la información de contactos se almacena localmente.
# ContactApp
