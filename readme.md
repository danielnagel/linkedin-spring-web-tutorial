# linkedin spring web tutorial

### 2025-04-15

##### GraalVM - Native Images

Mit GraalVM könne native Images gebaut werden.
[Link zur Konfiguration](https://docs.spring.io/spring-boot/how-to/native-image/developing-your-first-application.html#howto.native-image.developing-your-first-application.buildpacks.gradle)
Dies hat einen deutlichen Performance Boost zur Laufzeit, verlängert allerdings auch die Buildzeit, weshalb sich diese Umstellung eher für Cloud Umgebung eignet.

### 2025-04-14

##### Spring Actuator

Mit Spring Actuator lässt sich die Applikation im Produktiv System überwachen.
Wichtige Kennzahlen könne ausgelesen, sowie Administration der laufenden Anwendung sind möglich.
Es gibt eine Vielzahl von Actuator  HTTP Endpunkten.

Die wichtigsten sind

- `health`, HealthCheck
- `info`, allgemeine Informationen
- `metrics`, Metriken
- `threaddump`/`heapdump`, aus der JVM
- `log`, Logging-Einstellungen

Separater HTTP Port empfohlen, der Endpunkte sollte nicht von außen Zugreifbar sein.

##### Umgebungsvariablen

Spring ist leicht durch, YAML, Umgebungsvariablen, application.properties, JVM Properties konfigurierbar.

Mit der Annotation `@Value(${variable})` können Werte aus den application.properties, yaml Dateien, usw. ausgelesen werden.

##### Docker Image bauen

`./gradlew bootBuildImage`
Asuführen des Images, z.b. mit `docker run -p 8080:8080 docker.io/library/web:0.0.1-SNAPSHOT`

### 2025-04-12

##### Transaktionen

- `@Transactional` stellt sicher, dass eine Methode oder ein Block von Code als eine einzige Transaktion ausgeführt wird.
	- Wenn während der Ausführung ein Fehler auftritt, wird die gesamte Transaktion zurückgerollt (Rollback), sodass keine inkonsistenten Daten in der Datenbank verbleiben.
	- Sie ist besonders nützlich, wenn mehrere Datenbankoperationen in einer Methode ausgeführt werden, um Datenkonsistenz sicherzustellen.

Im Code Beispiel wurden mehrere Ids auf einmal gelöscht, daher wurde die Methode als eine Transaktion annotiert:

```java
@Transactional
public void deleteAll(List<UUID> ids) {
	for (UUID id : ids) {
		dirtySecretsRepository.deleteById(id);
	}
}
```

### 2025-04-11

##### JPA

- `implementation 'org.springframework.boot:spring-boot-starter-data-jpa'`
	- Java Persistence API
	- Schnittstellen für Datenbankoperationen (CRUD)
- `runtimeOnly 'com.h2database:h2'`
	- leichtgewichtige in-memory Datenbank
	- Meist für Testzwecke gedacht, sollte nicht produktiv genutzt werden.
- `CrudRepository` bietet grundlegende CRUD-Operationen für den Zugriff auf Daten in einer Datenbank.
	- Bestandteil von `SpringData`
	- Zum verwenden wird ein Interface erstellt und dieses erweitert.
		- `public interface UserRepository extends CrudRepository<User, Long> {}`
		- User ist hier eine Entität, Long der Schlüssel.
	- Eine Entität sieht wie folgt aus:

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
}
```

- `@Entity` kennzeichnet eine Klasse als JPA-Entität, die einer Tabelle in der Datenbank entspricht.
	- Jede Instanz der Klasse repräsentiert eine Zeile in der Tabelle.
- `@Id` definiert einen Primärschlüssel.
- `@GeneratedValue` sorgt dafür, dass dieser Primärschlüssel automatisch generiert wird.

##### docker-compose

- `runtimeOnly 'org.postgresql:postgresql'`
	- Laufzeitabhängigkeit zu Postgres Datenbank
- `developmentOnly 'org.springframework.boot:spring-boot-docker-compose'`
	- Unterstützt bei der Integration von Docker Compose in die Spring Anwendung.
	- Docker-Compose-Dienste werden automatisch gestartet und mit der Applikation verbunden.
	- Spring Boot erkennt automatisch die compose Datei.
	- Beispiel compose-Datei in einem Spring Boot Projekt:

```yaml
services:
	postgres:
		image: postgres:latest
		environment:
			POSTGRES_USER: linkedin
			POSTGRES_PASSWORD: linkedin
			POSTGRES_DB: dirtysecrets
		ports:
			- "5432"
```

##### Testcontainer

- `@Testcontainers` aus der Testcontainers Bibliothek
	- sorgt dafür, dass Container (wie z. B. eine PostgreSQL-Datenbank) vor dem Start des Tests initialisiert und nach dem Testlauf automatisch gestoppt werden.
- `@Container`  aus der Testcontainers Bibliothek
	- kennzeichnet ein Container-Objekt, das für die Dauer des Tests gestartet und verwaltet wird.
- `@ServiceConnaction` aus Spring Boot 3.1
	- um eine Verbindung zwischen einem Testcontainer und einer Spring Boot-Komponente herzustellen.

Beispiel Impltementierung:
```java
@Testcontainers
@SpringBootTest
public class DirtySecretsRepositoryTests {
	@Autowired
	private DirtySecretsRepository repository;
	
	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
	
	@Test
	public void shouldSaveSecrets() {
		// create a new secret
		var secret = new DirtySecret();
		secret.setName("test");
		secret.setSecret("blabla");
		
		// save the secret
		var savedSecret = this.repository.save(secret);
		
		// assert that the secret was saved
		assertNotNull(savedSecret.getId() != null);
	}
}
```

Benötigte Abhängigkeiten:

- `implementation 'org.testcontainers:junit-jupiter'`
- `implementation 'org.springframework.boot:spring-boot-testcontainers'`
- `implementation 'org.testcontainers:postgresql'`

### 2025-04-10

##### Blackbox Tests
Es sind Blackbox Tests gegen den gesamten Web Container möglich.

- `@SpringBootTest` lädt einen vollständigen Spring-Anwendungskontext für Tests.
	- eignet sich für Integrationstests, bei denen mehrere Schichten (z.B. Controller, Service, Repository) getestet werden sollen.
	- Mit dem Attribut `webEnvironment = WebEnvironment.RANDOM_PORT` wird der Server angewiesen mit einem zufälligen Port zu starten.
- `@LocalServerPort` injiziert den aktuellen Server Port in den Test Code.

Im folgenden ein kleines Beispiel für einen Blackbox Test:

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DemoWebTests {
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	void greetingShouldReturnMessage() {
		var url = "http://localhost:" + port + "/www/greeting";
		var body = this.restTemplate.getForObject(url, String.class);
		assertTrue(body.contains("Hello, World!"));
	}
}
```
##### Fehlerbehandlung
Es wurden drei Varianten zur Fehlerbehandlung in Endpunkten gezeigt:

1. Spring ResponseStatusException-Klasse
	- `new ResponseStatusException(HttpStatus.NOT_FOUND)`
2. Die Annotation `@ResponseStatus` an Exception-Klasse
	- `@ResponseStatus(value = HttpStatus.NOT_FOUND)`
3. eigene Exception-Handler Methode mit `@ExceptionHandler`

### 2025-04-09

`Gradle` ein Plugin, damit die korrekte Java Version genutzt werden kann:

```groovy
plugins {
	id 'org.gradle.toolchains.foojay-resolver-convention' version '0.9.0'
}
```

##### MVC Rest Controller Annotationen

1. `@RestControlller` ist eine Spezialisierung von `@Controller`, der HTTP-Anfragen verarbeitet und JSON- oder XML-Daten als Antwort zurückgibt.
2. `@RequestMapping`legt auf Klassenebene den Basis-URL-Pfad für alle Methoden in der Klasse fest.
	- Kann auch auf Methodenebene verwendet werden, dafür werden aber in der Regel `@GetMapping` usw. genutzt.
3. `@GetMapping` legt einen URL-Pfad für einen HTTP GET-Request fest, dasselbe gilt für die anderen HTTP-Methoden.
	- `@GetMapping` ist äquivalent zu `@RequestMapping(method = RequestMethod.GET)`.

##### MVC UnitTests

- `@WebMvcTest(<classname>.class)` ermöglicht das Testen eines MVC Web Controllers, mit der "Einschränkung", dass lediglich der Web-Layer des Spring Frameworks geladen wird
	- d.h. andere Services müssen gemocked werden.
- `MockMvc`kann Anfragen an Endpunkte des Web-Layers simulieren.
- Wird ein Service zum Testen des Endpunktes benötigt, kann dieser durch die `@MockitoBean` Annotation gemocked werden.

Kleines Beispiel welches alles zusammenfasst:

```java
@WebMvcTest(DirtySecretsRestController.class) 
// Diese Annotation konfiguriert den Test so, dass nur der Web-Layer (Controller) getestet wird.
// Spring Boot lädt nur die für den Controller relevanten Beans.
public class DirtySecretsRestControllerTests {

    @Autowired
    private MockMvc mockMvc;
    // MockMvc wird verwendet, um HTTP-Anfragen an den Controller zu simulieren und die Antworten zu testen.

    @MockitoBean
    private DirtySecretsRepository repository;
    // MockitoBean erstellt ein Mock-Objekt für das Repository, um dessen Verhalten zu simulieren.

    @Test
    public void shouldSaveSecrets() throws Exception {
        // Testmethode, die überprüft, ob ein DirtySecret erfolgreich gespeichert wird.

        DirtySecret secret = new DirtySecret("Doug", "Ex Alcoholic");
        // Ein Beispielobjekt, das gespeichert werden soll.

        when(repository.save(any(DirtySecret.class))).thenReturn(secret);
        // Simuliert das Verhalten des Repositorys: Wenn die `save`-Methode aufgerufen wird, gibt sie das `secret`-Objekt zurück.

        this.mockMvc.perform(
            MockMvcRequestBuilders.post("/api/dirty-secrets")
                .contentType(MediaType.APPLICATION_JSON)
                // Simuliert eine POST-Anfrage mit JSON-Daten.

                .content("{\"name\":\"Doug\", \"secret\":\"Ex Alcoholic\"}")
                // Der JSON-Body der Anfrage, der die Daten des DirtySecret enthält.
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        // Erwartet, dass die Antwort des Controllers den HTTP-Status 200 (OK) hat.
		.andExpect(MockMvcResultMatchers.content().contentType("application/json"))
.andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Doug"))
.andExpect(MockMvcResultMatchers.jsonPath("$.secret").value("Ex Alcoholic"));
    }
}
```

##### Template Engine Thymeleaf

Damit eine HTML-Datei Thymeleaf Attribute akzeptiert müssen folgende ergänzugen gemacht werden:

```html
<!-- thymeleaf Direktiven bekann machen -->
<html xmlns:th="http://www.thymeleaf.org">
<!-- thymeleaf Direktiven verwenden (th:<attribut>) -->
<h1 th:text="${message}" />
```

Verarbeitet werden diese Templates im entsprechenden Controller, dazu ein Beispiel Code:

```java
@Controller
// Markiert diese Klasse als einen Spring MVC Controller
@RequestMapping("/www/greeting")
// Ordnet HTTP-Anfragen an /www/greeting diesem Controller zu
public class GreetingWebController {

@GetMapping
// Verarbeitet HTTP-GET-Anfragen für die Wurzel von /www/greeting
public String index(Model model) {
	model.addAttribute("message", "Hello, World!");
	// Fügt dem Model ein Attribut "message" mit dem Wert "Hello, World!" hinzu
	return "greeting";
	// Gibt den Namen der View zurück, die gerendert werden soll, in diesem Fall "greeting"
}

}
```
### 2025-04-08

Grundlage ist dieser Kurs: https://www.linkedin.com/learning/spring-boot-grundkurs-23739242

Eine Spring Applikation kann mit dem [Spring Initializr](https://start.spring.io/), der [Spring CLI](https://docs.spring.io/spring-boot/installing.html#getting-started.installing.cli.manual-installation) oder über VS Code Plugins erstellt werden.

Es gibt drei Annotationen um Spring Beans zu erzeugen:

1. `@Component` Annotiert Klassen, welche keine spezifische Rolle haben.
2. `@Bean` Annotiert Methoden, wenn die Kontrolle über die Erstellung und Konfiguration einer Bean benötigt wird.
3. `@Service` Annotiert Klassen, welche Geschäftslogik beinhalten.

Weitere wichtige Annotationen:

1. `@Controller` fungiert als Controller in einer MVC-Architektur.
2. `@Repository` Persistenz schicht, z.B. Datenbankoperationen (CRUD).
3. `@SpringBootApplication` bestimmt das Wurzel Package der Anwendung.
	- Spring Boot such in allen Klassen unter dem Wurzel-Package nach Spring-Annotationen und veraltet sie als Spring Beans (oder kurz **Component Scanning**).
4. `@Autowired` injiziert automatisch eine Abhängigkeit. Spring Boot sucht eine passende Bean im Wurzel Package.