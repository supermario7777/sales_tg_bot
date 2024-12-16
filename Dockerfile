# Шаг 1: Используем базовый образ с JDK для сборки
FROM eclipse-temurin:17-jdk AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Шаг 2: Копируем файлы Maven проекта (pom.xml и src)
COPY pom.xml /app/
COPY src /app/src

# Копируем файл .env
COPY .env /app/

# Шаг 3: Устанавливаем Maven, скачиваем зависимости и собираем проект
RUN apt-get update && apt-get install -y maven && apt-get clean
RUN mvn clean package -X

# Шаг 4: Используем легковесный JRE образ для запуска
FROM eclipse-temurin:17-jre

# Устанавливаем рабочую директорию
WORKDIR /app

# Шаг 5: Копируем собранный JAR-файл из предыдущего этапа
COPY --from=build /app/target/myapp-1.0-SNAPSHOT.jar myapp.jar

# Шаг 6: Копируем файл .env для запуска
#COPY .env /app/

# Шаг 7: Открываем порт (если требуется)
EXPOSE 8080

# Шаг 8: Запускаем приложение
CMD ["java", "-jar", "/app/myapp.jar"]
