# Lucene camel-hump

A tiny PoC that demonstrates **IntelliJ‑style camel‑hump matching** using Apache Lucene.

## Examples
- `hr` → **H**TTP **R**equest
- `nuof` → **Nu**mber**O**f**F**iles (initials: `nof`; lowercase prefix match)
- `arra` → **Arra**ySize, **arra**yList (prefix at word starts)
- `nuf` → **Nu**mberO**f**Files (subsequence: `n.*u.*f`)

## Index fields
1. **`humps`**: initials of camel humps, edge‑n‑grammed → fast humps prefix matching (main signal).
2. **`parts`**: camel/digit‑split parts with edge‑n‑grams → prefix at word starts.
3. **`name_lc`**: full lowercase string (untokenized) → **subsequence** regex fallback (`n.*u.*f`).

## Build & Test
```bash
./gradlew test
```

## CLI Usage
### Index
```bash
./gradlew run --args="index ./index \
NumberOfFiles \
NumOf \
ArraySize \
arrayList \
HTTPRequest \
JSONParser \
UserID \
createUser \
NewUserForm"
```

### Search
```bash
./gradlew run --args='search ./index nof --topK 2'
```

Output:
```
1. NumberOfFiles    (score=5,0000)
2. NumOf    (score=1,0000)
```
