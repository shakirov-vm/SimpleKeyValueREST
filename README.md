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
