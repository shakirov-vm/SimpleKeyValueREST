Положить ключ:
```
curl -X POST "http://localhost:8080/api/put?key=age&value=23"
```
Достать по ключу:
```
curl -X GET "http://localhost:8080/api/get?key=age"
```

# Нагрузочное тестирование (имплементация через хэш-таблицу в памяти):
Проводилось с использованием Apache JMeter, файл, описывающий тестирование - Thread Group1.jmx
И сервис, и JMeter запускались на одном и том же ноутбуке (6 ядер + 16 ГБ RAM, на зарядке)
## Чтение
[Запрос: `/api/get?key=age`
Достигает обработанных 40000 запросов / в секунду при 5000 потоков, 35000 при 20000.
![5000 потоков](Other/Read5000.png)]()
![20000 потоков](Other/Read20000.png)
## Запись
Запрос: `/api/put?key=age&value=23`
Достигает обработанных 40000 запросов / в секунду при 5000 потоков, и 32000 при 20000 потоков. Заметим, однако, что запись из разных потоков здесь не синхронизирована (а стоило бы), поэтому rps почти как при чтении; при добавлении синхронизации можно было бы ожидать сильной просадки.
![5000 потоков](Other/Write5000.png)
![20000 потоков](Other/Write20000.png)

# Поднять Postgresql в Docker
```
docker run -it --name java_postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=mysecretpassword \
  -e POSTGRES_DB=java_db \
  -p 5432:5432 \
  postgres
```

# Сравнительные замеры
Теперь измеряем с 100 потоков, ждём по 30 секунд
## Post
InMemoryNonConcurrentStorage:
![NonConcurrentPost.png](Other/NewMeasures/NonConcurrentPost.png)
InMemoryConcurrentStorage:
![ConcurrentPost.png](Other/NewMeasures/ConcurrentPost.png)
DockerPostgreSQLStorage:
![PostgresPost.png](Other/NewMeasures/PostgresPost.png)
## Get
InMemoryNonConcurrentStorage:
![NonConcurrentGet.png](Other/NewMeasures/NonConcurrentGet.png)
InMemoryConcurrentStorage:
![ConcurrentGet.png](Other/NewMeasures/ConcurrentGet.png)
DockerPostgreSQLStorage:
![PostgresGet.png](Other/NewMeasures/PostgresGet.png)
## Однопоточный на примере InMemoryConcurrentStorage
Get:
![ConcurrentGet1Thread.png](Other/NewMeasures/ConcurrentGet1Thread.png)
Post:
![ConcurrentPost1Thread.png](Other/NewMeasures/ConcurrentPost1Thread.png)

Дальше только get:

Для примера JMeter Postgresql, со стандартными настройками Postgresql (с контроллером DockerPostgreSQLStorage):
с 1 потоком - 1700 TPS
![OneThread.png](Other/One-To-Many-Measures/OneThread.png)
с 2 потоками - 2915 TPS
![TwoThread.png](Other/One-To-Many-Measures/TwoThread.png)
с 5 потоками - 2920 TPS
![FiveThread.png](Other/One-To-Many-Measures/FiveThread.png)
с 25 потоками - 3030 TPS
![Thread25.png](Other/One-To-Many-Measures/Thread25.png)
Видим очень жесткое ограничение по TPS, судя по всему это происходит из-за того, что всего одно подключение к Postgresql на все потоки, естественно, concurrency там особого не получается.
Сначала пробуем открывать соединение в начале каждого get; это очень плохая идея, на 1 поток около 75 транзакций получается (такое стыдно скриншотить), это можно использовать разве что для измерения времени открытие соединения + его закрытие.
Однако мы понимаем, что упираемся в отсутствие concurrency из-за одного соединения, поэтому есть решение - пул соединений с помощью HikariCP.
Делаем пул соединений в Java-приложении, используем HikariCPDockerPostgreSQLStorage()
с 1 потоком - 1730 TPS
![HikariOneThread.png](Other/One-To-Many-Measures/HikariOneThread.png)
с 2 потоками - 2915 TPS
![HikariTwoThread.png](Other/One-To-Many-Measures/HikariTwoThread.png)
с 5 потоками - 8100 TPS
![HikariFiveThread.png](Other/One-To-Many-Measures/HikariFiveThread.png)
с 25 потоками - 17300 TPS
![Hikari25Thread.png](Other/One-To-Many-Measures/Hikari25Thread.png)
с 75 потоками - 15800 TPS
![Hikari75Thread.png](Other/One-To-Many-Measures/Hikari75Thread.png)
Видим высокую утилизацию CPU, что мы и хотели получить, 16000 TPS Postgresql на чтение вполне себе выдает.
Заметим, что тут нет особо смысла пытаться настроить max_connections внутри postgres, потому оно равно 100:
```
docker exec -it java_postgres psql -U postgres -c "SHOW max_connections;"
 max_connections 
-----------------
 100
(1 row)
```
А так как 100 > 75, то мы просто напросто не упираемся в этот предел, нет смысла увеличивать это число.
