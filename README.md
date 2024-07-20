Форма заявки на покупку тура "Путешествие дня - Марракэш" было проведено автоматизированное тестирование для проверки позитивных и негативных сценариев оплаты дебетовой и кредитной картами.
# Запуск SUT и автоматизированных тестов 
## Подключение SUT к MySQL
1.	Открыть проект `Diploma_Ticket2Marrakesh` в IntelliJ IDEA.
2.	Запустить Docker Desktop.
3.	Запустить DBeaver.
4.	В терминале в корне проекта запустить контейнер:
   `docker-compose up`.
6.	Запустить сервис с указанием пути к базе данных для mysql:

  	`java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" -jar artifacts/aqa-shop.jar`.
8.	Запустить jar-файл:
   `java -jar artifacts/aqa-shop.jar`.
10.	Настроить соединения с базой данных MySQL в DBeaver.
11.	Запустить тесты: `.\gradlew clean test -DdbUrl=jdbc:mysql://localhost:3306/app`.
13.	Открыть отчёт Gradle в браузере Google Chrome.
14.	Остановить приложение.
15.	Остановить контейнер: `docker-compose down`.

## Подключение SUT к PostgreSQL
1.	Открыть проект `Diploma_Ticket2Marrakesh` в IntelliJ IDEA.
2.	Запустить Docker Desktop.
3.	Запустить DBeaver.
4.	В терминале в корне проекта запустить контейнер:
   `docker-compose up`.
6.	Запустить сервис с указанием пути к базе данных для postgresql:

  	`java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar artifacts/aqa-shop.jar`.
8.	Запустить jar-файл:
   `java -jar artifacts/aqa-shop.jar`.
10.	Настроить соединения с базой данных PostgreSQL в DBeaver.
11.	Запустить тесты: `.\gradlew clean test -DdbUrl=jdbc:postgresql://localhost:5432/app`.
13.	Открыть отчёт Gradle в браузере Google Chrome.
14.	Остановить приложение.
15.	Остановить контейнер: `docker-compose down`.
