Положить ключ:
```
curl -X POST -H "Content-Type: application/json" -d '{"key": "age", "value": "23"}' http://localhost:8080/api/add
```
Достать по ключу:
```
curl -X GET http://localhost:8080/api/get/age
```