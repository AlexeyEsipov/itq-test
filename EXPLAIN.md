# Набор индексов

Для таблицы docs создается такой набор индексов:

```postgresql
-- 1. Индекс для поиска по inner_id (для методов репозитория)
CREATE INDEX idx_docs_inner_id ON docs (inner_id);

-- 2. Индекс для фильтра по статусу (точное совпадение)
CREATE INDEX idx_docs_status ON docs (status);

-- 3. Индекс для фильтра по created_at (диапазон)
CREATE INDEX idx_docs_created_at ON docs (created_at);

-- 4. Индекс для фильтра по updated_at (диапазон)
CREATE INDEX idx_docs_updated_at ON docs (updated_at);
```
Выбор из нескольких отдельных индексов, а не один составной обусловлен тем фактом, что в поисковом запросе реализована динамическая фильтрация. 
Индексы по отдельным полям позволяют PostgreSQL выбирать только те из них, которые нужны для конкретного запроса и объединять их.

Пример SQL-запроса, который генерирует Hibernate для случая, когда используются оба диапазона дат и статус:
```postgresql
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM docs
WHERE status = 'DRAFT'
  AND upper(created_by) LIKE '%generator%'
  AND created_at >= '2026-02-05 17:50:00+00'
  AND created_at <= '2026-02-05 19:59:59+00'
  AND updated_at >= '2026-02-05 17:59:00+00'
  AND updated_at <= '2026-02-05 19:59:59+00';
```
# Результат выполнения для таблицы, заполненной на 1000 строк:
Так как таблица небольшая, то используется последовательное сканирование.  Время выполнения -  Execution Time: 0.096 ms :
```text
Seq Scan on docs  (cost=0.00..38.90 rows=1 width=78) (actual time=0.085..0.085 rows=0 loops=1)
Filter: ((created_at >= '2026-02-02 10:00:00+00'::timestamp with time zone) AND (created_at <= '2026-02-02 12:59:59+00'::timestamp with time zone) AND (updated_at >= '2026-02-03 11:00:00+00'::timestamp with time zone) AND (updated_at <= '2026-02-03 14:59:59+00'::timestamp with time zone) AND ((status)::text = 'APPROVED'::text) AND (upper((created_by)::text) ~~ '%IVAN%'::text))
Rows Removed by Filter: 881
Buffers: shared hit=15
Planning Time: 0.073 ms
Execution Time: 0.096 ms
```

# Результат выполнения для таблицы, заполненной на 6000 строк:
Добавим в таблицу еще 5000 строк. Результат работы показывает, что стратегия планировщика изменилась и он использует индекс по полю updated_at,
Время выполнения уменьшилось, несмотря на увеличение таблицы - Execution Time: 0.031 ms :
```text
Index Scan using idx_docs_updated_at on docs  (cost=0.28..8.32 rows=1 width=582) (actual time=0.010..0.010 rows=0 loops=1)
  Index Cond: ((updated_at >= '2026-02-05 17:59:00+00'::timestamp with time zone) AND (updated_at <= '2026-02-05 18:59:59+00'::timestamp with time zone))
  Filter: ((created_at >= '2026-02-05 17:50:00+00'::timestamp with time zone) AND (created_at <= '2026-02-05 17:59:59+00'::timestamp with time zone) AND ((status)::text = 'DRAFT'::text) AND (upper((created_by)::text) ~~ '%generator%'::text))
  Buffers: shared hit=2
Planning:
  Buffers: shared hit=125
Planning Time: 0.602 ms
Execution Time: 0.031 ms
```

# Результат выполнения для таблицы, заполненной на 56000 строк:
Добавим в таблицу еще 50_000 строк. 
Время выполнения уменьшилось, несмотря на увеличение таблицы -  Execution Time: 0.021 ms :
```text
Index Scan using idx_docs_updated_at on docs  (cost=0.29..8.32 rows=1 width=582) (actual time=0.007..0.007 rows=0 loops=1)
  Index Cond: ((updated_at >= '2026-02-05 17:59:00+00'::timestamp with time zone) AND (updated_at <= '2026-02-05 18:59:59+00'::timestamp with time zone))
  Filter: ((created_at >= '2026-02-05 17:50:00+00'::timestamp with time zone) AND (created_at <= '2026-02-05 17:59:59+00'::timestamp with time zone) AND ((status)::text = 'DRAFT'::text) AND (upper((created_by)::text) ~~ '%generator%'::text))
  Buffers: shared hit=2
Planning:
  Buffers: shared hit=204
Planning Time: 0.608 ms
Execution Time: 0.021 ms
```
# Результат выполнения для таблицы, заполненной на 106000 строк:
Добавим в таблицу еще 50_000 строк. 
Время выполнения уменьшилось, несмотря на увеличение таблицы -  Execution Time: 0.016 ms :
```text
Index Scan using idx_docs_updated_at on docs  (cost=0.29..8.33 rows=1 width=582) (actual time=0.005..0.005 rows=0 loops=1)
  Index Cond: ((updated_at >= '2026-02-05 17:59:00+00'::timestamp with time zone) AND (updated_at <= '2026-02-05 19:59:59+00'::timestamp with time zone))
  Filter: ((created_at >= '2026-02-05 17:50:00+00'::timestamp with time zone) AND (created_at <= '2026-02-05 19:59:59+00'::timestamp with time zone) AND ((status)::text = 'DRAFT'::text) AND (upper((created_by)::text) ~~ '%generator%'::text))
  Buffers: shared hit=2
Planning:
  Buffers: shared hit=7
Planning Time: 0.123 ms
Execution Time: 0.016 ms
```

