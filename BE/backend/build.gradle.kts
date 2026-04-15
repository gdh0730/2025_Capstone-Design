plugins {
	java
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.poseman"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.projectlombok:lombok")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
	// 1) HttpClient 5.x → groupId는 org.apache.httpcomponents.client5, version은 5.2.1 (또는 5.2.0 중 실제 존재하는 버전)
    implementation("org.apache.httpcomponents.client5:httpclient5")
    // 2) HttpCore 5.x → groupId는 org.apache.httpcomponents.core5
    implementation("org.apache.httpcomponents.core5:httpcore5")
    // 3) (선택) HTTP/2 서포트를 위한 httpcore5-h2
    implementation("org.apache.httpcomponents.core5:httpcore5-h2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
